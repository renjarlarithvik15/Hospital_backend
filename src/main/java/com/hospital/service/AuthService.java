package com.hospital.service;

import com.hospital.config.JwtTokenProvider;
import com.hospital.dto.AuthRequest;
import com.hospital.dto.AuthResponse;
import com.hospital.dto.RegisterRequest;
import com.hospital.entity.Doctor;
import com.hospital.entity.Patient;
import com.hospital.entity.Role;
import com.hospital.entity.Staff;
import com.hospital.entity.User;
import com.hospital.exception.BadRequestException;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.StaffRepository;
import com.hospital.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final StaffRepository staffRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       PatientRepository patientRepository,
                       DoctorRepository doctorRepository,
                       StaffRepository staffRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider tokenProvider) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.patientRepository = patientRepository;
        this.doctorRepository = doctorRepository;
        this.staffRepository = staffRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse authenticateUser(AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);

        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new BadRequestException("User not found"));

        Long entityId = null;
        if (user.getRole() == Role.ROLE_PATIENT) {
            entityId = patientRepository.findByUserId(user.getId()).map(Patient::getId).orElse(null);
        } else if (user.getRole() == Role.ROLE_DOCTOR) {
            entityId = doctorRepository.findByUserId(user.getId()).map(Doctor::getId).orElse(null);
        } else if (user.getRole() == Role.ROLE_STAFF) {
            entityId = staffRepository.findByUserId(user.getId()).map(Staff::getId).orElse(null);
        }

        return new AuthResponse(
                jwt,
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().name(),
                entityId
        );
    }

    @Transactional
    public AuthResponse registerPatient(RegisterRequest registerRequest) {
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }

        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Email is already registered!");
        }

        User user = new User(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                passwordEncoder.encode(registerRequest.getPassword()),
                registerRequest.getFullName(),
                registerRequest.getPhone(),
                Role.ROLE_PATIENT
        );

        User savedUser = userRepository.save(user);

        Patient patient = new Patient(
                savedUser,
                registerRequest.getDateOfBirth(),
                registerRequest.getGender(),
                registerRequest.getBloodGroup(),
                registerRequest.getEmergencyContact(),
                registerRequest.getAddress(),
                registerRequest.getMedicalHistory()
        );

        Patient savedPatient = patientRepository.save(patient);

        String jwt = tokenProvider.generateTokenFromUsername(savedUser.getUsername());

        return new AuthResponse(
                jwt,
                savedUser.getId(),
                savedUser.getUsername(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().name(),
                savedPatient.getId()
        );
    }
}

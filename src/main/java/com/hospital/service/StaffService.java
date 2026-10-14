package com.hospital.service;

import com.hospital.dto.StaffDto;
import com.hospital.entity.Department;
import com.hospital.entity.Role;
import com.hospital.entity.Staff;
import com.hospital.entity.User;
import com.hospital.exception.BadRequestException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.DepartmentRepository;
import com.hospital.repository.StaffRepository;
import com.hospital.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StaffService {

    private final StaffRepository staffRepository;
    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final PasswordEncoder passwordEncoder;

    public StaffService(StaffRepository staffRepository,
                        UserRepository userRepository,
                        DepartmentRepository departmentRepository,
                        PasswordEncoder passwordEncoder) {
        this.staffRepository = staffRepository;
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<StaffDto> getAllStaff() {
        return staffRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public StaffDto getStaffById(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with ID: " + id));
        return convertToDto(staff);
    }

    @Transactional
    public StaffDto createStaff(StaffDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new BadRequestException("Username is already taken!");
        }
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Email is already in use!");
        }

        Department department = null;
        if (dto.getDepartmentId() != null) {
            department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + dto.getDepartmentId()));
        }

        User user = new User(
                dto.getUsername(),
                dto.getEmail(),
                passwordEncoder.encode(dto.getPassword() != null ? dto.getPassword() : "staff123"),
                dto.getFullName(),
                dto.getPhone(),
                Role.ROLE_STAFF
        );

        User savedUser = userRepository.save(user);

        Staff staff = new Staff(
                savedUser,
                department,
                dto.getDesignation(),
                dto.getSalary(),
                dto.getShiftTime(),
                dto.getJoiningDate() != null ? dto.getJoiningDate() : LocalDate.now()
        );

        Staff savedStaff = staffRepository.save(staff);
        return convertToDto(savedStaff);
    }

    @Transactional
    public StaffDto updateStaff(Long id, StaffDto dto) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with ID: " + id));

        User user = staff.getUser();
        user.setFullName(dto.getFullName());
        user.setPhone(dto.getPhone());
        if (dto.getEmail() != null && !dto.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmail(dto.getEmail())) {
                throw new BadRequestException("Email already taken!");
            }
            user.setEmail(dto.getEmail());
        }
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }
        userRepository.save(user);

        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found with ID: " + dto.getDepartmentId()));
            staff.setDepartment(department);
        }

        staff.setDesignation(dto.getDesignation());
        staff.setSalary(dto.getSalary());
        staff.setShiftTime(dto.getShiftTime());
        if (dto.getJoiningDate() != null) {
            staff.setJoiningDate(dto.getJoiningDate());
        }

        Staff updated = staffRepository.save(staff);
        return convertToDto(updated);
    }

    @Transactional
    public void deleteStaff(Long id) {
        Staff staff = staffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with ID: " + id));
        staffRepository.delete(staff);
    }

    public StaffDto convertToDto(Staff staff) {
        StaffDto dto = new StaffDto();
        dto.setId(staff.getId());
        if (staff.getUser() != null) {
            dto.setUserId(staff.getUser().getId());
            dto.setUsername(staff.getUser().getUsername());
            dto.setEmail(staff.getUser().getEmail());
            dto.setFullName(staff.getUser().getFullName());
            dto.setPhone(staff.getUser().getPhone());
        }
        if (staff.getDepartment() != null) {
            dto.setDepartmentId(staff.getDepartment().getId());
            dto.setDepartmentName(staff.getDepartment().getName());
        }
        dto.setDesignation(staff.getDesignation());
        dto.setSalary(staff.getSalary());
        dto.setShiftTime(staff.getShiftTime());
        dto.setJoiningDate(staff.getJoiningDate());
        return dto;
    }
}

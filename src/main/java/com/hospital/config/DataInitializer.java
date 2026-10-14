package com.hospital.config;

import com.hospital.entity.*;
import com.hospital.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final DepartmentRepository departmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final AppointmentRepository appointmentRepository;
    private final PrescriptionRepository prescriptionRepository;
    private final BillRepository billRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           DepartmentRepository departmentRepository,
                           DoctorRepository doctorRepository,
                           PatientRepository patientRepository,
                           StaffRepository staffRepository,
                           AppointmentRepository appointmentRepository,
                           PrescriptionRepository prescriptionRepository,
                           BillRepository billRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.departmentRepository = departmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.appointmentRepository = appointmentRepository;
        this.prescriptionRepository = prescriptionRepository;
        this.billRepository = billRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return; // Data already exists
        }

        // 1. Create Admin
        User adminUser = new User(
                "admin",
                "admin@hospital.com",
                passwordEncoder.encode("admin123"),
                "Dr. Administrator Smith",
                "+1 (555) 010-0001",
                Role.ROLE_ADMIN
        );
        userRepository.save(adminUser);

        // 2. Create Departments
        Department cardiology = departmentRepository.save(new Department(
                "Cardiology",
                "Specialized in heart conditions, vascular health, echocardiograms, and angioplasty care.",
                "Dr. Sarah Jenkins",
                "+1 (555) 010-1001",
                "Building A, 3rd Floor"
        ));

        Department neurology = departmentRepository.save(new Department(
                "Neurology",
                "Diagnosis and treatment of neurological disorders, spine care, and brain stroke recovery.",
                "Dr. Robert Chen",
                "+1 (555) 010-1002",
                "Building B, 2nd Floor"
        ));

        Department pediatrics = departmentRepository.save(new Department(
                "Pediatrics",
                "Comprehensive healthcare for infants, children, and adolescents, including immunizations.",
                "Dr. Emily Watson",
                "+1 (555) 010-1003",
                "Building A, 1st Floor"
        ));

        Department orthopedics = departmentRepository.save(new Department(
                "Orthopedics",
                "Treatment of musculoskeletal conditions, fractures, joints, and sports injuries.",
                "Dr. David Kim",
                "+1 (555) 010-1004",
                "Building C, 1st Floor"
        ));

        Department generalMed = departmentRepository.save(new Department(
                "General Medicine",
                "Primary patient care, acute health management, routine checkups, and chronic disease control.",
                "Dr. Marcus Vance",
                "+1 (555) 010-1005",
                "Building A, Ground Floor"
        ));

        // 3. Create Doctors
        User doctorUser1 = new User(
                "dr.sarah",
                "sarah.jenkins@hospital.com",
                passwordEncoder.encode("doctor123"),
                "Dr. Sarah Jenkins",
                "+1 (555) 010-2001",
                Role.ROLE_DOCTOR
        );
        Doctor doctor1 = new Doctor(
                doctorUser1,
                cardiology,
                "Cardiologist",
                "MD, FACC - Harvard Medical School",
                12,
                150.0,
                "Monday, Wednesday, Friday",
                "09:00 AM - 04:00 PM"
        );
        doctorRepository.save(doctor1);

        User doctorUser2 = new User(
                "dr.chen",
                "robert.chen@hospital.com",
                passwordEncoder.encode("doctor123"),
                "Dr. Robert Chen",
                "+1 (555) 010-2002",
                Role.ROLE_DOCTOR
        );
        Doctor doctor2 = new Doctor(
                doctorUser2,
                neurology,
                "Neurologist",
                "MD, PhD - Johns Hopkins University",
                15,
                180.0,
                "Tuesday, Thursday, Saturday",
                "10:00 AM - 05:00 PM"
        );
        doctorRepository.save(doctor2);

        User doctorUser3 = new User(
                "dr.emily",
                "emily.watson@hospital.com",
                passwordEncoder.encode("doctor123"),
                "Dr. Emily Watson",
                "+1 (555) 010-2003",
                Role.ROLE_DOCTOR
        );
        Doctor doctor3 = new Doctor(
                doctorUser3,
                pediatrics,
                "Pediatric Specialist",
                "MBBS, MD Pediatrics - Stanford Medicine",
                8,
                120.0,
                "Monday, Tuesday, Thursday, Friday",
                "08:30 AM - 03:30 PM"
        );
        doctorRepository.save(doctor3);

        // 4. Create Staff
        User staffUser = new User(
                "staff.mary",
                "mary.adams@hospital.com",
                passwordEncoder.encode("staff123"),
                "Mary Adams (RN)",
                "+1 (555) 010-3001",
                Role.ROLE_STAFF
        );
        Staff staff1 = new Staff(
                staffUser,
                cardiology,
                "Senior Nurse Supervisor",
                55000.0,
                "Morning Shift (07:00 AM - 03:30 PM)",
                LocalDate.now().minusYears(3)
        );
        staffRepository.save(staff1);

        // 5. Create Patients
        User patientUser1 = new User(
                "john.doe",
                "john.doe@gmail.com",
                passwordEncoder.encode("patient123"),
                "John Doe",
                "+1 (555) 010-4001",
                Role.ROLE_PATIENT
        );
        Patient patient1 = new Patient(
                patientUser1,
                LocalDate.of(1988, 5, 14),
                "Male",
                "O+",
                "Jane Doe (+1 555-010-4002)",
                "742 Evergreen Terrace, Springfield",
                "Mild hypertension diagnosed in 2021. No known drug allergies."
        );
        patientRepository.save(patient1);

        User patientUser2 = new User(
                "alice.smith",
                "alice.smith@gmail.com",
                passwordEncoder.encode("patient123"),
                "Alice Smith",
                "+1 (555) 010-4003",
                Role.ROLE_PATIENT
        );
        Patient patient2 = new Patient(
                patientUser2,
                LocalDate.of(1995, 11, 20),
                "Female",
                "A+",
                "Peter Smith (+1 555-010-4004)",
                "124 Conch Street, Bikini Bay",
                "Asthma managed with inhaler. Penicillin sensitive."
        );
        patientRepository.save(patient2);

        // 6. Create Appointments
        Appointment appt1 = new Appointment(
                patient1,
                doctor1,
                LocalDate.now().plusDays(1),
                "10:00 AM",
                "Routine cardiac follow-up and ECG review",
                "Occasional chest tightness after mild exertion"
        );
        appt1.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appt1);

        Appointment appt2 = new Appointment(
                patient2,
                doctor2,
                LocalDate.now().plusDays(3),
                "02:30 PM",
                "Migraine and tension headache evaluation",
                "Frequent throbbing headaches on left side, light sensitivity"
        );
        appt2.setStatus(AppointmentStatus.PENDING);
        appointmentRepository.save(appt2);

        Appointment appt3 = new Appointment(
                patient1,
                doctor1,
                LocalDate.now().minusDays(5),
                "11:00 AM",
                "Initial consultation for blood pressure screening",
                "Elevated systolic pressure on home monitor"
        );
        appt3.setStatus(AppointmentStatus.COMPLETED);
        appt3.setDoctorNotes("Blood pressure was 138/88 mmHg. Advised low sodium diet and prescribed Ramipril.");
        appointmentRepository.save(appt3);

        // 7. Create Prescriptions
        Prescription pres1 = new Prescription(
                appt3,
                doctor1,
                patient1,
                "Stage 1 Hypertension",
                "1. Ramipril 5mg - 1 tablet daily every morning\n2. Aspirin 75mg - 1 tablet daily post lunch\n3. Multivitamin Zinc - 1 tablet daily for 30 days",
                "Maintain low sodium diet, avoid excessive caffeine, measure BP daily at 9 AM, follow-up in 2 weeks.",
                LocalDate.now().minusDays(5)
        );
        prescriptionRepository.save(pres1);

        // 8. Create Bills
        Bill bill1 = new Bill(
                "INV-2026-001",
                appt3,
                patient1,
                LocalDate.now().minusDays(5),
                150.0,  // consultation
                45.0,   // medication
                60.0,   // lab charges (ECG)
                0.0,    // room
                15.0,   // other
                270.0,  // paid in full
                "CARD",
                "Payment received via Visa credit card ending in 4242."
        );
        bill1.setPaymentDate(LocalDate.now().minusDays(5));
        billRepository.save(bill1);

        Bill bill2 = new Bill(
                "INV-2026-002",
                appt1,
                patient1,
                LocalDate.now(),
                150.0,
                0.0,
                0.0,
                0.0,
                0.0,
                50.0, // partially paid
                "UPI",
                "Advance appointment deposit collected."
        );
        billRepository.save(bill2);
    }
}

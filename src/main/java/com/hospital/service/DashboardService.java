package com.hospital.service;

import com.hospital.dto.DashboardStatsDto;
import com.hospital.entity.AppointmentStatus;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.BillRepository;
import com.hospital.repository.DepartmentRepository;
import com.hospital.repository.DoctorRepository;
import com.hospital.repository.PatientRepository;
import com.hospital.repository.StaffRepository;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final StaffRepository staffRepository;
    private final DepartmentRepository departmentRepository;
    private final AppointmentRepository appointmentRepository;
    private final BillRepository billRepository;

    public DashboardService(DoctorRepository doctorRepository,
                            PatientRepository patientRepository,
                            StaffRepository staffRepository,
                            DepartmentRepository departmentRepository,
                            AppointmentRepository appointmentRepository,
                            BillRepository billRepository) {
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.staffRepository = staffRepository;
        this.departmentRepository = departmentRepository;
        this.appointmentRepository = appointmentRepository;
        this.billRepository = billRepository;
    }

    public DashboardStatsDto getDashboardStats() {
        DashboardStatsDto stats = new DashboardStatsDto();
        stats.setTotalDoctors(doctorRepository.count());
        stats.setTotalPatients(patientRepository.count());
        stats.setTotalStaff(staffRepository.count());
        stats.setTotalDepartments(departmentRepository.count());
        stats.setTotalAppointments(appointmentRepository.count());
        stats.setPendingAppointments(appointmentRepository.countByStatus(AppointmentStatus.PENDING));
        stats.setConfirmedAppointments(appointmentRepository.countByStatus(AppointmentStatus.CONFIRMED));
        stats.setCompletedAppointments(appointmentRepository.countByStatus(AppointmentStatus.COMPLETED));
        stats.setCancelledAppointments(appointmentRepository.countByStatus(AppointmentStatus.CANCELLED));
        stats.setTotalRevenue(billRepository.calculateTotalRevenue() != null ? billRepository.calculateTotalRevenue() : 0.0);
        stats.setPendingRevenue(billRepository.calculatePendingAmount() != null ? billRepository.calculatePendingAmount() : 0.0);
        return stats;
    }
}

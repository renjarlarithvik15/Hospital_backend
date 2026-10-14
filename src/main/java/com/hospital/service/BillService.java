package com.hospital.service;

import com.hospital.dto.BillRequest;
import com.hospital.entity.Appointment;
import com.hospital.entity.Bill;
import com.hospital.entity.Patient;
import com.hospital.entity.PaymentStatus;
import com.hospital.exception.BadRequestException;
import com.hospital.exception.ResourceNotFoundException;
import com.hospital.repository.AppointmentRepository;
import com.hospital.repository.BillRepository;
import com.hospital.repository.PatientRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class BillService {

    private final BillRepository billRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;

    public BillService(BillRepository billRepository,
                       PatientRepository patientRepository,
                       AppointmentRepository appointmentRepository) {
        this.billRepository = billRepository;
        this.patientRepository = patientRepository;
        this.appointmentRepository = appointmentRepository;
    }

    public List<Bill> getAllBills() {
        return billRepository.findAll();
    }

    public Bill getBillById(Long id) {
        return billRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with ID: " + id));
    }

    public Bill getBillByNumber(String billNumber) {
        return billRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Bill not found with number: " + billNumber));
    }

    public List<Bill> getBillsByPatient(Long patientId) {
        return billRepository.findByPatientId(patientId);
    }

    public List<Bill> getBillsByPaymentStatus(PaymentStatus paymentStatus) {
        return billRepository.findByPaymentStatus(paymentStatus);
    }

    @Transactional
    public Bill generateBill(BillRequest request) {
        Patient patient = patientRepository.findById(request.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with ID: " + request.getPatientId()));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId()).orElse(null);
        }

        String billNumber = request.getBillNumber();
        if (billNumber == null || billNumber.isBlank()) {
            billNumber = "INV-" + System.currentTimeMillis() % 1000000;
        }

        Bill bill = new Bill(
                billNumber,
                appointment,
                patient,
                request.getBillDate() != null ? request.getBillDate() : LocalDate.now(),
                request.getConsultationFee(),
                request.getMedicationFee(),
                request.getLabCharges(),
                request.getRoomCharges(),
                request.getOtherCharges(),
                request.getPaidAmount() != null ? request.getPaidAmount() : 0.0,
                request.getPaymentMethod(),
                request.getNotes()
        );

        if (request.getPaymentDate() != null) {
            bill.setPaymentDate(request.getPaymentDate());
        } else if (bill.getPaidAmount() > 0) {
            bill.setPaymentDate(LocalDate.now());
        }

        return billRepository.save(bill);
    }

    @Transactional
    public Bill recordPayment(Long billId, Double amount, String paymentMethod) {
        if (amount == null || amount <= 0) {
            throw new BadRequestException("Payment amount must be greater than zero");
        }

        Bill bill = getBillById(billId);
        double remainingBalance = Math.max(0.0, bill.getTotalAmount() - bill.getPaidAmount());
        double newPaidAmount = bill.getPaidAmount() + Math.min(amount, remainingBalance);
        bill.setPaidAmount(newPaidAmount);
        if (paymentMethod != null && !paymentMethod.isBlank()) {
            bill.setPaymentMethod(paymentMethod);
        }
        bill.setPaymentDate(LocalDate.now());
        return billRepository.save(bill);
    }

    @Transactional
    public void deleteBill(Long id) {
        Bill bill = getBillById(id);
        billRepository.delete(bill);
    }
}

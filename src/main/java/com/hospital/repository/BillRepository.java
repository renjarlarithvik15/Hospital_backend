package com.hospital.repository;

import com.hospital.entity.Bill;
import com.hospital.entity.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long> {
    Optional<Bill> findByBillNumber(String billNumber);
    List<Bill> findByPatientId(Long patientId);
    List<Bill> findByPaymentStatus(PaymentStatus paymentStatus);
    List<Bill> findByAppointmentId(Long appointmentId);

    @Query("SELECT COALESCE(SUM(b.totalAmount), 0.0) FROM Bill b")
    Double calculateTotalBilledAmount();

    @Query("SELECT COALESCE(SUM(b.paidAmount), 0.0) FROM Bill b")
    Double calculateTotalRevenue();

    @Query("SELECT COALESCE(SUM(b.balanceAmount), 0.0) FROM Bill b WHERE b.paymentStatus <> 'PAID'")
    Double calculatePendingAmount();
}

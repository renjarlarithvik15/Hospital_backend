package com.hospital.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bills")
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "bill_number", nullable = false, unique = true, length = 50)
    private String billNumber;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(name = "bill_date", nullable = false)
    private LocalDate billDate;

    @Column(name = "consultation_fee")
    private Double consultationFee = 0.0;

    @Column(name = "medication_fee")
    private Double medicationFee = 0.0;

    @Column(name = "lab_charges")
    private Double labCharges = 0.0;

    @Column(name = "room_charges")
    private Double roomCharges = 0.0;

    @Column(name = "other_charges")
    private Double otherCharges = 0.0;

    @Column(name = "total_amount", nullable = false)
    private Double totalAmount = 0.0;

    @Column(name = "paid_amount", nullable = false)
    private Double paidAmount = 0.0;

    @Column(name = "balance_amount", nullable = false)
    private Double balanceAmount = 0.0;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status", nullable = false, length = 20)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;

    @Column(name = "payment_method", length = 50)
    private String paymentMethod;

    @Column(name = "payment_date")
    private LocalDate paymentDate;

    @Column(columnDefinition = "TEXT")
    private String notes;

    public Bill() {
    }

    public Bill(String billNumber, Appointment appointment, Patient patient, LocalDate billDate,
                Double consultationFee, Double medicationFee, Double labCharges, Double roomCharges,
                Double otherCharges, Double paidAmount, String paymentMethod, String notes) {
        this.billNumber = billNumber;
        this.appointment = appointment;
        this.patient = patient;
        this.billDate = billDate != null ? billDate : LocalDate.now();
        this.consultationFee = consultationFee != null ? consultationFee : 0.0;
        this.medicationFee = medicationFee != null ? medicationFee : 0.0;
        this.labCharges = labCharges != null ? labCharges : 0.0;
        this.roomCharges = roomCharges != null ? roomCharges : 0.0;
        this.otherCharges = otherCharges != null ? otherCharges : 0.0;
        this.totalAmount = this.consultationFee + this.medicationFee + this.labCharges + this.roomCharges + this.otherCharges;
        this.paidAmount = paidAmount != null ? paidAmount : 0.0;
        this.balanceAmount = this.totalAmount - this.paidAmount;
        this.paymentMethod = paymentMethod;
        this.notes = notes;
        updateStatus();
    }

    public void recalculate() {
        if (this.consultationFee == null) this.consultationFee = 0.0;
        if (this.medicationFee == null) this.medicationFee = 0.0;
        if (this.labCharges == null) this.labCharges = 0.0;
        if (this.roomCharges == null) this.roomCharges = 0.0;
        if (this.otherCharges == null) this.otherCharges = 0.0;
        this.totalAmount = this.consultationFee + this.medicationFee + this.labCharges + this.roomCharges + this.otherCharges;
        if (this.paidAmount == null) this.paidAmount = 0.0;
        this.balanceAmount = Math.max(0.0, this.totalAmount - this.paidAmount);
        updateStatus();
    }

    private void updateStatus() {
        if (this.paidAmount >= this.totalAmount && this.totalAmount > 0) {
            this.paymentStatus = PaymentStatus.PAID;
        } else if (this.paidAmount > 0) {
            this.paymentStatus = PaymentStatus.PARTIALLY_PAID;
        } else {
            this.paymentStatus = PaymentStatus.PENDING;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public Appointment getAppointment() {
        return appointment;
    }

    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public LocalDate getBillDate() {
        return billDate;
    }

    public void setBillDate(LocalDate billDate) {
        this.billDate = billDate;
    }

    public Double getConsultationFee() {
        return consultationFee;
    }

    public void setConsultationFee(Double consultationFee) {
        this.consultationFee = consultationFee;
    }

    public Double getMedicationFee() {
        return medicationFee;
    }

    public void setMedicationFee(Double medicationFee) {
        this.medicationFee = medicationFee;
    }

    public Double getLabCharges() {
        return labCharges;
    }

    public void setLabCharges(Double labCharges) {
        this.labCharges = labCharges;
    }

    public Double getRoomCharges() {
        return roomCharges;
    }

    public void setRoomCharges(Double roomCharges) {
        this.roomCharges = roomCharges;
    }

    public Double getOtherCharges() {
        return otherCharges;
    }

    public void setOtherCharges(Double otherCharges) {
        this.otherCharges = otherCharges;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public Double getPaidAmount() {
        return paidAmount;
    }

    public void setPaidAmount(Double paidAmount) {
        this.paidAmount = paidAmount;
        recalculate();
    }

    public Double getBalanceAmount() {
        return balanceAmount;
    }

    public void setBalanceAmount(Double balanceAmount) {
        this.balanceAmount = balanceAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDate getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDate paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}

package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.PrescriptionRequest;
import com.hospital.entity.Prescription;
import com.hospital.service.PrescriptionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionController {

    private final PrescriptionService prescriptionService;

    public PrescriptionController(PrescriptionService prescriptionService) {
        this.prescriptionService = prescriptionService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<Prescription>>> getAllPrescriptions() {
        return ResponseEntity.ok(ApiResponse.success("Prescriptions retrieved", prescriptionService.getAllPrescriptions()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'PATIENT', 'STAFF')")
    public ResponseEntity<ApiResponse<Prescription>> getPrescriptionById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Prescription retrieved", prescriptionService.getPrescriptionById(id)));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'DOCTOR', 'STAFF')")
    public ResponseEntity<ApiResponse<List<Prescription>>> getPrescriptionsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.success("Patient prescriptions retrieved", prescriptionService.getPrescriptionsByPatient(patientId)));
    }

    @GetMapping("/doctor/{doctorId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF')")
    public ResponseEntity<ApiResponse<List<Prescription>>> getPrescriptionsByDoctor(@PathVariable Long doctorId) {
        return ResponseEntity.ok(ApiResponse.success("Doctor prescriptions retrieved", prescriptionService.getPrescriptionsByDoctor(doctorId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR')")
    public ResponseEntity<ApiResponse<Prescription>> createPrescription(@Valid @RequestBody PrescriptionRequest request) {
        Prescription created = prescriptionService.createPrescription(request);
        return ResponseEntity.ok(ApiResponse.success("Prescription added successfully", created));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePrescription(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.ok(ApiResponse.success("Prescription deleted successfully", null));
    }
}

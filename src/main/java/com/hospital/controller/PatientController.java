package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.PatientDto;
import com.hospital.service.PatientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF')")
    public ResponseEntity<ApiResponse<List<PatientDto>>> getAllPatients() {
        return ResponseEntity.ok(ApiResponse.success("Patients retrieved", patientService.getAllPatients()));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('PATIENT')")
    public ResponseEntity<ApiResponse<PatientDto>> getMyPatient(Principal principal) {
        return ResponseEntity.ok(ApiResponse.success(
                "Patient profile retrieved",
                patientService.getPatientByUsername(principal.getName())
        ));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'DOCTOR', 'STAFF', 'PATIENT')")
    public ResponseEntity<ApiResponse<PatientDto>> getPatientById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Patient retrieved", patientService.getPatientById(id)));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT')")
    public ResponseEntity<ApiResponse<PatientDto>> updatePatient(@PathVariable Long id, @Valid @RequestBody PatientDto patientDto) {
        PatientDto updated = patientService.updatePatient(id, patientDto);
        return ResponseEntity.ok(ApiResponse.success("Patient updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.ok(ApiResponse.success("Patient deleted successfully", null));
    }
}

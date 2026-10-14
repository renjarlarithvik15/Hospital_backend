package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.StaffDto;
import com.hospital.service.StaffService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/staff")
@PreAuthorize("hasRole('ADMIN')")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<StaffDto>>> getAllStaff() {
        return ResponseEntity.ok(ApiResponse.success("Staff members retrieved", staffService.getAllStaff()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffDto>> getStaffById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Staff member retrieved", staffService.getStaffById(id)));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StaffDto>> createStaff(@Valid @RequestBody StaffDto staffDto) {
        StaffDto created = staffService.createStaff(staffDto);
        return ResponseEntity.ok(ApiResponse.success("Staff member created successfully", created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StaffDto>> updateStaff(@PathVariable Long id, @Valid @RequestBody StaffDto staffDto) {
        StaffDto updated = staffService.updateStaff(id, staffDto);
        return ResponseEntity.ok(ApiResponse.success("Staff member updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteStaff(@PathVariable Long id) {
        staffService.deleteStaff(id);
        return ResponseEntity.ok(ApiResponse.success("Staff member deleted successfully", null));
    }
}

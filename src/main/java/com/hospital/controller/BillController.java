package com.hospital.controller;

import com.hospital.dto.ApiResponse;
import com.hospital.dto.BillRequest;
import com.hospital.entity.Bill;
import com.hospital.exception.BadRequestException;
import com.hospital.service.BillService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bills")
public class BillController {

    private final BillService billService;

    public BillController(BillService billService) {
        this.billService = billService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<List<Bill>>> getAllBills() {
        return ResponseEntity.ok(ApiResponse.success("Bills retrieved", billService.getAllBills()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'STAFF')")
    public ResponseEntity<ApiResponse<Bill>> getBillById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Bill retrieved", billService.getBillById(id)));
    }

    @GetMapping("/number/{billNumber}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'STAFF')")
    public ResponseEntity<ApiResponse<Bill>> getBillByNumber(@PathVariable String billNumber) {
        return ResponseEntity.ok(ApiResponse.success("Bill retrieved", billService.getBillByNumber(billNumber)));
    }

    @GetMapping("/patient/{patientId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'STAFF')")
    public ResponseEntity<ApiResponse<List<Bill>>> getBillsByPatient(@PathVariable Long patientId) {
        return ResponseEntity.ok(ApiResponse.success("Patient bills retrieved", billService.getBillsByPatient(patientId)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public ResponseEntity<ApiResponse<Bill>> generateBill(@Valid @RequestBody BillRequest request) {
        Bill created = billService.generateBill(request);
        return ResponseEntity.ok(ApiResponse.success("Bill generated successfully", created));
    }

    @PostMapping("/{id}/payment")
    @PreAuthorize("hasAnyRole('ADMIN', 'PATIENT', 'STAFF')")
    public ResponseEntity<ApiResponse<Bill>> recordPayment(
            @PathVariable Long id,
            @RequestBody Map<String, Object> paymentPayload) {
        if (paymentPayload == null || paymentPayload.get("amount") == null) {
            throw new BadRequestException("Payment amount is required");
        }

        double amount;
        try {
            amount = Double.parseDouble(paymentPayload.get("amount").toString());
        } catch (NumberFormatException ex) {
            throw new BadRequestException("Payment amount must be a valid number");
        }

        String paymentMethod = paymentPayload.get("paymentMethod") == null ? null : paymentPayload.get("paymentMethod").toString();
        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new BadRequestException("Payment method is required");
        }

        Bill updated = billService.recordPayment(id, amount, paymentMethod);
        return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", updated));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteBill(@PathVariable Long id) {
        billService.deleteBill(id);
        return ResponseEntity.ok(ApiResponse.success("Bill deleted successfully", null));
    }
}

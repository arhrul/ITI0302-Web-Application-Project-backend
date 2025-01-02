package org.example.backend.controller.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dtos.PaymentDTO;
import org.example.backend.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Tag(name = "Payment", description = "Operations related to payment")
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @Operation(summary = "Create a new payment", description = "Creates a new payment record.")
    @ApiResponse(responseCode = "200", description = "Payment created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid payment data")
    public ResponseEntity<PaymentDTO> createPayment(@Valid @RequestBody PaymentDTO paymentDTO) {
        log.info("Received request to create payment: {}", paymentDTO);
        PaymentDTO createdPayment = paymentService.createPayment(paymentDTO);
        log.info("Payment created successfully with ID: {}", createdPayment.getId());
        return ResponseEntity.ok(createdPayment);
    }

    @GetMapping
    @Operation(summary = "Retrieve all payments", description = "Fetches all payment records.")
    @ApiResponse(responseCode = "200", description = "List of payments")
    public ResponseEntity<List<PaymentDTO>> getPayments() {
        log.info("Received request to retrieve all payments.");
        List<PaymentDTO> payments = paymentService.getAllPayments();
        log.info("Successfully retrieved {} payment records.", payments.size());
        return ResponseEntity.ok(payments);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Retrieve payment by ID", description = "Fetches a single payment record by its ID.")
    @ApiResponse(responseCode = "200", description = "Payment retrieved successfully")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<PaymentDTO> getPaymentById(@PathVariable Long id) {
        log.info("Received request to retrieve payment with ID: {}", id);
        PaymentDTO payment = paymentService.getPaymentById(id);
        log.info("Successfully retrieved payment: {}", payment);
        return ResponseEntity.ok(payment);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update payment", description = "Updates an existing payment by ID.")
    @ApiResponse(responseCode = "200", description = "Payment updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid payment data")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<PaymentDTO> updatePayment(@PathVariable Long id, @Valid @RequestBody PaymentDTO paymentDTO) {
        log.info("Received request to update payment with ID: {}. New data: {}", id, paymentDTO);
        PaymentDTO updatedPayment = paymentService.updatePayment(id, paymentDTO);
        log.info("Payment updated successfully: {}", updatedPayment);
        return ResponseEntity.ok(updatedPayment);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete payment", description = "Deletes a payment by ID.")
    @ApiResponse(responseCode = "204", description = "Payment deleted successfully")
    @ApiResponse(responseCode = "404", description = "Payment not found")
    public ResponseEntity<PaymentDTO> deletePayment(@PathVariable long id) {
        log.info("Received request to delete payment with ID: {}", id);
        paymentService.deletePayment(id);
        log.info("Payment with ID: {} has been deleted.", id);
        return ResponseEntity.noContent().build();
    }
}

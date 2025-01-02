package org.example.backend.test.controller;

import org.example.backend.controller.payment.PaymentController;
import org.example.backend.dtos.PaymentDTO;
import org.example.backend.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PaymentControllerTest {

    @InjectMocks
    private PaymentController paymentController;

    @Mock
    private PaymentService paymentService;

    private PaymentDTO mockPayment;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockPayment = new PaymentDTO();
        mockPayment.setId(1L);
        mockPayment.setAmount(100.0);
    }

    @Test
    void createPayment_ShouldReturnCreatedPayment() {
        when(paymentService.createPayment(any(PaymentDTO.class))).thenReturn(mockPayment);

        ResponseEntity<PaymentDTO> response = paymentController.createPayment(mockPayment);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPayment, response.getBody());

        verify(paymentService, times(1)).createPayment(mockPayment);
    }

    @Test
    void getPayments_ShouldReturnListOfPayments() {
        List<PaymentDTO> paymentList = Collections.singletonList(mockPayment);
        when(paymentService.getAllPayments()).thenReturn(paymentList);

        ResponseEntity<List<PaymentDTO>> response = paymentController.getPayments();

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(paymentList, response.getBody());

        verify(paymentService, times(1)).getAllPayments();
    }

    @Test
    void getPaymentById_ShouldReturnPayment() {
        when(paymentService.getPaymentById(1L)).thenReturn(mockPayment);

        ResponseEntity<PaymentDTO> response = paymentController.getPaymentById(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockPayment, response.getBody());

        verify(paymentService, times(1)).getPaymentById(1L);
    }

    @Test
    void updatePayment_ShouldReturnUpdatedPayment() {
        PaymentDTO updatedPayment = new PaymentDTO();
        updatedPayment.setId(1L);
        updatedPayment.setAmount(150.0);

        when(paymentService.updatePayment(eq(1L), any(PaymentDTO.class))).thenReturn(updatedPayment);

        ResponseEntity<PaymentDTO> response = paymentController.updatePayment(1L, updatedPayment);

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(updatedPayment, response.getBody());

        verify(paymentService, times(1)).updatePayment(1L, updatedPayment);
    }

    @Test
    void deletePayment_ShouldReturnNoContent() {
        doNothing().when(paymentService).deletePayment(1L);

        ResponseEntity<PaymentDTO> response = paymentController.deletePayment(1L);

        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());


        verify(paymentService, times(1)).deletePayment(1L);
    }
}

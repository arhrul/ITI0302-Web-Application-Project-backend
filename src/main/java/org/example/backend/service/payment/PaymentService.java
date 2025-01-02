package org.example.backend.service.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dtos.PaymentDTO;
import org.example.backend.exception.exceptions.NoSuchPaymentException;
import org.example.backend.exception.exceptions.NoSuchReservationException;
import org.example.backend.mappers.PaymentMapper;
import org.example.backend.model.Client;
import org.example.backend.model.Payment;
import org.example.backend.model.Reservation;
import org.example.backend.repository.client.ClientRepository;
import org.example.backend.repository.reservation.ReservationRepository;
import org.example.backend.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final ClientRepository clientRepository;
    private final ReservationRepository reservationRepository;
    private static final String ERROR_MESSAGE = "Payment not found!";

    public PaymentDTO createPayment(PaymentDTO paymentDTO) {
        Payment payment = paymentMapper.toPayment(paymentDTO);
        log.info("Creating payment for reservation ID: {}", paymentDTO.getReservationId());
        Reservation reservation = reservationRepository.findById(paymentDTO.getReservationId())
                .orElseThrow(() -> new RuntimeException("reservation not found"));
        reservation.setStatus("CONFIRMED");
        payment.setAmount(reservation.getTotalPrice());
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment created with ID: {}", savedPayment.getId());

        return paymentMapper.toPaymentDTO(savedPayment);
    }

    public List<PaymentDTO> getAllPayments() {
        log.info("Fetching all payments.");
        List<Payment> payments = paymentRepository.findAll();
        log.info("Found {} payment records.", payments.size());
        return paymentMapper.toPaymentDTOList(payments);
    }

    public PaymentDTO updatePayment(Long id, PaymentDTO paymentDTO) {
        log.info("Updating payment with ID: {}", id);


        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchPaymentException(ERROR_MESSAGE));
        Client client = clientRepository.findById(paymentDTO.getClientId())
                .orElseThrow(() -> new NoSuchPaymentException(ERROR_MESSAGE));
        Reservation reservation = reservationRepository.findById(paymentDTO.getReservationId())
                .orElseThrow(() -> new NoSuchPaymentException(ERROR_MESSAGE));

        payment.setAmount(reservation.getTotalPrice());
        payment.setPaymentDate(paymentDTO.getPaymentDate());
        payment.setStatus(paymentDTO.getStatus());
        payment.setCardNumber(paymentDTO.getCardNumber());
        payment.setClientId(client);
        payment.setReservationId(reservation);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment with ID: {} updated successfully.", savedPayment.getId());

        return paymentMapper.toPaymentDTO(savedPayment);
    }

    public PaymentDTO getPaymentById(Long id) {
        log.info("Fetching payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchPaymentException(ERROR_MESSAGE));
        return paymentMapper.toPaymentDTO(payment);
    }

    public void deletePayment(Long id) {
        log.info("Deleting payment with ID: {}", id);
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new NoSuchPaymentException(ERROR_MESSAGE));

        Reservation reservation = reservationRepository.findById(payment.getReservationId().getId())
                .orElseThrow(() -> new NoSuchReservationException("Reservation not found!"));

        reservation.setStatus("UNCONFIRMED");
        paymentRepository.delete(payment);
        log.info("Payment with ID: {} deleted successfully.", id);
    }
}

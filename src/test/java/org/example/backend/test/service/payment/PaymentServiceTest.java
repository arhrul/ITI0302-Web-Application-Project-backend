package org.example.backend.test.service.payment;

import org.example.backend.dtos.PaymentDTO;
import org.example.backend.exception.exceptions.NoSuchPaymentException;
import org.example.backend.mappers.PaymentMapper;
import org.example.backend.model.Client;
import org.example.backend.model.Payment;
import org.example.backend.model.Reservation;
import org.example.backend.repository.client.ClientRepository;
import org.example.backend.repository.reservation.ReservationRepository;
import org.example.backend.repository.PaymentRepository;
import org.example.backend.service.payment.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    private PaymentDTO paymentDTO;
    private Payment payment;
    private Reservation reservation;
    private Client client;

    @BeforeEach
    void setUp() {
        paymentDTO = new PaymentDTO();
        paymentDTO.setReservationId(1L);
        paymentDTO.setAmount(100.0);
        paymentDTO.setPaymentDate(LocalDate.now());
        paymentDTO.setStatus("CONFIRMED");
        paymentDTO.setCardNumber("5555555555555555");

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setTotalPrice(100.0);
        reservation.setStatus("CONFIRMED");

        payment = new Payment();
        payment.setId(1L);
        payment.setAmount(100.0);
        payment.setPaymentDate(LocalDate.now());
        payment.setStatus("CONFIRMED");
        payment.setReservationId(reservation);

        client = new Client();
        client.setId(1L);
        client.setEmail("john.doe@example.com");
        client.setFirstName("John");
        client.setLastName("Doe");
    }

    @Test
    void shouldCreatePayment() {
        given(reservationRepository.findById(paymentDTO.getReservationId())).willReturn(Optional.of(reservation));
        given(paymentMapper.toPayment(paymentDTO)).willReturn(payment);
        given(paymentRepository.save(payment)).willReturn(payment);
        given(paymentMapper.toPaymentDTO(payment)).willReturn(paymentDTO);

        PaymentDTO result = paymentService.createPayment(paymentDTO);

        assertThat(result).isEqualTo(paymentDTO);
        then(paymentRepository).should().save(payment);
    }

    @Test
    void shouldThrowExceptionWhenReservationNotFoundDuringCreate() {
        given(reservationRepository.findById(paymentDTO.getReservationId())).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> paymentService.createPayment(paymentDTO));

        assertThat(thrown)
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("reservation not found");
    }

    @Test
    void shouldReturnPaymentById() {
        Long paymentId = 1L;
        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(paymentMapper.toPaymentDTO(payment)).willReturn(paymentDTO);

        PaymentDTO result = paymentService.getPaymentById(paymentId);

        assertThat(result).isEqualTo(paymentDTO);
        then(paymentRepository).should().findById(paymentId);
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFoundById() {
        Long paymentId = 1L;
        given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> paymentService.getPaymentById(paymentId));

        assertThat(thrown)
                .isInstanceOf(NoSuchPaymentException.class)
                .hasMessageContaining("Payment not found!");
    }

    @Test
    void shouldUpdatePayment() {
        Long paymentId = 1L;
        paymentDTO.setStatus("UPDATED");
        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(clientRepository.findById(paymentDTO.getClientId())).willReturn(Optional.of(client));
        given(reservationRepository.findById(paymentDTO.getReservationId())).willReturn(Optional.of(reservation));
        given(paymentRepository.save(payment)).willReturn(payment);
        given(paymentMapper.toPaymentDTO(payment)).willReturn(paymentDTO);

        PaymentDTO result = paymentService.updatePayment(paymentId, paymentDTO);

        assertThat(result).isEqualTo(paymentDTO);
        then(paymentRepository).should().save(payment);
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFoundForUpdate() {
        Long paymentId = 1L;
        given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> paymentService.updatePayment(paymentId, paymentDTO));

        assertThat(thrown)
                .isInstanceOf(NoSuchPaymentException.class)
                .hasMessageContaining("Payment not found!");
    }

    @Test
    void shouldDeletePayment() {
        Long paymentId = 1L;
        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(reservationRepository.findById(payment.getReservationId().getId())).willReturn(Optional.of(reservation));  // Используем reservationId напрямую

        paymentService.deletePayment(paymentId);

        then(paymentRepository).should().delete(payment);
    }

    @Test
    void shouldThrowExceptionWhenPaymentNotFoundForDelete() {
        Long paymentId = 1L;
        given(paymentRepository.findById(paymentId)).willReturn(Optional.empty());

        Throwable thrown = catchThrowable(() -> paymentService.deletePayment(paymentId));

        assertThat(thrown)
                .isInstanceOf(NoSuchPaymentException.class)
                .hasMessageContaining("Payment not found!");
    }

    @Test
    void shouldReturnAllPayments() {
        List<Payment> payments = List.of(payment);
        List<PaymentDTO> paymentDTOs = List.of(paymentDTO);
        given(paymentRepository.findAll()).willReturn(payments);
        given(paymentMapper.toPaymentDTOList(payments)).willReturn(paymentDTOs);

        List<PaymentDTO> result = paymentService.getAllPayments();

        assertThat(result).isEqualTo(paymentDTOs);
    }
}

package org.example.backend;

import org.example.backend.dtos.AmenityDTO;
import org.example.backend.dtos.ClientDTO;
import org.example.backend.dtos.ReservationDTO;
import org.example.backend.dtos.RoomDTO;
import org.example.backend.enums.RoomType;
import org.example.backend.mappers.AmenityMapper;
import org.example.backend.mappers.ReservationMapper;
import org.example.backend.mappers.RoomMapper;
import org.example.backend.model.Amenity;
import org.example.backend.model.Client;
import org.example.backend.model.Reservation;
import org.example.backend.model.Room;
import org.example.backend.repository.client.ClientRepository;
import org.example.backend.repository.reservation.ReservationRepository;
import org.example.backend.repository.room.AmenityRepository;
import org.example.backend.repository.room.RoomCriteriaRepository;
import org.example.backend.repository.room.RoomRepository;
import org.example.backend.service.client.ClientService;
import org.example.backend.service.reservation.ReservationService;
import org.example.backend.service.room.AmenityService;
import org.example.backend.service.room.RoomAmenityService;
import org.example.backend.service.room.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDate;

public class BaseTestsSetup {

    @Mock
    public ClientRepository clientRepository;

    @Mock
    public RoomRepository roomRepository;

    @Mock
    public ReservationRepository reservationRepository;

    @Mock
    public AmenityRepository amenityRepository;

    @Mock
    public RoomCriteriaRepository roomCriteriaRepository;

    @Mock
    public RoomMapper roomMapper;

    @Mock
    public ReservationMapper reservationMapper;

    @Mock
    public AmenityMapper amenityMapper;

    @InjectMocks
    public ClientService clientService;

    @InjectMocks
    public RoomService roomService;

    @InjectMocks
    public ReservationService reservationService;

    @InjectMocks
    public AmenityService amenityService;

    @InjectMocks
    public RoomAmenityService roomAmenityService;

    public ClientDTO clientDTO;
    public RoomDTO roomDTO;
    public ReservationDTO reservationDTO;
    public AmenityDTO amenityDTOFreeWifi;
    public AmenityDTO amenityDTOBreakfast;

    public Client client;
    public Room room;
    public Reservation reservation;
    public Amenity amenityFreeWifi;
    public Amenity amenityBreakfast;

    @BeforeEach
    void setUp() {
        clientDTO = new ClientDTO();
        clientDTO.setId(1L);
        clientDTO.setFirstName("John");
        clientDTO.setLastName("Doe");
        clientDTO.setEmail("john.doe@example.com");
        clientDTO.setPhone("+1234567890");

        client = new Client();
        client.setId(1L);
        client.setFirstName("John");
        client.setLastName("Doe");
        client.setEmail("john.doe@example.com");
        client.setPhone("+1234567890");
        client.setPassword("password123");

        roomDTO = new RoomDTO();
        roomDTO.setId(1L);
        roomDTO.setRoomNumber("101");
        roomDTO.setPrice(150.00);
        roomDTO.setAvailable(true);
        roomDTO.setType(RoomType.SINGLE);
        roomDTO.setDescription("A comfortable room.");

        room = new Room();
        room.setId(1L);
        room.setRoomNumber("101");
        room.setPrice(150.00);
        room.setAvailable(true);
        room.setType(RoomType.SINGLE);
        room.setDescription("A comfortable room.");

        reservationDTO = new ReservationDTO();
        reservationDTO.setId(1L);
        reservationDTO.setCheckInDate(LocalDate.of(2026, 12, 1));
        reservationDTO.setCheckOutDate(LocalDate.of(2026, 12, 7));
        reservationDTO.setNumberOfGuests(1L);
        reservationDTO.setTotalPrice(900.00);
        reservationDTO.setStatus("UNCONFIRMED");
        reservationDTO.setClientId(1L);
        reservationDTO.setRoomId(1L);

        reservation = new Reservation();
        reservation.setId(1L);
        reservation.setCheckInDate(LocalDate.of(2026, 12, 1));
        reservation.setCheckOutDate(LocalDate.of(2026, 12, 7));
        reservation.setNumberOfGuests(1L);
        reservation.setTotalPrice(900.00);
        reservation.setStatus("UNCONFIRMED");
        reservation.setClientId(client);
        reservation.setRoomId(room);

        amenityDTOFreeWifi = new AmenityDTO();
        amenityDTOFreeWifi.setId(1L);
        amenityDTOFreeWifi.setName("Free WiFi");
        amenityDTOFreeWifi.setDescription("High-speed wireless internet access");
        amenityDTOFreeWifi.setAdditionalCost(0.00);

        amenityFreeWifi = new Amenity();
        amenityFreeWifi.setId(1L);
        amenityFreeWifi.setName("Free WiFi");
        amenityFreeWifi.setDescription("High-speed wireless internet access");
        amenityFreeWifi.setAdditionalCost(0.00);

        amenityDTOBreakfast = new AmenityDTO();
        amenityDTOBreakfast.setId(2L);
        amenityDTOBreakfast.setName("Breakfast");
        amenityDTOBreakfast.setDescription("A delicious continental breakfast");
        amenityDTOBreakfast.setAdditionalCost(20.00);

        amenityBreakfast = new Amenity();
        amenityBreakfast.setId(2L);
        amenityBreakfast.setName("Breakfast");
        amenityBreakfast.setDescription("A delicious continental breakfast");
        amenityBreakfast.setAdditionalCost(20.00);
    }

}

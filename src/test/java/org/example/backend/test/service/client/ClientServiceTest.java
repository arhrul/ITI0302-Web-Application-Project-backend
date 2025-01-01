package org.example.backend.test.service.client;

import org.example.backend.dtos.ClientDTO;
import org.example.backend.exception.exceptions.ClientEmailAlreadyExistsException;
import org.example.backend.exception.exceptions.NoSuchClientException;
import org.example.backend.mappers.ClientMapper;
import org.example.backend.model.Client;
import org.example.backend.repository.client.ClientRepository;
import org.example.backend.repository.reservation.ReservationRepository;  // Import ReservationRepository
import org.example.backend.service.client.ClientService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository clientRepository;

    @Mock
    private ClientMapper clientMapper;

    @Mock
    private ReservationRepository reservationRepository;

    @InjectMocks
    private ClientService clientService;

    @Test
    void shouldCreateClientWhenEmailIsUnique() {
        // given
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setFirstName("John");
        clientDTO.setLastName("Doe");
        clientDTO.setEmail("john.doe@example.com");
        clientDTO.setPhone("+1234567890");

        Client clientEntity = new Client();
        clientEntity.setFirstName("John");
        clientEntity.setLastName("Doe");
        clientEntity.setEmail("john.doe@example.com");
        clientEntity.setPhone("+1234567890");
        clientEntity.setPassword("password123");

        Client savedClient = new Client();
        savedClient.setId(1L);
        savedClient.setFirstName("John");
        savedClient.setLastName("Doe");
        savedClient.setEmail("john.doe@example.com");
        savedClient.setPhone("+1234567890");
        savedClient.setPassword("password123");

        given(clientRepository.existsByEmail(clientDTO.getEmail())).willReturn(false);
        given(clientMapper.toClient(clientDTO)).willReturn(clientEntity);
        given(clientRepository.save(clientEntity)).willReturn(savedClient);
        given(clientMapper.toClientDto(savedClient)).willReturn(clientDTO);

        // when
        ClientDTO result = clientService.createClient(clientDTO);

        // then
        assertThat(result).isEqualTo(clientDTO);
        then(clientRepository).should().save(clientEntity);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        // given
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmail("john.doe@example.com");

        given(clientRepository.existsByEmail(clientDTO.getEmail())).willReturn(true);

        // when
        Throwable thrown = catchThrowable(() -> clientService.createClient(clientDTO));

        // then
        assertThat(thrown)
                .isInstanceOf(ClientEmailAlreadyExistsException.class)
                .hasMessageContaining("Account with email john.doe@example.com already exists.");
    }

    @Test
    void shouldReturnClientWhenEmailExists() {
        // given
        String email = "john.doe@example.com";
        Client clientEntity = new Client();
        clientEntity.setEmail(email);
        clientEntity.setFirstName("John");
        clientEntity.setLastName("Doe");

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmail(email);
        clientDTO.setFirstName("John");
        clientDTO.setLastName("Doe");

        given(clientRepository.findByEmail(email)).willReturn(clientEntity);
        given(clientMapper.toClientDto(clientEntity)).willReturn(clientDTO);

        // when
        ClientDTO result = clientService.getClientByEmail(email);

        // then
        assertThat(result).isEqualTo(clientDTO);
    }

    @Test
    void shouldUpdateClientWhenEmailIsUnique() {
        // given
        Long clientId = 1L;
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setFirstName("Jane");
        clientDTO.setLastName("Doe");
        clientDTO.setEmail("jane.doe@example.com");
        clientDTO.setPhone("+1234567890");

        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setEmail("john.doe@example.com");

        Client updatedClient = new Client();
        updatedClient.setId(clientId);
        updatedClient.setEmail(clientDTO.getEmail());
        updatedClient.setFirstName(clientDTO.getFirstName());
        updatedClient.setLastName(clientDTO.getLastName());
        updatedClient.setPhone(clientDTO.getPhone());

        given(clientRepository.findById(clientId)).willReturn(Optional.of(existingClient));
        given(clientRepository.existsByEmail(clientDTO.getEmail())).willReturn(false);
        given(clientRepository.save(existingClient)).willReturn(updatedClient);
        given(clientMapper.toClientDto(updatedClient)).willReturn(clientDTO);

        // when
        ClientDTO result = clientService.updateClient(clientId, clientDTO);

        // then
        assertThat(result).isEqualTo(clientDTO);
        then(clientRepository).should().save(existingClient);
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExistsDuringUpdate() {
        // given
        Long clientId = 1L;
        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setEmail("existing.email@example.com");

        Client existingClient = new Client();
        existingClient.setId(clientId);
        existingClient.setEmail("john.doe@example.com");

        given(clientRepository.findById(clientId)).willReturn(Optional.of(existingClient));
        given(clientRepository.existsByEmail(clientDTO.getEmail())).willReturn(true);

        // when
        Throwable thrown = catchThrowable(() -> clientService.updateClient(clientId, clientDTO));

        // then
        assertThat(thrown)
                .isInstanceOf(ClientEmailAlreadyExistsException.class)
                .hasMessageContaining("Account with email existing.email@example.com already exists.");
    }

    @Test
    void shouldDeleteClientById() {
        // given
        Long clientId = 1L;
        Client client = new Client();
        client.setId(clientId);

        given(clientRepository.findById(clientId)).willReturn(Optional.of(client));

        // when
        clientService.deleteClient(clientId);

        // then
        then(clientRepository).should().delete(client);
    }

    @Test
    void shouldReturnAllClientsDTO() {
        // given
        Client client1 = new Client();
        client1.setFirstName("John");
        client1.setLastName("Doe");
        client1.setEmail("john.doe@example.com");

        Client client2 = new Client();
        client2.setFirstName("Jane");
        client2.setLastName("Doe");
        client2.setEmail("jane.doe@example.com");

        ClientDTO clientDTO1 = new ClientDTO();
        clientDTO1.setFirstName("John");
        clientDTO1.setLastName("Doe");
        clientDTO1.setEmail("john.doe@example.com");

        ClientDTO clientDTO2 = new ClientDTO();
        clientDTO2.setFirstName("Jane");
        clientDTO2.setLastName("Doe");
        clientDTO2.setEmail("jane.doe@example.com");

        List<Client> clients = List.of(client1, client2);
        List<ClientDTO> clientDTOs = List.of(clientDTO1, clientDTO2);

        given(clientRepository.findAll()).willReturn(clients);
        given(clientMapper.toClientDTOList(clients)).willReturn(clientDTOs);

        // when
        List<ClientDTO> result = clientService.getAllClientsDTO();

        // then
        assertThat(result).isEqualTo(clientDTOs);
    }

    @Test
    void shouldReturnClientDTOWhenClientExists() {
        // given
        Long clientId = 1L;
        Client clientEntity = new Client();
        clientEntity.setId(clientId);
        clientEntity.setFirstName("John");
        clientEntity.setLastName("Doe");
        clientEntity.setEmail("john.doe@example.com");

        ClientDTO clientDTO = new ClientDTO();
        clientDTO.setId(clientId);
        clientDTO.setFirstName("John");
        clientDTO.setLastName("Doe");
        clientDTO.setEmail("john.doe@example.com");

        given(clientRepository.findById(clientId)).willReturn(Optional.of(clientEntity));
        given(clientMapper.toClientDto(clientEntity)).willReturn(clientDTO);

        // when
        ClientDTO result = clientService.getClient(clientId);

        // then
        assertThat(result).isEqualTo(clientDTO);
        then(clientRepository).should().findById(clientId);
        then(clientMapper).should().toClientDto(clientEntity);
    }

    @Test
    void shouldThrowExceptionWhenClientNotFound() {
        // given
        Long clientId = 1L;

        given(clientRepository.findById(clientId)).willReturn(Optional.empty());

        // when
        Throwable thrown = catchThrowable(() -> clientService.getClient(clientId));

        // then
        assertThat(thrown)
                .isInstanceOf(NoSuchClientException.class)
                .hasMessageContaining("Client not found!");

        then(clientRepository).should().findById(clientId);
        then(clientMapper).shouldHaveNoInteractions();
    }

}

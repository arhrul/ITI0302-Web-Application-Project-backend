package org.example.backend.service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dtos.ClientDTO;
import org.example.backend.exception.exceptions.ClientEmailAlreadyExistsException;
import org.example.backend.exception.exceptions.NoSuchClientException;
import org.example.backend.exception.exceptions.RoomDeletionException;
import org.example.backend.mappers.ClientMapper;
import org.example.backend.model.Client;
import org.example.backend.model.Reservation;
import org.example.backend.repository.client.ClientRepository;
import org.example.backend.repository.reservation.ReservationRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ReservationRepository reservationRepository;

    public ClientDTO createClient(ClientDTO clientDTO) {
        log.info("Creating a new client with email: {}", clientDTO.getEmail());

        validateClientEmail(clientDTO.getEmail(), null);
        Client client = clientMapper.toClient(clientDTO);

        Client savedClient = clientRepository.save(client);
        log.info("Client with email: {} created successfully", clientDTO.getEmail());
        return clientMapper.toClientDto(savedClient);
    }

    public ClientDTO getClientByEmail(String email) {
        return clientMapper.toClientDto(clientRepository.findByEmail(email));
    }

    public Client getClientById(Long id) {
        log.info("Fetching client by id: {}", id);
        return clientRepository.findById(id)
                .orElseThrow(() -> new NoSuchClientException("Client not found!"));
    }

    private void validateClientEmail(String email, Long clientId) {

        if (clientRepository.existsByEmail(email)
                && (clientId == null || !clientRepository.findById(clientId)
                .orElseThrow(() -> new ClientEmailAlreadyExistsException("Invalid client ID"))
                .getEmail().equals(email))) {
                throw new ClientEmailAlreadyExistsException("Account with email " + email + " already exists.");
            }

    }

    public ClientDTO getClient(Long id) {
        Client client = getClientById(id);
        return clientMapper.toClientDto(client);
    }

    public List<Client> getAllClientsEntity() {
        return clientRepository.findAll();
    }

    public List<ClientDTO> getAllClientsDTO() {
        return clientMapper.toClientDTOList(getAllClientsEntity());
    }

    public ClientDTO updateClient(Long id, ClientDTO clientDTO) {
        log.info("Updating client with id: {}", id);

        Client client = getClientById(id);
        validateClientEmail(clientDTO.getEmail(), id);

        client.setFirstName(clientDTO.getFirstName());
        client.setLastName(clientDTO.getLastName());
        client.setEmail(clientDTO.getEmail());
        client.setPhone(clientDTO.getPhone());
        Client updatedClient = clientRepository.save(client);
        log.info("Client with id: {} updated successfully", id);
        return clientMapper.toClientDto(updatedClient);
    }

    public void deleteClient(Long id) {
        log.info("Attempting to delete client with ID: {}", id);
        Client client = getClientById(id);

        if (hasReservations(client)) {
            log.warn("Client with ID: {} has active reservations. Cannot delete.", id);
            throw new RoomDeletionException("Client with ID: " + id + " has active reservations and cannot be deleted.");
        }

        clientRepository.delete(client);
        log.info("Client with id: {} deleted successfully", id);
    }

    private boolean hasReservations(Client client) {
        log.info("Checking for active reservations for room ID: {}", client.getId());
        List<Reservation> reservations = reservationRepository.findByClientId(client);
        return !reservations.isEmpty();
    }
}

package org.example.backend.service.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dtos.ClientDTO;
import org.example.backend.dtos.auth.NewPasswordRequestDTO;
import org.example.backend.exception.exceptions.ClientEmailAlreadyExistsException;
import org.example.backend.exception.exceptions.NewPasswordMustBeDifferentException;
import org.example.backend.exception.exceptions.NoSuchClientException;
import org.example.backend.exception.exceptions.RoomDeletionException;
import org.example.backend.mappers.ClientMapper;
import org.example.backend.model.Client;
import org.example.backend.model.Reservation;
import org.example.backend.repository.client.ClientRepository;
import org.example.backend.repository.reservation.ReservationRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientService {

    private final ClientRepository clientRepository;
    private final ClientMapper clientMapper;
    private final ReservationRepository reservationRepository;

    private final PasswordEncoder passwordEncoder;

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

    public NewPasswordRequestDTO updateClientPassword(String clientEmail, NewPasswordRequestDTO newPasswordRequestDTO) {
        //validate client old passes
        Client client = clientRepository.findByEmail(clientEmail);

        log.info("Validating new password for client with email: {}", clientEmail);
        validateClientOldPasswords(clientEmail, newPasswordRequestDTO);

        // get new hashed pass
        String newPassword = newPasswordRequestDTO.getNewPassword();
        String hashedPassword = passwordEncoder.encode(newPassword);

        //set new pass and save client
        client.setPassword(hashedPassword);
        clientRepository.save(client);

        log.info("Client password with id: {} updated successfully", clientEmail);
        return newPasswordRequestDTO;
    }

    private void validateClientOldPasswords(String clientEmail, NewPasswordRequestDTO newPasswordRequestDTO) {
        String newPassword = newPasswordRequestDTO.getNewPassword();

        // client pass must be included to the new pass
        Client client = clientRepository.findByEmail(clientEmail);
        String clientPassword = client.getPassword();

        if (clientPassword.equals(newPassword)) {
            throw new NewPasswordMustBeDifferentException("New password is the as the previous ones.");
        }
        if (client.getOldPassword2() != null) {
            String clientOldPassword2 = client.getOldPassword2();
            if (clientOldPassword2.equals(newPassword)) {
                throw new NewPasswordMustBeDifferentException("New password is the as the previous ones.");
            }
            client.setPassword(newPassword);
            client.setOldPassword1(clientOldPassword2);
            client.setOldPassword2(newPassword);
        }
        if (client.getOldPassword2() == null) {
            String clientOldPassword1 = client.getPassword();
            client.setOldPassword1(clientOldPassword1);
            client.setOldPassword2(newPasswordRequestDTO.getNewPassword());
        }
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

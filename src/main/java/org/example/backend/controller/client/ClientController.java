package org.example.backend.controller.client;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.dtos.ClientDTO;
import org.example.backend.service.client.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
@Tag(name = "Client", description = "Operations related to clients")
public class ClientController {

    private final ClientService clientService;

    @GetMapping
    @Operation(summary = "Get list of clients", description = "Retrieve a list of all clients")
    @ApiResponse(responseCode = "200", description = "List of clients")
    public ResponseEntity<List<ClientDTO>> getClients() {
        log.info("Fetching all clients");
        List<ClientDTO> clients = clientService.getAllClientsDTO();
        log.info("Found {} clients", clients.size());
        return ResponseEntity.ok(clients);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a client by ID", description = "Retrieve a client by their unique ID")
    @ApiResponse(responseCode = "200", description = "Client found")
    @ApiResponse(responseCode = "404", description = "Client not found")
    public ResponseEntity<ClientDTO> getClient(@PathVariable @Parameter(description = "Client ID") Long id) {
        log.info("Fetching client with ID: {}", id);
        ClientDTO client = clientService.getClient(id);
        if (client != null) {
            log.info("Client found: {}", client);
        } else {
            log.warn("Client with ID {} not found", id);
        }
        return ResponseEntity.ok(client);    }

    @PutMapping("/{id}")
    @Operation(summary = "Update client details", description = "Update the details of a specific client")
    @ApiResponse(responseCode = "200", description = "Client updated successfully")
    @ApiResponse(responseCode = "400", description = "Invalid client data")
    @ApiResponse(responseCode = "404", description = "Client not found")
    public ResponseEntity<ClientDTO> updateClient(
            @PathVariable @Parameter(description = "Client ID") Long id,
            @RequestBody @Valid @Parameter(description = "Updated client details") ClientDTO updatedClientDTO) {
        log.info("Updating client with ID: {}. New data: {}", id, updatedClientDTO);
        ClientDTO updatedClient = clientService.updateClient(id, updatedClientDTO);
        log.info("Client updated successfully: {}", updatedClient);
        return ResponseEntity.ok(updatedClient);    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a client", description = "Delete a client by their unique ID")
    @ApiResponse(responseCode = "204", description = "Client deleted successfully")
    @ApiResponse(responseCode = "404", description = "Client not found")
    public ResponseEntity<Void> deleteClient(@PathVariable @Parameter(description = "Client ID") Long id) {
        log.info("Deleting client with ID: {}", id);
        clientService.deleteClient(id);
        log.info("Client with ID {} deleted successfully", id);
        return ResponseEntity.noContent().build();
    }
}

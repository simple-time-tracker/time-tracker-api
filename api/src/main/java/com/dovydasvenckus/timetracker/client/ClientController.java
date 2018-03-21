package com.dovydasvenckus.timetracker.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Component
@Path("/clients")
public class ClientController {

    private final ClientRepository clientRepository;

    private final BCryptPasswordEncoder bcryptPasswordEncoder;

    @Autowired
    public ClientController(ClientRepository clientRepository, BCryptPasswordEncoder bcryptPasswordEncoder) {
        this.clientRepository = clientRepository;
        this.bcryptPasswordEncoder = bcryptPasswordEncoder;
    }

    @POST
    @Path("/register")
    public void register(@Valid @RequestBody ClientRegisterDTO clientRegister) {
        Client client = new Client();
        client.setUsername(clientRegister.getUsername());
        client.setPassword(bcryptPasswordEncoder.encode(clientRegister.getPassword()));

        clientRepository.save(client);
    }
}

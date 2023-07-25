package com.data.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.data.organization.dto.FormRequest;
import com.data.organization.dto.RegistrationRequest;
import com.data.organization.service.FormHandling;
import com.data.organization.service.RegistrationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/org", produces = "application/json")
@CrossOrigin(origins = "*")
@Slf4j
public class OrgUserController {

    private RegistrationService registrationService;
    private FormHandling formhandling;

    private final WebClient webClient;

    @Autowired
    public OrgUserController(WebClient.Builder webClientBuilder) {
        // Set the base URL of the external API
        this.webClient = webClientBuilder.baseUrl("/db").build();
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegistrationRequest request) {
        try {
            registrationService.register(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/form")
    public ResponseEntity<?> getForm(@RequestBody FormRequest fRequest) {
        try {
            // storing the questions in the database
            String response = webClient.post()
                    .uri("/form/save")
                    .bodyValue(fRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
            return ResponseEntity.ok().body(formhandling.createform(fRequest));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

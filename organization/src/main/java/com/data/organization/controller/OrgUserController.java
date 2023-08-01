package com.data.organization.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.organization.dto.FormRequest;
import com.data.organization.dto.RegistrationRequest;
import com.data.organization.service.FormService;
import com.data.organization.service.RegistrationService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/org", produces = "application/json")
@CrossOrigin(origins = "*")
@Slf4j
public class OrgUserController {

    @Autowired
    private RegistrationService registrationService;


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
}

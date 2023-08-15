package com.data.database.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.database.dto.FormDataRequest;
import com.data.database.service.FormDataService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/db")
@CrossOrigin("*")
@Slf4j
public class FormDataController {

    @Autowired
    private FormDataService fService;

    @PostMapping("/form/save")
    public ResponseEntity<?> storeformDetails(@RequestBody FormDataRequest fDataRequest) {
        try {
            System.out.println(fDataRequest.toString());
            // fService.saveFormData(fDataRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();

        }
    }

}

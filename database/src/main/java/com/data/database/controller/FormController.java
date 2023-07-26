package com.data.database.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.database.dto.FormDataRequest;
import com.data.database.dto.FormMetaDataRequest;
import com.data.database.service.FormDataService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/db")
@Slf4j
public class FormController {

    @Autowired
    private FormDataService fService;

    @PostMapping("/form/save")
    public ResponseEntity<?> storeformDetails(@RequestBody FormMetaDataRequest fDataRequest) {
        try {
            fService.saveFormMetaData(fDataRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();

        }
    }

    @PostMapping("/data/save")
    public ResponseEntity<?> storeFormData(@RequestBody FormDataRequest fDataRequest) {
        try {
            fService.saveAnswer(fDataRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();

        }
    }

    @GetMapping("/data/all")
    public ResponseEntity<?> storeFormData(@RequestHeader String name) {
        try {
            return ResponseEntity.ok().body(fService.getAllAnswersByForm(name));
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();

        }
    }

}

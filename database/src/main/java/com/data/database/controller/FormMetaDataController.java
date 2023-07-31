package com.data.database.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.data.database.dto.FormMetaDataRequest;
import com.data.database.service.FormDataService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/db")
@Slf4j
public class FormMetaDataController {

    @Autowired
    private FormDataService fService;

    @PostMapping("/form/meta/save")
    public ResponseEntity<?> storeformDetails(@RequestBody FormMetaDataRequest fDataRequest) {
        try {
            fService.saveFormMetaData(fDataRequest);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseEntity.badRequest().build();

        }
    }

}

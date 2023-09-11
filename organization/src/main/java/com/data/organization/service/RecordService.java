package com.data.organization.service;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.AttributeNotFoundException;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.data.organization.dto.FormDataRequest;
import com.data.organization.model.DataRecord;
import com.data.organization.model.MetaData;
import com.data.organization.repository.DataRecordRepo;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

@Service
public class RecordService {

    @Autowired
    private DataRecordRepo dataRecordRepo;

    public List<DataRecord> parseCsvFile(InputStream csvFile) throws IOException {
        List<DataRecord> csvDataList = new ArrayList<>();

        try (CSVReader csvReader = new CSVReader(new InputStreamReader(csvFile))) {
            // Read the CSV header to determine column names (fields)
            String[] header = csvReader.readNext();
            if (header == null) {
                throw new IOException("CSV file is empty.");
            }

            String[] row;
            while ((row = csvReader.readNext()) != null) {
                DataRecord dataRecord = new DataRecord();
                Map<String, Object> data = new HashMap<>();

                // Map each column to the corresponding field in CsvDataModel
                for (int i = 0; i < header.length && i < row.length; i++) {
                    data.put(header[i], row[i]);
                }

                dataRecord.setData(data);
                csvDataList.add(dataRecord);
            }
        } catch (CsvValidationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return csvDataList;
    }

    public void storeFileData(MultipartFile file) {
        try {
            List<DataRecord> data = parseCsvFile(file.getInputStream());
            System.out.println(data);
            dataRecordRepo.saveAll(data);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    // @RabbitListener(queues = MessagingConfig.QUEUE)
    // public void consumeMessageFromQueue(FormDataRequest formDataRequest) {
    //     log.info(formDataRequest.toString());
    //     try {
    //         if (formDataRequest.getUrl().equals(null)) {
    //             throw new AttributeNotFoundException("Url is not found");
    //         }
    //         MetaData formByLink = fUtilService.getFormMetaDataByLink(formDataRequest.getUrl());
    //         log.info("form found!!");
    //         saveAnswer(formDataRequest.getQa(), formByLink.getFId());
    //     } catch (Exception e) {
    //         log.error(e.getMessage());
    //     }

    // }
}

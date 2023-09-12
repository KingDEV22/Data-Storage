package com.data.organization.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.data.organization.model.DataRecord;
import com.data.organization.model.MetaData;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.DataRecordRepo;
import com.data.organization.repository.MetaDataRepo;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

@Service
public class RecordService {

    @Autowired
    private DataRecordRepo dataRecordRepo;

    @Autowired
    private MetaDataRepo mDataRepo;

    @Autowired
    private FormUtilService formUtilService;

    public List<DataRecord> parseCsvFile(InputStream csvFile, String metaDataId) throws IOException {
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
                dataRecord.setMetaDataId(metaDataId);
                csvDataList.add(dataRecord);
            }
        } catch (CsvValidationException e) {
            e.printStackTrace();
        }

        return csvDataList;
    }

    public void storeFileData(MultipartFile file, String fileName) {
        OrgUser orgUser = formUtilService.getOrgUser(formUtilService.getContextEmail());
        MetaData fileData = new MetaData();
        fileData.setName(fileName);
        fileData.setType("File");
        fileData.setOrgId(orgUser.getOrgId());
        mDataRepo.save(fileData);
        try {
            List<DataRecord> data = parseCsvFile(file.getInputStream(), fileData.getMetaDataId());
            dataRecordRepo.saveAll(data);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private List<DataRecord> getDataformDB(String name, String type) {
        MetaData fileOrFormDataByName = formUtilService.getMetaData(name, type);
        List<DataRecord> dataRecord = dataRecordRepo.findAllByMetaDataId(fileOrFormDataByName.getMetaDataId());
        return dataRecord;
    }

    public InputStreamResource exportDataToCsv(List<Map<String, Object>> data) throws IOException {
        // Create a ByteArrayOutputStream to hold the CSV data
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try (CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(outputStream))) {
            // Write the CSV header
            String[] header = data.get(0).keySet().toArray(new String[0]);
            csvWriter.writeNext(header);

            // Write data rows
            for (Map<String, Object> row : data) {
                String[] rowValues = new String[header.length];
                int i = 0;
                for (String headerColumn : header) {
                    rowValues[i] = row.get(headerColumn).toString();
                    i++;
                }
                csvWriter.writeNext(rowValues);
            }
        }

        // Create an InputStreamResource from the ByteArrayOutputStream
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        return new InputStreamResource(inputStream);
    }

    public InputStreamResource getDataInCSVFormat(String name, String type) {
        List<Map<String, Object>> dataList = getFileData(name,type);
        try {
            return exportDataToCsv(dataList);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getFileData(String fileName, String type) {
        List<DataRecord> fileData = getDataformDB(fileName, "File");
        List<Map<String, Object>> dataList = fileData.parallelStream()
                .map(DataRecord::getData)
                .collect(Collectors.toList());
        return dataList;
    }

    // @RabbitListener(queues = MessagingConfig.QUEUE)
    // public void consumeMessageFromQueue(FormDataRequest formDataRequest) {
    // log.info(formDataRequest.toString());
    // try {
    // if (formDataRequest.getUrl().equals(null)) {
    // throw new AttributeNotFoundException("Url is not found");
    // }
    // MetaData formByLink =
    // fUtilService.getFormMetaDataByLink(formDataRequest.getUrl());
    // log.info("form found!!");
    // saveAnswer(formDataRequest.getQa(), formByLink.getFId());
    // } catch (Exception e) {
    // log.error(e.getMessage());
    // }

    // }
}

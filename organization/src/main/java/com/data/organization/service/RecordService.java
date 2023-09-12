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

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.data.organization.configration.rabbitmq.MessagingConfig;
import com.data.organization.dto.FormDataRequest;
import com.data.organization.dto.FormMetaDataResponse;
import com.data.organization.dto.QuestionDTO;
import com.data.organization.model.DataRecord;
import com.data.organization.model.DataType;
import com.data.organization.model.MetaData;
import com.data.organization.model.OrgUser;
import com.data.organization.repository.DataRecordRepo;
import com.data.organization.repository.MetaDataRepo;
import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@AllArgsConstructor
public class RecordService {

    private DataRecordRepo dataRecordRepo;
    private MetaDataRepo mDataRepo;
    private FormUtilService formUtilService;
    private RabbitTemplate rabbitTemplate;
    private QuestionService qService;

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

    @Transactional
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
        List<Map<String, Object>> dataList = getFileData(name, type);
        try {
            return exportDataToCsv(dataList);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, Object>> getFileData(String fileName, String type) {
        List<DataRecord> fileData = getDataformDB(fileName, DataType.FILE.toString());
        List<Map<String, Object>> dataList = fileData.parallelStream()
                .map(DataRecord::getData)
                .collect(Collectors.toList());
        return dataList;
    }

    public List<FormMetaDataResponse> getAllFileMetaDataByOrg() {
        List<MetaData> formByOrg = formUtilService.getFormsOrFilesByOrg(DataType.FILE.toString());
        return formByOrg.parallelStream()
                .map(form -> new FormMetaDataResponse("", form.getName(), form.getCreateDate()))
                .collect(Collectors.toList());
    }

    private FormDataRequest maptFormDataRequest(List<QuestionDTO> quwList) {
        List<Map<String, Object>> questionList = new ArrayList<>();
        String[] keys = { "questionId", "qname", "qtype", "qlabel" };
       
        for (QuestionDTO question : quwList) {
             int i = 0;
            Map<String, Object> questionMap = new HashMap<>();
            questionMap.put(keys[i++], question.getQuestionId());
            questionMap.put(keys[i++], question.getQname());
            questionMap.put(keys[i++], question.getQtype());
            questionMap.put(keys[i], question.getQlabel());
            questionList.add(questionMap);
        }
        FormDataRequest formDataRequest = new FormDataRequest();
        formDataRequest.setData(questionList);
        return formDataRequest;
    }

    // @RabbitListener(queues = MessagingConfig.QUEUE2)
    public void sendMessageToQueue(String link) {
        MetaData formData = formUtilService.getFormMetaDataByLink(link);
        try {
            List<QuestionDTO> questionList = qService.getQuestionByForm(formData.getMetaDataId());
            log.info(questionList.toString());
            FormDataRequest formDataRequest = maptFormDataRequest(questionList);
            formDataRequest.setName(formData.getName());
            // log.info(questionListJson);
            rabbitTemplate.convertAndSend(MessagingConfig.EXCHANGE, MessagingConfig.ROUTING_KEY, formDataRequest);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }


    private void saveAnswerData(Map<String,Object> data, String url){
        MetaData formData = formUtilService.getFormMetaDataByLink(url);
        try {
            DataRecord answersRecord = new DataRecord();
            answersRecord.setData(data);
            answersRecord.setMetaDataId(formData.getMetaDataId());
            dataRecordRepo.save(answersRecord);
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @RabbitListener(queues = MessagingConfig.QUEUE1)
    public void consumeMessageFromQueue(FormDataRequest formDataRequest) {
        log.info(formDataRequest.toString());
        try {
            switch (formDataRequest.getMessage()) {
                case "QUESTIONS":
                    sendMessageToQueue(formDataRequest.getUrl());
                    break;
                case "SUBMIT":
                    saveAnswerData(formDataRequest.getFormData(), formDataRequest.getUrl());
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}

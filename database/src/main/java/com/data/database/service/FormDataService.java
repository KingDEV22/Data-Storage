package com.data.database.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.data.database.dto.FormDataRequest;
import com.data.database.dto.FormMetaDataRequest;
import com.data.database.dto.QuestionAnswerRequest;
import com.data.database.dto.QuestionsRequest;
import com.data.database.model.Answer;
import com.data.database.model.Form;
import com.data.database.model.Question;
import com.data.database.repository.AnswerRepo;
import com.data.database.repository.FormRepo;
import com.data.database.repository.QuestionRepo;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class FormDataService {

    private FormRepo formRepo;
    private AnswerRepo answerRepo;
    private QuestionRepo questionRepo;

    public void saveFormMetaData(FormMetaDataRequest fDataRequest) {
        Optional<Form> checkForm = formRepo.findByLink(fDataRequest.getLink());
        if (checkForm.isPresent()) {
            throw new EntityExistsException("Form is already present");
        }
        Form formdata = new Form();
        BeanUtils.copyProperties(fDataRequest, formdata);
        formRepo.save(formdata);
        saveQuestions(fDataRequest.getQuestions(), fDataRequest.getLink());
    }

    public List<Form> getFormsByOrg(String orgId) {
        List<Form> allforms = formRepo.findAllByOrgId(orgId);
        if (allforms.isEmpty()) {
            throw new EntityNotFoundException("No forms available for the org id");
        }

        return allforms;
    }

    public void saveQuestions(List<QuestionsRequest> questions, String link) {
        Optional<Form> checkForm = formRepo.findByLink(link);
        if (checkForm.isEmpty()) {
            throw new EntityNotFoundException("Form is not present");
        }
        for (QuestionsRequest question : questions) {
            Question questionData = new Question();
            questionData.setQuestion(question.getQuestion());
            questionData.setType(question.getType());
            questionData.setQ_id(checkForm.get().getFId());
            questionRepo.save(questionData);
        }

    }

    public void saveAnswer(FormDataRequest fDataRequest) {
        Optional<Form> checkForm = formRepo.findByName(fDataRequest.getFormName());
        if (checkForm.isEmpty()) {
            throw new EntityNotFoundException("Form is not present");
        }
        List<Question> questions = questionRepo.findAllByFId(checkForm.get().getFId());
        if (questions.isEmpty()) {
            throw new EntityNotFoundException("Questions not present");
        }
        Map<String, String> qa = questions.stream().collect(Collectors.toMap(Question::getQuestion, Question::getQ_id));

        for (QuestionAnswerRequest qAnswerRequest : fDataRequest.getQa()) {
            Answer answer = new Answer();
            answer.setQ_id(qa.get(qAnswerRequest.getQuestion()));
            answer.setAnswer(qAnswerRequest.getAnswer());
            answer.setF_id(checkForm.get().getFId());
            answerRepo.save(answer);
        }

    }

    public List<QuestionAnswerRequest> getAllAnswersByForm(String name) {
        Optional<Form> checkForm = formRepo.findByName(name);
        if (checkForm.isEmpty()) {
            throw new EntityNotFoundException("Form is not present");
        }
        List<Question> questions = questionRepo.findAllByFId(checkForm.get().getFId());
        if (questions.isEmpty()) {
            throw new EntityNotFoundException("Questions not present");
        }
        Map<String, String> qa = questions.stream().collect(Collectors.toMap(Question::getQ_id, Question::getQuestion));
        Iterable<Answer> answers = answerRepo.findAll();
        List<QuestionAnswerRequest> formdata = new ArrayList<>();
        for (Answer answer : answers) {
            QuestionAnswerRequest qaData = new QuestionAnswerRequest();
            qaData.setAnswer(answer.getAnswer());
            qaData.setQuestion(qa.get(answer.getQ_id()));
            formdata.add(qaData);
        }
        return formdata;

    }

}

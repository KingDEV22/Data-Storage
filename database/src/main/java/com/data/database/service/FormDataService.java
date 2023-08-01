package com.data.database.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;

import com.data.database.dto.FormDataRequest;
import com.data.database.dto.QuestionAnswerRequest;
import com.data.database.model.Answer;
import com.data.database.model.Form;
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

    public void saveFormData(FormDataRequest fDataRequest) {
        String url = fDataRequest.getUrl().replace("file:///", "");
        System.out.println(url);
        Optional<Form> formMetaData = formRepo.findByLink(url);
        if (formMetaData.isEmpty()) {
            throw new EntityNotFoundException("No form found. MetaData is corrupted!!");
        }
        saveAnswer(fDataRequest.getQa(), formMetaData.get().getFId());
    }

    private void saveAnswer(List<QuestionAnswerRequest> answersRequest, String fId) {
        List<Answer> answersData = answersRequest.stream()
                .map(answer -> new Answer(answer.getName(), answer.getValue(), fId))
                .collect(Collectors.toList());
        answerRepo.saveAll(answersData);

    }

    // public List<QuestionAnswerRequest> getAllAnswersByForm(String name) {
    //     Optional<Form> checkForm = formRepo.findByName(name);
    //     if (checkForm.isEmpty()) {
    //         throw new EntityNotFoundException("Form is not present");
    //     }
    //     List<Question> questions = questionRepo.findAllByFId(checkForm.get().getFId());
    //     if (questions.isEmpty()) {
    //         throw new EntityNotFoundException("Questions not present");
    //     }
    //     Map<String, String> qa = questions.stream().collect(Collectors.toMap(Question::getQ_id, Question::getQuestion));
    //     Iterable<Answer> answers = answerRepo.findAll();
    //     List<QuestionAnswerRequest> formdata = new ArrayList<>();
    //     for (Answer answer : answers) {
    //         QuestionAnswerRequest qaData = new QuestionAnswerRequest();
    //         qaData.setAnswer(answer.getAnswer());
    //         qaData.setQuestion(qa.get(answer.getQ_id()));
    //         formdata.add(qaData);
    //     }
    //     return formdata;

    // }

}

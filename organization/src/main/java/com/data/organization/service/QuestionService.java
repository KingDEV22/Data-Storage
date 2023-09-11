package com.data.organization.service;

import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.data.organization.dto.QuestionDTO;
import com.data.organization.exception.FormDataException;
import com.data.organization.model.Question;
import com.data.organization.repository.QuestionRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuestionService {

    @Autowired
    private QuestionRepo qRepo;

    @Transactional
    public void saveQuestion(List<QuestionDTO> questions, String mId) {
        List<Question> questionData = questions.parallelStream()
                .map(question -> new Question(question.getQname(), question.getQlabel(), question.getQtype(), mId))
                .collect(Collectors.toList());
        log.info("Questions object created");
        qRepo.saveAll(questionData);
        log.info("questions saved");
    }

    @Transactional
    public void updateQuestions(List<Question> questionData, String mId) throws Exception {
        List<Question> formQuestions = getAllQuestions(mId);
        for (Question newData : questionData) {
            formQuestions.parallelStream()
                    .filter(existingQuestion -> existingQuestion.getQId().equals(newData.getQId()))
                    .findFirst()
                    .ifPresent(existingQuestion -> {
                        existingQuestion.setQname(newData.getQname());
                        existingQuestion.setQlabel(newData.getQlabel());
                        existingQuestion.setQtype(newData.getQtype());
                    });
        }
        qRepo.saveAll(formQuestions);
    }

    @Transactional
    public void deleteQuestionsByIds(List<String> qids) {
        qRepo.deleteAllById(qids);
    }

    public void deleteByFormId(String mId) throws Exception {
        qRepo.deleteAllByMId(mId);
    }

    public List<QuestionDTO> getQuestionByForm(String mId) throws Exception {
        return getAllQuestions(mId).parallelStream()
                .map(question -> new QuestionDTO(question.getQId(), question.getQname(), question.getQtype(),
                        question.getQlabel()))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "questions", key = "#mId")
    private List<Question> getAllQuestions(String mId) throws Exception {
        List<Question> questions = qRepo.findAllByMId(mId);
        if (questions.isEmpty())
            throw new FormDataException("No Questions found!!!. Seems an alteration in Form Data");
        return questions;

    }

}

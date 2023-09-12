package com.data.organization.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.data.organization.dto.QuestionDTO;
import com.data.organization.exception.MetaDataException;
import com.data.organization.model.Question;
import com.data.organization.repository.QuestionRepo;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class QuestionService {

    @Autowired
    private QuestionRepo qRepo;

    @Transactional
    public void saveQuestion(List<QuestionDTO> questions, String metaDataId) throws Exception {
        List<Question> questionData = questions.parallelStream()
                .map(question -> new Question(question.getQname(), question.getQlabel(), question.getQtype(),
                        metaDataId))
                .collect(Collectors.toList());
        log.info("Questions object created");
        qRepo.saveAll(questionData);
        log.info("questions saved");
    }

    @Transactional
    public void updateQuestions(List<Question> questionData, String metaDataId) throws Exception {
        List<Question> formQuestions = getAllQuestions(metaDataId);
        for (Question newData : questionData) {
            formQuestions.parallelStream()
                    .filter(existingQuestion -> existingQuestion.getQuestionId().equals(newData.getQuestionId()))
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
    public void deleteQuestionsByIds(List<String> qids) throws Exception {
        qRepo.deleteAllById(qids);
    }

    public void deleteByFormId(String metaDataId) throws Exception {
        qRepo.deleteAllByMetaDataId(metaDataId);
    }

    public List<QuestionDTO> getQuestionByForm(String metaDataId) throws Exception {
        return getAllQuestions(metaDataId).parallelStream()
                .map(question -> new QuestionDTO(question.getQuestionId(), question.getQname(), question.getQtype(),
                        question.getQlabel()))
                .collect(Collectors.toList());
    }

    @Cacheable(value = "questions", key = "#metaDataId")
    private List<Question> getAllQuestions(String metaDataId) throws Exception {
        List<Question> questions = qRepo.findAllByMetaDataId(metaDataId);
        if (questions.isEmpty())
            throw new MetaDataException("No Questions found!!!. Seems an alteration in Form Data");
        return questions;

    }

}

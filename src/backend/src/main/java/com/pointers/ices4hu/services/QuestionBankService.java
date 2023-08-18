package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.*;
import com.pointers.ices4hu.repositories.MultipleChoiceRepository;
import com.pointers.ices4hu.repositories.QuestionBankRepository;
import com.pointers.ices4hu.repositories.QuestionRepository;
import com.pointers.ices4hu.repositories.UserRepository;
import com.pointers.ices4hu.requests.QuestionBankSaveRequest;
import com.pointers.ices4hu.requests.RequestMultipleChoice;
import com.pointers.ices4hu.requests.RequestQuestion;
import com.pointers.ices4hu.requests.SurveyRequest;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.responses.QuestionBankResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

@Service
public class QuestionBankService {

    private final QuestionBankRepository questionBankRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final MultipleChoiceRepository multipleChoiceRepository;

    public QuestionBankService(QuestionBankRepository questionBankRepository,
                               QuestionRepository questionRepository,
                               UserRepository userRepository,
                               MultipleChoiceRepository multipleChoiceRepository) {
        this.questionBankRepository = questionBankRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.multipleChoiceRepository = multipleChoiceRepository;
    }

    /**
     * Checks if the given RequestQuestion object corresponds to a valid (all-fields-nonempty question)
     * @param requestQuestion the RequestQuestion object to be checked
     * @return whether or not the given RequestQuestion object is valid
     */
    private boolean isRequestQuestionValid(RequestQuestion requestQuestion) {
        if (!StringUtils.hasText(requestQuestion.getQuestion()))
            return false;

        if (requestQuestion.getIsMultipleChoice()) {
            if (requestQuestion.getMultipleChoices().size() < 1)
                return false;

            for (RequestMultipleChoice requestMultipleChoice: requestQuestion.getMultipleChoices()) {
                if (!StringUtils.hasText(requestMultipleChoice.getContent()))
                    return false;
            }
        }

        return true;
    }

    private boolean saveSubmitSurveyQuestionCheck(QuestionBankSaveRequest questionBankSaveRequest) {
        boolean emptyQuestionExists = false;
        for (RequestQuestion requestQuestion: questionBankSaveRequest.getQuestions()) {
            if (!isRequestQuestionValid(requestQuestion)) {
                emptyQuestionExists = true;
                break;
            }
        }

        return emptyQuestionExists;

    }

    public ResponseEntity<MessageResponse> save(String instructorLoginId,
                                                QuestionBankSaveRequest questionBankSaveRequest) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(instructorLoginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"), HttpStatus.UNAUTHORIZED);

        QuestionBank questionBank = questionBankRepository.findQuestionBankByInstructorLoginId(instructorLoginId);
        User instructor = userRepository.findByLoginID(instructorLoginId);
        if (questionBank == null) {
            questionBank = new QuestionBank();
            questionBank.setUser(instructor);
            questionBankRepository.save(questionBank);
        }

        boolean emptyQuestionExists = saveSubmitSurveyQuestionCheck(questionBankSaveRequest);
        if (emptyQuestionExists) {
            return new ResponseEntity<>(
                    new MessageResponse("There are questions/multiple choice options that are empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        List<RequestQuestion> questions = questionBankSaveRequest.getQuestions();


        List<Question> existingQuestions = questionRepository.findQuestionsByQuestionBankId(questionBank.getId());
        List<MultipleChoice> existingMultipleChoices = new ArrayList<>();
        for (Question question: existingQuestions) {
            existingMultipleChoices.addAll(multipleChoiceRepository.findMultipleChoicesByQuestionId(question.getId()));
        }


        Set<Long> requestQuestionIds = new HashSet<>();
        Set<Long> requestMultipleQuestionIds = new HashSet<>();


        int orderIdCounter = 1;
        for (RequestQuestion requestQuestion: questions) {
            Long id = requestQuestion.getId();

            Question question = new Question();
            if (id != null) {
                question.setId(id);
                requestQuestionIds.add(id);
            }

            question.setContent(requestQuestion.getQuestion());
            question.setMultipleChoice(requestQuestion.getIsMultipleChoice());
            question.setQuestionBank(questionBank);
            question.setOrderId((long)(orderIdCounter++));
            questionRepository.save(question);

            if (question.getMultipleChoice()) {
                for (RequestMultipleChoice requestMultipleChoice: requestQuestion.getMultipleChoices()) {
                    MultipleChoice multipleChoice = new MultipleChoice();
                    if (requestMultipleChoice.getId() != null) {
                        multipleChoice.setId(requestMultipleChoice.getId());
                    }

                    multipleChoice.setContent(requestMultipleChoice.getContent());
                    multipleChoice.setQuestion(question);
                    multipleChoiceRepository.save(multipleChoice);
                    requestMultipleQuestionIds.add(multipleChoice.getId());
                }
            }


        }

        // remove questions that are not in the request from the database
        for (Question question: existingQuestions) {
            if (!requestQuestionIds.contains(question.getId())) {
                questionRepository.delete(question);
            }
        }

        for (MultipleChoice multipleChoice: existingMultipleChoices) {
            if (!requestMultipleQuestionIds.contains(multipleChoice.getId())) {
                multipleChoiceRepository.delete(multipleChoice);
            }
        }

        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);

    }

    public ResponseEntity<Object> getQuestionBank(String instructorLoginId) {
        QuestionBankResponse questionBankResponse = new QuestionBankResponse();
        QuestionBank questionBank = questionBankRepository.findQuestionBankByInstructorLoginId(instructorLoginId);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(instructorLoginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"), HttpStatus.UNAUTHORIZED);

        User instructor = userRepository.findByLoginID(instructorLoginId);
        if (questionBank == null) {
            questionBank = new QuestionBank();
            questionBank.setUser(instructor);
            questionBankRepository.save(questionBank);
            questionBankResponse.setId(questionBank.getId());
            questionBankResponse.setQuestions(new ArrayList<>());
            return new ResponseEntity<>(questionBankResponse, HttpStatus.OK);
        }

        questionBankResponse.setId(questionBank.getId());
        List<RequestQuestion> requestQuestions = new ArrayList<>();
        List<Question> questions = questionRepository.findQuestionsByQuestionBankId(questionBank.getId());
        Collections.sort(questions, QuestionComparator.getInstance());

        for (Question question: questions) {
            RequestQuestion requestQuestion = new RequestQuestion();
            requestQuestion.setId(question.getId());
            requestQuestion.setIsMultipleChoice(question.getMultipleChoice());
            requestQuestion.setQuestion(question.getContent());


            if (requestQuestion.getIsMultipleChoice()) {
                List<RequestMultipleChoice> requestMultipleChoices = new ArrayList<>();
                for (MultipleChoice multipleChoice : question.getMultipleChoices()) {
                    RequestMultipleChoice requestMultipleChoice = new RequestMultipleChoice();
                    requestMultipleChoice.setId(multipleChoice.getId());
                    requestMultipleChoice.setContent(multipleChoice.getContent());
                    requestMultipleChoices.add(requestMultipleChoice);
                }
                Collections.sort(requestMultipleChoices);
                requestQuestion.setMultipleChoices(requestMultipleChoices);
            }

            requestQuestions.add(requestQuestion);
        }

        questionBankResponse.setQuestions(requestQuestions);
        return new ResponseEntity<>(questionBankResponse, HttpStatus.OK);
    }
}

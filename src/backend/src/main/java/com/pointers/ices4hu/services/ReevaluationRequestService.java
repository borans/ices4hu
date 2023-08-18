package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.*;
import com.pointers.ices4hu.repositories.*;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.responses.SurveyResponse;
import com.pointers.ices4hu.types.RequestStatusType;
import com.pointers.ices4hu.types.UserType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class ReevaluationRequestService {

    private final ReevaluationRequestRepository reevaluationRequestRepository;
    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;
    private final QuestionRepository questionRepository;
    private final MultipleChoiceRepository multipleChoiceRepository;
    private final EmailServiceImpl emailService;

    @Value("${ices4hu.general.mail.inform_instructor_on_reevaluation}")
    private boolean INFORM_INSTRUCTOR_ON_REEVALUATION;

    public ReevaluationRequestService(ReevaluationRequestRepository reevaluationRequestRepository,
                                      UserRepository userRepository,
                                      SurveyRepository surveyRepository,
                                      QuestionRepository questionRepository,
                                      MultipleChoiceRepository multipleChoiceRepository,
                                      EmailServiceImpl emailService) {
        this.reevaluationRequestRepository = reevaluationRequestRepository;
        this.userRepository = userRepository;
        this.surveyRepository = surveyRepository;
        this.questionRepository = questionRepository;
        this.multipleChoiceRepository = multipleChoiceRepository;
        this.emailService = emailService;
    }

    public ResponseEntity<Object> requestReevaluation(Long surveyId, String user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User instructor = userRepository.findByLoginID(user);
        if (instructor == null)
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        Survey survey = surveyRepository.findById(surveyId).orElse(null);

        if (survey == null)
            return new ResponseEntity<>(new MessageResponse("No such survey!"),
                    HttpStatus.BAD_REQUEST);

        if (survey.getUserCreator() == null || survey.getUserCreator().getId() != instructor.getId())
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        ReevaluationRequest reevaluationRequest = reevaluationRequestRepository
                .findReevaluationRequestBySurveyId(surveyId);

        if (reevaluationRequest != null) {
            String status = RequestStatusType.values()[reevaluationRequest.getStatus().intValue()].getName();
            return new ResponseEntity<>(
                    new MessageResponse("You cannot request reevaluation for a survey more than once! Status: "
                            + status),
                    HttpStatus.BAD_REQUEST);
        }

        reevaluationRequest = new ReevaluationRequest();
        reevaluationRequest.setRequestDatetime(LocalDateTime.now());
        reevaluationRequest.setUserRequest(instructor);
        reevaluationRequest.setStatus(RequestStatusType.PENDING.getValue());
        reevaluationRequest.setSurvey(survey);
        reevaluationRequestRepository.save(reevaluationRequest);

        return new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);
    }

    public ResponseEntity<Object> denyReevaluationRequest(Long surveyId, String user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User departmentManager = userRepository.findByLoginID(user);
        if (departmentManager == null || departmentManager.getUserType() != UserType.DEPARTMENT_MANAGER.getValue())
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        ReevaluationRequest reevaluationRequest = reevaluationRequestRepository
                .findReevaluationRequestBySurveyId(surveyId);
        if (reevaluationRequest == null)
            return new ResponseEntity<>(new MessageResponse("No such reevaluation request!"),
                    HttpStatus.BAD_REQUEST);

        if (reevaluationRequest.getStatus() != RequestStatusType.PENDING.getValue())
            return new ResponseEntity<>(new MessageResponse("Request is not in pending state!"),
                    HttpStatus.BAD_REQUEST);

        Survey survey = reevaluationRequest.getSurvey();
        if (survey == null)
            return new ResponseEntity<>(new MessageResponse("No such survey!"),
                    HttpStatus.BAD_REQUEST);

        reevaluationRequest.setStatus(RequestStatusType.DENIED.getValue());
        reevaluationRequest.setUserEvaluate(departmentManager);
        reevaluationRequestRepository.save(reevaluationRequest);

        if (INFORM_INSTRUCTOR_ON_REEVALUATION
                && survey.getUserCreator() != null && survey.getUserCreator().getEmail() != null) {
            SurveyResponse surveyResponse = new SurveyResponse();
            surveyResponse.buildFrom(survey);
            emailService.sendReevaluationRequestMail(survey.getUserCreator().getEmail(), survey,
                    surveyResponse, false);
        }

        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);

    }

    public ResponseEntity<Object> approveReevaluationRequest(Long surveyId, String user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User departmentManager = userRepository.findByLoginID(user);
        if (departmentManager == null || departmentManager.getUserType() != UserType.DEPARTMENT_MANAGER.getValue())
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        ReevaluationRequest reevaluationRequest = reevaluationRequestRepository
                .findReevaluationRequestBySurveyId(surveyId);
        if (reevaluationRequest == null)
            return new ResponseEntity<>(new MessageResponse("No such reevaluation request!"),
                    HttpStatus.BAD_REQUEST);

        if (reevaluationRequest.getStatus() != RequestStatusType.PENDING.getValue())
            return new ResponseEntity<>(new MessageResponse("Request is not in pending state!"),
                    HttpStatus.BAD_REQUEST);

        Survey survey = reevaluationRequest.getSurvey();
        if (survey == null)
            return new ResponseEntity<>(new MessageResponse("No such survey!"),
                    HttpStatus.BAD_REQUEST);


        Survey newSurvey = new Survey();
        newSurvey.setCourse(survey.getCourse());
        newSurvey.setUserInstructor(survey.getUserInstructor());
        newSurvey.setUserCreator(survey.getUserCreator());
        newSurvey.setCreationDatetime(LocalDateTime.now());
        newSurvey.setStartingDatetime(LocalDateTime.now());
        newSurvey.setTrialCount(1);
        newSurvey.setDeadline(LocalDateTime.now().plusWeeks(1));
        surveyRepository.save(newSurvey);

        List<Question> questions =  new ArrayList<>();
        questions.addAll(survey.getQuestionSet());
        Collections.sort(questions, QuestionComparator.getInstance());
        for (Question question: questions) {

            Question newQuestion = new Question();
            newQuestion.setOrderId(question.getOrderId());
            newQuestion.setContent(question.getContent());
            newQuestion.setSurvey(newSurvey);
            newQuestion.setMultipleChoice(question.getMultipleChoice());
            questionRepository.save(newQuestion);

            if (question.getMultipleChoice()) {
                List<MultipleChoice> multipleChoices = new ArrayList<>();
                multipleChoices.addAll(question.getMultipleChoices());
                Collections.sort(multipleChoices, MultipleChoiceComparator.getInstance());

                for (MultipleChoice multipleChoice: multipleChoices) {
                    MultipleChoice newMultipleChoice = new MultipleChoice();
                    newMultipleChoice.setQuestion(newQuestion);
                    newMultipleChoice.setContent(multipleChoice.getContent());
                    multipleChoiceRepository.save(newMultipleChoice);
                }
            }
        }

        reevaluationRequest.setStatus(RequestStatusType.APPROVED.getValue());
        reevaluationRequest.setUserEvaluate(departmentManager);
        reevaluationRequestRepository.save(reevaluationRequest);

        if (INFORM_INSTRUCTOR_ON_REEVALUATION
                && survey.getUserCreator() != null && survey.getUserCreator().getEmail() != null) {
            SurveyResponse surveyResponse = new SurveyResponse();
            surveyResponse.buildFrom(survey);
            emailService.sendReevaluationRequestMail(survey.getUserCreator().getEmail(), survey,
                    surveyResponse, true);
        }

        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);

    }
}

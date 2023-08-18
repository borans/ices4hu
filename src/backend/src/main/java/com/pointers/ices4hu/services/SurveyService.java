package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.*;
import com.pointers.ices4hu.repositories.*;
import com.pointers.ices4hu.requests.*;
import com.pointers.ices4hu.responses.*;
import com.pointers.ices4hu.types.UserType;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class SurveyService {

    private final SurveyRepository surveyRepository;
    private final StudentSurveyFillRepository studentSurveyFillRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final MultipleChoiceRepository multipleChoiceRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final CourseService courseService;
    private final ReevaluationRequestRepository reevaluationRequestRepository;
    private final ScheduleRepository scheduleRepository;
    private final EmailServiceImpl emailService;

    @Value("${ices4hu.general.upcoming_survey_limit_days}")
    private int UPCOMING_SURVEY_LIMIT_DAYS;

    @Value("${ices4hu.general.instructor_survey.minimum_student_count}")
    private int INSTRUCTOR_SURVEY_MINIMUM_STUDENT_COUNT;

    @Value("${ices4hu.general.course_survey.undergraduate.minimum_student_count}")
    private int UNDERGRADUATE_COURSE_SURVEY_MINIMUM_STUDENT_COUNT;

    @Value("${ices4hu.general.course_survey.graduate.minimum_student_count}")
    private int GRADUATE_COURSE_SURVEY_MINIMUM_STUDENT_COUNT;

    @Value("${ices4hu.general.student_answer.auto_submission}")
    private boolean STUDENT_ANSWER_AUTO_SUBMISSION;

    @Value("${ices4hu.general.mail.inform_student_on_submission}")
    private boolean INFORM_STUDENT_ON_SUBMISSION;

    @Value("${ices4hu.general.mail.inform_instructor_on_submission}")
    private boolean INFORM_INSTRUCTOR_ON_SUBMISSION;

    public SurveyService(SurveyRepository surveyRepository,
                         StudentSurveyFillRepository studentSurveyFillRepository,
                         StudentAnswerRepository studentAnswerRepository,
                         MultipleChoiceRepository multipleChoiceRepository, QuestionRepository questionRepository,
                         UserRepository userRepository, CourseService courseService,
                         ReevaluationRequestRepository reevaluationRequestRepository,
                         ScheduleRepository scheduleRepository,
                         EmailServiceImpl emailService) {
        this.surveyRepository = surveyRepository;
        this.studentSurveyFillRepository = studentSurveyFillRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.multipleChoiceRepository = multipleChoiceRepository;
        this.questionRepository = questionRepository;
        this.userRepository = userRepository;
        this.courseService = courseService;
        this.reevaluationRequestRepository = reevaluationRequestRepository;
        this.scheduleRepository = scheduleRepository;
        this.emailService = emailService;
    }

    public List<Survey> getSurveysOfStudent(String loginId) {
        List<Course> courses = courseService.getCourses(loginId);
        List<Survey> surveyList = new ArrayList<>();
        Set<Long> addedSurveyIds = new HashSet<>();
        for (Course course: courses) {

            for (Survey survey: surveyRepository.findByCourseId(course.getId())) {
                if (!addedSurveyIds.contains(survey.getId()) && survey.getCreationDatetime() != null) {
                    addedSurveyIds.add(survey.getId());
                    surveyList.add(survey);
                }
            }

            /* The course has no instructor set, therefore, it cannot contribute any instructor type surveys
             * Therefore, go to next iteration */
            if (course.getUser() == null)
                continue;

            for (Survey survey: surveyRepository.findByInstructorId(course.getUser().getId())) {
                if (!addedSurveyIds.contains(survey.getId()) && survey.getCreationDatetime() != null) {
                    addedSurveyIds.add(survey.getId());
                    surveyList.add(survey);
                }
            }
        }

        Collections.sort(surveyList, SurveyComparator.getInstance());

        return surveyList;
    }

    public ResponseEntity<Object> getUpcomingSurveysOfStudent(String loginId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(loginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        List<Survey> surveys = getSurveysOfStudent(loginId);
        List<SurveyResponseRemainingDays> surveyResponseRemainingDaysList = new ArrayList<>();

        for(Survey survey: surveys) {
            StudentSurveyFill studentSurveyFill = studentSurveyFillRepository.
                    getStudentSurveyFillByStudentLoginIdAndSurveyId(loginId, survey.getId());
            // The student has already submitted the survey, go to the next iteration
            if (studentSurveyFill != null && studentSurveyFill.getCompletionDatetime() != null)
                continue;
            if (LocalDateTime.now().isBefore(survey.getDeadline())
                    && LocalDateTime.now().plusDays(UPCOMING_SURVEY_LIMIT_DAYS).isAfter(survey.getDeadline())) {
                SurveyResponseRemainingDays surveyResponseRemainingDays = new SurveyResponseRemainingDays();
                surveyResponseRemainingDays.buildFrom(survey);
                surveyResponseRemainingDaysList.add(surveyResponseRemainingDays);
            }
        }

        return new ResponseEntity<>(surveyResponseRemainingDaysList, HttpStatus.OK);

    }

    public ResponseEntity<Object> getUnsubmittedSurveysOfInstructor(String user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User instructor = userRepository.findByLoginID(user);
        if (instructor == null || instructor.getId() == null)
            return new ResponseEntity<>(new MessageResponse("No such user!"),
                    HttpStatus.BAD_REQUEST);

        List<Survey> surveys = surveyRepository.findByCreatorIntegerId(instructor.getId());
        List<SurveyResponse> surveyResponses = new ArrayList<>();
        for (Survey survey: surveys) {
            // survey is already submitted, go to next iteration
            if (survey.getCreationDatetime() != null) continue;
            SurveyResponse surveyResponse = new SurveyResponse();
            surveyResponse.buildFrom(survey);
            surveyResponses.add(surveyResponse);
        }

        return new ResponseEntity<>(surveyResponses, HttpStatus.OK);
    }

    public Survey getSurvey(Long surveyId){
        return surveyRepository.findById(surveyId).orElse(null);
    }

    public ResponseEntity<Object> getQuestionsOfSurveyWithAnswers(Long surveyId, String loginID) {
        return getQuestionsOfSurveyWithAnswers(surveyId, loginID, null,true);
    }

    private ResponseEntity<Object> getQuestionsOfSurveyWithAnswers(Long surveyId, String loginID,
                                                                   String departmentManager, boolean forStudent) {
        /*
        String compareUsername = null;
        if (forStudent) {
            compareUsername = loginID;
        } else {
            compareUsername = departmentManager;
        }

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(compareUsername))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);
        */

        User student = userRepository.findByLoginID(loginID);
        if (student == null)
            return new ResponseEntity<>(new MessageResponse("No such student!"), HttpStatus.BAD_REQUEST);
        Long studentId = student.getId();

        User departmentManagerUser;

        Survey survey = getSurvey(surveyId);
        if (survey == null)
            return new ResponseEntity<>(new MessageResponse("No such survey!"), HttpStatus.BAD_REQUEST);

        if (!forStudent) {
            departmentManagerUser = userRepository.findByLoginID(departmentManager);

            if (departmentManagerUser == null
                    || departmentManagerUser.getUserType() != UserType.DEPARTMENT_MANAGER.getValue()) {
                return new ResponseEntity<>(new MessageResponse("No such department manager!"),
                        HttpStatus.BAD_REQUEST);
            }

            if (departmentManagerUser.getDepartment() == null)
                return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                        HttpStatus.BAD_REQUEST);

            if (survey.getCourse() == null && survey.getUserCreator() == null) {
                if (student.getDepartment() == null
                        || student.getDepartment().getId() != departmentManagerUser.getDepartment().getId()) {
                    return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                            HttpStatus.BAD_REQUEST);
                }

            } else {
                if (survey.getCourse() == null) {
                    if (survey.getUserCreator().getDepartment() == null
                            || survey.getUserCreator().getDepartment().getId() != departmentManagerUser.getDepartment()
                            .getId()) {
                        return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                                HttpStatus.BAD_REQUEST);
                    }
                } else {
                    if (survey.getCourse().getDepartment() == null
                            || survey.getCourse().getDepartment().getId() != departmentManagerUser.getDepartment()
                            .getId()) {
                        return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                                HttpStatus.BAD_REQUEST);
                    }
                }
            }
        }

        Set<Question> questionSet = survey.getQuestionSet();
        List<Question> questions = new ArrayList<>();
        questions.addAll(questionSet);
        Collections.sort(questions, QuestionComparator.getInstance());

        List<QuestionWithAnswer> questionWithAnswerList = new ArrayList<>();

        for(Question question: questions) {
            QuestionWithAnswer questionWithAnswer = new QuestionWithAnswer();
            questionWithAnswer.setQuestion(question);

            StudentAnswer studentAnswer = studentAnswerRepository
                    .findStudentAnswerByStudentAndQuestionIds(studentId, question.getId());

            if (studentAnswer == null) {
                questionWithAnswer.setAnswer(null);
            } else {
                if (question.getMultipleChoice()) {
                    MultipleChoice multipleChoice = studentAnswer.getMultipleChoice();

                    if (multipleChoice != null)
                        questionWithAnswer.setAnswer(String.valueOf(multipleChoice.getId()));
                    else
                        questionWithAnswer.setAnswer(null);

                } else {
                    if (StringUtils.hasText(studentAnswer.getContent()))
                        questionWithAnswer.setAnswer(studentAnswer.getContent());
                    else
                        questionWithAnswer.setAnswer(null);
                }
            }

            questionWithAnswerList.add(questionWithAnswer);
        }

        SurveyQuestionsResponse surveyQuestionsResponse = new SurveyQuestionsResponse();

        surveyQuestionsResponse.setQuestions(questionWithAnswerList);

        StudentSurveyFill studentSurveyFill = studentSurveyFillRepository
                .getStudentSurveyFillByStudentLoginIdAndSurveyId(loginID, surveyId);
        if (studentSurveyFill != null) {
            surveyQuestionsResponse.setTrialCount(studentSurveyFill.getTrialCount());
        } else {
            surveyQuestionsResponse.setTrialCount(0);
        }

        return new ResponseEntity<>(surveyQuestionsResponse, HttpStatus.OK);
    }

    public ResponseEntity<Object> viewStudentAnswersForDepartmentManager(Long survey,
                                                                         String student, String departmentManager) {
        return getQuestionsOfSurveyWithAnswers(survey, student, departmentManager, false);
    }

    public ResponseEntity<Object> checkIfStudentIsBanned(String studentLoginId) {
        User student = userRepository.findByLoginID(studentLoginId);
        if (student == null || student.getUserType() != UserType.STUDENT.getValue()) {
            return new ResponseEntity<>(new MessageResponse("No such student exists!"), HttpStatus.BAD_REQUEST);
        }

        if (student.getBanned()) {
            return new ResponseEntity<>(new MessageResponse("You are banned and cannot fill any survey!"),
                    HttpStatus.BAD_REQUEST);
        }

        return null;
    }

    public void answerSurvey(String student, AnswerSurveyRequest answerSurveyRequest) {
        List<Answer> studentAnswer = answerSurveyRequest.getAnswers();

        for (Answer value : studentAnswer) {
            StudentAnswer answer = new StudentAnswer();
            answer.setUser(userRepository.findByLoginID(student));
            answer.setQuestion(questionRepository.findById(value.getQuestionId()).get());
            answer.setContent(value.getContent());
            if(value.getMultipleChoiceId() != null) {
                answer.setMultipleChoice(multipleChoiceRepository.findById(value.getMultipleChoiceId()).get());
            }
            studentAnswerRepository.save(answer);
        }

    }

    public boolean checkAnswerSurveyRequestRequirements(AnswerSurveyRequest answerSurveyRequest,
                                                        int minimumAnsweredQuestions) {
        int answeredQuestions = 0;
        for (Answer answer: answerSurveyRequest.getAnswers())
            if (answer.isAnswered())
                answeredQuestions++;

        return answeredQuestions >= minimumAnsweredQuestions;
    }

    /*
    public List<Survey> getSurveysRelatedToCourse(Course course) {
        return surveyRepository.findByCourseId(course.getId());
    }
    */

    public ResponseEntity<MessageResponse> createSurvey(String instructor, SurveyRequest surveyRequest) {
        Long courseId = surveyRequest.getCourseId();

        User user = userRepository.findByLoginID(instructor);
        if (user == null) {
            return new ResponseEntity<>(new MessageResponse("No such user!"), HttpStatus.BAD_REQUEST);
        }

        if (user.getUserType() != UserType.INSTRUCTOR.getValue()) {
            return new ResponseEntity<>(new MessageResponse("Not an instructor!"), HttpStatus.BAD_REQUEST);
        }

        Survey survey = new Survey();
        survey.setUserCreator(user);

        // the survey is for course
        if (courseId != null) {

            Course course = courseService.getCourseByCourseId(courseId);
            if (course == null) {
                return new ResponseEntity<>(new MessageResponse("No such course!"), HttpStatus.BAD_REQUEST);
            }

            survey.setCourse(course);

        } else { // the survey is for instructor
            survey.setUserInstructor(user);
        }

        // survey.setCreationDatetime(surveyRequest.getCreationDatetime());
        // survey.setCreationDatetime(LocalDateTime.now());
        survey.setStartingDatetime(surveyRequest. getStartingDatetime());
        survey.setDeadline(surveyRequest.getDeadline());
        survey.setTrialCount(1);

        Schedule schedule = scheduleRepository.findById(1L).orElse(null);
        if (schedule == null) {
            return new ResponseEntity<>(new MessageResponse("Schedule is not set!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (survey.getDeadline() == null || survey.getStartingDatetime() == null) {
            return new ResponseEntity<>(new MessageResponse("Survey start or end date is not set!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (survey.getDeadline().isAfter(schedule.getEndDate())) {
            return new ResponseEntity<>(new MessageResponse(
                    "Survey deadline cannot be after the end date of the academic schedule!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (survey.getStartingDatetime().isAfter(schedule.getEndDate())) {
            return new ResponseEntity<>(new MessageResponse(
                    "Survey starting date cannot be after the end date of the academic schedule!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (survey.getDeadline().isBefore(schedule.getStartDate())) {
            return new ResponseEntity<>(new MessageResponse(
                    "Survey deadline cannot be before the start date of the academic schedule!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (survey.getStartingDatetime().isBefore(schedule.getStartDate())) {
            return new ResponseEntity<>(new MessageResponse(
                    "Survey starting date cannot be before the start date of the academic schedule!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (survey.getStartingDatetime().isAfter(survey.getDeadline())) {
            return new ResponseEntity<>(new MessageResponse(
                    "Survey starting date cannot be after the deadline of the survey!"),
                    HttpStatus.BAD_REQUEST);
        }

        surveyRepository.save(survey);

        int orderIdCounter = 1;

        for (RequestQuestion requestQuestion: surveyRequest.getQuestions()) {
            Question question = new Question();
            question.setSurvey(survey);
            question.setContent(requestQuestion.getQuestion());
            question.setMultipleChoice(requestQuestion.getIsMultipleChoice());
            question.setOrderId((long)(orderIdCounter++));
            questionRepository.save(question);

            if (question.getMultipleChoice() && requestQuestion.getMultipleChoices() != null) {
                for (RequestMultipleChoice requestMultipleChoice: requestQuestion.getMultipleChoices()) {
                    MultipleChoice multipleChoice = new MultipleChoice();
                    multipleChoice.setQuestion(question);
                    multipleChoice.setContent(requestMultipleChoice.getContent());
                    /*if (requestMultipleChoice.getId() != null) {
                        multipleChoice.setId(requestMultipleChoice.getId());
                    }*/
                    multipleChoiceRepository.save(multipleChoice);
                }
            }
        }

        return new ResponseEntity<>(new MessageResponse("Survey has successfully been created!"), HttpStatus.OK);

    }

    public ResponseEntity<Object> saveSurvey(Long surveyId, String instructor, SurveyRequest surveyRequest) {
        return saveSurvey(surveyId, instructor, surveyRequest, 3, false, false);
    }

    public ResponseEntity<Object> saveSurveyForAdmin(Long surveyId, SurveyRequest surveyRequest) {

        return saveSurvey(surveyId, null, surveyRequest, -1, false, true);
    }

    public ResponseEntity<Object> submitSurvey(Long surveyId, String instructor, SurveyRequest surveyRequest) {
        return saveSurvey(surveyId, instructor, surveyRequest, 4, true, false);
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

    private int saveSubmitSurveyQuestionCheck(SurveyRequest surveyRequest) {
        int counter = 0;
        boolean nonEmptyQuestionExists = false;
        for (RequestQuestion requestQuestion: surveyRequest.getQuestions()) {
            if (isRequestQuestionValid(requestQuestion)) counter++;
            else nonEmptyQuestionExists = true;
        }

        if (nonEmptyQuestionExists)
            return -counter;
        else
            return counter;
    }

    private ResponseEntity<Object> saveSurvey(Long surveyId, String instructor, SurveyRequest surveyRequest,
                                              int trialCountLimit, boolean submitMode, boolean adminMode) {

        if (adminMode && submitMode)
            return new ResponseEntity<>(new MessageResponse("Illegal call!"), HttpStatus.PRECONDITION_FAILED);

        if (submitMode) {
            int validQuestionCount = saveSubmitSurveyQuestionCheck(surveyRequest);

            if (validQuestionCount == 0) {
                return new ResponseEntity<>(new MessageResponse("No valid question exists!"),
                        HttpStatus.BAD_REQUEST);
            }

            if (validQuestionCount < 0) {
                return new ResponseEntity<>(new MessageResponse(
                        "There are questions/multiple choice question options that are empty!"), HttpStatus.BAD_REQUEST);
            }

            if (validQuestionCount < 8) {
                return new ResponseEntity<>(new MessageResponse("At least 8 valid question must exist!"),
                        HttpStatus.BAD_REQUEST);
            }

            if(validQuestionCount <= 0) {
                return new ResponseEntity<>(new MessageResponse(
                        "There are questions/multiple choice question options that are empty!"), HttpStatus.BAD_REQUEST);
            }
        }


        if (!adminMode) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!username.equals(instructor))
                return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                        HttpStatus.UNAUTHORIZED);
        }

        Survey survey = surveyRepository.findById(surveyId).orElse(null);

        // returns null if no problem exists
        ResponseEntity<Object> commonEntranceResponse = saveSubmitSurveyCommonEntrance(survey,
                surveyRequest, trialCountLimit, adminMode);

        if (commonEntranceResponse != null)
            return commonEntranceResponse;

        if (!adminMode && survey.getCreationDatetime() != null) {
            return new ResponseEntity<>(new MessageResponse("A submitted survey can only be edited by the admin!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (adminMode) {
            if (survey.getCreationDatetime() != null) {

                int validQuestionCount = saveSubmitSurveyQuestionCheck(surveyRequest);

                if (validQuestionCount == 0) {
                    return new ResponseEntity<>(new MessageResponse("No valid question exists!"),
                            HttpStatus.BAD_REQUEST);
                }

                if (validQuestionCount < 0) {
                    return new ResponseEntity<>(new MessageResponse(
                            "There are questions/multiple choice question options that are empty!"),
                            HttpStatus.BAD_REQUEST);
                }

                if (validQuestionCount < 8) {
                    return new ResponseEntity<>(
                            new MessageResponse("A submitted survey cannot have less than 8 questions!"),
                            HttpStatus.BAD_REQUEST);
                }

                if(validQuestionCount <= 0) {
                    return new ResponseEntity<>(new MessageResponse(
                            "There are questions/multiple choice question options that are empty!"),
                            HttpStatus.BAD_REQUEST);
                }
            }
        } else { // instructor mode
            Schedule schedule = scheduleRepository.findById(1L).orElse(null);
            if (schedule == null) {
                return new ResponseEntity<>(new MessageResponse("Schedule is not set!"),
                        HttpStatus.BAD_REQUEST);
            }

            if (survey.getDeadline() == null || survey.getStartingDatetime() == null) {
                return new ResponseEntity<>(new MessageResponse("Survey start or end date is not set!"),
                        HttpStatus.BAD_REQUEST);
            }

            if (survey.getDeadline().isAfter(schedule.getEndDate())) {
                return new ResponseEntity<>(new MessageResponse(
                        "Survey deadline cannot be after the end date of the academic schedule!"),
                        HttpStatus.BAD_REQUEST);
            }

            if (survey.getStartingDatetime().isAfter(schedule.getEndDate())) {
                return new ResponseEntity<>(new MessageResponse(
                        "Survey starting date cannot be after the end date of the academic schedule!"),
                        HttpStatus.BAD_REQUEST);
            }

            if (survey.getDeadline().isBefore(schedule.getStartDate())) {
                return new ResponseEntity<>(new MessageResponse(
                        "Survey deadline cannot be before the start date of the academic schedule!"),
                        HttpStatus.BAD_REQUEST);
            }

            if (survey.getStartingDatetime().isBefore(schedule.getStartDate())) {
                return new ResponseEntity<>(new MessageResponse(
                        "Survey starting date cannot be before the start date of the academic schedule!"),
                        HttpStatus.BAD_REQUEST);
            }

            if (survey.getStartingDatetime().isAfter(survey.getDeadline())) {
                return new ResponseEntity<>(new MessageResponse(
                        "Survey starting date cannot be after the deadline of the survey!"),
                        HttpStatus.BAD_REQUEST);
            }

        }

        if (submitMode) {
            survey.setCreationDatetime(LocalDateTime.now());
            if (surveyRequest.getQuestions().size() < 8) {
                return new ResponseEntity<>(new MessageResponse("At least 8 questions must exist!"),
                        HttpStatus.BAD_REQUEST);
            }
        }


        List<RequestQuestion> questions = surveyRequest.getQuestions();

        List<Question> existingQuestions = questionRepository.findQuestionsBySurveyId(survey.getId());
        List <MultipleChoice> existingMultipleChoices = new ArrayList<>();

        for (Question question: existingQuestions) {
            existingMultipleChoices.addAll(question.getMultipleChoices());
        }

        /*
         * The questions and multiple choices that exist in the database but that do not exist
         * in the request will be deleted from the database since this means that those questions
         * were actually deleted by the user at frontend. For detecting those quickly,
         * we use two separate hash sets. One for ids of questions in the request, and the other
         * for ids of multiple choices in the request. After saving the questions, the questions and multiple
         * choices that do not exist in these sets will be removed from the database.
        */
        Set<Long> requestQuestionIds = new HashSet<>();
        Set<Long> requestMultipleChoiceIds = new HashSet<>();

        int orderIdCounter = 1;

        for (RequestQuestion requestQuestion: questions) {
            Long id = requestQuestion.getId();
            Question question = new Question();
            question.setSurvey(survey);
            if (id != null) {
                question.setId(id);
                requestQuestionIds.add(id);
            }

            question.setContent(requestQuestion.getQuestion());
            question.setMultipleChoice(requestQuestion.getIsMultipleChoice());
            question.setOrderId((long)(orderIdCounter++));
            questionRepository.save(question);

            if (question.getMultipleChoice() && requestQuestion.getMultipleChoices() != null) {
                for (RequestMultipleChoice requestMultipleChoice: requestQuestion.getMultipleChoices()) {
                    MultipleChoice multipleChoice = new MultipleChoice();
                    if (requestMultipleChoice.getId() != null) {
                        multipleChoice.setId(requestMultipleChoice.getId());
                    }

                    multipleChoice.setContent(requestMultipleChoice.getContent());
                    multipleChoice.setQuestion(question);
                    multipleChoiceRepository.save(multipleChoice);
                    requestMultipleChoiceIds.add(multipleChoice.getId());
                }
            }
        }

        for (Question question: existingQuestions)
            if (!requestQuestionIds.contains(question.getId()))
                questionRepository.delete(question);

        for (MultipleChoice multipleChoice: existingMultipleChoices)
            if (!requestMultipleChoiceIds.contains(multipleChoice.getId()))
                multipleChoiceRepository.delete(multipleChoice);

        surveyRepository.save(survey);

        if (submitMode && INFORM_INSTRUCTOR_ON_SUBMISSION) {
            User instructorUser = userRepository.findByLoginID(instructor);
            if (instructorUser != null && instructorUser.getEmail() != null) {
                SurveyResponse surveyResponse = new SurveyResponse();
                surveyResponse.buildFrom(survey);
                emailService.sendSubmissionMailToInstructor(instructorUser.getEmail(), survey, surveyResponse);
            }
        }

        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);
    }

    private ResponseEntity<Object> saveSubmitSurveyCommonEntrance(Survey survey, SurveyRequest surveyRequest,
                                                                  int trialCountLimit, boolean adminMode) {
        if (survey == null)
            return new ResponseEntity<>(new MessageResponse("No such survey!"), HttpStatus.BAD_REQUEST);

        if (!adminMode && survey.getTrialCount() >= trialCountLimit)
            return new ResponseEntity<>(new MessageResponse("Trial count was exceeded!"), HttpStatus.BAD_REQUEST);

        if (!adminMode)
            survey.setTrialCount(survey.getTrialCount() + 1);

        if (surveyRequest.getCourseId() != null) {
            Course course = courseService.getCourseByCourseId(surveyRequest.getCourseId());
            if (course == null)
                return new ResponseEntity<>(new MessageResponse("No such course!"), HttpStatus.BAD_REQUEST);

            survey.setCourse(courseService.getCourseByCourseId(surveyRequest.getCourseId()));
            survey.setUserInstructor(null);
        } else {
            survey.setUserInstructor(survey.getUserCreator());
            survey.setCourse(null);
        }

        if (!adminMode) {
            survey.setStartingDatetime(surveyRequest.getStartingDatetime());
            survey.setDeadline(surveyRequest.getDeadline());
        }

        return null;
    }

    public ResponseEntity<Object> getSurveysOfInstructor(String user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        List<Survey> surveys = surveyRepository.findByCreatorId(user);
        List<SurveyResponseInstructor> surveyResponseInstructorList = new ArrayList<>();
        for(Survey survey: surveys) {

            SurveyResponseInstructor surveyResponseInstructor = new SurveyResponseInstructor();
            surveyResponseInstructor.buildFrom(survey);
            //SurveyResponseInstructor surveyResponseInstructor = SurveyResponseInstructor.getInstance(survey);

            Set<Long> studentIdSet = new HashSet<>();

            if (surveyResponseInstructor.getSurveyType().equals("Instructor")) {
                List<Course> courses = courseService.getCoursesByInstructorLoginId(user);
                for (Course course: courses) {
                    for (User student: course.getStudents()) {
                        studentIdSet.add(student.getId());
                    }
                }
            } else {
                for (User student: survey.getCourse().getStudents()) {
                    studentIdSet.add(student.getId());
                }
            }

            Long numberOfStudentsFilledTheSurvey = studentSurveyFillRepository
                    .getNumberOfStudentsFilledTheSurvey(survey.getId());

            Double completedPercent = null;
            if (studentIdSet.size() == 0)
                completedPercent = 0.0;
            else
                completedPercent = numberOfStudentsFilledTheSurvey.doubleValue() / studentIdSet.size();

            completedPercent *= 100;
            surveyResponseInstructor.setCompletedPercent(completedPercent);
            surveyResponseInstructorList.add(surveyResponseInstructor);

        }

        return new ResponseEntity<>(surveyResponseInstructorList, HttpStatus.OK);

    }

    private int getNumberOfStudentsRelatedToSurvey(Survey survey) {
        SurveyResponseInstructor surveyResponseInstructor = new SurveyResponseInstructor();
        surveyResponseInstructor.buildFrom(survey);
        Set<Long> studentIdSet = new HashSet<>();

        if (surveyResponseInstructor.getSurveyType().equals("Instructor")
                && survey.getUserInstructor() != null) {
            List<Course> courses = courseService.getCoursesByInstructorLoginId(survey.getUserInstructor().getLoginID());
            for (Course course: courses) {
                for (User student: course.getStudents()) {
                    studentIdSet.add(student.getId());
                }
            }
        } else {
            for (User student: survey.getCourse().getStudents()) {
                studentIdSet.add(student.getId());
            }
        }
        System.out.println(studentIdSet.size());
        return studentIdSet.size();
    }

    public ResponseEntity<Object> getSurveysOfDepartmentManager(String user) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User departmentManager = userRepository.findByLoginID(user);
        Long departmentId = departmentManager.getDepartment().getId();

        List<ReevaluationRequest> reevaluationRequests = reevaluationRequestRepository
                .findReevaluationRequestsAtDepartment(departmentId);
        List<Survey> surveys = surveyRepository.findCourseSurveysByDepartmentId(departmentId);
        surveys.addAll(surveyRepository.findInstructorSurveysByDepartmentId(departmentId));

        Set<Long> reevaluationSurveyIdSet = new HashSet<>();

        for (ReevaluationRequest reevaluationRequest: reevaluationRequests) {
            reevaluationSurveyIdSet.add(reevaluationRequest.getSurvey().getId());
        }

        //List<SurveyResponse> surveyResponses = getSurveyResponseListFromSurveys(surveys);
        List<SurveyResponseDepartmentManager> surveyResponseDepartmentManagers = new ArrayList<>();

        for (Survey survey: surveys) {
            SurveyResponseDepartmentManager surveyResponseDepartmentManager = new SurveyResponseDepartmentManager();
            surveyResponseDepartmentManager.buildFrom(survey);
            if (reevaluationSurveyIdSet.contains(survey.getId())) {
                surveyResponseDepartmentManager.setOpenForReevaluation(true);
            } else {
                surveyResponseDepartmentManager.setOpenForReevaluation(false);
            }
            surveyResponseDepartmentManagers.add(surveyResponseDepartmentManager);
        }

        return new ResponseEntity<>(surveyResponseDepartmentManagers, HttpStatus.OK);
    }

    public ResponseEntity<Object> getSurveysOfAdmin() {
        List<Survey> surveys = surveyRepository.findAll();

        List<SurveyResponse> surveyResponses = getSurveyResponseListFromSurveys(surveys);

        return new ResponseEntity<>(surveyResponses, HttpStatus.OK);
    }

    private List<SurveyResponse> getSurveyResponseListFromSurveys(List<Survey> surveys) {
        List<SurveyResponse> surveyResponses = new ArrayList<>();
        for(Survey survey: surveys) {
            SurveyResponse surveyResponse = new SurveyResponse();
            surveyResponse.buildFrom(survey);
            surveyResponses.add(surveyResponse);
        }
        return surveyResponses;
    }

    public ResponseEntity<Object> getQuestionsOfSurveyForInstructor(Long surveyId, String instructor) {
        return getQuestionsOfSurvey(surveyId, instructor, UserType.INSTRUCTOR);
    }

    public ResponseEntity<Object> getQuestionsOfSurveyForDepartmentManager(Long surveyId, String departmentManager) {
        return getQuestionsOfSurvey(surveyId, departmentManager, UserType.DEPARTMENT_MANAGER);
    }

    public ResponseEntity<Object> getQuestionsOfSurveyForAdmin(Long surveyId) {
        return getQuestionsOfSurvey(surveyId, null, UserType.ADMIN);
    }

    private ResponseEntity<Object> getQuestionsOfSurvey(Long surveyId, String loginId, UserType userType) {

        if (userType != UserType.INSTRUCTOR && userType != UserType.ADMIN && userType != UserType.DEPARTMENT_MANAGER)
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"), HttpStatus.UNAUTHORIZED);

        if (userType != UserType.ADMIN) {
            String username = SecurityContextHolder.getContext().getAuthentication().getName();
            if (!username.equals(loginId))
                return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                        HttpStatus.UNAUTHORIZED);
        }

        Survey surveyObject = surveyRepository.findById(surveyId).get();
        if (surveyObject == null)
            return new ResponseEntity<>("No such survey!", HttpStatus.BAD_REQUEST);

        User instructorObject = null;

        if (userType != UserType.ADMIN)
            instructorObject = userRepository.findByLoginID(loginId);


        if (userType == UserType.INSTRUCTOR) {
            if (surveyObject.getUserCreator() == null || (surveyObject.getUserCreator().getId() != instructorObject.getId()))
                return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                        HttpStatus.UNAUTHORIZED);
        } else if (userType == UserType.DEPARTMENT_MANAGER) {
            if (instructorObject.getDepartment() == null)
                return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                        HttpStatus.UNAUTHORIZED);

            if (surveyObject.getCourse() == null && surveyObject.getUserCreator() == null)
                return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                        HttpStatus.UNAUTHORIZED);

            if (surveyObject.getCourse() == null) {
                if (surveyObject.getUserCreator().getDepartment() == null)
                    return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                            HttpStatus.UNAUTHORIZED);

                if (surveyObject.getUserCreator().getDepartment().getId() != instructorObject.getDepartment().getId())
                    return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                            HttpStatus.UNAUTHORIZED);
            } else {
                if (surveyObject.getCourse().getDepartment() == null
                        || surveyObject.getCourse().getDepartment().getId() != instructorObject.getDepartment().getId())
                    return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                            HttpStatus.UNAUTHORIZED);
            }
        }

        InstructorSurveyResponse instructorSurveyResponse = new InstructorSurveyResponse();
        instructorSurveyResponse.setTrialCount(surveyObject.getTrialCount());

        List<RequestQuestion> requestQuestions = new ArrayList<>();

        List<Question> questions = questionRepository.findQuestionsBySurveyId(surveyId);
        Collections.sort(questions, QuestionComparator.getInstance());
        for (Question question: questions) {
            RequestQuestion requestQuestion = new RequestQuestion();
            requestQuestion.setId(question.getId());
            requestQuestion.setIsMultipleChoice(question.getMultipleChoice());
            requestQuestion.setQuestion(question.getContent());

            if (requestQuestion.getIsMultipleChoice()) {
                List<RequestMultipleChoice> requestMultipleChoices = new ArrayList<>();
                for (MultipleChoice multipleChoice: question.getMultipleChoices()) {
                    RequestMultipleChoice requestMultipleChoice = new RequestMultipleChoice();
                    requestMultipleChoice.setContent(multipleChoice.getContent());
                    requestMultipleChoice.setId(multipleChoice.getId());
                    requestMultipleChoices.add(requestMultipleChoice);
                }
                Collections.sort(requestMultipleChoices);
                requestQuestion.setMultipleChoices(requestMultipleChoices);
            }

            requestQuestions.add(requestQuestion);
        }

        instructorSurveyResponse.setQuestions(requestQuestions);

        return new ResponseEntity<>(instructorSurveyResponse, HttpStatus.OK);
    }

    public ResponseEntity<Object> getSubmitterStudents(Long surveyId, String user) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User departmentManager = userRepository.findByLoginID(user);
        if (departmentManager == null)
            return new ResponseEntity<>(new MessageResponse("No such user!"), HttpStatus.BAD_REQUEST);

        if (departmentManager.getUserType() != UserType.DEPARTMENT_MANAGER.getValue())
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"), HttpStatus.UNAUTHORIZED);

        Survey survey = surveyRepository.findById(surveyId).orElse(null);
        if (survey == null)
            return new ResponseEntity<>(new MessageResponse("No such survey!"), HttpStatus.BAD_REQUEST);

        List<StudentSurveyFill> studentSurveyFills = studentSurveyFillRepository
                .getStudentSurveyFillsBySurveyId(surveyId);

        List<UserResponse> userResponses = new ArrayList<>();

        for (StudentSurveyFill studentSurveyFill: studentSurveyFills) {
            User student = studentSurveyFill.getUser();
            if (student == null)
                continue;

            UserResponse userResponse = new UserResponse();
            userResponse.buildFrom(student);
            userResponses.add(userResponse);
        }

        return new ResponseEntity<>(userResponses, HttpStatus.OK);

    }

    public ResponseEntity<Object> getStatistics(Long surveyId) {

        Survey survey = surveyRepository.findById(surveyId).orElse(null);
        if (survey == null)
            return new ResponseEntity<>(new MessageResponse("No such survey!"), HttpStatus.BAD_REQUEST);

        List<StudentSurveyFill> studentSurveyFills = studentSurveyFillRepository
                .getStudentSurveyFillsBySurveyId(surveyId);

        List<User> submitters = new ArrayList<>();

        for (StudentSurveyFill studentSurveyFill: studentSurveyFills) {
            User student = studentSurveyFill.getUser();
            if (student == null) continue;
            submitters.add(student);
        }

        Set<Question> questionSet = survey.getQuestionSet();
        List<Question> questions = new ArrayList<>();
        questions.addAll(questionSet);
        Collections.sort(questions, QuestionComparator.getInstance());

        List<SurveyStatisticsResponse> surveyStatisticsResponses = new ArrayList<>();

        for (Question question: questions) {
            SurveyStatisticsResponse surveyStatisticsResponse = new SurveyStatisticsResponse();
            surveyStatisticsResponse.setQuestion(question.getContent());
            if (question.getMultipleChoice()) {
                surveyStatisticsResponse.setIsMultipleChoice(true);
                surveyStatisticsResponse.setBase64(getBase64BarChartImageForQuestion(submitters, question));
            } else {
                surveyStatisticsResponse.setIsMultipleChoice(false);
                List<String> answers = new ArrayList<>();
                for(User student: submitters) {
                    StudentAnswer studentAnswer = studentAnswerRepository
                            .findStudentAnswerByStudentAndQuestionIds(student.getId(), question.getId());
                    if (StringUtils.hasText(studentAnswer.getContent()))
                        answers.add(studentAnswer.getContent());
                }
                surveyStatisticsResponse.setAnswers(answers);
            }
            surveyStatisticsResponses.add(surveyStatisticsResponse);
        }
        return new ResponseEntity<>(surveyStatisticsResponses, HttpStatus.OK);
    }

    private String getBase64BarChartImageForQuestion(List<User> submitters, Question question) {
        Map<Long, String> multipleChoiceContentMap = new HashMap<>();
        Map<Long, Integer> multipleChoiceCountMap = new HashMap<>();


        // initialize maps
        for (MultipleChoice multipleChoice: question.getMultipleChoices()) {
            if (!multipleChoiceCountMap.containsKey(multipleChoice.getId())) {
                multipleChoiceCountMap.put(multipleChoice.getId(), 0);
            }

            if (!multipleChoiceContentMap.containsKey(multipleChoice.getId())) {
                multipleChoiceContentMap.put(multipleChoice.getId(), multipleChoice.getContent());
            }
        }

        List<Long> multipleChoiceIds = new ArrayList<>();
        multipleChoiceIds.addAll(multipleChoiceCountMap.keySet());
        Collections.sort(multipleChoiceIds);

        // count answers
        for (User student: submitters) {
            StudentAnswer studentAnswer = studentAnswerRepository
                    .findStudentAnswerByStudentAndQuestionIds(student.getId(), question.getId());
            if (studentAnswer == null)
                continue;

            if (studentAnswer.getMultipleChoice() == null)
                continue;

            Integer count = multipleChoiceCountMap.get(studentAnswer.getMultipleChoice().getId());
            multipleChoiceCountMap.put(studentAnswer.getMultipleChoice().getId(), count + 1);
        }

        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (Long multipleChoiceId: multipleChoiceIds) {
            dataset.addValue(multipleChoiceCountMap.get(multipleChoiceId),
                    multipleChoiceContentMap.get(multipleChoiceId), "Answers");
        }

        JFreeChart chart = ChartFactory.createBarChart("Multiple Choice Question Statistics",
                "Options", "Number of Students", dataset,
                PlotOrientation.VERTICAL, true, true, false);
        BufferedImage image = chart.createBufferedImage(300, 400);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {
            ImageIO.write(image, "png", stream);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        String base64 = new String(Base64.getEncoder().encode(stream.toByteArray()));
        return base64;

    }

    public ResponseEntity<Object> checkIfEnoughStudentsExist(Long surveyId) {

        Survey survey = surveyRepository.findById(surveyId).orElse(null);
        if (survey == null) {
            return new ResponseEntity<>(new MessageResponse("No such survey!"), HttpStatus.BAD_REQUEST);
        }
        SurveyResponseInstructor surveyResponseInstructor = new SurveyResponseInstructor();
        surveyResponseInstructor.buildFrom(survey);
        int numberOfRelatedStudents = getNumberOfStudentsRelatedToSurvey(survey);

        int limit = 0;

        try {
            if (surveyResponseInstructor.getSurveyType().equals("Instructor")) {
                limit = INSTRUCTOR_SURVEY_MINIMUM_STUDENT_COUNT;
            } else {
                if (survey.getCourse() == null) {
                    limit = 0;
                } else {
                    if (survey.getCourse().getUndergraduate()) limit = UNDERGRADUATE_COURSE_SURVEY_MINIMUM_STUDENT_COUNT;
                    else limit = GRADUATE_COURSE_SURVEY_MINIMUM_STUDENT_COUNT;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (numberOfRelatedStudents < limit) {
            return new ResponseEntity<>(
                    new MessageResponse("The number of students related to this survey is not enough!"),
                    HttpStatus.BAD_REQUEST);
        }

        return null;
    }

    public ResponseEntity<Object> extendSurvey(Long surveyId, String instructorLoginId, SurveyRequest surveyRequest) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(instructorLoginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User instructor = userRepository.findByLoginID(instructorLoginId);
        if (instructor == null) {
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);
        }

        Survey survey = surveyRepository.findById(surveyId).orElse(null);
        if (survey == null) {
            return new ResponseEntity<>(new MessageResponse("No such survey!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (survey.getUserCreator().getId() != instructor.getId()) {
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);
        }

        if (surveyRequest.getDeadline() == null) {
            return new ResponseEntity<>(new MessageResponse("No deadline is specified!"),
                    HttpStatus.BAD_REQUEST);
        }

        Schedule schedule = scheduleRepository.findById(1L).orElse(null);
        if (schedule == null) {
            return new ResponseEntity<>(new MessageResponse("Academic schedule is not set!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (schedule.getEndDate() == null) {
            return new ResponseEntity<>(new MessageResponse("End date of semester is not set!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (surveyRequest.getDeadline().isAfter(schedule.getEndDate())) {
            return new ResponseEntity<>(new MessageResponse("New deadline cannot be after the semester ends!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (survey.getDeadline() != null && surveyRequest.getDeadline().isBefore(survey.getDeadline())) {
            return new ResponseEntity<>(
                    new MessageResponse("You cannot set the deadline to a date before the current deadline!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (surveyRequest.getDeadline().isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>(new MessageResponse("You cannot set the deadline to a date before now!"),
                    HttpStatus.BAD_REQUEST);
        }

        survey.setDeadline(surveyRequest.getDeadline());
        surveyRepository.save(survey);
        return new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);

    }

    public ResponseEntity<Object> removeSurveyOfInstructor(Long surveyId, String user) {

        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(user))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User instructor = userRepository.findByLoginID(user);
        if (instructor == null) {
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);
        }

        Survey survey = surveyRepository.findById(surveyId).orElse(null);
        if (survey == null) {
            return new ResponseEntity<>(new MessageResponse("No such survey!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (survey.getUserCreator().getId() != instructor.getId()) {
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);
        }

        if (survey.getCreationDatetime() != null) {
            return new ResponseEntity<>(new MessageResponse("A submitted survey cannot be removed!"),
                    HttpStatus.BAD_REQUEST);
        }

        surveyRepository.delete(survey);
        return new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);

    }

    private boolean checkIfFirstQuestionsAreAnswered(Survey survey, User student, int numberOfQuestions) {
        ResponseEntity<Object> responseEntity = getQuestionsOfSurveyWithAnswers(
                survey.getId(), student.getLoginID(), null, true);
        SurveyQuestionsResponse surveyQuestionsResponse = (SurveyQuestionsResponse) responseEntity.getBody();
        List<QuestionWithAnswer> questionWithAnswerList = surveyQuestionsResponse.getQuestions();
        if (questionWithAnswerList.size() < numberOfQuestions)
            return false;

        int counter = 1;
        boolean result = true;
        for (QuestionWithAnswer questionWithAnswer: questionWithAnswerList) {
            if (counter > numberOfQuestions)
                break;
            if (questionWithAnswer.getAnswer() == null) {
                result = false;
                break;
            }
            counter++;
        }
        return result;
    }

    public void autoSubmitStudentAnswers() {
        if (!STUDENT_ANSWER_AUTO_SUBMISSION)
            return;

//        System.out.println("Checking...");

        List<Survey> surveys = surveyRepository.findAll();

        List<Survey> targetSurveys = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (Survey survey: surveys) {
            if (survey.getCreationDatetime() != null && survey.getDeadline() != null
                    && now.isAfter(survey.getDeadline()) && survey.getId() != null) {
                targetSurveys.add(survey);
            }
        }

        for (Survey survey: targetSurveys) {
            List<StudentSurveyFill> studentSurveyFills = studentSurveyFillRepository
                    .getUnsubmittedStudentSurveyFillsBySurveyId(survey.getId());
            for (StudentSurveyFill studentSurveyFill: studentSurveyFills) {
                if (checkIfFirstQuestionsAreAnswered(survey, studentSurveyFill.getUser(), 8)) {
                    studentSurveyFill.setCompletionDatetime(survey.getDeadline());
                    studentSurveyFillRepository.save(studentSurveyFill);
//                    System.out.println("update!");
                }
            }
        }
    }

    public void sendSubmissionMailToStudent(User user, Survey surveyObj, SurveyResponse surveyResponse) {
        if (INFORM_STUDENT_ON_SUBMISSION) {
            emailService.sendSubmissionMailToStudent(user, surveyObj, surveyResponse);
        }
    }


}

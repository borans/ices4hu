package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.*;
import com.pointers.ices4hu.repositories.*;
import com.pointers.ices4hu.requests.AddInstructorRequest;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.responses.UserResponse;
import com.pointers.ices4hu.responses.UserResponseProfilePicture;
import com.pointers.ices4hu.security.password.PasswordGenerationManager;
import com.pointers.ices4hu.types.UserType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final StudentSurveyFillRepository studentSurveyFillRepository;
    private final DepartmentService departmentService;
    private final PasswordGenerationManager passwordGenerationManager;
    private final PasswordEncoder passwordEncoder;
    private final EmailServiceImpl emailService;
    private final CourseRepository courseRepository;
    private final CourseEnrolmentRequestRepository courseEnrolmentRequestRepository;
    private final EnrolmentRequestRepository enrolmentRequestRepository;
    private final HelpMessageRepository helpMessageRepository;
    private final NewsletterPostRepository newsletterPostRepository;
    private final ReevaluationRequestRepository reevaluationRequestRepository;
    private final StudentAnswerRepository studentAnswerRepository;
    private final SurveyRepository surveyRepository;
    private final ResetPasswordCodeRepository resetPasswordCodeRepository;

    public UserService(UserRepository userRepository,
                       StudentSurveyFillRepository studentSurveyFillRepository,
                       DepartmentService departmentService,
                       PasswordGenerationManager passwordGenerationManager,
                       PasswordEncoder passwordEncoder,
                       EmailServiceImpl emailService,
                       CourseRepository courseRepository,
                       CourseEnrolmentRequestRepository courseEnrolmentRequestRepository,
                       EnrolmentRequestRepository enrolmentRequestRepository,
                       HelpMessageRepository helpMessageRepository,
                       NewsletterPostRepository newsletterPostRepository,
                       ReevaluationRequestRepository reevaluationRequestRepository,
                       StudentAnswerRepository studentAnswerRepository,
                       SurveyRepository surveyRepository,
                       ResetPasswordCodeRepository resetPasswordCodeRepository) {
        this.userRepository = userRepository;
        this.studentSurveyFillRepository = studentSurveyFillRepository;
        this.departmentService = departmentService;
        this.passwordGenerationManager = passwordGenerationManager;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.courseRepository = courseRepository;
        this.courseEnrolmentRequestRepository = courseEnrolmentRequestRepository;
        this.enrolmentRequestRepository = enrolmentRequestRepository;
        this.helpMessageRepository = helpMessageRepository;
        this.newsletterPostRepository = newsletterPostRepository;
        this.reevaluationRequestRepository = reevaluationRequestRepository;
        this.studentAnswerRepository = studentAnswerRepository;
        this.surveyRepository = surveyRepository;
        this.resetPasswordCodeRepository = resetPasswordCodeRepository;
    }

    public ResponseEntity<Object> getUsers(){
        List<User> users = userRepository.findAll();

        List<UserResponse> userResponses = new ArrayList<>();
        for (User user: users) {
            if (UserType.values()[user.getUserType().intValue()] == UserType.ADMIN)
                continue;
            UserResponse userResponse = new UserResponse();
            userResponse.buildFrom(user);
            userResponses.add(userResponse);
        }
        return new ResponseEntity<>(userResponses, HttpStatus.OK);
    }

    public User createUser(User user){
        return userRepository.save(user);
    }

    public User getUser(Long userId){
        //TODO Exception eklenecek student yoksa diye
        return userRepository.findById(userId).orElse(null);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByLoginID(String loginID) {
        return userRepository.findByLoginID(loginID);
    }

    public User updateUser(Long userId, User updatedUser){
        Optional<User> user = userRepository.findById(userId);
        if(user.isPresent()) {
            User currentUser = user.get();
            currentUser.setEmail(updatedUser.getEmail());
            currentUser.setUserType(updatedUser.getUserType());
            currentUser.setName(updatedUser.getName());
            currentUser.setSurname(updatedUser.getSurname());
            currentUser.setPassword(updatedUser.getPassword());
            currentUser.setBanned(updatedUser.getBanned());
            currentUser.setProfilePhoto(updatedUser.getProfilePhoto());
            currentUser.setRegistrationDateTime(updatedUser.getRegistrationDateTime());
            userRepository.save(currentUser);
            return currentUser;
        }
        else {
            return null; //TODO buraya userın bulunmama durumu icin exception eklenecek.
        }
    }

    public ResponseEntity<Object> deleteUser(Long userId){
        User user = userRepository.findById(userId).orElse(null);
        if (user == null)
            return new ResponseEntity<>(new MessageResponse("No such user!"),
                    HttpStatus.BAD_REQUEST);

        if (user.getUserType() == UserType.ADMIN.getValue())
            return new ResponseEntity<>(new MessageResponse("Cannot remove an admin user!"),
                    HttpStatus.BAD_REQUEST);

        ResponseEntity<Object> cannotDeleteResponse = new ResponseEntity<>(
                new MessageResponse("Cannot delete a user being used in the system!"),
                HttpStatus.BAD_REQUEST);

        List<Course> courses = courseRepository.findCoursesByUserId(user.getId());
        if (courses.size() > 0)
            return cannotDeleteResponse;

        for (Course course: courseRepository.findAll()) {
            if (course.getStudents().contains(user))
                return cannotDeleteResponse;
        }

        List<CourseEnrolmentRequest> courseEnrolmentRequests = courseEnrolmentRequestRepository
                .getCourseEnrolmentRequestsByUserId(user.getId());
        if (courseEnrolmentRequests.size() > 0)
            return cannotDeleteResponse;

        List<EnrolmentRequest> enrolmentRequests = enrolmentRequestRepository
                .getEnrolmentRequestsByUserId(user.getId());
        if (enrolmentRequests.size() > 0)
            return cannotDeleteResponse;

        List<HelpMessage> helpMessages = helpMessageRepository.findHelpMessagesByUserId(user.getId());
        if (helpMessages.size() > 0)
            return cannotDeleteResponse;

        List<NewsletterPost> newsletterPosts = newsletterPostRepository.findNewsletterPostsByUserId(user.getId());
        if (newsletterPosts.size() > 0)
            return cannotDeleteResponse;

        List<ReevaluationRequest> reevaluationRequestsByRequesterId = reevaluationRequestRepository
                .findReevaluationRequestsByRequesterUserId(user.getId());
        if (reevaluationRequestsByRequesterId.size() > 0)
            return cannotDeleteResponse;

        List<ReevaluationRequest> reevaluationRequestsByEvaluatorId = reevaluationRequestRepository
                .findReevaluationRequestsByEvaluatorUserId(user.getId());
        if (reevaluationRequestsByEvaluatorId.size() > 0)
            return cannotDeleteResponse;

        List<StudentAnswer> studentAnswers = studentAnswerRepository.findStudentAnswersByUserId(user.getId());
        if (studentAnswers.size() > 0)
            return cannotDeleteResponse;

        List<StudentSurveyFill> studentSurveyFills = studentSurveyFillRepository
                .findStudentSurveyFillsByUserId(user.getId());
        if (studentSurveyFills.size() > 0)
            return cannotDeleteResponse;

        List<Survey> surveysByInstructorId = surveyRepository.findByInstructorId(user.getId());
        if (surveysByInstructorId.size() > 0)
            return cannotDeleteResponse;

        List<Survey> surveysByCreatorId = surveyRepository.findByCreatorIntegerId(user.getId());
        if (surveysByCreatorId.size() > 0)
            return cannotDeleteResponse;

        ResetPasswordCode resetPasswordCode = resetPasswordCodeRepository.findByLoginId(user.getLoginID());
        if (resetPasswordCode != null) {
            resetPasswordCodeRepository.delete(resetPasswordCode);
        }

        userRepository.delete(user);
        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);
    }

    public int getNumberOfSurveysFilledByUser(List<Survey> surveys, String loginID) {
        int count = 0;
        for (Survey survey: surveys) {
            StudentSurveyFill studentSurveyFill = studentSurveyFillRepository.
                    getStudentSurveyFillByStudentLoginIdAndSurveyId(loginID, survey.getId());

            if (studentSurveyFill == null) continue;
            if (studentSurveyFill.getCompletionDatetime() != null) count++;
        }

        return count;
    }

    public List<User> findInstructorsAtDepartment(Long departmentId) {
        return userRepository.findSpecificUsersAtDepartment(departmentId, UserType.INSTRUCTOR.getValue());
    }

    public ResponseEntity<Object> createInstructor(AddInstructorRequest addInstructorRequest) {
        String email = addInstructorRequest.getEmail();
        String name = addInstructorRequest.getName();
        String surname = addInstructorRequest.getSurname();
        String loginId = addInstructorRequest.getLoginId();
        Long departmentId = addInstructorRequest.getDepartmentId();

        if (!StringUtils.hasText(email.split("@")[0])) {
            return new ResponseEntity<>(new MessageResponse("Bad email address!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (!email.split("@")[1].equals("hacettepe.edu.tr")) {
            return new ResponseEntity<>(new MessageResponse("Only Hacettepe mails are accepted!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (getUserByLoginID(loginId) != null) {
            return new ResponseEntity<>(new MessageResponse("Login Id already exists!"),
                    HttpStatus.BAD_REQUEST);
        }

        Department department = departmentService.getDepartmentByDepartmentId(departmentId);
        if (department == null) {
            return new ResponseEntity<>(new MessageResponse("No such department!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(name)) {
            return new ResponseEntity<>(new MessageResponse("Name cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(surname)) {
            return new ResponseEntity<>(new MessageResponse("Surname cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        createUser(loginId, name, surname, email,
                UserType.INSTRUCTOR.getValue(), department, null);

        return new ResponseEntity<>(new MessageResponse("User has successfully been created."),
                HttpStatus.CREATED);
    }

    public void createUser(String loginId, String name, String surname, String email,
                            Byte userType,
                            Department department,
                            Byte degree) {
        User user = new User();
        user.setLoginID(loginId);
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setUserType(userType);
        user.setBanned(false);
        user.setRegistrationDateTime(LocalDateTime.now());
        user.setDepartment(department);
        user.setDegree(degree);

        String password = passwordGenerationManager.generatePassword();
        user.setPassword(passwordEncoder.encode(password));

        createUser(user);
        emailService.sendAccountCreationMail(email, name, loginId, password);
    }

    public ResponseEntity<Object> getUserInformation(String loginId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(loginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User user = userRepository.findByLoginID(loginId);

        if (user == null)
            return new ResponseEntity<>(new MessageResponse("No such user!"), HttpStatus.BAD_REQUEST);

        UserResponseProfilePicture userResponseProfilePicture = new UserResponseProfilePicture();
        userResponseProfilePicture.buildFrom(user);

        return new ResponseEntity<>(userResponseProfilePicture, HttpStatus.OK);

    }

    public ResponseEntity<Object> updateUserInformation(String loginId,
                                                        UserResponseProfilePicture userResponseProfilePicture) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!username.equals(loginId))
            return new ResponseEntity<>(new MessageResponse("Unauthorized access!"),
                    HttpStatus.UNAUTHORIZED);

        User user = userRepository.findByLoginID(loginId);

        if (user == null)
            return new ResponseEntity<>(new MessageResponse("No such user!"), HttpStatus.BAD_REQUEST);

        if (!StringUtils.hasText(userResponseProfilePicture.getName()))
            return new ResponseEntity<>(new MessageResponse("Name field cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        user.setName(userResponseProfilePicture.getName());

        if (!StringUtils.hasText(userResponseProfilePicture.getSurname()))
            return new ResponseEntity<>(new MessageResponse("Surname field cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        user.setSurname(userResponseProfilePicture.getSurname());

        user.setProfilePhoto(userResponseProfilePicture.getBase64());

        userRepository.save(user);

        return new ResponseEntity<>(new MessageResponse("Operation successful!"), HttpStatus.OK);

    }

    public ResponseEntity<Object> banUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(new MessageResponse("No such user!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (user.getBanned() == true) {
            return new ResponseEntity<>(new MessageResponse("The user is already banned!"),
                    HttpStatus.BAD_REQUEST);
        }

        user.setBanned(true);
        userRepository.save(user);

        return new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);
    }

    public ResponseEntity<Object> removeBanOfUser(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return new ResponseEntity<>(new MessageResponse("No such user!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (user.getBanned() == false) {
            return new ResponseEntity<>(new MessageResponse("The user is already not banned!"),
                    HttpStatus.BAD_REQUEST);
        }

        user.setBanned(false);
        userRepository.save(user);

        return new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);
    }
}

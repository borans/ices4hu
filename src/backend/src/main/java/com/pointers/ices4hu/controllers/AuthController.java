package com.pointers.ices4hu.controllers;

import com.pointers.ices4hu.models.Department;
import com.pointers.ices4hu.models.EnrolmentRequest;
import com.pointers.ices4hu.models.User;
import com.pointers.ices4hu.requests.SignUpRequest;
import com.pointers.ices4hu.requests.UserRequest;
import com.pointers.ices4hu.responses.LoginResponse;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.security.jwt.JwtTokenGenerator;
import com.pointers.ices4hu.security.password.PasswordGenerationManager;
import com.pointers.ices4hu.services.*;
import com.pointers.ices4hu.types.RequestStatusType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cglib.core.Local;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final PasswordGenerationManager passwordGenerationManager;
    private final DepartmentService departmentService;
    private final ResetPasswordService resetPasswordService;
    private final UserEnrolmentService userEnrolmentService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenGenerator jwtTokenGenerator,
                          UserService userService,
                          PasswordEncoder passwordEncoder,
                          EmailService emailService,
                          PasswordGenerationManager passwordGenerationManager,
                          DepartmentService departmentService, ResetPasswordService resetPasswordService,
                          UserEnrolmentService userEnrolmentService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenGenerator = jwtTokenGenerator;
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.passwordGenerationManager = passwordGenerationManager;
        this.departmentService = departmentService;
        this.resetPasswordService = resetPasswordService;
        this.userEnrolmentService = userEnrolmentService;
    }

    @Value("${ices4hu.security.token_prefix}")
    private String TOKEN_PREFIX;

    @PostMapping("/login")
    public LoginResponse login(@RequestBody UserRequest loginRequest) {
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                loginRequest.getLoginID(),
                loginRequest.getPassword());
        Authentication authentication = authenticationManager.authenticate(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwtToken = jwtTokenGenerator.generateJwtToken(authentication);
        jwtToken = TOKEN_PREFIX + " " + jwtToken;

        User user = userService.getUserByLoginID(loginRequest.getLoginID());

        return new LoginResponse(jwtToken, user.getId(), user.getUserType());
    }

    @PostMapping("/signup")
    public ResponseEntity<MessageResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        if (!StringUtils.hasText(signUpRequest.getEmail()))
            return new ResponseEntity<>(new MessageResponse("Email cannot be empty!"),
                    HttpStatus.BAD_REQUEST);

        String email = signUpRequest.getEmail();

        if (!email.split("@")[1].equals("hacettepe.edu.tr"))
            return new ResponseEntity<>(new MessageResponse("Only Hacettepe mails are allowed!"),
                    HttpStatus.BAD_REQUEST);

        String loginID = email.split("@")[0];

        if (!StringUtils.hasText(loginID)) {
            return new ResponseEntity<>(new MessageResponse("Bad email address!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (userService.getUserByLoginID(loginID) != null) {
            return new ResponseEntity<>(new MessageResponse("Login Id already exists!"),
                    HttpStatus.BAD_REQUEST);
        }

        List<EnrolmentRequest> enrolmentRequests = userEnrolmentService.getPendingUserEnrolmentRequestsByEmail(email);

        if (enrolmentRequests.size() > 0) {
            return new ResponseEntity<>(new MessageResponse("Your request is already pending!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(signUpRequest.getName())) {
            return new ResponseEntity<>(new MessageResponse("Name cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (!StringUtils.hasText(signUpRequest.getSurname())) {
            return new ResponseEntity<>(new MessageResponse("Surname cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (signUpRequest.getUserType() == null) {
            return new ResponseEntity<>(new MessageResponse("User type cannot be empty!"),
                    HttpStatus.BAD_REQUEST);
        }

        if (signUpRequest.getDepartmentId() == null) {
            return new ResponseEntity<>(new MessageResponse("You must choose a department!"),
                    HttpStatus.BAD_REQUEST);
        }

        Department department = departmentService.getDepartmentByDepartmentId(signUpRequest.getDepartmentId());
        if (department == null) {
            return new ResponseEntity<>(new MessageResponse("No such department!"), HttpStatus.BAD_REQUEST);
        }

        EnrolmentRequest enrolmentRequest = new EnrolmentRequest();
        enrolmentRequest.setEmail(email);
        enrolmentRequest.setRequestDateTime(LocalDateTime.now());
        enrolmentRequest.setName(signUpRequest.getName());
        enrolmentRequest.setStatus(RequestStatusType.PENDING.getValue());
        enrolmentRequest.setSurname(signUpRequest.getSurname());
        enrolmentRequest.setUserType(signUpRequest.getUserType());
        enrolmentRequest.setDepartment(department);

        if (signUpRequest.getDegree() != null) {
            enrolmentRequest.setDegree(signUpRequest.getDegree());
        }

        userEnrolmentService.saveEnrolmentRequest(enrolmentRequest);
        return new ResponseEntity<>(new MessageResponse(
                "Your enrolment request has successfully been sent to the administration!"),
                HttpStatus.OK);

        /*User user = new User();
        user.setLoginID(loginID);
        user.setName(signUpRequest.getName());
        user.setSurname(signUpRequest.getSurname());
        user.setEmail(signUpRequest.getEmail());
        user.setUserType(signUpRequest.getUserType());
        user.setBanned(false);
        user.setRegistrationDateTime(LocalDateTime.ofInstant(new Date().toInstant(), ZoneId.systemDefault()));

        user.setDepartment(departmentService.getDepartmentByDepartmentId(signUpRequest.getDepartmentId()));

        if (signUpRequest.getDegree() != null) {
            user.setDegree(signUpRequest.getDegree());
        }*/

        /*
        String password = passwordGenerationManager.generatePassword();
        // encode the password so that it does not get saved
        // in plain text format
        user.setPassword(passwordEncoder.encode(password));
        */


        //userService.createUser(user);

        /*EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(email);
        emailDetails.setSubject("Welcome to ICES4HU, " + user.getName());
        emailDetails.setBody(String.format("Your account has been created!%nYour Login ID:%s%nYour password:%s%n",
                loginID, password));
        emailService.sendMail(emailDetails);*/

        /*return new ResponseEntity<>(new MessageResponse("User successfully signed up."),
                HttpStatus.CREATED); */
    }

    @GetMapping("/departments")
    public ResponseEntity<Object> getDepartments() {
        return departmentService.getDepartments();
    }

    @PostMapping("/reset_password/request_code")
    public ResponseEntity<Object> requestCode(@RequestParam String loginId) {
        return resetPasswordService.requestCode(loginId);
    }

    @PostMapping("/reset_password/request_new_password")
    public ResponseEntity<Object> requestNewPassword(@RequestParam String loginId,
                                                     @RequestParam String code) {
        return resetPasswordService.requestNewPassword(loginId, code);
    }

}

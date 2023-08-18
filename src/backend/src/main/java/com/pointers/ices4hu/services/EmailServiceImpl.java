package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.EmailDetails;
import com.pointers.ices4hu.models.Survey;
import com.pointers.ices4hu.models.User;
import com.pointers.ices4hu.responses.SurveyResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendMail(EmailDetails emailDetails) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            setSimpleMailMessage(sender, simpleMailMessage, emailDetails);
            javaMailSender.send(simpleMailMessage);
            return "Success";
        }

        catch (Exception e) {
            return "Error!: " + e.getMessage();
        }

    }

    @Async
    public void sendAccountCreationMail(String recipientMail, String name,
                                        String loginId, String password) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(recipientMail);
        emailDetails.setSubject("Welcome to ICES4HU, " + name);
        emailDetails.setBody(String.format("Your account has been created!%nYour Login ID:%s%nYour password:%s%n",
                loginId, password));
        sendMail(emailDetails);
    }

    private void setSimpleMailMessage(String sender,
                                      SimpleMailMessage simpleMailMessage,
                                      EmailDetails emailDetails) {
        simpleMailMessage.setTo(emailDetails.getRecipient());
        simpleMailMessage.setSubject(emailDetails.getSubject());
        simpleMailMessage.setText(emailDetails.getBody());
    }

    @Async
    public void sendResetPasswordCode(User user, String code) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(user.getEmail());

        String name = user.getName();
        if (!StringUtils.hasText(name))
            name = "Unnamed User";

        emailDetails.setSubject("ICES4HU - Reset Password Request");
        emailDetails.setBody(String.format("Dear %s,\nUse this code to get your new password:%s", name, code));
        sendMail(emailDetails);
    }

    @Async
    public void sendNewPassword(User user, String newPassword) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(user.getEmail());

        String name = user.getName();
        if (!StringUtils.hasText(name))
            name = "Unnamed User";

        emailDetails.setSubject("ICES4HU - Reset Password Request");
        emailDetails.setBody(String.format("Dear %s,\nHere is your new password:%s", name, newPassword));
        sendMail(emailDetails);

    }

    @Async
    public void sendReevaluationRequestMail(String email, Survey survey, SurveyResponse surveyResponse,
                                            boolean approved) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(email);

        String deniedOrApproved = "";
        if (approved) deniedOrApproved = "Approved";
        else deniedOrApproved = "Denied";

        emailDetails.setSubject("ICES4HU - Your Reevaluation Request Has Been " + deniedOrApproved);
        String body = String.format("Your reevaluation request on the following survey has been %s:%n" +
                        "Survey id:%s%nSurvey Type:%s%n", deniedOrApproved.toLowerCase(),
                surveyResponse.getId(), surveyResponse.getSurveyType());

        if (survey.getCreationDatetime() != null) {
            body += "Creation time: " + survey.getCreationDatetime().toString() + "\n";
        }

        if (survey.getStartingDatetime() != null) {
            body += "Starting time: " + survey.getStartingDatetime().toString() + "\n";
        }

        if (survey.getDeadline() != null) {
            body += "Deadline: " + survey.getDeadline().toString() + "\n";
        }

        if (surveyResponse.getSurveyType() != null
                && surveyResponse.getSurveyType().equals("Course")) {
            if (surveyResponse.getCourseCode() != null)
                body += "Course code: " + surveyResponse.getCourseCode() + "\n";
            if (surveyResponse.getCourseName() != null)
                body += "Course name: " + surveyResponse.getCourseName() + "\n";
        }

        emailDetails.setBody(body);
        sendMail(emailDetails);
    }

    @Async
    public void sendSubmissionMailToStudent(User student, Survey survey, SurveyResponse surveyResponse) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(student.getEmail());

        emailDetails.setSubject("ICES4HU - You Have Successfully Completed A Survey!");
        String body = String.format("Congratulations on completing the following survey!:%n" +
                        "Survey id:%s%nSurvey Type:%s%n",
                surveyResponse.getId(), surveyResponse.getSurveyType());

        if (survey.getCreationDatetime() != null) {
            body += "Creation time: " + survey.getCreationDatetime().toString() + "\n";
        }

        if (survey.getStartingDatetime() != null) {
            body += "Starting time: " + survey.getStartingDatetime().toString() + "\n";
        }

        if (survey.getDeadline() != null) {
            body += "Deadline: " + survey.getDeadline().toString() + "\n";
        }

        if (surveyResponse.getSurveyType() != null
                && surveyResponse.getSurveyType().equals("Course")) {
            if (surveyResponse.getCourseCode() != null)
                body += "Course code: " + surveyResponse.getCourseCode() + "\n";
            if (surveyResponse.getCourseName() != null)
                body += "Course name: " + surveyResponse.getCourseName() + "\n";
        }

        if (surveyResponse.getInstructorName() != null) {
            body += "Instructor: " + surveyResponse.getInstructorName() + "\n";
        }

        emailDetails.setBody(body);
        sendMail(emailDetails);
    }

    @Async
    public void sendSubmissionMailToInstructor(String email, Survey survey, SurveyResponse surveyResponse) {
        EmailDetails emailDetails = new EmailDetails();
        emailDetails.setRecipient(email);

        emailDetails.setSubject("ICES4HU - You Have Successfully Submitted a New Survey");
        String body = String.format("You have successfully submitted the following survey to the system:%n" +
                        "Survey id:%s%nSurvey Type:%s%n",
                surveyResponse.getId(), surveyResponse.getSurveyType());

        if (survey.getCreationDatetime() != null) {
            body += "Creation time: " + survey.getCreationDatetime().toString() + "\n";
        }

        if (survey.getStartingDatetime() != null) {
            body += "Starting time: " + survey.getStartingDatetime().toString() + "\n";
        }

        if (survey.getDeadline() != null) {
            body += "Deadline: " + survey.getDeadline().toString() + "\n";
        }

        if (surveyResponse.getSurveyType() != null
                && surveyResponse.getSurveyType().equals("Course")) {
            if (surveyResponse.getCourseCode() != null)
                body += "Course code: " + surveyResponse.getCourseCode() + "\n";
            if (surveyResponse.getCourseName() != null)
                body += "Course name: " + surveyResponse.getCourseName() + "\n";
        }

        emailDetails.setBody(body);
        sendMail(emailDetails);
    }
}

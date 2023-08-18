package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.ResetPasswordCode;
import com.pointers.ices4hu.models.User;
import com.pointers.ices4hu.repositories.ResetPasswordCodeRepository;
import com.pointers.ices4hu.repositories.UserRepository;
import com.pointers.ices4hu.responses.MessageResponse;
import com.pointers.ices4hu.security.password.PasswordGenerationManager;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class ResetPasswordService {

    private final ResetPasswordCodeRepository resetPasswordCodeRepository;
    private final PasswordGenerationManager passwordGenerationManager;
    private final UserRepository userRepository;
    private final EmailServiceImpl emailService;
    private final PasswordEncoder passwordEncoder;

    public ResetPasswordService(ResetPasswordCodeRepository resetPasswordCodeRepository,
                                PasswordGenerationManager passwordGenerationManager,
                                UserRepository userRepository,
                                EmailServiceImpl emailService,
                                PasswordEncoder passwordEncoder) {
        this.resetPasswordCodeRepository = resetPasswordCodeRepository;
        this.passwordGenerationManager = passwordGenerationManager;
        this.userRepository = userRepository;
        this.emailService = emailService;
        this.passwordEncoder = passwordEncoder;
    }

    public ResponseEntity<Object> requestCode(String loginId) {

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);

        String code = passwordGenerationManager.generatePassword();
        User user = userRepository.findByLoginID(loginId);

        if (user == null)
            return responseEntity;

        if (!StringUtils.hasText(user.getEmail()))
            return responseEntity;

        ResetPasswordCode resetPasswordCode = resetPasswordCodeRepository.findByLoginId(loginId);
        if (resetPasswordCode == null) {
            resetPasswordCode = new ResetPasswordCode();
            resetPasswordCode.setLoginId(loginId);
        }

        resetPasswordCode.setCode(code);
        resetPasswordCodeRepository.save(resetPasswordCode);

        emailService.sendResetPasswordCode(user, code);

        return responseEntity;
    }

    public ResponseEntity<Object> requestNewPassword(String loginId, String code) {

        ResponseEntity<Object> responseEntity = new ResponseEntity<>(new MessageResponse("Operation successful!"),
                HttpStatus.OK);

        ResponseEntity<Object> noMatch = new ResponseEntity<>(new MessageResponse("The code does not match!"),
                HttpStatus.BAD_REQUEST);

        ResetPasswordCode resetPasswordCode = resetPasswordCodeRepository.findByLoginId(loginId);
        if (resetPasswordCode == null)
            return noMatch;

        String realCode = resetPasswordCode.getCode();
        if (realCode == null)
            return noMatch;

        if (!realCode.equals(code))
            return noMatch;

        User user = userRepository.findByLoginID(loginId);
        if (user == null)
            return noMatch;

        resetPasswordCode.setCode(null);
        resetPasswordCodeRepository.save(resetPasswordCode);

        String newPassword = passwordGenerationManager.generatePassword();
        emailService.sendNewPassword(user, newPassword);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return responseEntity;
    }
}

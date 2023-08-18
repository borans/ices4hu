package com.pointers.ices4hu.services;

import com.pointers.ices4hu.models.EmailDetails;

public interface EmailService {
    String sendMail(EmailDetails emailDetails);
}

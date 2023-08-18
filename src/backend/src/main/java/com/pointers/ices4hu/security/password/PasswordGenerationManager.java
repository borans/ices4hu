package com.pointers.ices4hu.security.password;

import org.passay.CharacterData;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class PasswordGenerationManager {

    private final PasswordGenerator passwordGenerator;
    private final Random random;

    @Value("${ices4hu.security.password.generator.minimum_length}")
    private int MINIMUM_LENGTH;

    @Value("${ices4hu.security.password.generator.maximum_length}")
    private int MAXIMUM_LENGTH;

    public PasswordGenerationManager() {
        this.passwordGenerator = new PasswordGenerator();
        this.random = new Random();
    }

    public String generatePassword() {
        int length = random.nextInt(MAXIMUM_LENGTH - MINIMUM_LENGTH + 1) + MINIMUM_LENGTH;

        int lengthPerRule = length / 4;

        CharacterRule specialCharacterRule = new CharacterRule(new CharacterData() {
            @Override
            public String getErrorCode() {
                return null;
            }

            @Override
            public String getCharacters() {
                return "*-._()/";
            }
        });
        CharacterRule digitCharacterRule = new CharacterRule(EnglishCharacterData.Digit);
        CharacterRule upperCaseCharacterRule = new CharacterRule(EnglishCharacterData.UpperCase);
        CharacterRule lowerCaseCharacterRule = new CharacterRule(EnglishCharacterData.LowerCase);

        specialCharacterRule.setNumberOfCharacters(lengthPerRule);
        digitCharacterRule.setNumberOfCharacters(lengthPerRule);
        upperCaseCharacterRule.setNumberOfCharacters(lengthPerRule);
        lowerCaseCharacterRule.setNumberOfCharacters(length - (lengthPerRule * 3));

        return passwordGenerator.generatePassword(length,
                specialCharacterRule,
                digitCharacterRule,
                upperCaseCharacterRule,
                lowerCaseCharacterRule);
    }

}

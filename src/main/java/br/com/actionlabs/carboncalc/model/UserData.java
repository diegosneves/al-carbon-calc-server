package br.com.actionlabs.carboncalc.model;

import br.com.actionlabs.carboncalc.exceptions.UserDataCreateException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Slf4j
public class UserData {

    private static final String USERNAME_REQUIRED_MESSAGE = "Username is required";
    private static final String EMAIL_REQUIRED_MESSAGE = "Email is required";
    private static final String FEDERATIVE_UNIT_MISSING_MESSAGE = "Federative unit is required";
    private static final String PHONE_NUMBER_REQUIRED_MESSAGE = "Phone number is required";

    private String name;
    private String email;
    private String uf;
    private String phoneNumber;

    public static UserData newUser(final String name, final String email, final String uf, final String phoneNumber) {
        return validate(new UserData(name, email, uf, phoneNumber));
    }


    private static UserData validate(final UserData userData) {
        validateNonEmptyString(userData.getName(), USERNAME_REQUIRED_MESSAGE);
        validateNonEmptyString(userData.getEmail(), EMAIL_REQUIRED_MESSAGE);
        validateNonEmptyString(userData.getUf(), FEDERATIVE_UNIT_MISSING_MESSAGE);
        validateNonEmptyString(userData.getPhoneNumber(), PHONE_NUMBER_REQUIRED_MESSAGE);
        log.info("UserData created: {} at: {}", userData, Instant.now().toString());
        return userData;
    }

    private static void validateNonEmptyString(String param, String errorMessage) {
        if (param == null || param.isBlank()) {
            log.error(errorMessage);
            throw new UserDataCreateException(errorMessage);
        }
    }

}

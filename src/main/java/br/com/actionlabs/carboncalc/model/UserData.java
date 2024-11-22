package br.com.actionlabs.carboncalc.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
        return userData;
    }

    private static void validateNonEmptyString(String param, String errorMessage) {
        if (param == null || param.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

}

package br.com.actionlabs.carboncalc.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserData {

    @NotNull(message = "Username is required")
    private String name;
    @NotNull(message = "User e-mail is required")
    private String email;
    @NotNull(message = "Federative unit is required")
    private String uf;
    @NotNull(message = "Phone number is required")
    private String phoneNumber;

    

}

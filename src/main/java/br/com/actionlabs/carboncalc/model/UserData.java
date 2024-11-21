package br.com.actionlabs.carboncalc.model;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserData {

    @NotNull
    private String name;
    @NotNull
    private String email;
    @NotNull
    private String uf;
    @NotNull
    private String phoneNumber;

    

}

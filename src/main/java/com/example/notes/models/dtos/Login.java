package com.example.notes.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class Login {

    @NotEmpty
    @Email(message = "Email is invalid!")
    private String email;

    @Size(min = 8, max = 32, message = "Password should be between 8 and 32 symbols!")
    @NotNull(message = "Password can not be empty!")
    private String password;
}

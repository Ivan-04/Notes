package com.example.notes.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class Register extends Login{

    @Size(min = 8, max = 32, message = "Password should be between 8 and 32 symbols!")
    @NotNull(message = "Password can not be empty!")
    private String passwordConfirm;

    @NotNull(message = "First name can not be empty!")
    @Size(min = 4, max = 32, message = "First name should be between 4 and 32 symbols!")
    private String firstName;

    @NotNull(message = "Last name can not be empty!")
    @Size(min = 4, max = 32, message = "Last name should be between 4 and 32 symbols!")
    private String lastName;
}

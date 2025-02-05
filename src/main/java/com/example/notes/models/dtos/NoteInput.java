package com.example.notes.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class NoteInput {

    @NotNull(message = "Title cannot be empty.")
    @Size(min = 4 , max = 64, message = "Title must be between 4 and 64 symbols.")
    private String title;

    @NotNull(message = "Content cannot be empty.")
    @Size(min = 4, max = 8192, message = "Content must be between 4 and 8192 symbols.")
    private String content;

}

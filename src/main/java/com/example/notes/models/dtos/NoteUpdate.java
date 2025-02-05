package com.example.notes.models.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteUpdate {


    @NotNull(message = "Title can not be empty!")
    @Size(min = 4, max = 32, message = "Title should be between 4 and 64 symbols!")
    private String title;

    @NotNull(message = "Content can not be empty!")
    @Size(min = 4, max = 8192, message = "Content should be between 4 and 8192 symbols!")
    private String content;

}

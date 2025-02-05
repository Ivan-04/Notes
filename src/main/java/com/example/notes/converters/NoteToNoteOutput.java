package com.example.notes.converters;


import com.example.notes.models.Note;
import com.example.notes.models.User;
import com.example.notes.models.dtos.NoteOutput;
import com.example.notes.models.dtos.UserOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NoteToNoteOutput implements Converter<Note, NoteOutput> {

    @Override
    public NoteOutput convert(Note note) {
        return NoteOutput.builder()
                .title(note.getTitle())
                .content(note.getContent())
                .build();
    }

}

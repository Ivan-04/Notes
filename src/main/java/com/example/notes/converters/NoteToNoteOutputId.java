package com.example.notes.converters;

import com.example.notes.models.Note;
import com.example.notes.models.User;
import com.example.notes.models.dtos.NoteOutputId;
import com.example.notes.models.dtos.UserOutputId;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class NoteToNoteOutputId implements Converter<Note, NoteOutputId> {

    @Override
    public NoteOutputId convert(Note note) {
        return NoteOutputId.builder()
                .nateId(note.getId()).build();
    }

}

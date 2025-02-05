package com.example.notes.services.contracts;

import com.example.notes.models.Note;
import com.example.notes.models.User;
import com.example.notes.models.dtos.NoteInput;
import com.example.notes.models.dtos.NoteOutput;
import com.example.notes.models.dtos.NoteOutputId;
import com.example.notes.models.dtos.NoteUpdate;

import java.util.List;

public interface NoteService {
    NoteOutput getNoteById(int id);

    Note getNoteEntityById(int id);

    NoteOutput findNoteByTitle(String title);

    List<NoteOutput> findAllNotesOfUser(User user);

    NoteOutputId createNote(NoteInput noteInput, User user);

    NoteOutput editNote(User user, Note note, NoteUpdate noteUpdate);

    void deleteNote(User user, int id);
}

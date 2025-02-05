package com.example.notes.services;

import com.example.notes.exceptions.DuplicateEntityException;
import com.example.notes.exceptions.EntityNotFoundException;
import com.example.notes.exceptions.UnauthorizedOperationException;
import com.example.notes.models.Note;
import com.example.notes.models.User;
import com.example.notes.models.dtos.*;
import com.example.notes.repositories.NoteRepository;
import com.example.notes.services.contracts.NoteService;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final ConversionService conversionService;


    @Override
    public NoteOutput getNoteById(int id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note", id));
        return conversionService.convert(note, NoteOutput.class);
    }

    @Override
    public Note getNoteEntityById(int id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note", id));
    }

    @Override
    public NoteOutput findNoteByTitle(String title){
        Note note = noteRepository.findByTitle(title).orElseThrow(
                () -> new EntityNotFoundException("Note", "title", title));

        return conversionService.convert(note, NoteOutput.class);
    }

    @Override
    public List<NoteOutput> findAllNotesOfUser(User user){
        List<Note> notesOfUser = noteRepository.findAllByUser(user);

        List<NoteOutput> allNotesOutputsOfUser = new ArrayList<>();

        for(Note note : notesOfUser){
            allNotesOutputsOfUser.add(conversionService.convert(note, NoteOutput.class));
        }

        return allNotesOutputsOfUser;
    }

    @Override
    public NoteOutputId createNote(NoteInput noteInput, User user){

        Note note = Note.builder()
                .title(noteInput.getTitle())
                .content(noteInput.getContent())
                .user(user)
                .build();

        if(noteRepository.existsByTitle(noteInput.getTitle())){
            throw new DuplicateEntityException("Note", "title", note.getTitle());
        }

        noteRepository.save(note);

        return conversionService.convert(note, NoteOutputId.class);
    }

    @Override
    public NoteOutput editNote(User user, Note note, NoteUpdate noteUpdate){

        if(!note.getUser().getEmail().equals(user.getEmail())){
            throw new UnauthorizedOperationException("You can edit only your notes");
        }

        note.setTitle(noteUpdate.getTitle());
        note.setContent(noteUpdate.getContent());

        noteRepository.save(note);

        return conversionService.convert(note, NoteOutput.class);
    }

    @Override
    public void deleteNote(User user, int id){
        Note noteToDelete = noteRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Note", id));

        if(!noteToDelete.getUser().getEmail().equals(user.getEmail())){
            throw new UnauthorizedOperationException("You can delete only your notes!");
        }

        noteRepository.delete(noteToDelete);
    }



}

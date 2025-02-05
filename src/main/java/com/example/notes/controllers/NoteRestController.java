package com.example.notes.controllers;

import com.example.notes.helpers.AuthenticationHelper;
import com.example.notes.models.Note;
import com.example.notes.models.User;
import com.example.notes.models.dtos.*;
import com.example.notes.services.contracts.NoteService;
import com.example.notes.services.contracts.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notes")
public class NoteRestController {


    private final NoteService noteService;
    private final AuthenticationHelper authenticationHelper;

    @GetMapping("/{id}")
    public ResponseEntity<NoteOutput> getNoteById(@PathVariable int id) {
        return ResponseEntity.ok(noteService.getNoteById(id));
    }

    @GetMapping("/title")
    public ResponseEntity<NoteOutput> getNoteByTitle(@RequestParam String title) {
        return ResponseEntity.ok(noteService.findNoteByTitle(title));
    }

    @GetMapping("/all/user")
    public ResponseEntity<List<NoteOutput>> getAllNotesOfUser(@RequestHeader HttpHeaders headers) {
        User user = authenticationHelper.tryGetUser(headers);
        return ResponseEntity.ok(noteService.findAllNotesOfUser(user));
    }

    @PostMapping
    public ResponseEntity<NoteOutputId> createNote(@Valid @RequestBody NoteInput noteInput, @RequestHeader HttpHeaders headers) {
        User user = authenticationHelper.tryGetUser(headers);
        return ResponseEntity.ok(noteService.createNote(noteInput, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NoteOutput> editNote(@PathVariable int id, @RequestHeader HttpHeaders headers, @Valid @RequestBody NoteUpdate noteUpdate) {
        User user = authenticationHelper.tryGetUser(headers);
        Note note = noteService.getNoteEntityById(id);
        return ResponseEntity.ok(noteService.editNote(user, note, noteUpdate));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deactivateNote(@RequestHeader HttpHeaders headers, @Valid @PathVariable int id) {
        User user = authenticationHelper.tryGetUser(headers);
        noteService.deleteNote(user, id);
        return ResponseEntity.ok("Note deleted successfully!");
    }

}

package com.example.notes.controllers.mvc;

import com.example.notes.exceptions.*;
import com.example.notes.helpers.AuthenticationHelper;
import com.example.notes.models.Note;
import com.example.notes.models.User;
import com.example.notes.models.dtos.NoteInput;
import com.example.notes.models.dtos.NoteOutput;
import com.example.notes.models.dtos.NoteUpdate;
import com.example.notes.services.contracts.NoteService;
import com.example.notes.services.contracts.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Controller
@RequestMapping("/notes")
public class NoteMvcController {


    private final ConversionService conversionService;
    private final NoteService noteService;
    private final AuthenticationHelper authenticationHelper;

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("isAuthenticated")
    public boolean populateIsAuthenticated(HttpSession session) {
        return session.getAttribute("currentUser") != null;
    }


    @GetMapping("/{id}")
    public String showSingleNote(@PathVariable int id, Model model, HttpSession session) {
        try {
            Note note = noteService.getNoteEntityById(id);
            model.addAttribute("note", note);
            model.addAttribute("currentUser", authenticationHelper.tryGetUser(session));
            return "NoteView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthenticationFailureException e) {
            return "AccessDeniedView";
        }
    }


    @GetMapping("/my")
    public String showUserNotes(Model model, HttpSession session) {
        try {
            User user = authenticationHelper.tryGetUser(session);
            List<Note> notes = noteService.findAllNotesEntitiesOfUser(user);
            model.addAttribute("notes", notes);
        } catch (AuthenticationFailureException e) {
            return "HomeView";
        }

        return "MyNotesView";
    }


    @GetMapping("/new")
    public String showNewNotePage(Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/Login";
        }catch (UnauthorizedOperationException e){
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }

        model.addAttribute("note", new NoteInput());
        return "CreateNoteView";
    }

    @PostMapping("/new")
    public String createNote(@Valid @ModelAttribute("note") NoteInput noteInput,
                             BindingResult bindingResult,
                             Model model,
                             HttpSession session) {

        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/Login";
        }

        if (bindingResult.hasErrors()) {
            return "CreateNoteView";
        }

        try {
            noteService.createNote(noteInput, user);
            return "redirect:/";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("title", "duplicate_post", e.getMessage());
            return "CreateNoteView";
        }
    }



    @GetMapping("/{id}/edit")
    public String showEditNotePage(@PathVariable int id, Model model, HttpSession session) {
        try {
            authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/Login";
        }

        Note note = noteService.getNoteEntityById(id);
        model.addAttribute("note", note);

        return "NoteUpdateView";
    }

    @PostMapping("/{id}/edit")
    public String changeNameOfWarehouse(@PathVariable int id, @Valid @ModelAttribute("note") NoteUpdate noteUpdate,
                                        BindingResult bindingResult, Model model, HttpSession session) {

        User user;
        try{
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/Login";
        }

        if (bindingResult.hasErrors()) {
            return "NoteUpdateView";
        }

        try {
            Note note = noteService.getNoteEntityById(id);
            noteService.editNote(user, note, noteUpdate);
            return "redirect:/notes/" + id;
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (DuplicateEntityException e) {
            bindingResult.rejectValue("name", "duplicate_post", e.getMessage());
            return "NoteUpdateView";
        } catch (AuthorizationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }catch (InvalidDataException e) {
            model.addAttribute("error", e.getMessage());
            return "NoteUpdateView";
        } catch (UnauthorizedOperationException e){
            model.addAttribute("statusCode", HttpStatus.FORBIDDEN.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        }catch (DataIntegrityViolationException e) {  // 💡 Нов блок за MySQL грешки
            model.addAttribute("error", "Въведеното име е твърде дълго! Максимум 50 символа.");
            return "NoteUpdateView";
        }
    }


    @PostMapping("/{id}/delete")
    public String deleteNote(@PathVariable int id, Model model, HttpSession session) {

        User user;
        try {
            user = authenticationHelper.tryGetUser(session);
        } catch (AuthenticationFailureException e) {
            return "redirect:/auth/login";
        }
        try {
            noteService.deleteNote(user, id);
            return "HomeView";
        } catch (EntityNotFoundException e) {
            model.addAttribute("statusCode", HttpStatus.NOT_FOUND.getReasonPhrase());
            model.addAttribute("error", e.getMessage());
            return "ErrorView";
        } catch (AuthorizationException e) {
            model.addAttribute("error", e.getMessage());
            return "AccessDeniedView";
        }
    }

}

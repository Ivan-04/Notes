package com.example.notes.repositories;

import com.example.notes.models.Note;
import com.example.notes.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Integer> {

    List<Note> findAllByUser (User user);

    Optional<Note> findById (int id);

    Optional<Note> findByTitle (String title);
}

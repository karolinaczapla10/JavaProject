package com.karolinaczapla.usermanagementsystem.repository;

import com.karolinaczapla.usermanagementsystem.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Integer> {
    List<Note> findByUser_Email(String email);  // Lepsza metoda pobierania notatek


}



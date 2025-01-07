package com.karolinaczapla.usermanagementsystem.repository;

import com.karolinaczapla.usermanagementsystem.entity.Coment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentRepository extends JpaRepository<Coment, Integer> {
    List<Coment> findByNoteId(Integer noteId);// Pobieranie komentarzy dla danej notatki
}
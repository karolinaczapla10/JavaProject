package com.karolinaczapla.usermanagementsystem.controller;

import com.karolinaczapla.usermanagementsystem.entity.Note;
import com.karolinaczapla.usermanagementsystem.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notes")
public class NoteController {

    @Autowired
    private NoteService noteService;

    @PostMapping("/create")
    public ResponseEntity<Note> createNote(
            @RequestBody String content,
            @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7); // usuwa "Bearer " z tokena
        Note note = noteService.createNote(content, jwtToken);
        return ResponseEntity.ok(note);
    }
    // Edytowanie notatki
    @PutMapping("/update/{noteId}")
    public ResponseEntity<Note> updateNote(
            @PathVariable Integer noteId,
            @RequestBody String newContent,
            @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        Note updatedNote = noteService.updateNote(noteId, newContent, jwtToken);
        return ResponseEntity.ok(updatedNote);
    }

    // Usuwanie notatki
    @DeleteMapping("/delete/{noteId}")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Integer noteId,
            @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        noteService.deleteNote(noteId, jwtToken);
        return ResponseEntity.noContent().build();
    }

    // Pobieranie wszystkich notatek użytkownika
    @GetMapping("/all")
    public ResponseEntity<List<Note>> getAllNotes(@RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7); // Remove "Bearer " from token
        List<Note> notes = noteService.getAllNotesForUser(jwtToken);
        return ResponseEntity.ok(notes);
    }




}

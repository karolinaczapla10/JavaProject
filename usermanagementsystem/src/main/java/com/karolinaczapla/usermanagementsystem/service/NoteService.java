package com.karolinaczapla.usermanagementsystem.service;

import com.karolinaczapla.usermanagementsystem.entity.Note;
import com.karolinaczapla.usermanagementsystem.entity.Users;
import com.karolinaczapla.usermanagementsystem.repository.NoteRepository;
import com.karolinaczapla.usermanagementsystem.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteService {

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public Note createNote(String content, String jwtToken) {
        String userEmail = jwtUtils.extractUsername(jwtToken);
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("Content:" + content);
        Note note = new Note();
        note.setContent(content);
        note.setUser(user);
        return noteRepository.save(note);
    }
    /**
     * Edytowanie istniejącej notatki
     */
    public Note updateNote(Integer noteId, String newContent, String jwtToken) {
        String email = jwtUtils.extractUsername(jwtToken);
        Users user = usersRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        System.out.println("Content:" + newContent);

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // Sprawdzenie właściciela notatki
        if (!note.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Not authorized to edit this note");
        }

        note.setContent(newContent);
        return noteRepository.save(note);
    }




    /**
     * Usuwanie notatki
     */
    public void deleteNote(Integer noteId, String jwtToken) {
        String userEmail = jwtUtils.extractUsername(jwtToken);
        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        if (!note.getUser().getEmail().equals(userEmail)) {
            throw new SecurityException("Unauthorized to delete this note");
        }

        noteRepository.delete(note);
    }


    // Pobieranie wszystkich notatek dla danego użytkownika
    public List<Note> getAllNotesForUser(String jwtToken) {
        String userEmail = jwtUtils.extractUsername(jwtToken);
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return noteRepository.findByUser_Email(user.getEmail());
    }

}


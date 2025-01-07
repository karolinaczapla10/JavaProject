package com.karolinaczapla.usermanagementsystem.controller;

import com.karolinaczapla.usermanagementsystem.entity.Coment;
import com.karolinaczapla.usermanagementsystem.service.ComentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class ComentController {

    @Autowired
    private ComentService commentService;

    // Dodawanie komentarza do notatki
    @PostMapping("/add/{noteId}")
    public ResponseEntity<Coment> addComment(
            @PathVariable Integer noteId,
            @RequestBody String content,
            @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7); // usuwa "Bearer " z tokena
        Coment comment = commentService.addCommentToNote(noteId, content, jwtToken);
        return ResponseEntity.ok(comment);
    }

    // Usuwanie komentarza
    @DeleteMapping("/delete/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer commentId,
            @RequestHeader("Authorization") String authHeader) {
        String jwtToken = authHeader.substring(7);
        commentService.deleteComment(commentId, jwtToken);
        return ResponseEntity.noContent().build();
    }

    // Pobieranie komentarzy pod daną notatką
    @GetMapping("/all/{noteId}")
    public ResponseEntity<List<Coment>> getComments(
            @PathVariable Integer noteId) {
        List<Coment> comments = commentService.getCommentsForNote(noteId);
        return ResponseEntity.ok(comments);
    }
}


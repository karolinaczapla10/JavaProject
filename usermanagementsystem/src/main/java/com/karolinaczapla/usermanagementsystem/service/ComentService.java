package com.karolinaczapla.usermanagementsystem.service;

import com.karolinaczapla.usermanagementsystem.entity.Coment;
import com.karolinaczapla.usermanagementsystem.entity.Note;
import com.karolinaczapla.usermanagementsystem.entity.Users;
import com.karolinaczapla.usermanagementsystem.repository.ComentRepository;
import com.karolinaczapla.usermanagementsystem.repository.NoteRepository;
import com.karolinaczapla.usermanagementsystem.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ComentService {

    @Autowired
    private ComentRepository commentRepository;

    @Autowired
    private NoteRepository noteRepository;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private JwtUtils jwtUtils;

    public Coment addCommentToNote(Integer noteId, String content, String jwtToken) {
        String userEmail = jwtUtils.extractUsername(jwtToken);
        Users user = usersRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Note note = noteRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        Coment comment = new Coment();
        comment.setContent(content);
        comment.setUser(user);
        comment.setNote(note);

        return commentRepository.save(comment);
    }

    public void deleteComment(Integer commentId, String jwtToken) {
        String userEmail = jwtUtils.extractUsername(jwtToken);
        Coment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("Not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public List<Coment> getCommentsForNote(Integer noteId) {
        return commentRepository.findByNoteId(noteId);
    }
}
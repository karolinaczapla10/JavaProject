package com.karolinaczapla.usermanagementsystem.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
@Entity

public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "Content cannot be empty")
    private String content;

    @ManyToOne(fetch = FetchType.EAGER)  // Załaduj użytkownika razem z notatką
    @JoinColumn(name = "user_id")
    private Users user;


}

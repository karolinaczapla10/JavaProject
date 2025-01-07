package com.karolinaczapla.usermanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity

public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.EAGER)  // Załaduj użytkownika razem z notatką
    @JoinColumn(name = "user_id")
    private Users user;


}

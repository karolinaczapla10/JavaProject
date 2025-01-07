package com.karolinaczapla.usermanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Data;
@Data
@Entity
public class Coment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String content;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;

    @ManyToOne
    @JoinColumn(name = "note_id")
    private Note note;


}
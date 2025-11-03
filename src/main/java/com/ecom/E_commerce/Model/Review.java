package com.ecom.E_commerce.Model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private String sentiment;

    @ManyToOne
    @JsonIgnore
    private Item item;

    @ManyToOne
    @JsonIgnore
    private User user;

    @Column(nullable = false)
    private String username;

    private Date createdAt = new Date();
}

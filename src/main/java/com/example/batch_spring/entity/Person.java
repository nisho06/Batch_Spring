package com.example.batch_spring.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table (name="PERSON")
// Getters and setters defined.
@Data
@NoArgsConstructor
public class Person {
    @SequenceGenerator(
            name="person_sequence",
            sequenceName = "person_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "person_sequence"
    )
    @Id
    @Column(name="PERSON_ID")
    private Integer id;

    @Column(name = "code")
    private String code;

    @Column(name = "country")
    private String country;

    @Column(name = "name")
    private String name;

    @Column(name = "sport")
    private String sport;
}

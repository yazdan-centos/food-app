package com.mapnaom.foodapp.models;

import jakarta.persistence.*;
import lombok.*;


@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Personnel extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column
    private String password;

    @Column(nullable = false)
    private String persCode;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    public Personnel(Long id) {
        this.id = id;
    }

    public Personnel(String username, String password, String persCode, String firstName, String lastName) {
        this.username = username;
        this.password = password;
        this.persCode = persCode;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}


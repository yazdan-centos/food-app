package com.mapnaom.foodapp.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CostCenter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Long id;
    private String name;
    @OneToMany(fetch = FetchType.EAGER, mappedBy = "costCenter")
    private List<Guest> guests;

    public CostCenter(String name) {
        this.name = name;
    }
}

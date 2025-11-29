package com.mapnaom.foodapp.models;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "dish")
public class Dish extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = true)
    private Integer price = 0;

    public Dish(String name) {
        this.name = name;
    }

    public Dish(Long id) {
        this.id = id;
    }

    // 🆕 New constructor for name + price
    public Dish(String name, Integer price) {
        this.name = name;
        this.price = price;
    }
}

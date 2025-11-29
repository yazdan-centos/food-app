package com.mapnaom.foodapp.models;


import jakarta.persistence.*;
import lombok.*;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DailyMealDish {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    private DailyMeal dailyMeal;
    @ManyToOne(cascade = {CascadeType.MERGE})
    @JoinColumn(name = "dish_id")
    private Dish dish;

    public DailyMealDish(DailyMeal dailyMeal, Dish dish) {
        this.dailyMeal = dailyMeal;
        this.dish = dish;
    }
}

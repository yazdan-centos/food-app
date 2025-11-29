package com.mapnaom.foodapp.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Builder
@Getter
@Setter
@Entity
@Table(name = "guest")
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class Guest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "first_name")
    @JdbcTypeCode(SqlTypes.CHAR)
    private String firstName;

    @Column(name = "last_name")
    @JdbcTypeCode(SqlTypes.CHAR)
    private String lastName;

    @ManyToOne
    @JoinColumn(name = "cost_center_id")
    private CostCenter costCenter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "daily_meal_dish_id")
    private DailyMealDish dailyMealDish;

}
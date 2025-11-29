package com.mapnaom.foodapp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "cost_shares", schema = "public")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString(onlyExplicitlyIncluded = true)
public class CostShare implements Serializable {

    @Serial
    private static final long serialVersionUID = 9091628922889320000L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "employee_portion", precision = 10, scale = 2)
    private BigDecimal employeePortion;

    @Column(name = "employee_share_percentage", precision = 5, scale = 2)
    private BigDecimal employeeSharePercentage;

    @Column(name = "employer_portion", precision = 10, scale = 2)
    private BigDecimal employerPortion;

    @NotNull
    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "total_cost", precision = 10, scale = 2)
    private BigDecimal totalCost;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id", nullable = false)
    private Reservation reservation;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CostShare costShare = (CostShare) o;
        return Objects.equals(id, costShare.id) && Objects.equals(employeePortion, costShare.employeePortion) && Objects.equals(employeeSharePercentage, costShare.employeeSharePercentage) && Objects.equals(employerPortion, costShare.employerPortion) && Objects.equals(quantity, costShare.quantity) && Objects.equals(totalCost, costShare.totalCost) && Objects.equals(reservation, costShare.reservation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, employeePortion, employeeSharePercentage, employerPortion, quantity, totalCost, reservation);
    }
}
package com.mapnaom.foodapp.models;

import com.mapnaom.foodapp.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reservation")
public class Reservation extends Auditable<String> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "personnel_id", nullable = false)
    private Personnel personnel;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "daily_meal_id", nullable = false)
    private DailyMeal dailyMeal;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "daily_meal_dish_id", nullable = false)
    private DailyMealDish dailyMealDish;

    @Column(name = "reservation_time", nullable = false)
    private LocalDateTime reservationTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "reservation_status", nullable = false)
    private ReservationStatus reservationStatus = ReservationStatus.ACTIVE;

    @OneToOne(mappedBy = "reservation", cascade = CascadeType.ALL, orphanRemoval = true)
    private CostShare costShare;

    public void setCostShare(CostShare costShare) {
        if (costShare != null) {
            costShare.setReservation(this);
        }
        this.costShare = costShare;
    }

    public void deliver() {
        this.reservationStatus = ReservationStatus.DELIVERED;
    }
    
    @PrePersist
    public void prePersist() {
        this.reservationTime = LocalDateTime.now();
    }

    public void cancel() {
        this.reservationStatus = ReservationStatus.CANCELLED;
    }

    public void active() {
        this.reservationStatus = ReservationStatus.ACTIVE;
    }

    public void expired() {
        this.reservationStatus = ReservationStatus.EXPIRED;
    }
}
//@Getter
//public enum ReservationStatus {
//    PENDING("در انتظار"), // The reservation exists in the system but no action has been taken yet
//    ACTIVE("فعال"), // The reservation date has arrived (it’s the day of the meal)
//    DELIVERED("تحویل شده"), // the meal has been physically delivered to the employee
//    EXPIRED("منقضی شده"), CANCELLED("لغو شده");
//    private final String persianCaption;
//
//    ReservationStatus(String persianCaption) {
//        this.persianCaption = persianCaption;
//    }
//
//}

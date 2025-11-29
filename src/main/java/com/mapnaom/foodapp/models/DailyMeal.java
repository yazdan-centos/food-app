package com.mapnaom.foodapp.models;


import com.github.eloyzone.jalalicalendar.JalaliDate;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.mapnaom.foodapp.utils.JalaliUtils.toJalali;

@Accessors(chain = true)
@Builder
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

public class DailyMeal extends Auditable<String> {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "meal_date", nullable = false)
    private LocalDate date;


    @OneToMany(mappedBy = "dailyMeal",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.EAGER)
    private List<DailyMealDish> dailyMealDishes = new ArrayList<>();

    // --- Jalali parts -------------------------------------------------------
    @Column(nullable = false) private int jalaliMonth;
    @Column(nullable = false) private int jalaliDay;
    @Column(nullable = false) private int jalaliYear;

    /* -----------------------------------------------------------------------
       Life‑cycle callback: fills (or refreshes) the Jalali columns
       -------------------------------------------------------------------- */
    @PrePersist
    @PreUpdate
    private void computeJalaliParts() {
        if (date == null) {                           // sanity check
            return;
        }
        JalaliDate jd = toJalali(date);
        jalaliYear = jd.getYear();
        jalaliMonth = jd.getMonthPersian().getValue();
        jalaliDay = jd.getDay();
    }

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        DailyMeal dailyMeal = (DailyMeal) o;
        return getId() != null && Objects.equals(getId(), dailyMeal.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}

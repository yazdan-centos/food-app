package com.mapnaom.foodapp.models;


import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.*;

/**
 * Represents application settings stored in the database.
 */
@Entity
@Table(name = "app_settings")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AppSetting extends Auditable<String> {
    public static final Long SINGLETON_ID = 1L;
    @Id
    private Long id = SINGLETON_ID;
    private Boolean foodPricesActive = true;
    @Embedded
    private PriceShares priceShares;
    @Embedded
    private ReservationTime reservationTime;
    private String companyName;
    private String address;
    private String phone;
    @Version
    private Long version;

    /**
     * Ensures the entity always uses the singleton ID before being persisted.
     */
    @PrePersist
    private void enforceSingletonId() {
        if (this.id == null || !this.id.equals(SINGLETON_ID)) {
            throw new IllegalStateException("Attempted to persist AppSetting with an incorrect ID. Only ID %d is allowed.".formatted(SINGLETON_ID));
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        try {
            return o != null && getClass() == o.getClass();
        } catch (NullPointerException e) {
            return false;
        } catch (SecurityException e) {
            throw new SecurityException("Security manager prevented access to class information for comparison", e);
        }
        // For a singleton entity, all instances are conceptually equal.
    }

    @Override
    public int hashCode() {
        // A singleton entity should always have the same hash code.
        // Using the hash code of the fixed ID is a good practice.
        return SINGLETON_ID.hashCode();
    }
}

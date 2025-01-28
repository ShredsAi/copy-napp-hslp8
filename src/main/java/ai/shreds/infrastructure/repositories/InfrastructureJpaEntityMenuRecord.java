package ai.shreds.infrastructure.repositories;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "menu_record",
    indexes = {
        @Index(name = "idx_menu_record_restaurant_id", columnList = "restaurant_id"),
        @Index(name = "idx_menu_record_status", columnList = "status"),
        @Index(name = "idx_menu_record_restaurant_status", columnList = "restaurant_id,status")
    }
)
@EntityListeners(AuditingEntityListener.class)
public class InfrastructureJpaEntityMenuRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @OneToMany(mappedBy = "menuRecord", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<InfrastructureJpaEntityDishItem> items = new ArrayList<>();

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "status", nullable = false, length = 20)
    private String status;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void addDishItem(InfrastructureJpaEntityDishItem item) {
        items.add(item);
        item.setMenuRecord(this);
    }

    public void removeDishItem(InfrastructureJpaEntityDishItem item) {
        items.remove(item);
        item.setMenuRecord(null);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(UUID restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<InfrastructureJpaEntityDishItem> getItems() {
        return items;
    }

    public void setItems(List<InfrastructureJpaEntityDishItem> items) {
        this.items.clear();
        if (items != null) {
            items.forEach(this::addDishItem);
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

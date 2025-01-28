package ai.shreds.infrastructure.repositories;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Entity
@Table(name = "dish_item",
    indexes = {
        @Index(name = "idx_dish_item_menu_record", columnList = "menu_record_id"),
        @Index(name = "idx_dish_item_name", columnList = "dish_name")
    }
)
public class InfrastructureJpaEntityDishItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_record_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_dish_item_menu_record"))
    private InfrastructureJpaEntityMenuRecord menuRecord;

    @Column(name = "dish_name", nullable = false, length = 255)
    private String dishName;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "description", length = 1000)
    private String description;

    @Column(name = "additional_metadata", columnDefinition = "TEXT")
    private String additionalMetadata;

    public InfrastructureJpaEntityDishItem() {
    }

    public InfrastructureJpaEntityDishItem(String dishName, BigDecimal price) {
        this.dishName = dishName;
        this.price = price;
    }

    @PrePersist
    @PreUpdate
    protected void validatePrice() {
        if (price != null && price.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public InfrastructureJpaEntityMenuRecord getMenuRecord() {
        return menuRecord;
    }

    public void setMenuRecord(InfrastructureJpaEntityMenuRecord menuRecord) {
        this.menuRecord = menuRecord;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalMetadata() {
        return additionalMetadata;
    }

    public void setAdditionalMetadata(String additionalMetadata) {
        this.additionalMetadata = additionalMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof InfrastructureJpaEntityDishItem)) return false;
        InfrastructureJpaEntityDishItem that = (InfrastructureJpaEntityDishItem) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}

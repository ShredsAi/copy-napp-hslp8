package ai.shreds.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface InfrastructureSpringDataDishItemRepository extends JpaRepository<InfrastructureJpaEntityDishItem, UUID> {

    @Query("SELECT d FROM InfrastructureJpaEntityDishItem d WHERE d.menuRecord.id = :menuRecordId")
    List<InfrastructureJpaEntityDishItem> findByMenuRecordId(@Param("menuRecordId") UUID menuRecordId);

    @Modifying
    @Query("DELETE FROM InfrastructureJpaEntityDishItem d WHERE d.menuRecord.id = :menuRecordId")
    void deleteByMenuRecordId(@Param("menuRecordId") UUID menuRecordId);

    @Query("SELECT d FROM InfrastructureJpaEntityDishItem d WHERE d.menuRecord.id = :menuRecordId AND d.price <= :maxPrice")
    List<InfrastructureJpaEntityDishItem> findByMenuRecordIdAndPriceLessThanEqual(
        @Param("menuRecordId") UUID menuRecordId,
        @Param("maxPrice") BigDecimal maxPrice
    );

    @Query("SELECT d FROM InfrastructureJpaEntityDishItem d WHERE d.menuRecord.id = :menuRecordId AND LOWER(d.dishName) LIKE LOWER(CONCAT('%', :namePattern, '%'))")
    List<InfrastructureJpaEntityDishItem> findByMenuRecordIdAndDishNameContainingIgnoreCase(
        @Param("menuRecordId") UUID menuRecordId,
        @Param("namePattern") String namePattern
    );

    @Query("SELECT COUNT(d) > 0 FROM InfrastructureJpaEntityDishItem d WHERE d.menuRecord.id = :menuRecordId")
    boolean existsByMenuRecordId(@Param("menuRecordId") UUID menuRecordId);

    @Modifying
    @Query(value = "UPDATE InfrastructureJpaEntityDishItem d SET d.price = d.price * :multiplier WHERE d.menuRecord.id = :menuRecordId")
    void updatePricesByMultiplier(
        @Param("menuRecordId") UUID menuRecordId,
        @Param("multiplier") BigDecimal multiplier
    );
}

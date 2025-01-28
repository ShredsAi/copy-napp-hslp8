package ai.shreds.infrastructure.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface InfrastructureSpringDataMenuRecordRepository extends JpaRepository<InfrastructureJpaEntityMenuRecord, UUID> {

    List<InfrastructureJpaEntityMenuRecord> findByRestaurantIdAndStatus(UUID restaurantId, String status);

    @Query("SELECT m FROM InfrastructureJpaEntityMenuRecord m LEFT JOIN FETCH m.items WHERE m.id = :id")
    Optional<InfrastructureJpaEntityMenuRecord> findByIdWithItems(@Param("id") UUID id);

    @Query("SELECT m FROM InfrastructureJpaEntityMenuRecord m LEFT JOIN FETCH m.items WHERE m.restaurantId = :restaurantId AND m.status = :status")
    List<InfrastructureJpaEntityMenuRecord> findByRestaurantIdAndStatusWithItems(
        @Param("restaurantId") UUID restaurantId,
        @Param("status") String status
    );

    boolean existsByRestaurantIdAndStatus(UUID restaurantId, String status);

    @Query("SELECT COUNT(m) > 0 FROM InfrastructureJpaEntityMenuRecord m WHERE m.restaurantId = :restaurantId")
    boolean existsByRestaurantId(@Param("restaurantId") UUID restaurantId);

    void deleteByRestaurantIdAndStatus(UUID restaurantId, String status);
}

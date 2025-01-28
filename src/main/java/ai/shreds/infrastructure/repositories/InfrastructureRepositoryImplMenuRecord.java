package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityDishItem;
import ai.shreds.domain.entities.DomainEntityMenuRecord;
import ai.shreds.domain.exceptions.DomainMenuRecordNotFoundException;
import ai.shreds.domain.exceptions.DomainValidationException;
import ai.shreds.domain.ports.DomainOutputPortMenuRecordRepository;
import ai.shreds.domain.value_objects.DomainValueMenuRecordStatus;
import ai.shreds.domain.value_objects.DomainValuePrice;
import ai.shreds.domain.value_objects.DomainValueRestaurantId;
import ai.shreds.infrastructure.exceptions.InfrastructureDataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class InfrastructureRepositoryImplMenuRecord implements DomainOutputPortMenuRecordRepository {

    private final InfrastructureSpringDataMenuRecordRepository jpaMenuRecordRepository;
    private final InfrastructureSpringDataDishItemRepository jpaDishItemRepository;

    public InfrastructureRepositoryImplMenuRecord(
            InfrastructureSpringDataMenuRecordRepository jpaMenuRecordRepository,
            InfrastructureSpringDataDishItemRepository jpaDishItemRepository
    ) {
        this.jpaMenuRecordRepository = jpaMenuRecordRepository;
        this.jpaDishItemRepository = jpaDishItemRepository;
    }

    @Override
    @Transactional
    public DomainEntityMenuRecord save(DomainEntityMenuRecord menuRecord) {
        try {
            // Convert domain entity to JPA entity
            InfrastructureJpaEntityMenuRecord jpaEntity = toJpaEntity(menuRecord);
            // Save using Spring Data
            InfrastructureJpaEntityMenuRecord saved = jpaMenuRecordRepository.save(jpaEntity);

            // Convert back to domain
            return toDomainEntity(saved);
        } catch (Exception e) {
            throw new InfrastructureDataAccessException("Error while saving menu record", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public DomainEntityMenuRecord findById(String menuRecordId) {
        try {
            UUID recordUuid = UUID.fromString(menuRecordId);
            Optional<InfrastructureJpaEntityMenuRecord> optionalEntity = jpaMenuRecordRepository.findById(recordUuid);
            if (optionalEntity.isEmpty()) {
                throw new DomainMenuRecordNotFoundException("Menu record not found for id: " + menuRecordId);
            }
            return toDomainEntity(optionalEntity.get());
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("Invalid MenuRecord ID format", ex);
        } catch (DomainMenuRecordNotFoundException e) {
            throw e; // rethrow
        } catch (Exception e) {
            throw new InfrastructureDataAccessException("Error while finding menu record by id", e);
        }
    }

    @Override
    @Transactional
    public void deleteById(String menuRecordId) {
        try {
            UUID recordUuid = UUID.fromString(menuRecordId);
            if (!jpaMenuRecordRepository.existsById(recordUuid)) {
                throw new DomainMenuRecordNotFoundException("Menu record not found for id: " + menuRecordId);
            }
            jpaMenuRecordRepository.deleteById(recordUuid);
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("Invalid MenuRecord ID format", ex);
        } catch (DomainMenuRecordNotFoundException e) {
            throw e;
        } catch (Exception e) {
            throw new InfrastructureDataAccessException("Error while deleting menu record", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<DomainEntityMenuRecord> findByRestaurantIdAndStatus(String restaurantId, DomainValueMenuRecordStatus status) {
        try {
            UUID restaurantUuid = UUID.fromString(restaurantId);
            String statusString = status.getStatus().name();

            // We will retrieve all records in the DB with that restaurant ID and matching status.
            // Suppose we will filter in memory if needed, but let's do the search directly in the DB.
            List<InfrastructureJpaEntityMenuRecord> jpaEntities = jpaMenuRecordRepository.findAll();

            return jpaEntities.stream()
                    .filter(e -> e.getRestaurantId().equals(restaurantUuid))
                    .filter(e -> e.getStatus().equalsIgnoreCase(statusString))
                    .map(this::toDomainEntity)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException ex) {
            throw new DomainValidationException("Invalid Restaurant ID format", ex);
        } catch (Exception e) {
            throw new InfrastructureDataAccessException("Error while finding menu records by criteria", e);
        }
    }

    private InfrastructureJpaEntityMenuRecord toJpaEntity(DomainEntityMenuRecord domain) {
        InfrastructureJpaEntityMenuRecord jpa = new InfrastructureJpaEntityMenuRecord();
        if (domain.getId() != null && !domain.getId().isBlank()) {
            try {
                jpa.setId(UUID.fromString(domain.getId()));
            } catch (Exception e) {
                // ignore invalid parse, let DB generate it instead
            }
        }
        jpa.setRestaurantId(parseUUID(domain.getRestaurantId().getValue()));
        jpa.setCreatedAt(domain.getCreatedAt());
        jpa.setUpdatedAt(domain.getUpdatedAt() != null ? domain.getUpdatedAt() : LocalDateTime.now());
        jpa.setStatus(domain.getStatus().getStatus().name());

        // Convert dish items
        jpa.setItems(domain.getDishItems().stream().map(dish -> {
            InfrastructureJpaEntityDishItem itemJPA = toJpaDishEntity(dish);
            itemJPA.setMenuRecord(jpa);
            return itemJPA;
        }).collect(Collectors.toList()));

        return jpa;
    }

    private InfrastructureJpaEntityDishItem toJpaDishEntity(DomainEntityDishItem domainDish) {
        InfrastructureJpaEntityDishItem dishJPA = new InfrastructureJpaEntityDishItem();
        if (domainDish.getId() != null && !domainDish.getId().isBlank()) {
            try {
                dishJPA.setId(UUID.fromString(domainDish.getId()));
            } catch (Exception e) {
                // let DB generate new ID if parsing fails
            }
        }
        dishJPA.setDishName(domainDish.getDishName());
        dishJPA.setPrice(domainDish.getPrice().getAmount());
        dishJPA.setDescription(domainDish.getDescription());
        dishJPA.setAdditionalMetadata(domainDish.getAdditionalMetadata());
        return dishJPA;
    }

    private DomainEntityMenuRecord toDomainEntity(InfrastructureJpaEntityMenuRecord jpa) {
        DomainEntityMenuRecord domain = new DomainEntityMenuRecord();
        domain.setId(jpa.getId().toString());
        domain.setRestaurantId(new DomainValueRestaurantId(jpa.getRestaurantId().toString()));
        domain.setCreatedAt(jpa.getCreatedAt());
        domain.setUpdatedAt(jpa.getUpdatedAt());
        domain.setStatus(new DomainValueMenuRecordStatus(jpa.getStatus()));

        // Convert dish items
        List<DomainEntityDishItem> dishItems = jpa.getItems().stream().map(this::toDomainDishEntity).collect(Collectors.toList());
        domain.setDishItems(dishItems);

        return domain;
    }

    private DomainEntityDishItem toDomainDishEntity(InfrastructureJpaEntityDishItem jpaDish) {
        DomainEntityDishItem dish = new DomainEntityDishItem();
        dish.setId(jpaDish.getId().toString());
        dish.setDishName(jpaDish.getDishName());
        dish.setPrice(new DomainValuePrice(jpaDish.getPrice()));
        dish.setDescription(jpaDish.getDescription());
        dish.setAdditionalMetadata(jpaDish.getAdditionalMetadata());
        return dish;
    }

    private UUID parseUUID(String value) {
        try {
            return UUID.fromString(value);
        } catch (Exception e) {
            throw new DomainValidationException("Invalid UUID: " + value, e);
        }
    }
}

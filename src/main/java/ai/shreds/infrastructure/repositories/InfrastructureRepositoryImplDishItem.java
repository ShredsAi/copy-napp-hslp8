package ai.shreds.infrastructure.repositories;

import ai.shreds.domain.entities.DomainEntityDishItem;
import ai.shreds.domain.exceptions.DomainValidationException;
import ai.shreds.domain.ports.DomainOutputPortDishItemRepository;
import ai.shreds.infrastructure.exceptions.InfrastructureDataAccessException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class InfrastructureRepositoryImplDishItem implements DomainOutputPortDishItemRepository {

    private static final Logger logger = LoggerFactory.getLogger(InfrastructureRepositoryImplDishItem.class);
    private static final int BATCH_SIZE = 50;

    private final InfrastructureSpringDataDishItemRepository jpaDishItemRepository;

    public InfrastructureRepositoryImplDishItem(InfrastructureSpringDataDishItemRepository jpaDishItemRepository) {
        this.jpaDishItemRepository = jpaDishItemRepository;
    }

    @Override
    @Transactional
    public void saveAll(List<DomainEntityDishItem> items) {
        if (items == null || items.isEmpty()) {
            logger.debug("No dish items to save");
            return;
        }

        try {
            List<InfrastructureJpaEntityDishItem> jpaEntities = items.stream()
                    .map(this::toJpaEntity)
                    .collect(Collectors.toList());

            // Process in batches for better performance
            List<InfrastructureJpaEntityDishItem> batch = new ArrayList<>(BATCH_SIZE);
            for (InfrastructureJpaEntityDishItem entity : jpaEntities) {
                batch.add(entity);
                if (batch.size() >= BATCH_SIZE) {
                    saveBatch(batch);
                    batch.clear();
                }
            }
            
            // Save remaining items
            if (!batch.isEmpty()) {
                saveBatch(batch);
            }

            logger.debug("Successfully saved {} dish items", items.size());

        } catch (DataAccessException e) {
            logger.error("Database error while saving dish items", e);
            throw InfrastructureDataAccessException.databaseError("Error while saving dish items")
                    .addDetail("itemCount", items.size())
                    .addDetail("errorType", e.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Unexpected error while saving dish items", e);
            throw new InfrastructureDataAccessException("Error while saving dish items", e);
        }
    }

    @Override
    @Transactional
    public void deleteByMenuRecordId(String menuRecordId) {
        try {
            UUID recordUuid = UUID.fromString(menuRecordId);
            
            // Using the new method we added to the repository interface
            if (jpaDishItemRepository.existsByMenuRecordId(recordUuid)) {
                jpaDishItemRepository.deleteByMenuRecordId(recordUuid);
                logger.debug("Successfully deleted all dish items for menu record {}", menuRecordId);
            } else {
                logger.debug("No dish items found for menu record {}", menuRecordId);
            }

        } catch (IllegalArgumentException ex) {
            logger.error("Invalid menu record ID format: {}", menuRecordId);
            throw new DomainValidationException("Invalid MenuRecord ID format: " + menuRecordId, ex);
        } catch (DataAccessException e) {
            logger.error("Database error while deleting dish items for menu record {}", menuRecordId, e);
            throw InfrastructureDataAccessException.databaseError("Error while deleting dish items")
                    .addDetail("menuRecordId", menuRecordId)
                    .addDetail("errorType", e.getClass().getSimpleName());
        } catch (Exception e) {
            logger.error("Unexpected error while deleting dish items for menu record {}", menuRecordId, e);
            throw new InfrastructureDataAccessException("Error while deleting dish items", e);
        }
    }

    private void saveBatch(List<InfrastructureJpaEntityDishItem> batch) {
        try {
            jpaDishItemRepository.saveAll(batch);
        } catch (Exception e) {
            logger.error("Error saving batch of {} dish items", batch.size(), e);
            throw e;
        }
    }

    private InfrastructureJpaEntityDishItem toJpaEntity(DomainEntityDishItem domainDish) {
        if (domainDish == null) {
            throw new DomainValidationException("Domain dish item cannot be null");
        }

        InfrastructureJpaEntityDishItem dishJPA = new InfrastructureJpaEntityDishItem();
        
        if (domainDish.getId() != null && !domainDish.getId().isBlank()) {
            try {
                dishJPA.setId(UUID.fromString(domainDish.getId()));
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid dish item ID format: {}", domainDish.getId());
                // let DB generate new ID if parsing fails
            }
        }

        dishJPA.setDishName(domainDish.getDishName());
        dishJPA.setPrice(domainDish.getPrice().getAmount());
        dishJPA.setDescription(domainDish.getDescription());
        dishJPA.setAdditionalMetadata(domainDish.getAdditionalMetadata());

        return dishJPA;
    }
}

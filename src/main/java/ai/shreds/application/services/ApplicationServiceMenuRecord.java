package ai.shreds.application.services;

import ai.shreds.application.exceptions.ApplicationMenuRecordNotFoundException;
import ai.shreds.application.exceptions.ApplicationValidationException;
import ai.shreds.application.ports.ApplicationInputPortMenuRecord;
import ai.shreds.application.ports.ApplicationOutputPortMenuRecord;
import ai.shreds.domain.entities.DomainEntityDishItem;
import ai.shreds.domain.entities.DomainEntityMenuRecord;
import ai.shreds.domain.exceptions.DomainMenuRecordNotFoundException;
import ai.shreds.domain.exceptions.DomainValidationException;
import ai.shreds.domain.ports.DomainInputPortMenuRecord;
import ai.shreds.domain.value_objects.DomainValueMenuRecordStatus;
import ai.shreds.domain.value_objects.DomainValuePrice;
import ai.shreds.domain.value_objects.DomainValueRestaurantId;
import ai.shreds.shared.dtos.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ApplicationServiceMenuRecord implements ApplicationInputPortMenuRecord {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationServiceMenuRecord.class);
    private final DomainInputPortMenuRecord domainPort;
    private final ApplicationOutputPortMenuRecord outputPort;

    public ApplicationServiceMenuRecord(DomainInputPortMenuRecord domainPort,
                                      ApplicationOutputPortMenuRecord outputPort) {
        this.domainPort = Objects.requireNonNull(domainPort, "Domain port cannot be null");
        this.outputPort = Objects.requireNonNull(outputPort, "Output port cannot be null");
    }

    @Override
    @Transactional
    public SharedMenuRecordResponse createMenuRecord(SharedCreateMenuRecordParams request) {
        logger.debug("Creating menu record for restaurant: {}", request.getRestaurantId());
        try {
            validateCreateMenuRecordRequest(request);
            
            if (!outputPort.validateRestaurantId(request.getRestaurantId())) {
                logger.error("Restaurant ID validation failed for: {}", request.getRestaurantId());
                throw new ApplicationValidationException("Invalid Restaurant ID",
                    List.of(String.format("Restaurant with ID %s does not exist", request.getRestaurantId())));
            }

            DomainEntityMenuRecord domainMenuRecord = mapToDomainModel(request);
            DomainEntityMenuRecord createdDomainRecord = domainPort.createMenuRecord(domainMenuRecord);
            
            logger.info("Successfully created menu record with ID: {}", createdDomainRecord.getId());
            return mapToResponse(createdDomainRecord, false);
        } catch (DomainValidationException dve) {
            logger.error("Domain validation failed during menu record creation", dve);
            throw new ApplicationValidationException(dve.getMessage(), dve.getErrors());
        } catch (DomainMenuRecordNotFoundException dnfe) {
            logger.error("Menu record not found during creation process", dnfe);
            throw new ApplicationMenuRecordNotFoundException(dnfe.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error during menu record creation", e);
            throw new ApplicationValidationException("Failed to create menu record",
                List.of("An unexpected error occurred while processing your request"));
        }
    }

    @Override
    @Transactional
    public SharedMenuRecordResponse updateMenuRecord(String menuRecordId, SharedUpdateMenuRecordParams request) {
        logger.debug("Updating menu record: {}", menuRecordId);
        try {
            validateUpdateMenuRecordRequest(request);

            DomainEntityMenuRecord existingMenuRecord = domainPort.findMenuRecordById(menuRecordId);
            applyUpdateToDomainMenuRecord(existingMenuRecord, request);

            DomainEntityMenuRecord updatedDomainRecord = domainPort.updateMenuRecord(menuRecordId, existingMenuRecord);
            
            logger.info("Successfully updated menu record: {}", menuRecordId);
            return mapToResponse(updatedDomainRecord, false);
        } catch (DomainMenuRecordNotFoundException dnfe) {
            logger.error("Menu record not found during update: {}", menuRecordId, dnfe);
            throw new ApplicationMenuRecordNotFoundException(dnfe.getMessage());
        } catch (DomainValidationException dve) {
            logger.error("Domain validation failed during menu record update", dve);
            throw new ApplicationValidationException(dve.getMessage(), dve.getErrors());
        } catch (Exception e) {
            logger.error("Unexpected error during menu record update", e);
            throw new ApplicationValidationException("Failed to update menu record",
                List.of("An unexpected error occurred while processing your request"));
        }
    }

    @Override
    @Transactional
    public void deleteMenuRecord(String menuRecordId) {
        logger.debug("Deleting menu record: {}", menuRecordId);
        try {
            domainPort.deleteMenuRecord(menuRecordId);
            logger.info("Successfully deleted menu record: {}", menuRecordId);
        } catch (DomainMenuRecordNotFoundException dnfe) {
            logger.error("Menu record not found during deletion: {}", menuRecordId, dnfe);
            throw new ApplicationMenuRecordNotFoundException(dnfe.getMessage());
        } catch (DomainValidationException dve) {
            logger.error("Domain validation failed during menu record deletion", dve);
            throw new ApplicationValidationException(dve.getMessage(), dve.getErrors());
        } catch (Exception e) {
            logger.error("Unexpected error during menu record deletion", e);
            throw new ApplicationValidationException("Failed to delete menu record",
                List.of("An unexpected error occurred while processing your request"));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<SharedMenuRecordResponse> getMenuRecords(SharedGetMenuRecordsParams request) {
        logger.debug("Fetching menu records for restaurant: {}, status: {}", request.getRestaurantId(), request.getStatus());
        try {
            validateGetMenuRecordsRequest(request);

            DomainValueMenuRecordStatus status = null;
            if (request.getStatus() != null) {
                try {
                    SharedMenuRecordStatusEnum enumValue = SharedMenuRecordStatusEnum.valueOf(request.getStatus().toUpperCase());
                    status = new DomainValueMenuRecordStatus(enumValue);
                } catch (IllegalArgumentException e) {
                    logger.error("Invalid status value provided: {}", request.getStatus());
                    throw new ApplicationValidationException("Invalid status",
                        List.of(String.format("Unrecognized status value: %s", request.getStatus())));
                }
            }

            List<DomainEntityMenuRecord> domainRecords = domainPort.findMenuRecordsByCriteria(request.getRestaurantId(), status);
            List<SharedMenuRecordResponse> responses = new ArrayList<>();
            for (DomainEntityMenuRecord domainRecord : domainRecords) {
                responses.add(mapToResponse(domainRecord, false));
            }
            
            logger.info("Successfully retrieved {} menu records", responses.size());
            return responses;
        } catch (DomainValidationException dve) {
            logger.error("Domain validation failed during menu records retrieval", dve);
            throw new ApplicationValidationException(dve.getMessage(), dve.getErrors());
        } catch (Exception e) {
            logger.error("Unexpected error during menu records retrieval", e);
            throw new ApplicationValidationException("Failed to retrieve menu records",
                List.of("An unexpected error occurred while processing your request"));
        }
    }

    @Override
    @Transactional(readOnly = true)
    public SharedMenuRecordResponse getMenuRecord(String menuRecordId, boolean withLocation) {
        logger.debug("Fetching menu record: {}, withLocation: {}", menuRecordId, withLocation);
        try {
            DomainEntityMenuRecord domainRecord = domainPort.findMenuRecordById(menuRecordId);
            SharedMenuRecordResponse response = mapToResponse(domainRecord, withLocation);
            
            logger.info("Successfully retrieved menu record: {}", menuRecordId);
            return response;
        } catch (DomainMenuRecordNotFoundException dnfe) {
            logger.error("Menu record not found: {}", menuRecordId, dnfe);
            throw new ApplicationMenuRecordNotFoundException(dnfe.getMessage());
        } catch (DomainValidationException dve) {
            logger.error("Domain validation failed during menu record retrieval", dve);
            throw new ApplicationValidationException(dve.getMessage(), dve.getErrors());
        } catch (Exception e) {
            logger.error("Unexpected error during menu record retrieval", e);
            throw new ApplicationValidationException("Failed to retrieve menu record",
                List.of("An unexpected error occurred while processing your request"));
        }
    }

    private void validateCreateMenuRecordRequest(SharedCreateMenuRecordParams request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            throw new ApplicationValidationException("Request cannot be null", List.of("Request object is null"));
        }

        if (request.getRestaurantId() == null || request.getRestaurantId().trim().isEmpty()) {
            errors.add("Restaurant ID is required");
        }

        if (request.getStatus() == null || request.getStatus().trim().isEmpty()) {
            errors.add("Status is required");
        } else {
            try {
                SharedMenuRecordStatusEnum.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.add("Invalid status value: " + request.getStatus());
            }
        }

        validateDishItems(request.getItems(), errors);

        if (!errors.isEmpty()) {
            throw new ApplicationValidationException("Validation failed", errors);
        }
    }

    private void validateUpdateMenuRecordRequest(SharedUpdateMenuRecordParams request) {
        List<String> errors = new ArrayList<>();

        if (request == null) {
            throw new ApplicationValidationException("Request cannot be null", List.of("Request object is null"));
        }

        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                SharedMenuRecordStatusEnum.valueOf(request.getStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                errors.add("Invalid status value: " + request.getStatus());
            }
        }

        validateDishItems(request.getItems(), errors);

        if (!errors.isEmpty()) {
            throw new ApplicationValidationException("Validation failed", errors);
        }
    }

    private void validateGetMenuRecordsRequest(SharedGetMenuRecordsParams request) {
        if (request == null) {
            throw new ApplicationValidationException("Request cannot be null", List.of("Request object is null"));
        }

        List<String> errors = new ArrayList<>();
        if (request.getRestaurantId() == null || request.getRestaurantId().trim().isEmpty()) {
            errors.add("Restaurant ID is required");
        }

        if (!errors.isEmpty()) {
            throw new ApplicationValidationException("Validation failed", errors);
        }
    }

    private void validateDishItems(List<SharedDishItemParam> items, List<String> errors) {
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                SharedDishItemParam item = items.get(i);
                if (item.getDishName() == null || item.getDishName().trim().isEmpty()) {
                    errors.add(String.format("Dish name is required for item at position %d", i));
                }
                if (item.getPrice() != null && item.getPrice().compareTo(BigDecimal.ZERO) < 0) {
                    errors.add(String.format("Price cannot be negative for item: %s", item.getDishName()));
                }
            }
        }
    }

    private DomainEntityMenuRecord mapToDomainModel(SharedCreateMenuRecordParams request) {
        DomainEntityMenuRecord domainMenuRecord = new DomainEntityMenuRecord();
        domainMenuRecord.setRestaurantId(new DomainValueRestaurantId(request.getRestaurantId()));
        domainMenuRecord.setCreatedAt(LocalDateTime.now());
        domainMenuRecord.setUpdatedAt(LocalDateTime.now());

        SharedMenuRecordStatusEnum statusEnum = SharedMenuRecordStatusEnum.valueOf(request.getStatus().toUpperCase());
        domainMenuRecord.setStatus(new DomainValueMenuRecordStatus(statusEnum));

        List<DomainEntityDishItem> domainDishItems = new ArrayList<>();
        if (request.getItems() != null) {
            for (SharedDishItemParam item : request.getItems()) {
                DomainEntityDishItem domainItem = new DomainEntityDishItem();
                domainItem.setDishName(item.getDishName());
                domainItem.setPrice(new DomainValuePrice(item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO));
                domainItem.setDescription(item.getDescription());
                domainItem.setAdditionalMetadata(item.getAdditionalMetadata());
                domainDishItems.add(domainItem);
            }
        }
        domainMenuRecord.setDishItems(domainDishItems);

        return domainMenuRecord;
    }

    private void applyUpdateToDomainMenuRecord(DomainEntityMenuRecord domainRecord, SharedUpdateMenuRecordParams request) {
        if (request.getStatus() != null) {
            SharedMenuRecordStatusEnum statusEnum = SharedMenuRecordStatusEnum.valueOf(request.getStatus().toUpperCase());
            domainRecord.setStatus(new DomainValueMenuRecordStatus(statusEnum));
        }
        domainRecord.setUpdatedAt(LocalDateTime.now());

        if (request.getItems() != null) {
            List<DomainEntityDishItem> newDishItems = new ArrayList<>();
            for (SharedDishItemParam item : request.getItems()) {
                DomainEntityDishItem domainItem = new DomainEntityDishItem();
                domainItem.setDishName(item.getDishName());
                domainItem.setPrice(new DomainValuePrice(item.getPrice() != null ? item.getPrice() : BigDecimal.ZERO));
                domainItem.setDescription(item.getDescription());
                domainItem.setAdditionalMetadata(item.getAdditionalMetadata());
                newDishItems.add(domainItem);
            }
            domainRecord.setDishItems(newDishItems);
        }
    }

    private SharedMenuRecordResponse mapToResponse(DomainEntityMenuRecord domain, boolean withLocation) {
        SharedMenuRecordResponse response = new SharedMenuRecordResponse();
        response.setId(domain.getId());
        response.setRestaurantId(domain.getRestaurantId().getValue());
        response.setCreatedAt(domain.getCreatedAt() != null ? domain.getCreatedAt().toString() : null);
        response.setUpdatedAt(domain.getUpdatedAt() != null ? domain.getUpdatedAt().toString() : null);
        response.setStatus(domain.getStatus().getStatus().name());

        List<SharedDishItemResponse> dishResponses = new ArrayList<>();
        if (domain.getDishItems() != null) {
            for (DomainEntityDishItem domainItem : domain.getDishItems()) {
                SharedDishItemResponse itemResponse = new SharedDishItemResponse();
                itemResponse.setId(domainItem.getId());
                itemResponse.setDishName(domainItem.getDishName());
                itemResponse.setPrice(domainItem.getPrice().getAmount());
                itemResponse.setDescription(domainItem.getDescription());
                itemResponse.setAdditionalMetadata(domainItem.getAdditionalMetadata());
                dishResponses.add(itemResponse);
            }
        }
        response.setItems(dishResponses);

        if (withLocation) {
            String restaurantId = domain.getRestaurantId().getValue();
            SharedRestaurantLocation location = outputPort.getRestaurantLocation(restaurantId);
            response.setRestaurantLocation(location);
        }

        return response;
    }
}

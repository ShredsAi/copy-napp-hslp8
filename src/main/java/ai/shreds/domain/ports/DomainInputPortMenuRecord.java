package ai.shreds.domain.ports;

import ai.shreds.domain.entities.DomainEntityMenuRecord;
import ai.shreds.domain.value_objects.DomainValueMenuRecordStatus;
import java.util.List;

public interface DomainInputPortMenuRecord {

    DomainEntityMenuRecord createMenuRecord(DomainEntityMenuRecord menuRecord);

    DomainEntityMenuRecord updateMenuRecord(String menuRecordId, DomainEntityMenuRecord menuRecord);

    void deleteMenuRecord(String menuRecordId);

    DomainEntityMenuRecord findMenuRecordById(String menuRecordId);

    List<DomainEntityMenuRecord> findMenuRecordsByCriteria(String restaurantId, DomainValueMenuRecordStatus status);
}

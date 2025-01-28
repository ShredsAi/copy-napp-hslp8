package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedCreateMenuRecordParams;
import ai.shreds.shared.dtos.SharedUpdateMenuRecordParams;
import ai.shreds.shared.dtos.SharedGetMenuRecordsParams;
import ai.shreds.shared.dtos.SharedMenuRecordResponse;
import java.util.List;

public interface ApplicationInputPortMenuRecord {
    SharedMenuRecordResponse createMenuRecord(SharedCreateMenuRecordParams request);
    SharedMenuRecordResponse updateMenuRecord(String menuRecordId, SharedUpdateMenuRecordParams request);
    void deleteMenuRecord(String menuRecordId);
    List<SharedMenuRecordResponse> getMenuRecords(SharedGetMenuRecordsParams request);
    SharedMenuRecordResponse getMenuRecord(String menuRecordId, boolean withLocation);
}

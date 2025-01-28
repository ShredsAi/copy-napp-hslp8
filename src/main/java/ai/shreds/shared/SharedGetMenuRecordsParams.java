package ai.shreds.shared;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class SharedGetMenuRecordsParams {

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    private String status;

    public SharedGetMenuRecordsParams() {
    }

    public SharedGetMenuRecordsParams(String restaurantId, String status) {
        this.restaurantId = restaurantId;
        this.status = status;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SharedMenuRecordStatusEnum getStatusEnum() {
        return status != null ? SharedMenuRecordStatusEnum.fromString(status) : null;
    }

    public boolean hasStatus() {
        return status != null && !status.trim().isEmpty();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedGetMenuRecordsParams that = (SharedGetMenuRecordsParams) o;
        return Objects.equals(restaurantId, that.restaurantId) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, status);
    }

    @Override
    public String toString() {
        return "SharedGetMenuRecordsParams{" +
                "restaurantId='" + restaurantId + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}

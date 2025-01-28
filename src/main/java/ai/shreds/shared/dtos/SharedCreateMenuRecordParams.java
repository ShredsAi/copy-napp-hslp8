package ai.shreds.shared.dtos;

import ai.shreds.shared.SharedMenuRecordStatusEnum;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class SharedCreateMenuRecordParams {

    @NotBlank(message = "Restaurant ID is required")
    private String restaurantId;

    @NotEmpty(message = "Menu items cannot be empty")
    private List<SharedDishItemParam> items;

    @NotNull(message = "Status is required")
    private String status;

    public SharedCreateMenuRecordParams() {
    }

    public SharedCreateMenuRecordParams(String restaurantId, List<SharedDishItemParam> items, String status) {
        this.restaurantId = restaurantId;
        this.items = items;
        this.status = status;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<SharedDishItemParam> getItems() {
        return items;
    }

    public void setItems(List<SharedDishItemParam> items) {
        this.items = items;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SharedMenuRecordStatusEnum getStatusEnum() {
        return SharedMenuRecordStatusEnum.fromString(status);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedCreateMenuRecordParams that = (SharedCreateMenuRecordParams) o;
        return Objects.equals(restaurantId, that.restaurantId) &&
                Objects.equals(items, that.items) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, items, status);
    }

    @Override
    public String toString() {
        return "SharedCreateMenuRecordParams{" +
                "restaurantId='" + restaurantId + '\'' +
                ", items=" + items +
                ", status='" + status + '\'' +
                '}';
    }
}

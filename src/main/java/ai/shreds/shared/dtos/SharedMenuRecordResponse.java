package ai.shreds.shared.dtos;

import ai.shreds.shared.SharedMenuRecordStatusEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Objects;

public class SharedMenuRecordResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("restaurant_id")
    private String restaurantId;

    @JsonProperty("items")
    private List<SharedDishItemResponse> items;

    @JsonProperty("created_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String createdAt;

    @JsonProperty("updated_at")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private String updatedAt;

    @JsonProperty("status")
    private String status;

    @JsonProperty("restaurant_location")
    private SharedRestaurantLocation restaurantLocation;

    public SharedMenuRecordResponse() {
    }

    public SharedMenuRecordResponse(String id,
                                   String restaurantId,
                                   List<SharedDishItemResponse> items,
                                   String createdAt,
                                   String updatedAt,
                                   String status,
                                   SharedRestaurantLocation restaurantLocation) {
        this.id = id;
        this.restaurantId = restaurantId;
        this.items = items;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.restaurantLocation = restaurantLocation;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public List<SharedDishItemResponse> getItems() {
        return items;
    }

    public void setItems(List<SharedDishItemResponse> items) {
        this.items = items;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
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

    public SharedRestaurantLocation getRestaurantLocation() {
        return restaurantLocation;
    }

    public void setRestaurantLocation(SharedRestaurantLocation restaurantLocation) {
        this.restaurantLocation = restaurantLocation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedMenuRecordResponse that = (SharedMenuRecordResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(restaurantId, that.restaurantId) &&
                Objects.equals(items, that.items) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(status, that.status) &&
                Objects.equals(restaurantLocation, that.restaurantLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, restaurantId, items, createdAt, updatedAt, status, restaurantLocation);
    }

    @Override
    public String toString() {
        return "SharedMenuRecordResponse{" +
                "id='" + id + '\'' +
                ", restaurantId='" + restaurantId + '\'' +
                ", items=" + items +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", status='" + status + '\'' +
                ", restaurantLocation=" + restaurantLocation +
                '}';
    }
}

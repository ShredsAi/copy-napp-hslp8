package ai.shreds.shared.dtos;

public class SharedGetMenuRecordsParams {

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
}

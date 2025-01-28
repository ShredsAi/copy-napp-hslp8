package ai.shreds.shared.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class SharedRestaurantLocation {

    @NotBlank(message = "Restaurant ID is required")
    @JsonProperty("restaurant_id")
    private String restaurantId;

    @NotBlank(message = "Restaurant name is required")
    @JsonProperty("restaurant_name")
    private String restaurantName;

    @JsonProperty("location_details")
    private String locationDetails;

    public SharedRestaurantLocation() {
    }

    public SharedRestaurantLocation(String restaurantId, String restaurantName, String locationDetails) {
        this.restaurantId = restaurantId;
        this.restaurantName = restaurantName;
        this.locationDetails = locationDetails;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public String getRestaurantName() {
        return restaurantName;
    }

    public void setRestaurantName(String restaurantName) {
        this.restaurantName = restaurantName;
    }

    public String getLocationDetails() {
        return locationDetails;
    }

    public void setLocationDetails(String locationDetails) {
        this.locationDetails = locationDetails;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedRestaurantLocation that = (SharedRestaurantLocation) o;
        return Objects.equals(restaurantId, that.restaurantId) &&
                Objects.equals(restaurantName, that.restaurantName) &&
                Objects.equals(locationDetails, that.locationDetails);
    }

    @Override
    public int hashCode() {
        return Objects.hash(restaurantId, restaurantName, locationDetails);
    }

    @Override
    public String toString() {
        return "SharedRestaurantLocation{" +
                "restaurantId='" + restaurantId + '\'' +
                ", restaurantName='" + restaurantName + '\'' +
                ", locationDetails='" + locationDetails + '\'' +
                '}';
    }
}

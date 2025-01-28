package ai.shreds.application.ports;

import ai.shreds.shared.dtos.SharedRestaurantLocation;

public interface ApplicationOutputPortMenuRecord {
    boolean validateRestaurantId(String restaurantId);
    SharedRestaurantLocation getRestaurantLocation(String restaurantId);
}

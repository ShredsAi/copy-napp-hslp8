package ai.shreds.shared.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Objects;

public class SharedDishItemResponse {

    @JsonProperty("id")
    private String id;

    @JsonProperty("dish_name")
    private String dishName;

    @JsonProperty("price")
    private BigDecimal price;

    @JsonProperty("description")
    private String description;

    @JsonProperty("additional_metadata")
    private String additionalMetadata;

    public SharedDishItemResponse() {
    }

    public SharedDishItemResponse(String id, String dishName, BigDecimal price, String description, String additionalMetadata) {
        this.id = id;
        this.dishName = dishName;
        this.price = price;
        this.description = description;
        this.additionalMetadata = additionalMetadata;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAdditionalMetadata() {
        return additionalMetadata;
    }

    public void setAdditionalMetadata(String additionalMetadata) {
        this.additionalMetadata = additionalMetadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SharedDishItemResponse that = (SharedDishItemResponse) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(dishName, that.dishName) &&
                Objects.equals(price, that.price) &&
                Objects.equals(description, that.description) &&
                Objects.equals(additionalMetadata, that.additionalMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, dishName, price, description, additionalMetadata);
    }

    @Override
    public String toString() {
        return "SharedDishItemResponse{" +
                "id='" + id + '\'' +
                ", dishName='" + dishName + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", additionalMetadata='" + additionalMetadata + '\'' +
                '}';
    }
}

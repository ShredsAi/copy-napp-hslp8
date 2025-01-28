package ai.shreds.shared.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.util.Objects;

public class SharedDishItemParam {

    @NotBlank(message = "Dish name is required")
    private String dishName;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than zero")
    private BigDecimal price;

    private String description;

    private String additionalMetadata;

    public SharedDishItemParam() {
    }

    public SharedDishItemParam(String dishName, BigDecimal price, String description, String additionalMetadata) {
        this.dishName = dishName;
        this.price = price;
        this.description = description;
        this.additionalMetadata = additionalMetadata;
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
        SharedDishItemParam that = (SharedDishItemParam) o;
        return Objects.equals(dishName, that.dishName) &&
                Objects.equals(price, that.price) &&
                Objects.equals(description, that.description) &&
                Objects.equals(additionalMetadata, that.additionalMetadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dishName, price, description, additionalMetadata);
    }

    @Override
    public String toString() {
        return "SharedDishItemParam{" +
                "dishName='" + dishName + '\'' +
                ", price=" + price +
                ", description='" + description + '\'' +
                ", additionalMetadata='" + additionalMetadata + '\'' +
                '}';
    }
}

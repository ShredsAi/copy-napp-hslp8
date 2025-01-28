package ai.shreds.shared.dtos;

import ai.shreds.shared.SharedMenuRecordStatusEnum;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;

public class SharedUpdateMenuRecordParams {

    @NotEmpty(message = "Menu items cannot be empty")
    @Valid
    private List<SharedDishItemParam> items;

    @NotNull(message = "Status is required")
    private String status;

    public SharedUpdateMenuRecordParams() {
    }

    public SharedUpdateMenuRecordParams(List<SharedDishItemParam> items, String status) {
        this.items = items;
        this.status = status;
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
        SharedUpdateMenuRecordParams that = (SharedUpdateMenuRecordParams) o;
        return Objects.equals(items, that.items) &&
                Objects.equals(status, that.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(items, status);
    }

    @Override
    public String toString() {
        return "SharedUpdateMenuRecordParams{" +
                "items=" + items +
                ", status='" + status + '\'' +
                '}';
    }
}

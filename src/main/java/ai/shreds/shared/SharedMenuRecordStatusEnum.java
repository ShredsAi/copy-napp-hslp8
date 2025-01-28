package ai.shreds.shared;

public enum SharedMenuRecordStatusEnum {
    ACTIVE,
    INACTIVE;

    public static SharedMenuRecordStatusEnum fromString(String status) {
        try {
            return SharedMenuRecordStatusEnum.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid menu record status: " + status + ". Allowed values are: ACTIVE, INACTIVE");
        }
    }

    public static boolean isValid(String status) {
        try {
            SharedMenuRecordStatusEnum.valueOf(status.toUpperCase());
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
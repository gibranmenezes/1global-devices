package one.globa.api.domain.enums;

public enum State {
    AVAILABLE("available"),
    IN_USE("in_use"),
    INACTIVE("inactive");

    private final String value;

    State(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}

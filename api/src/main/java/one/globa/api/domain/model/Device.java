package one.globa.api.domain.model;

import one.globa.api.domain.enums.State;
import one.globa.api.domain.exception.DeviceInUseException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Device {
    private Long id;
    private String name;
    private String brand;
    private State state;
    private final LocalDateTime createdAt;

    public Device(String name, String brand, State state) {
        if (name != null && !name.isBlank()) this.name = name;
        if (brand != null && !brand.isBlank()) this.brand = brand;
        this.state = State.AVAILABLE;
        this.createdAt = LocalDateTime.now();
    }

    protected Device(Long id, String name, String brand, State state, LocalDateTime createdAt) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.state = state;
        this.createdAt = createdAt;
    }

    public void changeState(State newState) {
        if (newState == null) {
            throw new DeviceInUseException("State cannot be null");
        }
        this.state = newState;
    }

    public void updateDetails(String name, String brand) {
        if (this.state == State.IN_USE ) {
            throw new DeviceInUseException("Cannot update name or brand: device is currently in use.");
        }

        if (name != null && !name.isBlank()) {
            this.name = name;
        }

        if (brand != null && !brand.isBlank()) {
            this.brand = brand;
        }

    }

    public void ensureCanBeDeleted() {
        if (this.state == State.IN_USE) {
            throw new DeviceInUseException("Cannot delete device: it is currently in use.");
        }
    }

    protected void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public State getState() {
        return state;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Device device = (Device) o;
        return Objects.equals(id, device.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}

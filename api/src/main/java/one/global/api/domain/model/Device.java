package one.global.api.domain.model;

import one.global.api.domain.enums.State;
import one.global.api.domain.exception.DeviceInUseException;

import java.time.LocalDateTime;
import java.util.Objects;

public class Device {
    private Long id;
    private String name;
    private String brand;
    private State state;
    private final LocalDateTime creationDate;

    public Device(String name, String brand) {
        if (name != null && !name.isBlank()) this.name = name;
        if (brand != null && !brand.isBlank()) this.brand = brand;
        this.state = State.AVAILABLE;
        this.creationDate = LocalDateTime.now();
    }

    private Device(Long id, String name, String brand, State state, LocalDateTime creationDate) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.state = state;
        this.creationDate = creationDate;
    }

    public static Device reconstruct(Long id, String name, String brand, String state, LocalDateTime creationDate) {
        return new Device(id, name, brand, State.valueOf(state), creationDate);
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

        //We could implement a validation over the sequence of State changes.

        if (name != null && !name.isEmpty()) {
            this.name = name;
        }

        if (brand != null && !brand.isEmpty()) {
            this.brand = brand;
        }

    }

    public void ensureCanBeDeleted() {
        if (this.state == State.IN_USE) {
            throw new DeviceInUseException("Cannot delete device: it is currently in use.");
        }
    }

    public void setId(Long id) {
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


    public LocalDateTime getCreationDate() {
        return creationDate;
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

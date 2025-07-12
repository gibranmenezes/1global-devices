package one.globa.api.presentation.dto;

import one.globa.api.domain.model.Device;

public record DeviceResponseDTO(String name, String brand, String state, String createdAt) {

}

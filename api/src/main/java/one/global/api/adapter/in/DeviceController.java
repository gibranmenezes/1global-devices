package one.global.api.adapter.in;

import lombok.RequiredArgsConstructor;
import one.global.api.adapter.out.DeviceMapper;
import one.global.api.application.port.in.DeviceUseCase;
import one.global.api.domain.enums.State;
import one.global.api.domain.model.Device;
import one.global.api.presentation.dto.AppResponse;
import one.global.api.presentation.dto.DeviceRequestDTO;
import one.global.api.presentation.dto.DeviceResponseDTO;
import one.global.api.presentation.dto.PaginatedResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceUseCase deviceUseCase;
    private final DeviceMapper deviceMapper;

    @PostMapping("/register")
    public ResponseEntity<AppResponse<DeviceResponseDTO>> registerDevice(@RequestBody DeviceRequestDTO deviceRequestDTO) {
        var device = deviceUseCase.registerDevice(deviceRequestDTO.name(), deviceRequestDTO.brand());
        return AppResponse
                .ok("Device registered successfully", deviceMapper.fromDeviceToDeviceResponseDTO(device))
                .getResponseEntity();
    }

    @GetMapping
    public ResponseEntity<AppResponse<List<DeviceResponseDTO>>> getDevices(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)  {

        var validState = state != null && !state.isBlank() ? State.valueOf(state) : null;

        PaginatedResponse<Device> paginatedDevices = deviceUseCase
                .getAllDevices(brand, validState, page, size);

        List<DeviceResponseDTO> deviceResponseDTOs = paginatedDevices.getContent().stream()
                .map(deviceMapper::fromDeviceToDeviceResponseDTO)
                .toList();

        AppResponse<List<DeviceResponseDTO>> finalAppResponse = AppResponse.ok("Devices retrieved successfully", deviceResponseDTOs)
                .buildParametersPagination(
                        paginatedDevices.getPageNumber(),
                        paginatedDevices.getPageSize(),
                        paginatedDevices.getTotalElements(),
                        paginatedDevices.getTotalPages()
                );

        return finalAppResponse.getResponseEntity();

    }

}

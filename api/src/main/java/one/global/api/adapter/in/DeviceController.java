package one.global.api.adapter.in;

import lombok.RequiredArgsConstructor;
import one.global.api.Utils.Utils;
import one.global.api.adapter.out.DeviceMapper;
import one.global.api.application.port.in.DeviceUseCase;
import one.global.api.domain.model.Device;
import one.global.api.web.dto.*;
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
    public ResponseEntity<AppResponse<DeviceResponseDTO>> createDevice(@RequestBody DeviceRequestDTO deviceRequestDTO) {
        var device = deviceUseCase.createDevice(deviceRequestDTO.name(), deviceRequestDTO.brand());
        return AppResponse
                .created("Device registered successfully", deviceMapper.fromDeviceToDeviceResponseDTO(device))
                .getResponseEntity();
    }

    @GetMapping("/{id}")
    public ResponseEntity<AppResponse<List<DeviceResponseDTO>>> getDevice(@PathVariable Long id) {
        Device device = deviceUseCase.getDeviceById(id);
        DeviceResponseDTO deviceResponseDTO = deviceMapper.fromDeviceToDeviceResponseDTO(device);
        return AppResponse.ok("Device retrieved successfully", List.of(deviceResponseDTO)).getResponseEntity();
    }

    @GetMapping
    public ResponseEntity<AppResponse<List<DeviceResponseDTO>>> getDevices(
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) String state,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size)  {

        var validState = Utils.getValidState(state);

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
    @PutMapping("/{id}")
    public ResponseEntity<AppResponse<DeviceResponseDTO>> updateDevice(@PathVariable Long id,
                                                                       @RequestBody DeviceUpdateDTO updateDTO) {

        var validState = Utils.getValidState(updateDTO.state());

        Device updatedDevice = deviceUseCase.updateDevice(id, updateDTO.name(), updateDTO.brand(), validState);
        DeviceResponseDTO deviceResponseDTO = deviceMapper.fromDeviceToDeviceResponseDTO(updatedDevice);

       return AppResponse.ok("Device updated successfully", deviceResponseDTO).getResponseEntity();

    }

    @PatchMapping("/{id}")
    public ResponseEntity<AppResponse<DeviceResponseDTO>> partiallyUpdateDevice(@PathVariable Long id,
                                                                                 @RequestBody DeviceUpdateDTO patchDTO) {
        var validState = Utils.getValidState(patchDTO.state());

        Device updatedDevice = deviceUseCase.partiallyUpdateDevice(id, patchDTO.name(), patchDTO.brand(), validState);
        DeviceResponseDTO deviceResponseDTO = deviceMapper.fromDeviceToDeviceResponseDTO(updatedDevice);

        return AppResponse.ok("Device partially updated successfully", deviceResponseDTO).getResponseEntity();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<AppResponse<Void>> deleteDevice(@PathVariable Long id) {
        deviceUseCase.deleteDevice(id);

        return AppResponse.noContent("Device deleted successfully").getNoContentResponseEntity();
    }

}

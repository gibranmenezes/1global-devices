package one.globa.api.adapter.in;

import lombok.RequiredArgsConstructor;
import one.globa.api.adapter.out.DeviceMapper;
import one.globa.api.application.port.in.DeviceUseCase;
import one.globa.api.domain.enums.State;
import one.globa.api.domain.model.Device;
import one.globa.api.presentation.dto.AppResponse;
import one.globa.api.presentation.dto.DeviceRequestDTO;
import one.globa.api.presentation.dto.DeviceResponseDTO;
import one.globa.api.presentation.dto.PaginatedResponse;
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



}

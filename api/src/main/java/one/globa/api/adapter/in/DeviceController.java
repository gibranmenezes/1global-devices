package one.globa.api.adapter.in;

import lombok.RequiredArgsConstructor;
import one.globa.api.adapter.out.DeviceMapper;
import one.globa.api.application.port.in.DeviceUseCase;
import one.globa.api.presentation.dto.DeviceRequestDTO;
import one.globa.api.presentation.dto.DeviceResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {
    private final DeviceUseCase deviceUseCase;
    private final DeviceMapper deviceMapper;

    @PostMapping("/register")
    public ResponseEntity<DeviceResponseDTO> registerDevice(@RequestBody DeviceRequestDTO deviceRequestDTO) {
        var device = deviceUseCase.createDevice(deviceRequestDTO.name(), deviceRequestDTO.brand());
        return ResponseEntity.ok(deviceMapper.fromDeviceToDeviceResponseDTO(device));
    }
}

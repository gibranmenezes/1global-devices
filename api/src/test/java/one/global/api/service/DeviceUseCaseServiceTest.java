package one.global.api.service;

import one.global.api.application.validation.NameBrandValidation;
import one.global.api.domain.exception.InvalidDeviceParameter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import one.global.api.application.port.out.DeviceRepository;
import one.global.api.application.service.DeviceUserCaseService;
import one.global.api.application.validation.DeviceAttributesValidator;
import one.global.api.domain.enums.State;
import one.global.api.domain.exception.DeviceInUseException;
import one.global.api.domain.exception.DeviceNotFoundException;
import one.global.api.domain.model.Device;
import one.global.api.Utils.Utils;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceUseCaseServiceTest {

    @Mock
    private DeviceRepository deviceRepository;

    @Mock
    private DeviceAttributesValidator mockNameBrandValidator;

    @InjectMocks
    private DeviceUserCaseService deviceUserCaseService;

    private Device testDevice;

    private final NameBrandValidation validator = new NameBrandValidation();


    @BeforeEach
    void setUp() {
        deviceUserCaseService = new DeviceUserCaseService(deviceRepository, Arrays.asList(mockNameBrandValidator));
        testDevice = new Device("TestName", "TestBrand");
        testDevice.setId(1L);
    }

    @Test
    @DisplayName("Register device should save new device")
    void createDevice_shouldSaveNewDevice() {
        String name = "NewDevice";
        String brand = "NewBrand";
        Device newDevice = new Device(name, brand);
        newDevice.setId(2L);

        when(deviceRepository.save(any(Device.class))).thenReturn(newDevice);

        Device result = deviceUserCaseService.createDevice(name, brand);

        assertNotNull(result.getId());
        assertEquals(name, result.getName());
        assertEquals(brand, result.getBrand());
        assertEquals(State.AVAILABLE, result.getState());
        verify(deviceRepository, times(1)).save(any(Device.class));
        verify(mockNameBrandValidator, times(1)).validate(name, brand);
    }

    @Test
    @DisplayName("Should not throw exception when name and brand are valid")
    void shouldNotThrowExceptionWhenNameAndBrandAreValid() {
        assertDoesNotThrow(() -> validator.validate("ValidName", "ValidBrand"));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when name is null or empty")
    void shouldThrowExceptionWhenNameIsNullOrEmpty(String invalidName) {
        assertThrows(InvalidDeviceParameter.class,
                () -> validator.validate(invalidName, "ValidBrand"),
                "Name and brand must not be empty");
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Should throw exception when brand is null or empty")
    void shouldThrowExceptionWhenBrandIsNullOrEmpty(String invalidBrand) {
        assertThrows(InvalidDeviceParameter.class,
                () -> validator.validate("ValidName", invalidBrand),
                "Name and brand must not be empty");
    }

    @Test
    @DisplayName("Should throw exception when both name and brand are null")
    void shouldThrowExceptionWhenBothNameAndBrandAreNull() {
        assertThrows(InvalidDeviceParameter.class,
                () -> validator.validate(null, null),
                "Name and brand must not be empty");
    }

    @Test
    @DisplayName("Get device by ID should return device when found")
    void getDeviceById_shouldReturnDevice_whenFound() {
        Long id = 1L;
        when(deviceRepository.findById(anyLong())).thenReturn(testDevice);
        Device result = deviceUserCaseService.getDeviceById(id);


        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(deviceRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Get device by ID should throw exception when not found")
    void getDeviceById_shouldThrowDeviceNotFoundException_whenNotFound() {
        Long nonExistentId = 99L;
        when(deviceRepository.findById(nonExistentId)).thenReturn(null);
        assertThrows(DeviceNotFoundException.class, () -> deviceUserCaseService.getDeviceById(nonExistentId));
    }

    @Test
    @DisplayName("Update device should update all details and state")
    void updateDevice_shouldUpdateAllDetailsAndState() {
        Long id = 1L;
        String newName = "UpdatedName";
        String newBrand = "UpdatedBrand";
        State newState = State.INACTIVE;

        when(deviceRepository.findById(anyLong())).thenReturn(testDevice);
        when(deviceRepository.save(any(Device.class))).thenReturn(testDevice);


        deviceUserCaseService.updateDevice(id, newName, newBrand, newState);

        assertEquals(newName, testDevice.getName());
        assertEquals(newBrand, testDevice.getBrand());
        assertEquals(newState, testDevice.getState());
        verify(deviceRepository, times(1)).save(testDevice);
    }

    @Test
    @DisplayName("Update device should throw exception when device in use and updating name or brand")
    void updateDevice_shouldThrowException_whenDeviceInUseAndUpdatingNameOrBrand() {
        Long id = 1L;
        Device inUseDevice = new Device("TestName", "TestBrand");
        inUseDevice.setId(id);
        inUseDevice.changeState(State.IN_USE);

        when(deviceRepository.findById(id)).thenReturn(inUseDevice);

        String newName = "UpdatedName";
        String newBrand = "TestBrand";

        Exception exception = assertThrows(DeviceInUseException.class,
                () -> deviceUserCaseService.updateDevice(id, newName, newBrand, State.IN_USE));

        verify(deviceRepository, never()).save(any(Device.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Update device should throw InvalidDeviceParameter when name is null or empty (via NameBrandValidation)")
    void updateDevice_shouldThrowInvalidDeviceParameter_whenNameIsNullOrEmptyViaValidation(String invalidName) {
        Long id = 1L;
        String validBrand = "ValidBrand";
        State newState = State.AVAILABLE;

        when(deviceRepository.findById(anyLong())).thenReturn(testDevice);

        doThrow(new InvalidDeviceParameter("Name and brand must not be empty"))
                .when(mockNameBrandValidator).validate(eq(invalidName), eq(validBrand));

        assertThrows(InvalidDeviceParameter.class,
                () -> deviceUserCaseService.updateDevice(id, invalidName, validBrand, newState));

        verify(mockNameBrandValidator, times(1)).validate(eq(invalidName), eq(validBrand));
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @ParameterizedTest
    @NullAndEmptySource
    @DisplayName("Update device should throw InvalidDeviceParameter when brand is null or empty (via NameBrandValidation)")
    void updateDevice_shouldThrowInvalidDeviceParameter_whenBrandIsNullOrEmptyViaValidation(String invalidBrand) {
        Long id = 1L;
        String validName = "ValidName";
        State newState = State.AVAILABLE;

        when(deviceRepository.findById(anyLong())).thenReturn(testDevice);

        doThrow(new InvalidDeviceParameter("Name and brand must not be empty"))
                .when(mockNameBrandValidator).validate(eq(validName), eq(invalidBrand));

        assertThrows(InvalidDeviceParameter.class,
                () -> deviceUserCaseService.updateDevice(id, validName, invalidBrand, newState));

        verify(mockNameBrandValidator, times(1)).validate(eq(validName), eq(invalidBrand));
        verify(deviceRepository, never()).save(any(Device.class));
    }

    @Test
    @DisplayName("Update device should throw InvalidDeviceParameter when both name and brand are null (via NameBrandValidation)")
    void updateDevice_shouldThrowInvalidDeviceParameter_whenBothNameAndBrandAreNullViaValidation() {
        Long id = 1L;
        State newState = State.AVAILABLE;

        when(deviceRepository.findById(anyLong())).thenReturn(testDevice);

        doThrow(new InvalidDeviceParameter("Name and brand must not be empty"))
                .when(mockNameBrandValidator).validate(eq(null), eq(null));

        assertThrows(InvalidDeviceParameter.class,
                () -> deviceUserCaseService.updateDevice(id, null, null, newState));

        verify(mockNameBrandValidator, times(1)).validate(null, null);
        verify(deviceRepository, never()).save(any(Device.class));
    }


    @Test
    @DisplayName("Update device should throw exception when state is null")
    void updateDevice_shouldThrowException_whenStateIsNull() {
        Long id = 1L;
        String newName = "UpdatedName";
        String newBrand = "UpdatedBrand";

        when(deviceRepository.findById(anyLong())).thenReturn(testDevice);

        assertThrows(InvalidDeviceParameter.class, () -> deviceUserCaseService.updateDevice(id, newName, newBrand, null));
        verify(deviceRepository, never()).save(any(Device.class));
    }



    @Test
    @DisplayName("Partially update device should update name and brand when provided and device not in use")
    void partiallyUpdateDevice_shouldUpdateNameAndBrand_whenProvidedAndDeviceNotIN_USE() {
        Long id = 1L;
        String newName = "PartialName";
        String newBrand = "PartialBrand";

        try (var mockedStatic = mockStatic(Utils.class)) {
            mockedStatic.when(() -> Utils.isUpdatingNameAndBrand(newName, newBrand)).thenReturn(true);
            when(deviceRepository.findById(anyLong())).thenReturn(testDevice);

            deviceUserCaseService.partiallyUpdateDevice(id, newName, newBrand, null);

            assertEquals(newName, testDevice.getName());
            assertEquals(newBrand, testDevice.getBrand());
            assertEquals(State.AVAILABLE, testDevice.getState());
            verify(deviceRepository, times(1)).save(testDevice);
        }
    }

    @Test
    @DisplayName("Partially update device should update state when provided and different")
    void partiallyUpdateDevice_shouldUpdateState_whenProvidedAndDifferent() {
        Long id = 1L;

        State newState = State.INACTIVE;
        testDevice.changeState(State.AVAILABLE);


        try (var mockedStatic = mockStatic(Utils.class)) {
            mockedStatic.when(() -> Utils.isUpdatingNameAndBrand(null, null)).thenReturn(false);
            when(deviceRepository.findById(anyLong())).thenReturn(testDevice);

            deviceUserCaseService.partiallyUpdateDevice(id, null, null, newState);

            assertEquals(newState, testDevice.getState());
            verify(deviceRepository, times(1)).save(testDevice);
        }
    }

    @Test
    @DisplayName("Partially update device should not update anything when no relevant params provided")
    void partiallyUpdateDevice_shouldNotUpdateAnything_whenNoRelevantParamsProvided() {
        Long id = 1L;
        String originalName = testDevice.getName();
        String originalBrand = testDevice.getBrand();
        State originalState = testDevice.getState();

        try (var mockedStatic = mockStatic(Utils.class)) {
            mockedStatic.when(() -> Utils.isUpdatingNameAndBrand(null, null)).thenReturn(false);
            when(deviceRepository.findById(anyLong())).thenReturn(testDevice);

            deviceUserCaseService.partiallyUpdateDevice(id, null, null, null);

            assertEquals(originalName, testDevice.getName());
            assertEquals(originalBrand, testDevice.getBrand());
            assertEquals(originalState, testDevice.getState());
            verify(deviceRepository, times(1)).save(testDevice);
        }
    }

    @Test
    @DisplayName("Partially update device should throw exception when device in use and updating name or brand")
    void partiallyUpdateDevice_shouldThrowException_whenDeviceInUseAndUpdatingNameOrBrand() {
        testDevice.changeState(State.IN_USE);
        Long id = 1L;
        String newName = "PartialName";
        String newBrand = "PartialBrand";

        try (var mockedStatic = mockStatic(Utils.class)) {
            mockedStatic.when(() -> Utils.isUpdatingNameAndBrand(newName, newBrand)).thenReturn(true);
            when(deviceRepository.findById(anyLong())).thenReturn(testDevice);

            assertThrows(DeviceInUseException.class, () -> deviceUserCaseService.partiallyUpdateDevice(id, newName, newBrand, null));
            verify(deviceRepository, never()).save(any(Device.class));
        }
    }

    @Test
    @DisplayName("Delete device should delete when not in use")
    void deleteDevice_shouldDeleteDevice_whenNotIN_USE() {
        Long id = 1L;
        testDevice.changeState(State.AVAILABLE);
        when(deviceRepository.findById(anyLong())).thenReturn(testDevice);


        deviceUserCaseService.deleteDevice(id);

        verify(deviceRepository, times(1)).delete(id);
    }

    @Test
    @DisplayName("Delete device should throw exception when device in use")
    void deleteDevice_shouldThrowException_whenDeviceIN_USE() {
        Long id = 1L;
        testDevice.changeState(State.IN_USE);
        when(deviceRepository.findById(anyLong())).thenReturn(testDevice);

        assertThrows(DeviceInUseException.class, () -> deviceUserCaseService.deleteDevice(id));
        verify(deviceRepository, never()).delete(anyLong());
    }
}

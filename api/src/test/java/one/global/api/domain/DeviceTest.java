package one.global.api.domain;

import one.global.api.domain.enums.State;
import one.global.api.domain.exception.DeviceInUseException;
import one.global.api.domain.model.Device;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class DeviceTest {

    private Device device;

    @BeforeEach
    void setUp() {
        this.device = new Device("Device1", "BrandA");
    }

    @Test
    @DisplayName("Should create a device with AVAILABLE state and creation date now")
    void constructor_ShouldCreateDeviceWithAvailableState() {

        assertEquals("Device1", device.getName());
        assertEquals("BrandA", device.getBrand());
        assertEquals(State.AVAILABLE, device.getState());
        assertNotNull(device.getCreationDate());
    }


    @Nested
    @DisplayName("Tests for method updateDetails")
    class UpdateDetailsTests {

        @Test
        @DisplayName("Should update when a device is available")
        void updateDetails_ShouldUpdateNameAndBrand_WhenDeviceIsAvailable() {

            device.updateDetails("Galaxy S24", "Samsung Inc.");

            assertThat(device.getName()).isEqualTo("Galaxy S24");
            assertThat(device.getBrand()).isEqualTo("Samsung Inc.");
        }

        @Test
        @DisplayName("Should throw exception when trying to update details of a device in use")
        void updateDetails_ShouldThrowException_WhenDeviceIsInUse() {
            getInUseDevice();

            assertThatThrownBy(() -> device.updateDetails("New name", "New brand"))
                    .isInstanceOf(DeviceInUseException.class)
                    .hasMessage("Cannot update name or brand: device is currently in use.");
        }

        @Test
        @DisplayName("Shouldn't update with null or blank values")
        void updateDetails_ShouldNotUpdateWithNullOrBlankValues() {

            device.updateDetails(null, " ");

            assertThat(device.getName()).isEqualTo("Device1");
            assertThat(device.getBrand()).isEqualTo("BrandA");
        }
    }

    @Nested
    @DisplayName("Tests for method changeState")
    class ChangeStateTests {

        @Test
        @DisplayName("Should change state successfully")
        void changeState_ShouldChangeStateSuccessfully() {
            device.changeState(State.IN_USE);

            assertThat(device.getState()).isEqualTo(State.IN_USE);
        }

        @Test
        @DisplayName("Should throw exception when trying to change state to null")
        void changeState_ShouldThrowException_WhenStateIsNull() {

            assertThatThrownBy(() -> device.changeState(null))
                    .isInstanceOf(DeviceInUseException.class)
                    .hasMessage("State cannot be null");
        }
    }

    @Nested
    @DisplayName("Tests for method ensureCanBeDeleted")
    class EnsureCanBeDeletedTests {

        @Test
        @DisplayName("Should allow deletion when device is not in use")
        void ensureCanBeDeleted_ShouldNotThrowException_WhenDeviceIsNotInUse() {

            device.ensureCanBeDeleted();
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar excluir um dispositivo em uso")
        void ensureCanBeDeleted_ShouldThrowException_WhenDeviceIsInUse() {
           var newDevice = getInUseDevice();

            assertThatThrownBy(newDevice::ensureCanBeDeleted)
                    .isInstanceOf(DeviceInUseException.class)
                    .hasMessage("Cannot delete device: it is currently in use.");
        }
    }


    private Device getInUseDevice() {
        var newDevice = this.device;
        newDevice.changeState(State.IN_USE);
        return newDevice;
    }


}

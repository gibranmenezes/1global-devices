package one.global.api.adapter.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import one.global.api.domain.enums.State;
import one.global.api.web.dto.DeviceRequestDTO;
import one.global.api.web.dto.DeviceUpdateDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@DisplayName("DeviceController Integration Tests")
class DeviceControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:17-alpine")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.flyway.enabled", () -> true);
        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }


    @BeforeEach
    void setUp() {
        jdbcTemplate.update("TRUNCATE TABLE device RESTART IDENTITY CASCADE");
    }

    @Test
    @DisplayName("Should create a new device successfully")
    void shouldCreateNewDeviceSuccessfully() throws Exception {
        DeviceRequestDTO requestDTO = new DeviceRequestDTO("Test Device", "BrandX");

        mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Device registered successfully"))
                .andExpect(jsonPath("$.content.name").value("Test Device"))
                .andExpect(jsonPath("$.content.brand").value("BrandX"))
                .andExpect(jsonPath("$.content.state").value(State.AVAILABLE.name()));
    }

    @Test
    @DisplayName("Should retrieve a device by ID successfully")
    void shouldRetrieveDeviceByIdSuccessfully() throws Exception {
        DeviceRequestDTO createRequest = new DeviceRequestDTO("Device to Get", "BrandY");
        String createResponse = mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();

        Long createdDeviceId = objectMapper.readTree(createResponse).at("/content/id").asLong();

        mockMvc.perform(MockMvcRequestBuilders.get("/devices/{id}", createdDeviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Device retrieved successfully"))
                .andExpect(jsonPath("$.content.id").value(createdDeviceId))
                .andExpect(jsonPath("$.content.name").value("Device to Get"));
    }

    @Test
    @DisplayName("Should return NOT_FOUND when device ID does not exist")
    void shouldReturnNotFoundWhenDeviceIdDoesNotExist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/devices/{id}", 999L))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should retrieve all devices with pagination")
    void shouldRetrieveAllDevicesWithPagination() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new DeviceRequestDTO("Device1", "BrandA"))));
        mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new DeviceRequestDTO("Device2", "BrandA"))));
        mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new DeviceRequestDTO("Device3", "BrandB"))));

        mockMvc.perform(MockMvcRequestBuilders.get("/devices?page=0&size=2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.parameters.pagination.totalElements").value(3))
                .andExpect(jsonPath("$.parameters.pagination.totalPages").value(2));
    }

    @Test
    @DisplayName("Should retrieve devices filtered by brand")
    void shouldRetrieveDevicesFilteredByBrand() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new DeviceRequestDTO("DeviceX", "BrandFilter"))));
        mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                .contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(new DeviceRequestDTO("DeviceY", "BrandOther"))));

        mockMvc.perform(MockMvcRequestBuilders.get("/devices?brand=BrandFilter"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].brand").value("BrandFilter"));
    }

    @Test
    @DisplayName("Should retrieve devices filtered by state")
    void shouldRetrieveDevicesFilteredByState() throws Exception {
        DeviceRequestDTO createRequest = new DeviceRequestDTO("DeviceState", "BrandZ");
        String createResponse = mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        Long createdDeviceId = objectMapper.readTree(createResponse).at("/content/id").asLong();

        DeviceUpdateDTO updateDTO = new DeviceUpdateDTO("Updated Name", "Updated Brand", State.IN_USE.name());
        mockMvc.perform(MockMvcRequestBuilders.put("/devices/{id}", createdDeviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.get("/devices?state=IN_USE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].state").value(State.IN_USE.name()));

        mockMvc.perform(MockMvcRequestBuilders.get("/devices?state=AVAILABLE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(0)));
    }


    @Test
    @DisplayName("Should update an existing device successfully")
    void shouldUpdateExistingDeviceSuccessfully() throws Exception {
        DeviceRequestDTO createRequest = new DeviceRequestDTO("Original Name", "Original Brand");
        String createResponse = mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        Long createdDeviceId = objectMapper.readTree(createResponse).at("/content/id").asLong();

        DeviceUpdateDTO updateDTO = new DeviceUpdateDTO("Updated Name", "Updated Brand", State.IN_USE.name());

        mockMvc.perform(MockMvcRequestBuilders.put("/devices/{id}", createdDeviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Device updated successfully"))
                .andExpect(jsonPath("$.content.id").value(createdDeviceId))
                .andExpect(jsonPath("$.content.name").value("Updated Name"))
                .andExpect(jsonPath("$.content.brand").value("Updated Brand"))
                .andExpect(jsonPath("$.content.state").value(State.IN_USE.name()));
    }

    @Test
    @DisplayName("Should partially update an existing device successfully")
    void shouldPartiallyUpdateExistingDeviceSuccessfully() throws Exception {
        DeviceRequestDTO createRequest = new DeviceRequestDTO("Original Name", "Original Brand");
        String createResponse = mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        Long createdDeviceId = objectMapper.readTree(createResponse).at("/content/id").asLong();

        DeviceUpdateDTO patchDTO = new DeviceUpdateDTO("New Name", null, State.INACTIVE.name());

        mockMvc.perform(MockMvcRequestBuilders.patch("/devices/{id}", createdDeviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(patchDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Device partially updated successfully"))
                .andExpect(jsonPath("$.content.id").value(createdDeviceId))
                .andExpect(jsonPath("$.content.name").value("New Name"))
                .andExpect(jsonPath("$.content.brand").value("Original Brand"))
                .andExpect(jsonPath("$.content.state").value(State.INACTIVE.name()));
    }

    @Test
    @DisplayName("Should return IS_CONFLICT when updating brand/name of a device in IN_USE state via PATCH")
    void shouldNotUpdateBrandOrNameOfInUseDeviceViaPatch() throws Exception {
        DeviceRequestDTO createRequest = new DeviceRequestDTO("Original Device", "Original Brand");
        String createResponse = mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        Long createdDeviceId = objectMapper.readTree(createResponse).at("/content/id").asLong();

        DeviceUpdateDTO updateDTO = new DeviceUpdateDTO("Updated Name", "Updated Brand", State.IN_USE.name());
        mockMvc.perform(MockMvcRequestBuilders.put("/devices/{id}", createdDeviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());


        DeviceUpdateDTO updateAttemptDTO = new DeviceUpdateDTO("New Name Attempt", null, null);
        mockMvc.perform(MockMvcRequestBuilders.patch("/devices/{id}", createdDeviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateAttemptDTO)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Conflict: Device in use"));
    }



    @Test
    @DisplayName("Should delete a device by ID successfully")
    void shouldDeleteDeviceByIdSuccessfully() throws Exception {
        DeviceRequestDTO createRequest = new DeviceRequestDTO("Device to Delete", "BrandToDelete");
        String createResponse = mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        Long createdDeviceId = objectMapper.readTree(createResponse).at("/content/id").asLong();

        mockMvc.perform(MockMvcRequestBuilders.delete("/devices/{id}", createdDeviceId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Device deleted successfully"));

        mockMvc.perform(MockMvcRequestBuilders.get("/devices/{id}", createdDeviceId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return IS_CONFLICT when attempting to delete a device in IN_USE state")
    void shouldNotDeleteInUseDevice() throws Exception {
        DeviceRequestDTO createRequest = new DeviceRequestDTO("Device to Delete", "BrandToDelete");
        String createResponse = mockMvc.perform(MockMvcRequestBuilders.post("/devices/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getContentAsString();
        Long createdDeviceId = objectMapper.readTree(createResponse).at("/content/id").asLong();

        DeviceUpdateDTO updateDTO = new DeviceUpdateDTO("Updated Name", "Updated Brand", State.IN_USE.name());
        mockMvc.perform(MockMvcRequestBuilders.put("/devices/{id}", createdDeviceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk());

        mockMvc.perform(MockMvcRequestBuilders.delete("/devices/{id}", createdDeviceId))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Conflict: Device in use"));
    }

    @Test
    @DisplayName("Should return NOT_FOUND when trying to delete a non-existent device")
    void shouldReturnNotFoundWhenDeletingNonExistentDevice() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/devices/{id}", 999L))
                .andExpect(status().isNotFound());
    }

}

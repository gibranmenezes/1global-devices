package one.global.api.adapter.in;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import one.global.api.web.dto.AppResponse;
import one.global.api.web.dto.DeviceRequestDTO;
import one.global.api.web.dto.DeviceResponseDTO;
import one.global.api.web.dto.DeviceUpdateDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Tag(name = "Devices", description = "API for managing device resouces")
public interface IDeviceController {

    @Operation(summary = "Create a new device", description = "Adds a new device to the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Device created successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid device data provided",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class)))
    })
    ResponseEntity<AppResponse<DeviceResponseDTO>> createDevice(@RequestBody DeviceRequestDTO deviceRequestDTO);


    @Operation(summary = "Get a device by its ID", description = "Retrieves detailed information for a single device.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device found successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class)))
    })
    ResponseEntity<AppResponse<DeviceResponseDTO>> getDevice(
            @Parameter(description = "ID of the device to retrieve", required = true) Long id);


    @Operation(summary = "Get all devices", description = "Retrieves a list of all registered devices or list all using parameters, with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of devices retrieved successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class)))
    })
    ResponseEntity<AppResponse<List<DeviceResponseDTO>>> getDevices(
            @Parameter(description = "Filter by brand", example = "Apple") String brand,
            @Parameter(description = "Filter by state", example = "AVAILABLE") String state,
            @Parameter(description = "Page number (0-indexed)", example = "0") int page,
            @Parameter(description = "Number of items per page", example = "10")  int size);



    @Operation(summary = "Update an existing device", description = "Updates details (name, brand, state) of a device by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid device data or state transition",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict, e.g., device is in use",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "500", description = "Internal server error",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class)))
    })
    ResponseEntity<AppResponse<DeviceResponseDTO>> updateDevice(
            @Parameter(description = "ID of the device to update", required = true) Long id,
            @RequestBody DeviceUpdateDTO updateDTO);


    @Operation(summary = "Partially update device state", description = "Changes the state of a device by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device state updated successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid state provided",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict, invalid state transition",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class)))
    })
    ResponseEntity<AppResponse<DeviceResponseDTO>> partiallyUpdateDevice(
            @Parameter(description = "ID of the device to update", required = true) Long id,
           @RequestBody DeviceUpdateDTO updateDTO);


    @Operation(summary = "Delete a device by its ID", description = "Removes a device from the system.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Device deleted successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "404", description = "Device not found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "409", description = "Conflict, e.g., device cannot be deleted due to its state",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class))),
            @ApiResponse(responseCode = "503", description = "Service unavailable",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AppResponse.class)))
    })
    ResponseEntity<AppResponse<Object>> deleteDevice(
            @Parameter(description = "ID of the device to delete", required = true) Long id);

}

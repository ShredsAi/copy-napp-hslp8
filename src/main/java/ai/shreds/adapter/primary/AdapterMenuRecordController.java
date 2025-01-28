package ai.shreds.adapter.primary;

import ai.shreds.application.ports.ApplicationInputPortMenuRecord;
import ai.shreds.shared.dtos.SharedCreateMenuRecordParams;
import ai.shreds.shared.dtos.SharedUpdateMenuRecordParams;
import ai.shreds.shared.dtos.SharedGetMenuRecordsParams;
import ai.shreds.shared.dtos.SharedMenuRecordResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/menu-records")
@Validated
@Tag(name = "Menu Records", description = "API endpoints for managing restaurant menu records")
public class AdapterMenuRecordController {

    private final ApplicationInputPortMenuRecord menuRecordPort;

    @Autowired
    public AdapterMenuRecordController(ApplicationInputPortMenuRecord menuRecordPort) {
        this.menuRecordPort = menuRecordPort;
    }

    @PostMapping
    @Operation(summary = "Create a new menu record", 
              description = "Creates a new menu record with the provided dishes and restaurant information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Menu record created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Restaurant not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SharedMenuRecordResponse> createMenuRecord(
            @Valid @RequestBody SharedCreateMenuRecordParams request) {
        SharedMenuRecordResponse response = menuRecordPort.createMenuRecord(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{menuRecordId}")
    @Operation(summary = "Update an existing menu record",
              description = "Updates an existing menu record with new information")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu record updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Menu record not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SharedMenuRecordResponse> updateMenuRecord(
            @Parameter(description = "ID of the menu record to update", required = true)
            @PathVariable("menuRecordId") String menuRecordId,
            @Valid @RequestBody SharedUpdateMenuRecordParams request) {
        SharedMenuRecordResponse response = menuRecordPort.updateMenuRecord(menuRecordId, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{menuRecordId}")
    @Operation(summary = "Delete a menu record",
              description = "Deletes an existing menu record by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Menu record deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Menu record not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<Void> deleteMenuRecord(
            @Parameter(description = "ID of the menu record to delete", required = true)
            @PathVariable("menuRecordId") String menuRecordId) {
        menuRecordPort.deleteMenuRecord(menuRecordId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @Operation(summary = "Get menu records",
              description = "Retrieves menu records based on filter criteria")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu records retrieved successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid filter parameters"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<SharedMenuRecordResponse>> getMenuRecords(
            @Valid @ModelAttribute SharedGetMenuRecordsParams request) {
        List<SharedMenuRecordResponse> response = menuRecordPort.getMenuRecords(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{menuRecordId}")
    @Operation(summary = "Get a menu record by ID",
              description = "Retrieves a specific menu record by its ID with optional location details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Menu record retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Menu record not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<SharedMenuRecordResponse> getMenuRecordById(
            @Parameter(description = "ID of the menu record to retrieve", required = true)
            @PathVariable("menuRecordId") String menuRecordId,
            @Parameter(description = "Include restaurant location details", required = false)
            @RequestParam(value = "withLocation", defaultValue = "false") boolean withLocation) {
        SharedMenuRecordResponse response = menuRecordPort.getMenuRecord(menuRecordId, withLocation);
        return ResponseEntity.ok(response);
    }
}

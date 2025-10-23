package prac.com_file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.FileNotificationResponseDto;
import prac.com_file.service.FileNotificationService;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class FileNotificationController {

    private final FileNotificationService fileNotificationService;

    public FileNotificationController(FileNotificationService fileNotificationService) {
        this.fileNotificationService = fileNotificationService;
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<ApiResponse<List<FileNotificationResponseDto>>> getNotificationsByFileId(@PathVariable Long fileId) {
        ApiResponse<List<FileNotificationResponseDto>> response = fileNotificationService.getNotificationsByFileId(fileId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FileNotificationResponseDto>>> getNotificationsByUserId(@PathVariable Long userId) {
        ApiResponse<List<FileNotificationResponseDto>> response = fileNotificationService.getNotificationsByUserId(userId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/user/{userId}/active")
    public ResponseEntity<ApiResponse<List<FileNotificationResponseDto>>> getActiveNotificationsByUser(@PathVariable Long userId) {
        ApiResponse<List<FileNotificationResponseDto>> response = fileNotificationService.getActiveNotificationsByUser(userId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<FileNotificationResponseDto>>> getNotificationsByStatus(@PathVariable String status) {
        // Validate that only SENT or FAILED status is requested
        if (!status.equalsIgnoreCase("SENT") && !status.equalsIgnoreCase("FAILED")) {
            ApiResponse<List<FileNotificationResponseDto>> errorResponse =
                    new ApiResponse<>(false, "Invalid status. Only 'SENT' and 'FAILED' are supported.", null);
            return ResponseEntity.status(400).body(errorResponse);
        }

        ApiResponse<List<FileNotificationResponseDto>> response = fileNotificationService.getNotificationsByStatus(status);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FileNotificationResponseDto>> getNotificationById(@PathVariable Long id) {
        ApiResponse<FileNotificationResponseDto> response = fileNotificationService.getNotificationById(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(@PathVariable Long id) {
        ApiResponse<String> response = fileNotificationService.deleteNotification(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/expiry-reminders/{days}")
    public ResponseEntity<ApiResponse<List<FileNotificationResponseDto>>> getExpiryRemindersByDays(@PathVariable Integer days) {
        ApiResponse<List<FileNotificationResponseDto>> response = fileNotificationService.getExpiryRemindersByDays(days);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @DeleteMapping("/cleanup/{daysOld}")
    public ResponseEntity<ApiResponse<String>> cleanupOldNotifications(@PathVariable int daysOld) {
        ApiResponse<String> response = fileNotificationService.cleanupOldNotifications(daysOld);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FileNotificationResponseDto>>> getAllNotifications() {
        ApiResponse<List<FileNotificationResponseDto>> response = fileNotificationService.getAllNotifications();
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }
}
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
        ApiResponse<List<FileNotificationResponseDto>> response = fileNotificationService.getNotificationsByStatus(status);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<FileNotificationResponseDto>>> getPendingNotifications() {
        ApiResponse<List<FileNotificationResponseDto>> response = fileNotificationService.getPendingNotifications();
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @PutMapping("/{id}/mark-read")
    public ResponseEntity<ApiResponse<FileNotificationResponseDto>> markNotificationAsRead(@PathVariable Long id) {
        ApiResponse<FileNotificationResponseDto> response = fileNotificationService.markNotificationAsRead(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @PutMapping("/user/{userId}/mark-all-read")
    public ResponseEntity<ApiResponse<String>> markAllNotificationsAsRead(@PathVariable Long userId) {
        ApiResponse<String> response = fileNotificationService.markAllNotificationsAsRead(userId);
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

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadNotificationCount(@PathVariable Long userId) {
        ApiResponse<Long> response = fileNotificationService.getUnreadNotificationCount(userId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }
}
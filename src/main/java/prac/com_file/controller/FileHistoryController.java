package prac.com_file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.FileHistoryResponseDto;
import prac.com_file.service.FileHistoryService;

import java.util.List;

@RestController
@RequestMapping("/api/file-history")
public class FileHistoryController {

    private final FileHistoryService fileHistoryService;

    public FileHistoryController(FileHistoryService fileHistoryService) {
        this.fileHistoryService = fileHistoryService;
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<ApiResponse<List<FileHistoryResponseDto>>> getFileHistoryByFileId(@PathVariable Long fileId) {
        ApiResponse<List<FileHistoryResponseDto>> response = fileHistoryService.getFileHistoryByFileId(fileId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/file/{fileId}/active")
    public ResponseEntity<ApiResponse<List<FileHistoryResponseDto>>> getFileHistoryByFileIdAndActiveStatus(@PathVariable Long fileId) {
        ApiResponse<List<FileHistoryResponseDto>> response = fileHistoryService.getFileHistoryByFileIdAndActiveStatus(fileId, true);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<FileHistoryResponseDto>>> getFileHistoryByModifiedUser(@PathVariable Long userId) {
        ApiResponse<List<FileHistoryResponseDto>> response = fileHistoryService.getFileHistoryByModifiedUser(userId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FileHistoryResponseDto>> getFileHistoryById(@PathVariable Long id) {
        ApiResponse<FileHistoryResponseDto> response = fileHistoryService.getFileHistoryById(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FileHistoryResponseDto>>> getAllFileHistory() {
        ApiResponse<List<FileHistoryResponseDto>> response = fileHistoryService.getAllFileHistory();
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFileHistory(@PathVariable Long id) {
        ApiResponse<String> response = fileHistoryService.deleteFileHistory(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @DeleteMapping("/file/{fileId}")
    public ResponseEntity<ApiResponse<String>> deleteAllHistoryForFile(@PathVariable Long fileId) {
        ApiResponse<String> response = fileHistoryService.deleteAllHistoryForFile(fileId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/file/{fileId}/count")
    public ResponseEntity<ApiResponse<Integer>> getFileHistoryCount(@PathVariable Long fileId) {
        ApiResponse<Integer> response = fileHistoryService.getFileHistoryCount(fileId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/file/{fileId}/previous")
    public ResponseEntity<ApiResponse<FileHistoryResponseDto>> getPreviousVersion(
            @PathVariable Long fileId,
            @RequestParam String currentVersion) {
        ApiResponse<FileHistoryResponseDto> response = fileHistoryService.getPreviousVersion(fileId, currentVersion);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }
}
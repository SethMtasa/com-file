package prac.com_file.controller;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.FileRequestDto;
import prac.com_file.dto.FileResponseDto;
import prac.com_file.service.FileService;

import java.util.List;

@RestController
@RequestMapping("/api/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    // Helper method to get username from security context
    private String getCurrentUsername() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else {
            return principal.toString(); // This will return "161119" from your JWT sub
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileResponseDto>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @ModelAttribute FileRequestDto fileRequestDto) {

        String username = getCurrentUsername();
        fileRequestDto.setFile(file);
        ApiResponse<FileResponseDto> response = fileService.uploadFile(fileRequestDto, file, username);
        return ResponseEntity.status(response.success() ? 200 : 400).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<FileResponseDto>> getFileById(@PathVariable Long id) {
        ApiResponse<FileResponseDto> response = fileService.getFileById(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<FileResponseDto>>> getAllFiles() {
        ApiResponse<List<FileResponseDto>> response = fileService.getAllFiles();
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/kar/{karUserId}")
    public ResponseEntity<ApiResponse<List<FileResponseDto>>> getFilesByAssignedKAR(@PathVariable Long karUserId) {
        ApiResponse<List<FileResponseDto>> response = fileService.getFilesByAssignedKAR(karUserId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/region/{regionId}")
    public ResponseEntity<ApiResponse<List<FileResponseDto>>> getFilesByRegion(@PathVariable Long regionId) {
        ApiResponse<List<FileResponseDto>> response = fileService.getFilesByRegion(regionId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<ApiResponse<List<FileResponseDto>>> getFilesByChannelPartnerType(@PathVariable Long typeId) {
        ApiResponse<List<FileResponseDto>> response = fileService.getFilesByChannelPartnerType(typeId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/expiring/{days}")
    public ResponseEntity<ApiResponse<List<FileResponseDto>>> getExpiringFiles(@PathVariable int days) {
        ApiResponse<List<FileResponseDto>> response = fileService.getExpiringFiles(days);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/expired")
    public ResponseEntity<ApiResponse<List<FileResponseDto>>> getExpiredFiles() {
        ApiResponse<List<FileResponseDto>> response = fileService.getExpiredFiles();
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<FileResponseDto>> updateFile(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @ModelAttribute FileRequestDto fileRequestDto) {

        String username = getCurrentUsername();
        fileRequestDto.setFile(file);
        ApiResponse<FileResponseDto> response = fileService.updateFile(id, fileRequestDto, file, username);
        return ResponseEntity.status(response.success() ? 200 : 400).body(response);
    }

    @PutMapping("/{id}/version")
    public ResponseEntity<ApiResponse<FileResponseDto>> updateFileVersion(
            @PathVariable Long id,
            @RequestParam("version") String version,
            @RequestParam("file") MultipartFile file) {

        String username = getCurrentUsername();
        ApiResponse<FileResponseDto> response = fileService.updateFileVersion(id, version, file, username);
        return ResponseEntity.status(response.success() ? 200 : 400).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteFile(@PathVariable Long id) {
        ApiResponse<String> response = fileService.deleteFile(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<FileResponseDto>>> searchFiles(
            @RequestParam(required = false) String fileName,
            @RequestParam(required = false) Long regionId,
            @RequestParam(required = false) Long typeId,
            @RequestParam(required = false) Long karUserId) {

        ApiResponse<List<FileResponseDto>> response = fileService.searchFiles(fileName, regionId, typeId, karUserId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/my-files")
    public ResponseEntity<ApiResponse<List<FileResponseDto>>> getMyFiles() {
        String username = getCurrentUsername();
        // If username is numeric ID like "1783648", convert to Long
        try {
            Long userId = Long.valueOf(username);
            ApiResponse<List<FileResponseDto>> response = fileService.getFilesByAssignedKAR(userId);
            return ResponseEntity.status(response.success() ? 200 : 404).body(response);
        } catch (NumberFormatException e) {
            // If username is not numeric, you'll need to look up the user by username first
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid user format", null));
        }
    }

    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long id) {
        return fileService.downloadFile(id);
    }

    @GetMapping("/user/{karUserId}/all")
    public ResponseEntity<ApiResponse<List<FileResponseDto>>> getFilesByAssignedKAROrUploadedBy(
            @PathVariable Long karUserId) {

        ApiResponse<List<FileResponseDto>> response = fileService.getFilesByAssignedKAROrUploadedBy(karUserId);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

}
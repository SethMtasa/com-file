package prac.com_file.service;

import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import prac.com_file.config.FileStorageConfig;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.FileRequestDto;
import prac.com_file.dto.FileResponseDto;
import prac.com_file.model.*;
import prac.com_file.repository.FileRepository;
import prac.com_file.repository.UserRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileServiceImpl implements FileService {

    private final FileRepository fileRepository;
    private final UserRepository userRepository;
    private final RegionService regionService;
    private final ChannelPartnerTypeService channelPartnerTypeService;
    private final FileHistoryService fileHistoryService;
    private final FileNotificationService fileNotificationService;
    private final EmailService emailService;
    private final FileStorageConfig fileStorageConfig;

    // File type mappings
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(
            "pdf", "doc", "docx", "xls", "xlsx", "csv", "txt"
    );

    private static final List<String> ALLOWED_CONTENT_TYPES = Arrays.asList(
            "application/pdf",
            "application/msword",
            "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            "application/vnd.ms-excel",
            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
            "text/csv",
            "text/plain",
            "application/vnd.ms-excel.sheet.macroEnabled.12",
            "application/vnd.ms-excel.sheet.binary.macroEnabled.12"
    );

    // Dangerous file types to block
    private static final List<String> BLOCKED_EXTENSIONS = Arrays.asList(
            "exe", "bat", "sh", "jar", "js", "php", "py", "html", "htm", "zip", "rar", "7z"
    );

    // Maximum file size (10MB)
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;

    public FileServiceImpl(FileRepository fileRepository,
                           UserRepository userRepository,
                           RegionService regionService,
                           ChannelPartnerTypeService channelPartnerTypeService,
                           FileHistoryService fileHistoryService,
                           FileNotificationService fileNotificationService,
                           EmailService emailService,
                           FileStorageConfig fileStorageConfig) {
        this.fileRepository = fileRepository;
        this.userRepository = userRepository;
        this.regionService = regionService;
        this.channelPartnerTypeService = channelPartnerTypeService;
        this.fileHistoryService = fileHistoryService;
        this.fileNotificationService = fileNotificationService;
        this.emailService = emailService;
        this.fileStorageConfig = fileStorageConfig;

        // Initialize upload directory
        initializeUploadDirectory();
    }

    private void initializeUploadDirectory() {
        try {
            Path uploadPath = Paths.get(fileStorageConfig.getPath());
            System.out.println("Upload path: " + uploadPath.toAbsolutePath());
            System.out.println("Path exists: " + Files.exists(uploadPath));

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("Created upload directory: " + uploadPath.toAbsolutePath());
            }

            // Test if directory is writable
            Path testFile = uploadPath.resolve("test_write.tmp");
            Files.createFile(testFile);
            Files.delete(testFile);
            System.out.println("Upload directory is writable");

        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory: " + fileStorageConfig.getPath(), e);
        }
    }

    @Override
    @Transactional
    public ApiResponse<FileResponseDto> uploadFile(FileRequestDto fileRequestDto, MultipartFile multipartFile, String uploadedByUsername) {
        try {
            // Validate file exists
            if (multipartFile == null || multipartFile.isEmpty()) {
                return new ApiResponse<>(false, "File is empty or not provided", null);
            }

            // Validate file size
            if (multipartFile.getSize() > MAX_FILE_SIZE) {
                return new ApiResponse<>(false, "File size exceeds maximum allowed size (10MB)", null);
            }

            // Validate file type
            ApiResponse<String> fileValidation = validateFileType(multipartFile);
            if (!fileValidation.success()) {
                return new ApiResponse<>(false, fileValidation.message(), null);
            }

            // Get user who is uploading
            Optional<User> uploadedByUser = userRepository.findByUsername(uploadedByUsername);
            if (uploadedByUser.isEmpty()) {
                return new ApiResponse<>(false, "Uploading user not found", null);
            }

            // Get assigned KAR user
            Optional<User> assignedKAR = userRepository.findById(fileRequestDto.getAssignedKarUserId());
            if (assignedKAR.isEmpty()) {
                return new ApiResponse<>(false, "Assigned KAR user not found", null);
            }

            // Get region and channel partner type with validation
            Region region = regionService.findRegionEntityById(fileRequestDto.getRegionId());
            if (region == null) {
                return new ApiResponse<>(false, "Region not found with ID: " + fileRequestDto.getRegionId(), null);
            }

            ChannelPartnerType channelPartnerType = channelPartnerTypeService.findChannelPartnerTypeEntityById(fileRequestDto.getChannelPartnerTypeId());
            if (channelPartnerType == null) {
                return new ApiResponse<>(false, "Channel Partner Type not found with ID: " + fileRequestDto.getChannelPartnerTypeId(), null);
            }

            // Save file to storage
            String fileUrl = saveFileToStorage(multipartFile);

            // Handle file name
            String fileName = fileRequestDto.getFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = multipartFile.getOriginalFilename();
            }
            if (fileName == null) {
                fileName = "unnamed_file";
            }

            // Create file entity
            File file = new File();
            file.setFileName(fileName);
            file.setFileUrl(fileUrl);
            file.setFileSize(multipartFile.getSize());
            file.setFileType(multipartFile.getContentType());
            file.setUploadDate(LocalDateTime.now());
            file.setValidityDate(fileRequestDto.getValidityDate());
            file.setExpiryDate(fileRequestDto.getExpiryDate());
            file.setFileVersion("1.0");
            file.setDescription(fileRequestDto.getDescription());
            file.setUploadedBy(uploadedByUser.get());
            file.setAssignedKAR(assignedKAR.get());
            file.setRegion(region);
            file.setChannelPartnerType(channelPartnerType);
            file.setActiveStatus(true);

            File savedFile = fileRepository.save(file);
            FileResponseDto responseDto = convertToDto(savedFile);


            // ✅ Send file assignment notification to KAR with comment
            sendFileAssignmentNotification(savedFile, uploadedByUser.get(), fileRequestDto.getComment());
            return new ApiResponse<>(true, "File uploaded successfully", responseDto);
        } catch (Exception e) {
            e.printStackTrace(); // Better logging
            return new ApiResponse<>(false, "Failed to upload file: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ApiResponse<FileResponseDto> getFileById(Long id) {
        Optional<File> file = fileRepository.findById(id);
        return file.map(value -> new ApiResponse<>(true, "File found", convertToDto(value)))
                .orElseGet(() -> new ApiResponse<>(false, "File not found with ID: " + id, null));
    }

    @Override
    public ApiResponse<List<FileResponseDto>> getAllFiles() {
        List<File> files = fileRepository.findAll();
        if (!files.isEmpty()) {
            List<FileResponseDto> fileDtos = files.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Files retrieved successfully", fileDtos);
        } else {
            return new ApiResponse<>(false, "No files found", null);
        }
    }

    @Override
    public ApiResponse<List<FileResponseDto>> getFilesByAssignedKAR(Long karUserId) {
        List<File> files = fileRepository.findByAssignedKARIdAndActiveStatus(karUserId, true);
        if (!files.isEmpty()) {
            List<FileResponseDto> fileDtos = files.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Files retrieved successfully", fileDtos);
        } else {
            return new ApiResponse<>(false, "No files found for the specified KAR", null);
        }
    }

    @Override
    public ApiResponse<List<FileResponseDto>> getFilesByRegion(Long regionId) {
        List<File> files = fileRepository.findByRegionIdAndActiveStatus(regionId, true);
        if (!files.isEmpty()) {
            List<FileResponseDto> fileDtos = files.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Files retrieved successfully", fileDtos);
        } else {
            return new ApiResponse<>(false, "No files found for the specified region", null);
        }
    }

    @Override
    public ApiResponse<List<FileResponseDto>> getFilesByChannelPartnerType(Long typeId) {
        List<File> files = fileRepository.findByChannelPartnerTypeIdAndActiveStatus(typeId, true);
        if (!files.isEmpty()) {
            List<FileResponseDto> fileDtos = files.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Files retrieved successfully", fileDtos);
        } else {
            return new ApiResponse<>(false, "No files found for the specified channel partner type", null);
        }
    }

    @Override
    public ApiResponse<List<FileResponseDto>> getExpiringFiles(int daysThreshold) {
        List<File> files = fileRepository.findFilesExpiringInDays(daysThreshold);
        if (!files.isEmpty()) {
            List<FileResponseDto> fileDtos = files.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Expiring files retrieved successfully", fileDtos);
        } else {
            return new ApiResponse<>(false, "No expiring files found", null);
        }
    }

    @Override
    public ApiResponse<List<File>> getExpiringFilesEntities(int daysThreshold) {
        try {
            List<File> files = fileRepository.findFilesExpiringInDays(daysThreshold);
            if (!files.isEmpty()) {
                return new ApiResponse<>(true, "Expiring files retrieved successfully", files);
            } else {
                return new ApiResponse<>(false, "No expiring files found", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve expiring files: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<FileResponseDto>> getFilesByAssignedKAROrUploadedBy(Long karUserId) {
        try {
            // Validate user exists
            Optional<User> karUser = userRepository.findById(karUserId);
            if (karUser.isEmpty()) {
                return new ApiResponse<>(false, "User not found with ID: " + karUserId, null);
            }

            List<File> files = fileRepository.findFilesByAssignedKAROrUploadedBy(karUserId);

            if (!files.isEmpty()) {
                List<FileResponseDto> fileDtos = files.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());

                // Count files by type for the response message
                long assignedFiles = files.stream()
                        .filter(file -> file.getAssignedKAR().getId().equals(karUserId))
                        .count();
                long uploadedFiles = files.stream()
                        .filter(file -> file.getUploadedBy().getId().equals(karUserId))
                        .count();

                String message = String.format(
                        "Files retrieved successfully. Assigned: %d, Uploaded: %d, Total: %d",
                        assignedFiles, uploadedFiles, files.size()
                );

                return new ApiResponse<>(true, message, fileDtos);
            } else {
                return new ApiResponse<>(false, "No files found for user ID: " + karUserId, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve files: " + e.getMessage(), null);
        }
    }


    @Override
    public ResponseEntity<Resource> downloadFile(Long fileId) {
        try {
            // Find the file by ID
            Optional<File> fileOptional = fileRepository.findById(fileId);
            if (fileOptional.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            File file = fileOptional.get();

            // Check if file is active
            if (!file.isActiveStatus()) {
                return ResponseEntity.status(HttpStatus.GONE).body(null); // 410 Gone
            }

            // Get the file path from the file URL
            String fileUrl = file.getFileUrl();
            Path filePath = Paths.get(fileStorageConfig.getPath()).resolve(fileUrl).normalize();

            // Security check: ensure the file is within the allowed directory
            Path storagePath = Paths.get(fileStorageConfig.getPath()).normalize();
            if (!filePath.startsWith(storagePath)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Check if file exists and is readable
            if (!Files.exists(filePath)) {
                return ResponseEntity.notFound().build();
            }

            if (!Files.isReadable(filePath)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(null);
            }

            // Create Resource from file path
            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }

            // Set headers
            String filename = file.getFileName();
            String contentType = file.getFileType();
            String safeFilename = filename != null ?
                    filename.replaceAll("[^a-zA-Z0-9.-]", "_") : "file_" + fileId;

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + safeFilename + "\"");
            headers.add(HttpHeaders.CONTENT_TYPE, contentType != null ? contentType : "application/octet-stream");
            headers.add(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate");
            headers.add(HttpHeaders.PRAGMA, "no-cache");
            headers.add(HttpHeaders.EXPIRES, "0");
            headers.add("X-File-Name", safeFilename);
            headers.add("X-File-Size", String.valueOf(file.getFileSize()));

            // Log download activity
            System.out.println("File downloaded - ID: " + fileId + ", Name: " + file.getFileName());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(file.getFileSize())
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .body(resource);

        } catch (Exception e) {
            System.err.println("Download failed for file ID " + fileId + ": " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @Override
    public ApiResponse<List<FileResponseDto>> getExpiredFiles() {
        List<File> files = fileRepository.findExpiredFiles();
        if (!files.isEmpty()) {
            List<FileResponseDto> fileDtos = files.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Expired files retrieved successfully", fileDtos);
        } else {
            return new ApiResponse<>(false, "No expired files found", null);
        }
    }

    @Override
    @Transactional
    public ApiResponse<FileResponseDto> updateFile(Long fileId, FileRequestDto fileRequestDto, MultipartFile multipartFile, String updatedByUsername) {
        try {
            Optional<File> fileOptional = fileRepository.findById(fileId);
            if (fileOptional.isEmpty()) {
                return new ApiResponse<>(false, "File not found with ID: " + fileId, null);
            }

            File existingFile = fileOptional.get();
            User updatedByUser = userRepository.findByUsername(updatedByUsername)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Track if KAR assignment changed
            boolean karChanged = fileRequestDto.getAssignedKarUserId() != null &&
                    !fileRequestDto.getAssignedKarUserId().equals(existingFile.getAssignedKAR().getId());

            // Create file history before updating
            fileHistoryService.createFileHistoryFromFile(existingFile, updatedByUser, "File updated");

            // Update file fields
            if (fileRequestDto.getFileName() != null) {
                existingFile.setFileName(fileRequestDto.getFileName());
            }
            if (fileRequestDto.getDescription() != null) {
                existingFile.setDescription(fileRequestDto.getDescription());
            }
            if (fileRequestDto.getExpiryDate() != null) {
                existingFile.setExpiryDate(fileRequestDto.getExpiryDate());
            }
            if (fileRequestDto.getValidityDate() != null) {
                existingFile.setValidityDate(fileRequestDto.getValidityDate());
            }
            if (fileRequestDto.getAssignedKarUserId() != null) {
                User assignedKAR = userRepository.findById(fileRequestDto.getAssignedKarUserId())
                        .orElseThrow(() -> new RuntimeException("Assigned KAR user not found"));
                existingFile.setAssignedKAR(assignedKAR);
            }
            if (fileRequestDto.getRegionId() != null) {
                Region region = regionService.findRegionEntityById(fileRequestDto.getRegionId());
                if (region == null) {
                    return new ApiResponse<>(false, "Region not found with ID: " + fileRequestDto.getRegionId(), null);
                }
                existingFile.setRegion(region);
            }
            if (fileRequestDto.getChannelPartnerTypeId() != null) {
                ChannelPartnerType channelPartnerType = channelPartnerTypeService.findChannelPartnerTypeEntityById(fileRequestDto.getChannelPartnerTypeId());
                if (channelPartnerType == null) {
                    return new ApiResponse<>(false, "Channel Partner Type not found with ID: " + fileRequestDto.getChannelPartnerTypeId(), null);
                }
                existingFile.setChannelPartnerType(channelPartnerType);
            }

            // Handle file update if new file is provided
            if (multipartFile != null && !multipartFile.isEmpty()) {
                // Validate file size
                if (multipartFile.getSize() > MAX_FILE_SIZE) {
                    return new ApiResponse<>(false, "File size exceeds maximum allowed size (10MB)", null);
                }

                // Validate new file type
                ApiResponse<String> fileValidation = validateFileType(multipartFile);
                if (!fileValidation.success()) {
                    return new ApiResponse<>(false, fileValidation.message(), null);
                }

                String newFileUrl = saveFileToStorage(multipartFile);
                existingFile.setFileUrl(newFileUrl);
                existingFile.setFileSize(multipartFile.getSize());
                existingFile.setFileType(multipartFile.getContentType());

                // Increment version
                String currentVersion = existingFile.getFileVersion();
                String newVersion = incrementVersion(currentVersion);
                existingFile.setFileVersion(newVersion);
            }

            File updatedFile = fileRepository.save(existingFile);

            // ✅ Send notification if KAR assignment changed
            if (karChanged) {
                String comment = fileRequestDto.getComment() != null ?
                        fileRequestDto.getComment() : "File reassigned to you. Please review.";
                sendFileAssignmentNotification(updatedFile, updatedByUser, comment);
            }

            return new ApiResponse<>(true, "File updated successfully", convertToDto(updatedFile));
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, "Failed to update file: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ApiResponse<String> deleteFile(Long id) {
        try {
            Optional<File> fileOptional = fileRepository.findById(id);
            if (fileOptional.isPresent()) {
                File file = fileOptional.get();
                file.setActiveStatus(false);
                fileRepository.save(file);
                return new ApiResponse<>(true, "File deleted successfully", null);
            } else {
                return new ApiResponse<>(false, "File not found with ID: " + id, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, "Failed to delete file: " + e.getMessage(), null);
        }
    }

    @Override
    @Transactional
    public ApiResponse<FileResponseDto> updateFileVersion(Long fileId, String newVersion, MultipartFile newFile, String updatedByUsername) {
        try {
            Optional<File> fileOptional = fileRepository.findById(fileId);
            if (fileOptional.isEmpty()) {
                return new ApiResponse<>(false, "File not found with ID: " + fileId, null);
            }

            // Validate file size
            if (newFile.getSize() > MAX_FILE_SIZE) {
                return new ApiResponse<>(false, "File size exceeds maximum allowed size (10MB)", null);
            }

            // Validate new file type
            ApiResponse<String> fileValidation = validateFileType(newFile);
            if (!fileValidation.success()) {
                return new ApiResponse<>(false, fileValidation.message(), null);
            }

            File existingFile = fileOptional.get();
            User updatedByUser = userRepository.findByUsername(updatedByUsername)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            // Create file history before updating version
            fileHistoryService.createFileHistoryFromFile(existingFile, updatedByUser, "Version updated to " + newVersion);

            // Save new file
            String newFileUrl = saveFileToStorage(newFile);

            // Update file with new version
            existingFile.setFileVersion(newVersion);
            existingFile.setFileUrl(newFileUrl);
            existingFile.setFileSize(newFile.getSize());
            existingFile.setFileType(newFile.getContentType());

            File updatedFile = fileRepository.save(existingFile);
            return new ApiResponse<>(true, "File version updated successfully", convertToDto(updatedFile));
        } catch (Exception e) {
            e.printStackTrace();
            return new ApiResponse<>(false, "Failed to update file version: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<FileResponseDto>> searchFiles(String fileName, Long regionId, Long typeId, Long karUserId) {
        List<File> files = fileRepository.searchFiles(fileName, regionId, typeId, karUserId);
        if (!files.isEmpty()) {
            List<FileResponseDto> fileDtos = files.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Search results retrieved successfully", fileDtos);
        } else {
            return new ApiResponse<>(false, "No files found matching the search criteria", null);
        }
    }

    /**
     * Sends notification to KAR when a file is assigned to them
     */
    /**
     * Sends notification to KAR when a file is assigned to them
     */
    private void sendFileAssignmentNotification(File file, User uploadedBy, String comment) {
        try {
            // Create notification record
            fileNotificationService.createNotification(
                    file,
                    file.getAssignedKAR(),
                    FileNotification.NotificationType.ASSIGNMENT,
                    "New File Assigned to You",
                    String.format(
                            "A new file has been assigned to you by %s %s.\n\n" +
                                    "File Details:\n" +
                                    "• File Name: %s\n" +
                                    "• Description: %s\n" +
                                    "• Region: %s\n" +
                                    "• Channel Partner Type: %s\n" +
                                    "• Upload Date: %s\n" +
                                    "• Expiry Date: %s\n\n" +
                                    "Instructions from Uploader:\n" +
                                    "%s\n\n" +
                                    "Please review the file and ensure it meets all requirements.",
                            uploadedBy.getFirstName(),
                            uploadedBy.getLastName(),
                            file.getFileName(),
                            file.getDescription() != null ? file.getDescription() : "N/A",
                            file.getRegion().getRegionName(),
                            file.getChannelPartnerType().getTypeName(),
                            file.getUploadDate().toString(),
                            file.getExpiryDate().toString(),
                            comment
                    ),
                    null // No days until expiry for assignments
            );

            // Send email notification - FIXED: Added comment parameter
            if (file.getAssignedKAR().getEmail() != null) {
                boolean emailSent = emailService.sendFileAssignmentNotification(
                        file.getAssignedKAR().getEmail(),
                        file.getFileName(),
                        uploadedBy.getFirstName() + " " + uploadedBy.getLastName(),
                        file.getDescription(),
                        file.getRegion().getRegionName(),
                        file.getChannelPartnerType().getTypeName(),
                        file.getExpiryDate().toString(),
                        file.getFileUrl(),
                        comment // ADDED: This was missing
                );

                if (emailSent) {
                    System.out.println("File assignment notification sent to KAR: " + file.getAssignedKAR().getEmail());
                } else {
                    System.err.println("Failed to send assignment email to KAR: " + file.getAssignedKAR().getEmail());
                }
            }
        } catch (Exception e) {
            System.err.println("Error sending file assignment notification: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Validates that the uploaded file is one of the allowed types
     */
    private ApiResponse<String> validateFileType(MultipartFile file) {
        String originalFileName = file.getOriginalFilename();
        String contentType = file.getContentType();

        // Check if file has a name
        if (originalFileName == null || originalFileName.trim().isEmpty()) {
            return new ApiResponse<>(false, "File name cannot be empty", null);
        }

        // Get file extension
        String fileExtension = "";
        if (originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();
        }

        // Check for blocked extensions first (security)
        if (BLOCKED_EXTENSIONS.contains(fileExtension)) {
            return new ApiResponse<>(false, "File type " + fileExtension + " is not allowed for security reasons", null);
        }

        // Check allowed extensions
        if (!ALLOWED_EXTENSIONS.contains(fileExtension)) {
            return new ApiResponse<>(false,
                    "File type " + fileExtension + " is not allowed. Allowed types: " + String.join(", ", ALLOWED_EXTENSIONS),
                    null);
        }

        // Check content type (additional security)
        if (contentType != null && !isContentTypeAllowed(contentType)) {
            return new ApiResponse<>(false,
                    "File content type " + contentType + " is not allowed",
                    null);
        }

        return new ApiResponse<>(true, "Valid file type", null);
    }

    /**
     * Checks if content type is allowed
     */
    private boolean isContentTypeAllowed(String contentType) {
        return ALLOWED_CONTENT_TYPES.stream()
                .anyMatch(allowedType -> {
                    // Exact match or partial match for text files
                    if (allowedType.equals(contentType)) {
                        return true;
                    }
                    // Allow text/plain for txt files, text/csv for csv files
                    if (contentType.startsWith("text/") &&
                            (allowedType.equals("text/plain") || allowedType.equals("text/csv"))) {
                        return true;
                    }
                    return false;
                });
    }

    /**
     * Saves file to storage and returns the stored filename/path
     */
    private String saveFileToStorage(MultipartFile file) throws IOException {
        try {
            String originalFileName = file.getOriginalFilename();

            // Preserve original extension
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            // Create filename with timestamp and original extension
            String baseName = originalFileName != null ?
                    originalFileName.replaceAll("\\.[^.]+$", "") : "document";
            String fileName = System.currentTimeMillis() + "_" + baseName + fileExtension;

            // Clean filename (remove special characters)
            fileName = fileName.replaceAll("[^a-zA-Z0-9._-]", "_");

            Path filePath = Paths.get(fileStorageConfig.getPath(), fileName);

            // Ensure directory exists
            Files.createDirectories(filePath.getParent());

            // Copy file to the destination
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Return only the filename (not absolute path)
            return fileName;

        } catch (IOException e) {
            throw new IOException("Failed to save file to storage: " + e.getMessage(), e);
        }
    }

    private String incrementVersion(String currentVersion) {
        try {
            // Simple version increment logic: v1.0 -> v1.1, v1.1 -> v1.2, etc.
            if (currentVersion.startsWith("v")) {
                String versionNumber = currentVersion.substring(1);
                String[] parts = versionNumber.split("\\.");
                if (parts.length == 2) {
                    int minor = Integer.parseInt(parts[1]);
                    return "v" + parts[0] + "." + (minor + 1);
                }
            }
            // Fallback: just add .1 if version format is unexpected
            return currentVersion + ".1";
        } catch (Exception e) {
            return currentVersion + ".1";
        }
    }

    private FileResponseDto convertToDto(File file) {
        FileResponseDto dto = new FileResponseDto();
        dto.setId(file.getId());
        dto.setFileName(file.getFileName());
        dto.setFileUrl(file.getFileUrl());
        dto.setFileSize(file.getFileSize());
        dto.setFileType(file.getFileType());
        dto.setUploadDate(file.getUploadDate());
        dto.setValidityDate(file.getValidityDate());
        dto.setExpiryDate(file.getExpiryDate());
        dto.setFileVersion(file.getFileVersion());
        dto.setDescription(file.getDescription());
        dto.setExpired(file.isExpired());
        dto.setValid(file.isValid());
//        dto.setActiveStatus(file.isActiveStatus());

        // Convert nested entities to DTOs
        if (file.getUploadedBy() != null) {
            dto.setUploadedBy(new prac.com_file.dto.UserResponseDto(file.getUploadedBy()));
        }
        if (file.getAssignedKAR() != null) {
            dto.setAssignedKAR(new prac.com_file.dto.UserResponseDto(file.getAssignedKAR()));
        }
        if (file.getRegion() != null) {
            prac.com_file.dto.RegionResponseDto regionDto = new prac.com_file.dto.RegionResponseDto();
            regionDto.setId(file.getRegion().getId());
            regionDto.setRegionName(file.getRegion().getRegionName());
            regionDto.setRegionCode(file.getRegion().getRegionCode());
            regionDto.setDescription(file.getRegion().getDescription());
            dto.setRegion(regionDto);
        }
        if (file.getChannelPartnerType() != null) {
            prac.com_file.dto.ChannelPartnerTypeResponseDto typeDto = new prac.com_file.dto.ChannelPartnerTypeResponseDto();
            typeDto.setId(file.getChannelPartnerType().getId());
            typeDto.setTypeName(file.getChannelPartnerType().getTypeName());
            typeDto.setDescription(file.getChannelPartnerType().getDescription());
            dto.setChannelPartnerType(typeDto);
        }

        return dto;
    }
}
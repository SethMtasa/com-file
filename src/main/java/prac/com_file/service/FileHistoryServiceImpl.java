package prac.com_file.service;

import org.springframework.stereotype.Service;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.FileHistoryResponseDto;
import prac.com_file.dto.FileResponseDto;
import prac.com_file.model.File;
import prac.com_file.model.FileHistory;
import prac.com_file.model.User;
import prac.com_file.repository.FileHistoryRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class FileHistoryServiceImpl implements FileHistoryService {

    private final FileHistoryRepository fileHistoryRepository;

    public FileHistoryServiceImpl(FileHistoryRepository fileHistoryRepository) {
        this.fileHistoryRepository = fileHistoryRepository;
    }

    @Override
    public ApiResponse<FileHistoryResponseDto> createFileHistory(File file, User modifiedBy, String changeDescription) {
        try {
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileVersion(file.getFileVersion());
            fileHistory.setFileUrl(file.getFileUrl());
            fileHistory.setFileName(file.getFileName());
            fileHistory.setFileSize(file.getFileSize());
            fileHistory.setFileType(file.getFileType());
            fileHistory.setModifiedDate(LocalDateTime.now());
            fileHistory.setChangeDescription(changeDescription);
            fileHistory.setFile(file);
            fileHistory.setModifiedBy(modifiedBy);

            FileHistory savedHistory = fileHistoryRepository.save(fileHistory);
            FileHistoryResponseDto responseDto = convertToDto(savedHistory);

            return new ApiResponse<>(true, "File history created successfully", responseDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to create file history: " + e.getMessage(), null);
        }
    }

    @Override
    public void createFileHistoryFromFile(File file, User modifiedBy, String changeDescription) {
        try {
            FileHistory fileHistory = new FileHistory();
            fileHistory.setFileVersion(file.getFileVersion());
            fileHistory.setFileUrl(file.getFileUrl());
            fileHistory.setFileName(file.getFileName());
            fileHistory.setFileSize(file.getFileSize());
            fileHistory.setFileType(file.getFileType());
            fileHistory.setModifiedDate(LocalDateTime.now());
            fileHistory.setChangeDescription(changeDescription);
            fileHistory.setFile(file);
            fileHistory.setModifiedBy(modifiedBy);

            fileHistoryRepository.save(fileHistory);
        } catch (Exception e) {
            // Log the error but don't throw to avoid interrupting the main file operation
            System.err.println("Failed to create file history: " + e.getMessage());
        }
    }

    @Override
    public ApiResponse<List<FileHistoryResponseDto>> getFileHistoryByFileId(Long fileId) {
        try {
            List<FileHistory> fileHistories = fileHistoryRepository.findByFileIdOrderByModifiedDateDesc(fileId);
            if (!fileHistories.isEmpty()) {
                List<FileHistoryResponseDto> historyDtos = fileHistories.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return new ApiResponse<>(true, "File history retrieved successfully", historyDtos);
            } else {
                return new ApiResponse<>(false, "No history found for file ID: " + fileId, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve file history: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<FileHistoryResponseDto>> getFileHistoryByFileIdAndActiveStatus(Long fileId, boolean activeStatus) {
        try {
            List<FileHistory> fileHistories = fileHistoryRepository.findByFileIdAndActiveStatusOrderByModifiedDateDesc(fileId, activeStatus);
            if (!fileHistories.isEmpty()) {
                List<FileHistoryResponseDto> historyDtos = fileHistories.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return new ApiResponse<>(true, "File history retrieved successfully", historyDtos);
            } else {
                return new ApiResponse<>(false, "No active history found for file ID: " + fileId, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve file history: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<FileHistoryResponseDto>> getFileHistoryByModifiedUser(Long userId) {
        try {
            List<FileHistory> fileHistories = fileHistoryRepository.findByModifiedByIdOrderByModifiedDateDesc(userId);
            if (!fileHistories.isEmpty()) {
                List<FileHistoryResponseDto> historyDtos = fileHistories.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return new ApiResponse<>(true, "File history retrieved successfully", historyDtos);
            } else {
                return new ApiResponse<>(false, "No history found for user ID: " + userId, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve file history: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<FileHistoryResponseDto> getFileHistoryById(Long id) {
        try {
            Optional<FileHistory> fileHistory = fileHistoryRepository.findById(id);
            return fileHistory.map(history -> new ApiResponse<>(true, "File history found", convertToDto(history)))
                    .orElseGet(() -> new ApiResponse<>(false, "File history not found with ID: " + id, null));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve file history: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<List<FileHistoryResponseDto>> getAllFileHistory() {
        try {
            List<FileHistory> fileHistories = fileHistoryRepository.findAllByOrderByModifiedDateDesc();
            if (!fileHistories.isEmpty()) {
                List<FileHistoryResponseDto> historyDtos = fileHistories.stream()
                        .map(this::convertToDto)
                        .collect(Collectors.toList());
                return new ApiResponse<>(true, "All file history retrieved successfully", historyDtos);
            } else {
                return new ApiResponse<>(false, "No file history found", null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve file history: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> deleteFileHistory(Long id) {
        try {
            Optional<FileHistory> fileHistoryOptional = fileHistoryRepository.findById(id);
            if (fileHistoryOptional.isPresent()) {
                FileHistory fileHistory = fileHistoryOptional.get();
                fileHistory.setActiveStatus(false);
                fileHistoryRepository.save(fileHistory);
                return new ApiResponse<>(true, "File history deleted successfully", null);
            } else {
                return new ApiResponse<>(false, "File history not found with ID: " + id, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to delete file history: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> deleteAllHistoryForFile(Long fileId) {
        try {
            List<FileHistory> fileHistories = fileHistoryRepository.findByFileId(fileId);
            if (!fileHistories.isEmpty()) {
                fileHistories.forEach(history -> history.setActiveStatus(false));
                fileHistoryRepository.saveAll(fileHistories);
                return new ApiResponse<>(true, "All history for file ID " + fileId + " deleted successfully", null);
            } else {
                return new ApiResponse<>(false, "No history found for file ID: " + fileId, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to delete file history: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<Integer> getFileHistoryCount(Long fileId) {
        try {
            int count = fileHistoryRepository.countByFileIdAndActiveStatus(fileId, true);
            return new ApiResponse<>(true, "File history count retrieved successfully", count);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to get file history count: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<FileHistoryResponseDto> getPreviousVersion(Long fileId, String currentVersion) {
        try {
            Optional<FileHistory> previousVersion = fileHistoryRepository.findPreviousVersion(fileId, currentVersion);
            return previousVersion.map(history -> new ApiResponse<>(true, "Previous version found", convertToDto(history)))
                    .orElseGet(() -> new ApiResponse<>(false, "No previous version found for file ID: " + fileId, null));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to retrieve previous version: " + e.getMessage(), null);
        }
    }

    /**
     * Converts FileHistory entity to FileHistoryResponseDto
     */
    private FileHistoryResponseDto convertToDto(FileHistory fileHistory) {
        FileHistoryResponseDto dto = new FileHistoryResponseDto();
        dto.setId(fileHistory.getId());
        dto.setFileVersion(fileHistory.getFileVersion());
        dto.setFileUrl(fileHistory.getFileUrl());
        dto.setFileName(fileHistory.getFileName());
        dto.setFileSize(fileHistory.getFileSize());
        dto.setFileType(fileHistory.getFileType());
        dto.setModifiedDate(fileHistory.getModifiedDate());
        dto.setChangeDescription(fileHistory.getChangeDescription());

        // Convert nested entities to DTOs
        if (fileHistory.getModifiedBy() != null) {
            prac.com_file.dto.UserResponseDto userDto = new prac.com_file.dto.UserResponseDto(fileHistory.getModifiedBy());
            dto.setModifiedBy(userDto);
        }

        if (fileHistory.getFile() != null) {
            // Create a minimal file DTO to avoid circular references
            FileResponseDto fileDto = new FileResponseDto();
            fileDto.setId(fileHistory.getFile().getId());
            fileDto.setFileName(fileHistory.getFile().getFileName());
            fileDto.setFileVersion(fileHistory.getFile().getFileVersion());
            fileDto.setUploadDate(fileHistory.getFile().getUploadDate());
            dto.setFile(fileDto);
        }

        return dto;
    }
}
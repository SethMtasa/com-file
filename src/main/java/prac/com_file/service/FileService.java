package prac.com_file.service;

import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.FileRequestDto;
import prac.com_file.dto.FileResponseDto;
import prac.com_file.model.File;

import java.util.List;

public interface FileService {

    ApiResponse<FileResponseDto> uploadFile(FileRequestDto fileRequestDto, MultipartFile multipartFile, String uploadedByUsername);

    ApiResponse<FileResponseDto> getFileById(Long id);

    ApiResponse<List<FileResponseDto>> getAllFiles();

    ApiResponse<List<FileResponseDto>> getFilesByAssignedKAR(Long karUserId);

    ApiResponse<List<FileResponseDto>> getFilesByRegion(Long regionId);

    ApiResponse<List<FileResponseDto>> getFilesByChannelPartnerType(Long typeId);

    ApiResponse<List<FileResponseDto>> getExpiringFiles(int daysThreshold);

    ApiResponse<List<FileResponseDto>> getExpiredFiles();

    ApiResponse<FileResponseDto> updateFile(Long fileId, FileRequestDto fileRequestDto, MultipartFile multipartFile, String updatedByUsername);

    ApiResponse<String> deleteFile(Long id);

    ApiResponse<FileResponseDto> updateFileVersion(Long fileId, String newVersion, MultipartFile newFile, String updatedByUsername);

    ApiResponse<List<FileResponseDto>> searchFiles(String fileName, Long regionId, Long typeId, Long karUserId);

    // Add this method to the FileService interface
    ApiResponse<List<File>> getExpiringFilesEntities(int daysThreshold);



    ResponseEntity<Resource> downloadFile(Long fileId);
}
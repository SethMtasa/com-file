package prac.com_file.service;

import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.FileHistoryResponseDto;
import prac.com_file.model.File;
import prac.com_file.model.User;

import java.util.List;

public interface FileHistoryService {

    ApiResponse<FileHistoryResponseDto> createFileHistory(File file, User modifiedBy, String changeDescription);

    void createFileHistoryFromFile(File file, User modifiedBy, String changeDescription);

    ApiResponse<List<FileHistoryResponseDto>> getFileHistoryByFileId(Long fileId);

    ApiResponse<List<FileHistoryResponseDto>> getFileHistoryByFileIdAndActiveStatus(Long fileId, boolean activeStatus);

    ApiResponse<List<FileHistoryResponseDto>> getFileHistoryByModifiedUser(Long userId);

    ApiResponse<FileHistoryResponseDto> getFileHistoryById(Long id);

    ApiResponse<List<FileHistoryResponseDto>> getAllFileHistory();

    ApiResponse<String> deleteFileHistory(Long id);

    ApiResponse<String> deleteAllHistoryForFile(Long fileId);

    ApiResponse<Integer> getFileHistoryCount(Long fileId);

    ApiResponse<FileHistoryResponseDto> getPreviousVersion(Long fileId, String currentVersion);
}
package prac.com_file.service;

import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.RegionRequestDto;
import prac.com_file.dto.RegionResponseDto;
import prac.com_file.model.Region;

import java.util.List;

public interface RegionService {

    ApiResponse<RegionResponseDto> createRegion(RegionRequestDto regionRequestDto);

    ApiResponse<RegionResponseDto> getRegionById(Long id);

    ApiResponse<List<RegionResponseDto>> getAllRegions();

    ApiResponse<List<RegionResponseDto>> getAllActiveRegions();

    ApiResponse<RegionResponseDto> updateRegion(Long id, RegionRequestDto regionRequestDto);

    ApiResponse<String> deleteRegion(Long id);

    ApiResponse<RegionResponseDto> getRegionByCode(String regionCode);

    // Internal method for service-to-service communication
    Region findRegionEntityById(Long id);
}
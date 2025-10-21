package prac.com_file.service;

import org.springframework.stereotype.Service;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.RegionRequestDto;
import prac.com_file.dto.RegionResponseDto;
import prac.com_file.model.Region;
import prac.com_file.repository.RegionRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class RegionServiceImpl implements RegionService {

    private final RegionRepository regionRepository;

    public RegionServiceImpl(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }

    @Override
    public ApiResponse<RegionResponseDto> createRegion(RegionRequestDto regionRequestDto) {
        try {
            // Check if region code already exists
            Optional<Region> existingRegion = regionRepository.findByRegionCode(regionRequestDto.getRegionCode());
            if (existingRegion.isPresent()) {
                return new ApiResponse<>(false, "Region with code " + regionRequestDto.getRegionCode() + " already exists", null);
            }

            Region region = new Region();
            region.setRegionName(regionRequestDto.getRegionName());
            region.setRegionCode(regionRequestDto.getRegionCode());
            region.setDescription(regionRequestDto.getDescription());

            Region savedRegion = regionRepository.save(region);
            RegionResponseDto responseDto = convertToDto(savedRegion);

            return new ApiResponse<>(true, "Region created successfully", responseDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to create region: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<RegionResponseDto> getRegionById(Long id) {
        Optional<Region> region = regionRepository.findById(id);
        return region.map(value -> new ApiResponse<>(true, "Region found", convertToDto(value)))
                .orElseGet(() -> new ApiResponse<>(false, "Region not found with ID: " + id, null));
    }

    @Override
    public ApiResponse<List<RegionResponseDto>> getAllRegions() {
        List<Region> regions = regionRepository.findAll();
        if (!regions.isEmpty()) {
            List<RegionResponseDto> regionDtos = regions.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Regions retrieved successfully", regionDtos);
        } else {
            return new ApiResponse<>(false, "No regions found", null);
        }
    }

    @Override
    public ApiResponse<List<RegionResponseDto>> getAllActiveRegions() {
        List<Region> activeRegions = regionRepository.findByActiveStatus(true);
        if (!activeRegions.isEmpty()) {
            List<RegionResponseDto> regionDtos = activeRegions.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Active regions retrieved successfully", regionDtos);
        } else {
            return new ApiResponse<>(false, "No active regions found", null);
        }
    }

    @Override
    public ApiResponse<RegionResponseDto> updateRegion(Long id, RegionRequestDto regionRequestDto) {
        try {
            Optional<Region> regionOptional = regionRepository.findById(id);
            if (regionOptional.isEmpty()) {
                return new ApiResponse<>(false, "Region not found with ID: " + id, null);
            }

            Region region = regionOptional.get();

            // Check if new region code conflicts with existing ones (excluding current region)
            if (regionRequestDto.getRegionCode() != null &&
                    !regionRequestDto.getRegionCode().equals(region.getRegionCode())) {
                Optional<Region> regionWithSameCode = regionRepository.findByRegionCode(regionRequestDto.getRegionCode());
                if (regionWithSameCode.isPresent() && !regionWithSameCode.get().getId().equals(id)) {
                    return new ApiResponse<>(false, "Region code " + regionRequestDto.getRegionCode() + " already exists", null);
                }
                region.setRegionCode(regionRequestDto.getRegionCode());
            }

            if (regionRequestDto.getRegionName() != null) {
                region.setRegionName(regionRequestDto.getRegionName());
            }
            if (regionRequestDto.getDescription() != null) {
                region.setDescription(regionRequestDto.getDescription());
            }

            Region updatedRegion = regionRepository.save(region);
            return new ApiResponse<>(true, "Region updated successfully", convertToDto(updatedRegion));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to update region: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> deleteRegion(Long id) {
        try {
            Optional<Region> regionOptional = regionRepository.findById(id);
            if (regionOptional.isPresent()) {
                Region region = regionOptional.get();
                region.setActiveStatus(false);
                regionRepository.save(region);
                return new ApiResponse<>(true, "Region deleted successfully", null);
            } else {
                return new ApiResponse<>(false, "Region not found with ID: " + id, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to delete region: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<RegionResponseDto> getRegionByCode(String regionCode) {
        Optional<Region> region = regionRepository.findByRegionCode(regionCode);
        return region.map(value -> new ApiResponse<>(true, "Region found", convertToDto(value)))
                .orElseGet(() -> new ApiResponse<>(false, "Region not found with code: " + regionCode, null));
    }

    @Override
    public Region findRegionEntityById(Long id) {
        return regionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Region not found with ID: " + id));
    }

    private RegionResponseDto convertToDto(Region region) {
        RegionResponseDto dto = new RegionResponseDto();
        dto.setId(region.getId());
        dto.setRegionName(region.getRegionName());
        dto.setRegionCode(region.getRegionCode());
        dto.setDescription(region.getDescription());
        return dto;
    }
}
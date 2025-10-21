package prac.com_file.service;

import org.springframework.stereotype.Service;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.ChannelPartnerTypeRequestDto;
import prac.com_file.dto.ChannelPartnerTypeResponseDto;
import prac.com_file.model.ChannelPartnerType;
import prac.com_file.repository.ChannelPartnerTypeRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ChannelPartnerTypeServiceImpl implements ChannelPartnerTypeService {

    private final ChannelPartnerTypeRepository channelPartnerTypeRepository;

    public ChannelPartnerTypeServiceImpl(ChannelPartnerTypeRepository channelPartnerTypeRepository) {
        this.channelPartnerTypeRepository = channelPartnerTypeRepository;
    }

    @Override
    public ApiResponse<ChannelPartnerTypeResponseDto> createChannelPartnerType(ChannelPartnerTypeRequestDto requestDto) {
        try {
            // Check if type name already exists
            Optional<ChannelPartnerType> existingType = channelPartnerTypeRepository.findByTypeName(requestDto.getTypeName());
            if (existingType.isPresent()) {
                return new ApiResponse<>(false, "Channel partner type " + requestDto.getTypeName() + " already exists", null);
            }

            ChannelPartnerType type = new ChannelPartnerType();
            type.setTypeName(requestDto.getTypeName());
            type.setDescription(requestDto.getDescription());

            ChannelPartnerType savedType = channelPartnerTypeRepository.save(type);
            ChannelPartnerTypeResponseDto responseDto = convertToDto(savedType);

            return new ApiResponse<>(true, "Channel partner type created successfully", responseDto);
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to create channel partner type: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<ChannelPartnerTypeResponseDto> getChannelPartnerTypeById(Long id) {
        Optional<ChannelPartnerType> type = channelPartnerTypeRepository.findById(id);
        return type.map(value -> new ApiResponse<>(true, "Channel partner type found", convertToDto(value)))
                .orElseGet(() -> new ApiResponse<>(false, "Channel partner type not found with ID: " + id, null));
    }

    @Override
    public ApiResponse<List<ChannelPartnerTypeResponseDto>> getAllChannelPartnerTypes() {
        List<ChannelPartnerType> types = channelPartnerTypeRepository.findAll();
        if (!types.isEmpty()) {
            List<ChannelPartnerTypeResponseDto> typeDtos = types.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Channel partner types retrieved successfully", typeDtos);
        } else {
            return new ApiResponse<>(false, "No channel partner types found", null);
        }
    }

    @Override
    public ApiResponse<List<ChannelPartnerTypeResponseDto>> getAllActiveChannelPartnerTypes() {
        List<ChannelPartnerType> activeTypes = channelPartnerTypeRepository.findByActiveStatus(true);
        if (!activeTypes.isEmpty()) {
            List<ChannelPartnerTypeResponseDto> typeDtos = activeTypes.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
            return new ApiResponse<>(true, "Active channel partner types retrieved successfully", typeDtos);
        } else {
            return new ApiResponse<>(false, "No active channel partner types found", null);
        }
    }

    @Override
    public ApiResponse<ChannelPartnerTypeResponseDto> updateChannelPartnerType(Long id, ChannelPartnerTypeRequestDto requestDto) {
        try {
            Optional<ChannelPartnerType> typeOptional = channelPartnerTypeRepository.findById(id);
            if (typeOptional.isEmpty()) {
                return new ApiResponse<>(false, "Channel partner type not found with ID: " + id, null);
            }

            ChannelPartnerType type = typeOptional.get();

            // Check if new type name conflicts with existing ones (excluding current type)
            if (requestDto.getTypeName() != null && !requestDto.getTypeName().equals(type.getTypeName())) {
                Optional<ChannelPartnerType> typeWithSameName = channelPartnerTypeRepository.findByTypeName(requestDto.getTypeName());
                if (typeWithSameName.isPresent() && !typeWithSameName.get().getId().equals(id)) {
                    return new ApiResponse<>(false, "Channel partner type " + requestDto.getTypeName() + " already exists", null);
                }
                type.setTypeName(requestDto.getTypeName());
            }

            if (requestDto.getDescription() != null) {
                type.setDescription(requestDto.getDescription());
            }

            ChannelPartnerType updatedType = channelPartnerTypeRepository.save(type);
            return new ApiResponse<>(true, "Channel partner type updated successfully", convertToDto(updatedType));
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to update channel partner type: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<String> deleteChannelPartnerType(Long id) {
        try {
            Optional<ChannelPartnerType> typeOptional = channelPartnerTypeRepository.findById(id);
            if (typeOptional.isPresent()) {
                ChannelPartnerType type = typeOptional.get();
                type.setActiveStatus(false);
                channelPartnerTypeRepository.save(type);
                return new ApiResponse<>(true, "Channel partner type deleted successfully", null);
            } else {
                return new ApiResponse<>(false, "Channel partner type not found with ID: " + id, null);
            }
        } catch (Exception e) {
            return new ApiResponse<>(false, "Failed to delete channel partner type: " + e.getMessage(), null);
        }
    }

    @Override
    public ApiResponse<ChannelPartnerTypeResponseDto> getChannelPartnerTypeByName(String typeName) {
        Optional<ChannelPartnerType> type = channelPartnerTypeRepository.findByTypeName(typeName);
        return type.map(value -> new ApiResponse<>(true, "Channel partner type found", convertToDto(value)))
                .orElseGet(() -> new ApiResponse<>(false, "Channel partner type not found with name: " + typeName, null));
    }

    @Override
    public prac.com_file.model.ChannelPartnerType findChannelPartnerTypeEntityById(Long id) {
        return channelPartnerTypeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Channel partner type not found with ID: " + id));
    }

    private ChannelPartnerTypeResponseDto convertToDto(ChannelPartnerType type) {
        ChannelPartnerTypeResponseDto dto = new ChannelPartnerTypeResponseDto();
        dto.setId(type.getId());
        dto.setTypeName(type.getTypeName());
        dto.setDescription(type.getDescription());
        return dto;
    }
}
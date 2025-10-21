package prac.com_file.service;

import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.ChannelPartnerTypeRequestDto;
import prac.com_file.dto.ChannelPartnerTypeResponseDto;

import java.util.List;

public interface ChannelPartnerTypeService {

    ApiResponse<ChannelPartnerTypeResponseDto> createChannelPartnerType(ChannelPartnerTypeRequestDto requestDto);

    ApiResponse<ChannelPartnerTypeResponseDto> getChannelPartnerTypeById(Long id);

    ApiResponse<List<ChannelPartnerTypeResponseDto>> getAllChannelPartnerTypes();

    ApiResponse<List<ChannelPartnerTypeResponseDto>> getAllActiveChannelPartnerTypes();

    ApiResponse<ChannelPartnerTypeResponseDto> updateChannelPartnerType(Long id, ChannelPartnerTypeRequestDto requestDto);

    ApiResponse<String> deleteChannelPartnerType(Long id);

    ApiResponse<ChannelPartnerTypeResponseDto> getChannelPartnerTypeByName(String typeName);

    // Internal method for service-to-service communication
    prac.com_file.model.ChannelPartnerType findChannelPartnerTypeEntityById(Long id);
}
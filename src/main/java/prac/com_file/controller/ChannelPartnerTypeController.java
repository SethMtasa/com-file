package prac.com_file.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import prac.com_file.dto.ApiResponse;
import prac.com_file.dto.ChannelPartnerTypeRequestDto;
import prac.com_file.dto.ChannelPartnerTypeResponseDto;
import prac.com_file.service.ChannelPartnerTypeService;

import java.util.List;

@RestController
@RequestMapping("/api/channel-partner-types")
public class ChannelPartnerTypeController {

    private final ChannelPartnerTypeService channelPartnerTypeService;

    public ChannelPartnerTypeController(ChannelPartnerTypeService channelPartnerTypeService) {
        this.channelPartnerTypeService = channelPartnerTypeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChannelPartnerTypeResponseDto>> createChannelPartnerType(
            @RequestBody ChannelPartnerTypeRequestDto requestDto) {
        ApiResponse<ChannelPartnerTypeResponseDto> response = channelPartnerTypeService.createChannelPartnerType(requestDto);
        return ResponseEntity.status(response.success() ? 201 : 400).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ChannelPartnerTypeResponseDto>> getChannelPartnerTypeById(@PathVariable Long id) {
        ApiResponse<ChannelPartnerTypeResponseDto> response = channelPartnerTypeService.getChannelPartnerTypeById(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ChannelPartnerTypeResponseDto>>> getAllChannelPartnerTypes() {
        ApiResponse<List<ChannelPartnerTypeResponseDto>> response = channelPartnerTypeService.getAllChannelPartnerTypes();
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<List<ChannelPartnerTypeResponseDto>>> getAllActiveChannelPartnerTypes() {
        ApiResponse<List<ChannelPartnerTypeResponseDto>> response = channelPartnerTypeService.getAllActiveChannelPartnerTypes();
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ChannelPartnerTypeResponseDto>> updateChannelPartnerType(
            @PathVariable Long id,
            @RequestBody ChannelPartnerTypeRequestDto requestDto) {
        ApiResponse<ChannelPartnerTypeResponseDto> response = channelPartnerTypeService.updateChannelPartnerType(id, requestDto);
        return ResponseEntity.status(response.success() ? 200 : 400).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteChannelPartnerType(@PathVariable Long id) {
        ApiResponse<String> response = channelPartnerTypeService.deleteChannelPartnerType(id);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }

    @GetMapping("/name/{typeName}")
    public ResponseEntity<ApiResponse<ChannelPartnerTypeResponseDto>> getChannelPartnerTypeByName(@PathVariable String typeName) {
        ApiResponse<ChannelPartnerTypeResponseDto> response = channelPartnerTypeService.getChannelPartnerTypeByName(typeName);
        return ResponseEntity.status(response.success() ? 200 : 404).body(response);
    }
}
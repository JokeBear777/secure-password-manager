package com.InfoSec.dynamic_password.domain.share.controller;

import com.InfoSec.dynamic_password.domain.share.dto.*;
import com.InfoSec.dynamic_password.domain.share.service.StoreService;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import com.InfoSec.dynamic_password.global.utils.dto.StatusResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/stores")
@Slf4j
@AllArgsConstructor
public class StoreController {

    private final StoreService storeService;

    @PostMapping
    public ResponseEntity<?> createStore(
            @Valid @RequestBody CreateStoreDto createStoreDto,
            @AuthenticationPrincipal SecurityUserDto securityUserDto
    ) {
        Long storeId = storeService.createStore(createStoreDto, securityUserDto);
        return ResponseEntity.ok(StatusResponseDto.success(storeId));
    }

    @PostMapping("/{store_id}/invite")
    public ResponseEntity<?> inviteStore(
            @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @PathVariable("store_id") Long storeId
    ) {
        String inviteLink = storeService.generateInviteLink(securityUserDto, storeId);

        return ResponseEntity.ok(StatusResponseDto.success(
                new ResponseInviteStoreDto(inviteLink)
        ));
    }

    @PostMapping("/{store_id}/members/{token}")
    public ResponseEntity<?> requestJoin(
            @PathVariable("store_id") Long storeId,
            @PathVariable("token") String token,
            @AuthenticationPrincipal SecurityUserDto securityUserDto
    ) {
        storeService.requestJoin(storeId, token, securityUserDto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("{store_id}/members")
    public ResponseEntity<?> getRequestJoinMembers(
            @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @PathVariable("store_id") Long storeId
    ) {
        List<RequestJoinMembersDto> requestJoinMembersDtoList = storeService
                .getRequestJoinMembers(securityUserDto,storeId);
        return ResponseEntity.ok(StatusResponseDto.success(requestJoinMembersDtoList));
    }

    @PatchMapping("/{store_id}/members/approve")
    public ResponseEntity<?> approveJoinMembers(
            @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @PathVariable("store_id") Long storeId,
            @RequestBody ApproveMemberRequestDto[] approveMemberRequestDtos
    ) {
        storeService.approveRequestJoinMembers(securityUserDto,storeId, approveMemberRequestDtos);
        return ResponseEntity.ok(StatusResponseDto.success());
    }

    @PatchMapping("/{store_id}/members/disapprove")
    public ResponseEntity<?> disapproveJoinMembers(
            @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @PathVariable("store_id") Long storeId,
            @RequestBody DisapproveMemberRequestDto[] disapproveMemberRequestDtos
    ) {
        storeService.disApproveRequestJoinMembers(securityUserDto,storeId, disapproveMemberRequestDtos);
        return ResponseEntity.ok(StatusResponseDto.success());
    }



}

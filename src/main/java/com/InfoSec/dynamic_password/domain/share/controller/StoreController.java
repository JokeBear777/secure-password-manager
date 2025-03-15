package com.InfoSec.dynamic_password.domain.share.controller;

import com.InfoSec.dynamic_password.domain.password.dto.RequestSavePasswordDto;
import com.InfoSec.dynamic_password.domain.share.dto.CreateStoreDto;
import com.InfoSec.dynamic_password.domain.share.dto.ResponseInviteStoreDto;
import com.InfoSec.dynamic_password.domain.share.service.StoreService;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import com.InfoSec.dynamic_password.global.utils.dto.StatusResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        storeService.createStore(createStoreDto, securityUserDto);
        return ResponseEntity.ok().build();
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

}

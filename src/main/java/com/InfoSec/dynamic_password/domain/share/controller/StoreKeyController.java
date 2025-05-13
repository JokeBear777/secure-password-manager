package com.InfoSec.dynamic_password.domain.share.controller;

import com.InfoSec.dynamic_password.domain.share.dto.ExchangeKeyRequestDto;
import com.InfoSec.dynamic_password.domain.share.dto.GetStoreKeyExchangeRequestDto;
import com.InfoSec.dynamic_password.domain.share.service.StoreKeyExchangeService;
import com.InfoSec.dynamic_password.domain.share.service.StoreService;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import com.InfoSec.dynamic_password.global.utils.dto.StatusResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequestMapping("/stores/{store_id}/keys")
@Slf4j
@AllArgsConstructor
public class StoreKeyController {

    private final StoreKeyExchangeService storeKeyExchangeService;

    @PostMapping("/exchange}")
    public ResponseEntity<?> exchangeKeyRequest(
            @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @PathVariable("store_id") Long storeId,
            @Valid @RequestBody ExchangeKeyRequestDto exchangeKeyRequestDto
    ) {
        storeKeyExchangeService.saveExchangeKeyRequest(securityUserDto, storeId, exchangeKeyRequestDto.getPublicKey());
        return ResponseEntity.ok(StatusResponseDto.success());
    }

    @GetMapping("/exchange")
    public ResponseEntity<?> getExchangeKeyRequest(
            @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @PathVariable("store_id") Long storeId
    ) {
        Set<GetStoreKeyExchangeRequestDto> getStoreKeyExchangeRequestDtos
                = storeKeyExchangeService.getStoreKeyExchangeRequests(securityUserDto, storeId);
        return ResponseEntity.ok(StatusResponseDto.success(getStoreKeyExchangeRequestDtos));
    }

    @GetMapping("/store-encrypted-secret")
    public ResponseEntity<?> getStoreKeyExchangeStatus(
            @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @PathVariable("store_id") Long storeId
    ) {
        String encryptedStoreKey = storeKeyExchangeService.getStoreKeyExchangeStatus(securityUserDto,storeId);
        return ResponseEntity.ok(StatusResponseDto.success(encryptedStoreKey));
    }
}

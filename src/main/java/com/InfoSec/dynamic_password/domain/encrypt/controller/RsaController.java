package com.InfoSec.dynamic_password.domain.encrypt.controller;

import com.InfoSec.dynamic_password.domain.encrypt.dto.SaveRsaPublicDto;
import com.InfoSec.dynamic_password.domain.encrypt.dto.SaveStoreRsaPublicDto;
import com.InfoSec.dynamic_password.domain.encrypt.service.RsaService;
import com.InfoSec.dynamic_password.domain.encrypt.service.StoreRsaService;
import com.InfoSec.dynamic_password.global.security.auth.jwt.dto.SecurityUserDto;
import com.InfoSec.dynamic_password.global.utils.dto.StatusResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/encrypt/rsa")
@RequiredArgsConstructor
public class RsaController {

    private final RsaService rsaService;
    private final StoreRsaService storeRsaService;

    @PostMapping()
    public ResponseEntity<?> saveRsaPublic(
            @Valid @RequestBody SaveRsaPublicDto saveRsaPublicDto,
            @AuthenticationPrincipal SecurityUserDto securityUserDto
    ) {
        rsaService.saveRsaPublic(saveRsaPublicDto,securityUserDto);
        return ResponseEntity.ok(StatusResponseDto.success());
    }

    @PostMapping("/store/{store_id}")
    public ResponseEntity<?> saveStoreRsaPublic(
            @Valid @RequestBody SaveStoreRsaPublicDto saveStoreRsaPublicDto,
            @AuthenticationPrincipal SecurityUserDto securityUserDto,
            @PathVariable("store_id") Long storeId
    ) {
        storeRsaService.saveStoreRsaPublic(saveStoreRsaPublicDto,securityUserDto, storeId);
        return ResponseEntity.ok(StatusResponseDto.success());
    }


}

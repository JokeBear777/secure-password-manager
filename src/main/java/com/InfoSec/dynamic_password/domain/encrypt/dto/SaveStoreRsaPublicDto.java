package com.InfoSec.dynamic_password.domain.encrypt.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SaveStoreRsaPublicDto {

    @NotBlank(message = "Public key input is required")
    private String publicKey;
}

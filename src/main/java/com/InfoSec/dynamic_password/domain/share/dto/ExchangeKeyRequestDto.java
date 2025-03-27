package com.InfoSec.dynamic_password.domain.share.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExchangeKeyRequestDto {

    @NotBlank(message = "Public key input is required")
    private String publicKey;

}

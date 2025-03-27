package com.InfoSec.dynamic_password.domain.share.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GetStoreKeyExchangeRequestDto {
    private Long exchangedId;
    private Long memberId;
    private String memberRsaPublicKey;
}

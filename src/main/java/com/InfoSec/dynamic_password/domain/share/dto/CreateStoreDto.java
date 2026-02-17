package com.InfoSec.dynamic_password.domain.share.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStoreDto {

    @NotBlank(message = "Store name is essential")
    @Size(max = 255, message = "Name size should under 255")
    private String storeName;

    @Size(max = 1024, message = "Description size should under 1024")
    private String description;

}

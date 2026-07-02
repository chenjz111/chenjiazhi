package com.travelshare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CityDTO {
    @NotBlank(message = "城市名称不能为空")
    private String name;
    private String province;
    private String description;
    private String coverImage;
}
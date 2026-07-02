package com.travelshare.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class AttractionDTO {
    @NotBlank(message = "景点名称不能为空")
    private String name;
    private Long cityId;
    private String description;
    private String openTime;
    private BigDecimal ticketPrice;
    private String coverImage;
    private Double rating;
}
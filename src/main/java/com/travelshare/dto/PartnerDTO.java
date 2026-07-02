package com.travelshare.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PartnerDTO {

    private String title;

    private String startCity;

    private String targetCity;

    private LocalDate departureDate;

    private Integer requiredPeople;

    private Integer currentPeople;

    private String description;

    private Long publisherId;

    private String images;         // 图片URL(多张逗号分隔)
}
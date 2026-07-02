package com.travelshare.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RouteDTO {

    private String province;

    private String startCity;

    private String middleCity;

    private String endCity;

    private String transportType;

    private String duration;

    private BigDecimal price;

    private String description;

    private Long publisherId;
}
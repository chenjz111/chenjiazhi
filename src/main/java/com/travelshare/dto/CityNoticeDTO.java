package com.travelshare.dto;

import lombok.Data;

@Data
public class CityNoticeDTO {

    private String city;

    private String title;

    private String content;

    private Long publisherId;

}
package com.travelshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelshare.dto.CityNoticeDTO;
import com.travelshare.entity.CityNotice;

import java.util.List;

public interface CityNoticeService extends IService<CityNotice> {

    void add(CityNoticeDTO dto);

    CityNotice detail(Long id);

    List<CityNotice> searchByCity(String city);

    List<CityNotice> approvedList();

    void approve(Long id);
}
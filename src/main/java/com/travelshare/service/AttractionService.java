package com.travelshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelshare.entity.Attraction;
import java.util.List;

public interface AttractionService extends IService<Attraction> {
    List<Attraction> listByCityId(Long cityId);
    Attraction detail(Long id);
    List<Attraction> searchByName(String name);
}
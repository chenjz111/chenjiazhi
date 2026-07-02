package com.travelshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelshare.entity.City;
import java.util.List;

public interface CityService extends IService<City> {
    List<City> listAll();
    List<City> searchByProvince(String province);
    City detail(Long id);
}
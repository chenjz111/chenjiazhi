package com.travelshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelshare.entity.City;
import com.travelshare.mapper.CityMapper;
import com.travelshare.service.CityService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CityServiceImpl extends ServiceImpl<CityMapper, City> implements CityService {

    @Override
    public List<City> listAll() {
        return list();
    }

    @Override
    public List<City> searchByProvince(String province) {
        LambdaQueryWrapper<City> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(City::getProvince, province);
        return list(wrapper);
    }

    @Override
    public City detail(Long id) {
        return getById(id);
    }
}
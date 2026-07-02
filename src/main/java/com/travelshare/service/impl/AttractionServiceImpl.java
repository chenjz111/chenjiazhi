package com.travelshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelshare.entity.Attraction;
import com.travelshare.mapper.AttractionMapper;
import com.travelshare.service.AttractionService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class AttractionServiceImpl extends ServiceImpl<AttractionMapper, Attraction> implements AttractionService {

    @Override
    public List<Attraction> listByCityId(Long cityId) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Attraction::getCityId, cityId);
        return list(wrapper);
    }

    @Override
    public Attraction detail(Long id) {
        return getById(id);
    }

    @Override
    public List<Attraction> searchByName(String name) {
        LambdaQueryWrapper<Attraction> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(Attraction::getName, name);
        return list(wrapper);
    }
}
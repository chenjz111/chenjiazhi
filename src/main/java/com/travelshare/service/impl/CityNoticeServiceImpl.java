package com.travelshare.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.travelshare.dto.CityNoticeDTO;
import com.travelshare.entity.CityNotice;
import com.travelshare.mapper.CityNoticeMapper;
import com.travelshare.service.CityNoticeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CityNoticeServiceImpl
        extends ServiceImpl<CityNoticeMapper, CityNotice>
        implements CityNoticeService {

    @Override
    public void add(CityNoticeDTO dto) {

        CityNotice notice = new CityNotice();

        notice.setCity(dto.getCity());
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setPublisherId(dto.getPublisherId());

        notice.setStatus("PENDING");

        this.save(notice);
    }

    @Override
    public CityNotice detail(Long id) {

        return this.getById(id);
    }

    @Override
    public List<CityNotice> searchByCity(String city) {

        QueryWrapper<CityNotice> wrapper = new QueryWrapper<>();

        wrapper.eq("city", city);
        wrapper.eq("status", "APPROVED");

        return this.list(wrapper);
    }

    @Override
    public List<CityNotice> approvedList() {

        QueryWrapper<CityNotice> wrapper = new QueryWrapper<>();

        wrapper.eq("status", "APPROVED");

        return this.list(wrapper);
    }

    @Override
    public void approve(Long id) {

        CityNotice notice = this.getById(id);

        if (notice == null) {
            throw new RuntimeException("动态不存在");
        }

        notice.setStatus("APPROVED");

        this.updateById(notice);
    }
}
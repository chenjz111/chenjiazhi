package com.travelshare.service.impl;

import com.travelshare.entity.Notice;
import com.travelshare.mapper.NoticeMapper;
import com.travelshare.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeServiceImpl
implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;

    @Override
    public List<Notice> list() {

        return noticeMapper.selectList(null);

    }
}
package com.travelshare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travelshare.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {
}
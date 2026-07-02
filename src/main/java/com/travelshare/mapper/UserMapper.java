package com.travelshare.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.travelshare.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {

}
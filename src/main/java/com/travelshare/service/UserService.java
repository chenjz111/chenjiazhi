package com.travelshare.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.travelshare.dto.RegisterDTO;
import com.travelshare.entity.User;
import com.travelshare.dto.LoginDTO;
import com.travelshare.vo.LoginVO;

public interface UserService extends IService<User> {
    LoginVO register(RegisterDTO dto);
    LoginVO login(LoginDTO dto);
    User getCurrentUser(Long userId);
}
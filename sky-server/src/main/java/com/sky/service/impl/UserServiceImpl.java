package com.sky.service.impl;

import com.sky.dto.UserLoginDTO;
import com.sky.entity.User;
import com.sky.service.UserService;
import com.sky.vo.UserLoginVO;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    /**
     * 用户登录
     * @param userLoginDTO
     * @return
     */
    @Override
    // TODO 微信用户登录逻辑层设计未完成
    public User wxLogin(UserLoginDTO userLoginDTO) {
        // 调用微信接口服务，获取当前用户的openId
        return null;
    }
}

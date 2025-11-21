package me.yan.service.user.Impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import me.yan.dao.UserMapper;
import me.yan.pojo.UserDomain;
import me.yan.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;

    @Override
    public UserDomain login(String username, String password) {
        LambdaQueryWrapper<UserDomain> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(UserDomain::getUsername, username);
        queryWrapper.eq(UserDomain::getPassword, password);
        UserDomain userDomain = userMapper.selectOne(queryWrapper);
        return userDomain;
    }
}

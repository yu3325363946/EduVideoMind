package it.service.impl;

import it.mapper.UserMapper;
import it.pojo.User;
import it.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String id, String password) {
        return userMapper.login(id, password);
    }

}

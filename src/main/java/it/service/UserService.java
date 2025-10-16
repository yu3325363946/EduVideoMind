package it.service;

import it.pojo.User;

public interface UserService {
    User login(String id, String password);
}


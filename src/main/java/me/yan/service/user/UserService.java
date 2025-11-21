package me.yan.service.user;

import me.yan.pojo.UserDomain;

public interface UserService {
    UserDomain login(String username, String password);
}

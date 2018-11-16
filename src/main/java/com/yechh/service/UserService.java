package com.yechh.service;

import com.yechh.entity.CmsUserInfo;
import com.yechh.entity.User;

import java.util.List;
import java.util.Map;

public interface UserService {
    public User getUserById(int userId);


    public  CmsUserInfo getCmsUserInfo(Map<String,Object> map);

    boolean addUser(User record);

}
package com.yechh.service.impl;

import com.github.pagehelper.PageHelper;
import com.sun.org.apache.bcel.internal.generic.RETURN;
import com.yechh.dao.CmsUserInfoMapper;
import com.yechh.dao.UserDao;
import com.common.datasources.DataSourceNames;
import com.common.datasources.annotation.DataSource;
import com.yechh.entity.CmsUserInfo;
import com.yechh.entity.User;
import com.yechh.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.validation.constraints.Max;
import java.util.List;
import java.util.Map;

@Service("userService")
public class UserServiceImpl implements UserService {

    @Resource
    private UserDao userDao;


    @Resource
    private CmsUserInfoMapper userMapper;

    @DataSource(name = DataSourceNames.FIRST)
    public User getUserById(int userId) {


        PageHelper.startPage(0,1);
        return userDao.selectByPrimaryKey(userId);
    }

    public CmsUserInfo getCmsUserInfo(Map<String,Object> map){
        int id=Integer.parseInt(map.get("id").toString());
        return userMapper.selectByPrimaryKey(id);

    }


    public boolean addUser(User record){
        boolean result = false;
        try {
            userDao.insertSelective(record);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

}

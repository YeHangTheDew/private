package com.yechh.service;

import com.yechh.entity.CmsRoleInfo;
import com.yechh.entity.CmsUserInfo;
import com.yechh.entity.User;

import java.util.List;
import java.util.Map;

public interface CmsUserService {

    public List<CmsUserInfo> getCmsUserInfos(Map<String,String> map);

    public List<CmsRoleInfo> getCmsRoleInfos(Map<String,Object> map);

    CmsUserInfo findByUsername(String tokenValue);
}

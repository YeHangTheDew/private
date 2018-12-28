package com.yechh.service.impl;

import com.yechh.dao.CmsRoleInfoMapper;
import com.yechh.dao.CmsUserInfoMapper;
import com.yechh.entity.CmsRoleInfo;
import com.yechh.entity.CmsUserInfo;
import com.yechh.service.CmsUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service("cmsUserService")
public class CmsUserServiceImpl implements CmsUserService {
    @Resource
    private CmsUserInfoMapper cmsUserInfoMapper;

    @Resource
    private CmsRoleInfoMapper cmsRoleInfoMapper;

    @Override
    public List<CmsUserInfo> getCmsUserInfos(Map<String, String> map) {

        return cmsUserInfoMapper.selectAllByPrimaryKey(map);
    }

    @Override
    public List<CmsRoleInfo> getCmsRoleInfos(Map<String, Object> map) {
        return cmsRoleInfoMapper.selectAllByPrimaryKey(map);
    }

    @Override
    public CmsUserInfo findByUsername(String tokenValue) {
        return cmsUserInfoMapper.selectByUsercode(tokenValue);
    }
}

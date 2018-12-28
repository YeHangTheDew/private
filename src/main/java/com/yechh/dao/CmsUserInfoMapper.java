package com.yechh.dao;

import com.yechh.entity.CmsUserInfo;

import java.util.List;
import java.util.Map;

public interface CmsUserInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CmsUserInfo record);

    CmsUserInfo selectByPrimaryKey(Integer id);
    CmsUserInfo selectByUsercode(String userCode);

    List<CmsUserInfo> selectAll();

    List<CmsUserInfo> selectAllByPrimaryKey(Map<String,String> map);

    int updateByPrimaryKey(CmsUserInfo record);
}
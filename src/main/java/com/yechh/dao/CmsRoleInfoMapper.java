package com.yechh.dao;

import com.yechh.entity.CmsRoleInfo;

import java.util.List;
import java.util.Map;

public interface CmsRoleInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CmsRoleInfo record);

    CmsRoleInfo selectByPrimaryKey(Integer id);

    List<CmsRoleInfo> selectAll();

    List<CmsRoleInfo> selectAllByPrimaryKey(Map<String,Object> map);

    int updateByPrimaryKey(CmsRoleInfo record);
}
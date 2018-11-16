package com.yechh.dao;

import com.yechh.entity.CmsUserRoleR;

import java.util.List;

public interface CmsUserRoleRMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CmsUserRoleR record);

    CmsUserRoleR selectByPrimaryKey(Integer id);

    List<CmsUserRoleR> selectAll();

    int updateByPrimaryKey(CmsUserRoleR record);
}
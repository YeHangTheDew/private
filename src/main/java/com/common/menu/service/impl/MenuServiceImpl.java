package com.common.menu.service.impl;


import com.common.menu.dao.MenuMapper;
import com.common.menu.entity.Menu;
import com.common.menu.service.MenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class MenuServiceImpl implements MenuService {
    @Resource
    private MenuMapper menuMapper;

    public List<Menu> getAllMenuList() {
        return menuMapper.selectAll();
    }
}

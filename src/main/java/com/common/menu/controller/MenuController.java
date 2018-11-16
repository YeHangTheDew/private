package com.common.menu.controller;


import com.common.menu.entity.Menu;
import com.common.entity.Tree;
import com.common.menu.service.MenuService;
import com.common.utils.BuildTreeUtil;
import com.common.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
public class MenuController {
    @Resource
    private MenuService menuService;


    @ResponseBody
    @RequestMapping(value="/getMenu", method= RequestMethod.GET)
    public Map<String, Object> getAllMenuList(){
        List<Menu> menuList=menuService.getAllMenuList();
        List<Tree<Menu>> trees = new ArrayList<Tree<Menu>>();
        for (Menu test : menuList) {
            Tree<Menu> tree = new Tree<Menu>();
            tree.setId(test.getId().toString());
            tree.setParentId(test.getParentId().toString());
            tree.setText(test.getMenuName());
            tree.setUrl(test.getMenuUrl());
            trees.add(tree);
        }
        List<Tree<Menu>> children=BuildTreeUtil.build(trees);
        R result=R.ok().put("extend",children);
        Map<String,Object> map=new HashMap<String, Object>();
        map.put("menuList",result);

        return map;
    }
}

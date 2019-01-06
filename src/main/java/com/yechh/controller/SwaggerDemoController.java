package com.yechh.controller;
import com.yechh.entity.User;
import com.yechh.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by Administrator on 2017/9/13.
 */
@RestController
@RequestMapping("/api")
@Api("swaggerDemoController相关的api")
public class SwaggerDemoController {
    @Autowired
    private UserService userService;

    private static final Logger logger= LoggerFactory.getLogger(SwaggerDemoController.class);


    @ApiOperation(value = "根据id查询学生信息", notes = "查询数据库中某个的学生信息")
    @ApiImplicitParam(name = "id", value = "学生ID", paramType = "path", required = true, dataType = "Integer")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public User getStudent(@PathVariable int id) {
        logger.info("开始查询某个学生信息");
        return userService.getUserById(id);
    }


}
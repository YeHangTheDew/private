package com.common.shiro.controller;
import com.common.constant.CookieConstant;
import com.common.constant.IConstants;
import com.common.constant.RedisConstant;
import com.common.entity.JsonBean;
import com.common.utils.CookieUtil;
import com.common.utils.ParamUtils;
import com.yechh.entity.CmsUserInfo;
import com.yechh.entity.User;
import com.yechh.service.CmsUserService;
import org.apache.commons.collections.MapUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.DisabledAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @Auther: pf
 * @Date: 2017/12/12 19:41
 * @Description:
 */
@Controller
@RequestMapping(value = "/auth")
public class AuthenticationController {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private StringRedisTemplate redisTemplate;
    @Autowired
    private CmsUserService cmsUserService;

    @RequestMapping(value="/getData")
    @ResponseBody
    public List<String[]> getData(){
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        List<String[]> list= new ArrayList<>();
        String[] str=new String[]{"1","2","3","4","5"};
        list.add(str);
        list.add(str);
        list.add(str);

        return list;
    }

    @RequestMapping(value="/getDataArry")
    @ResponseBody
    public String[] getDataArry(){
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        String[] str=new String[]{"1","2","3","4","5"};
        return str;
    }
    @RequestMapping(value="/getDataMap")
    @ResponseBody
    public Map<String,String> getDataMap(){
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        HashMap<String,String> map=new HashMap<>();
        map.put("1","yechanghang");
        map.put("2","yujianhuan");
        return  map;
    }

    @RequestMapping(value = "/login")
    public String login() {
        //return "/index.html";
        return  "/toLogin.html";
    }
    @RequestMapping(value = "/index")
    public String index(){
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        return "/index.html";

    }
    @RequestMapping(value = "/toDemo")
    public String toDemo(){
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getSession().getAttribute("user");
        return "/echartsDemo.html";

    }
    @RequestMapping(value = "/toMenu")
    public String toMenu() {
        return "/toIndex.html";
    }
    @RequestMapping(value = "/toIndex")
    public String toIndex() {
        return "/main.html";
    }
    @RequestMapping(value="/toLogin")
    @ResponseBody
    public JsonBean toLogin(HttpServletRequest request, HttpServletResponse response){
        JsonBean reJson= new JsonBean();
        Map paramMap = ParamUtils.handleServletParameter(request);
        String userCode = MapUtils.getString(paramMap, "userCode");
        String userPwd = MapUtils.getString(paramMap, "userPwd");

        // shiro认证
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userCode, userPwd);
        try {
            subject.login(token);
        } catch (UnknownAccountException e) {
            reJson.setMessage("账户不存在");
        } catch (DisabledAccountException e) {
            reJson.setMessage("账户存在问题");
        } catch (AuthenticationException e) {
            reJson.setMessage("密码错误");
        } catch (Exception e) {
            log.info("登陆异常", e);
            reJson.setMessage("登陆异常");
        }
        if(!StringUtils.isEmpty(reJson.getMessage())){
/*            ModelAndView model= new ModelAndView("login.html");
            model.addObject("reJson",reJson);*/
            return reJson;
        }
        reJson.setStatus(IConstants.RESULT_INT_SUCCESS);
        String res = subject.getPrincipals().toString();
        if (subject.hasRole("admin")) {
            res = res + "----------你拥有admin权限";
        }
        if (subject.hasRole("guest")) {
            res = res + "----------你拥有guest权限";
        }
        reJson.setData(res);
        reJson.setMessage("登陆成功");
        //验证成功后存入redis
        // 设置token至redis
        String  redisToken= UUID.randomUUID().toString();
        Integer expire = RedisConstant.EXPIRE;
        redisTemplate.opsForValue().set(String.format(RedisConstant.TOKEN_PREFIX ,redisToken),userCode,expire, TimeUnit.SECONDS);
        // 设置token至cookie
        CookieUtil.set(response, CookieConstant.TOKEN,redisToken,expire);
        return reJson;
/*        ModelAndView model= new ModelAndView("toIndex.html");
        model.addObject("reJson",reJson);
        return model;*/


    }

    @GetMapping("/loginin")
    public ModelAndView login(Map<String, Object> map){
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();

        // 查询cookie
        Cookie cookie = CookieUtil.get(request, CookieConstant.TOKEN);
        if (cookie == null){
            return new ModelAndView("/toLogin.html");
        }
        //去Redis里查询
        String tokenValue = redisTemplate.opsForValue().get(String.format(RedisConstant.TOKEN_PREFIX,cookie.getValue()));
        if (StringUtils.isEmpty(tokenValue)){
            return new ModelAndView("/toLogin.html");
        }

        CmsUserInfo user = cmsUserService.findByUsername(tokenValue);
        if (user == null){
            return new ModelAndView("/toLogin.html");
        }else{
            return new ModelAndView("/index.html");
        }
    }

    @ResponseBody
    @RequestMapping(value = "/login_in", produces = "application/json;charset=UTF-8")
    public JsonBean loginIn(HttpServletRequest request) {
        JsonBean reJson = new JsonBean();
        Map paramMap = ParamUtils.handleServletParameter(request);
        String userCode = MapUtils.getString(paramMap, "userCode");
        String userPwd = MapUtils.getString(paramMap, "userPwd");
        // shiro认证
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(userCode, userPwd);
        try {
            subject.login(token);
        } catch (UnknownAccountException e) {
            reJson.setMessage("账户不存在");
            return reJson;
        } catch (DisabledAccountException e) {
            reJson.setMessage("账户存在问题");
            return reJson;
        } catch (AuthenticationException e) {
            reJson.setMessage("密码错误");
            return reJson;
        } catch (Exception e) {
            log.info("登陆异常", e);
            reJson.setMessage("登陆异常");
            return reJson;
        }
        reJson.setStatus(IConstants.RESULT_INT_SUCCESS);
        String res = subject.getPrincipals().toString();
        if (subject.hasRole("admin")) {
            res = res + "----------你拥有admin权限";
        }
        if (subject.hasRole("guest")) {
            res = res + "----------你拥有guest权限";
        }
        reJson.setData(res);
        reJson.setMessage("登陆成功");
        return reJson;
    }
}

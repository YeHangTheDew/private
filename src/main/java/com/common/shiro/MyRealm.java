package com.common.shiro;

import com.yechh.entity.CmsRoleInfo;
import com.yechh.entity.CmsUserInfo;
import com.yechh.service.CmsUserService;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Auther: pf
 * @Date: 2017/12/12 19:29
 * @Description: 认证和授权具体实现
 */
public class MyRealm extends AuthorizingRealm {
    @Autowired
    private CmsUserService cmsUserService;

    /**
     * 为当前subject授权
     * @param principalCollection
     * @return AuthorizationInfo
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        Map<String, Object> params = new HashMap<>();
        params.put("userCode", (String) super.getAvailablePrincipal(principalCollection));
        List<CmsRoleInfo> CmsRoleInfos = cmsUserService.getCmsRoleInfos(params);
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        if(!CmsRoleInfos.isEmpty()) {
            for(CmsRoleInfo role : CmsRoleInfos) {
                info.addRole(role.getRoleCode());
            }
        }
        return info;
    }

    /**
     * 认证登陆subject身份
     * @param authenticationToken
     * @return AuthenticationInfo
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        UsernamePasswordToken token = (UsernamePasswordToken) authenticationToken;
        Map<String, String> params = new HashMap<>();
        params.put("userCode", (String)authenticationToken.getPrincipal());
        List<CmsUserInfo> CmsUserInfos = cmsUserService.getCmsUserInfos(params);
        if (CmsUserInfos.isEmpty()) {
            throw new UnknownAccountException();
        } else if(CmsUserInfos.size() > 1) {
            throw new DisabledAccountException();
        } else {
            CmsUserInfo user = CmsUserInfos.get(0);
            // 校验密码
            return new SimpleAuthenticationInfo(authenticationToken.getPrincipal(), user.getUserPwd(), ByteSource.Util.bytes("2w@W"),  getName());
        }
    }

}

package cn.itcast.realm;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class SaaSRealm extends AuthorizingRealm {

    @Autowired
    private UserService userService;

    @Autowired
    private ModuleService moduleService;

    /**
     * 做认证
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //判断email和密码是否正确
        UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
        String email = usernamePasswordToken.getUsername();
        String password = new String(usernamePasswordToken.getPassword());

        User user = userService.findUserByEmail(email);
        if (user == null) {
            //一旦return null,在登录方法那里就会抛异常
            return null;
        } else {
            //判断密码是否正确
            if (StringUtils.equals(password, user.getPassword())) {
                //密码正确
                //principal 主角
                // credentials 密码
                // realmName  当前类名

                return new SimpleAuthenticationInfo(user, password, getName());
            } else {
                return null;
            }
        }
    }

    /**
     * 做授权    principal表示主角  判断登录人是否有权限访问方法
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {

        User user = (User) principals.getPrimaryPrincipal();

        //在此方法中告诉shiro框架，给当前登录人什么权限
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();

        List<Module> modules = moduleService.findModulesByUser(user);
        for (Module module : modules) {

            info.addStringPermission(module.getName());
        }
        return info;
    }

}

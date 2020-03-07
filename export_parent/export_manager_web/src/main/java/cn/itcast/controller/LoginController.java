package cn.itcast.controller;


import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.UserService;
import org.apache.commons.lang.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.subject.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
public class LoginController{

    @Autowired
    private UserService userService;
    @Autowired
    private ModuleService moduleService;
	@RequestMapping("/login")
	public String login(HttpServletRequest request, HttpSession session,String email, String password) {

	  /*  //1.先通过email查询User
        User user = userService.findUserByEmail(email);
        //2.1如果user存在
        if (user == null){
            //设置提示消息然后重定向
            request.setAttribute("error","用户名或密码错误");
            return "forward:/login.jsp";
        }else {
            //   2.1.1判断password和user中的user是否相等，
            String password_form = new Md5Hash(password,user.getEmail(),2).toString();
            String password_db = user.getPassword();
            if (password_db.equals(password_form)) {
                //如果相等则登录成功，将usre存到session中
                session.setAttribute("loginUser",user);
            }else{
//           2.1.2两个password不相等，说明密码错误，设置提示消息并重定向
                request.setAttribute("error","用户名或密码错误");
                return "forward:/login.jsp";
            }
        }

        //查询当前登录人拥有的模块
        List<Module> modules = moduleService.findModulesByUser(user);
        session.setAttribute("modules",modules);
        //2.2如果User不存在，则设置提示消息并重定向*/

	  if (StringUtils.isEmpty(email) || StringUtils.isEmpty(password)){
          request.setAttribute("error","用户名或密码错误");
          return "forward:/login.jsp";
      }

	    //使用shiro登录
        //1.获取主题
        Subject subject = SecurityUtils.getSubject();

        String password_form = new Md5Hash(password,email,2).toString();
        //2.创建令牌 存有个人信息，密码存储加密后的
        UsernamePasswordToken token = new UsernamePasswordToken(email,password_form);

        //3.进行认证
        try {
            subject.login(token);
        } catch (AuthenticationException e) {
            request.setAttribute("error","用户名或密码错误");
            return "forward:/login.jsp";
        }

        User user = (User) subject.getPrincipal();

        session.setAttribute("loginUser",user);
        //查询当前登录人拥有的模块
        List<Module> modules = moduleService.findModulesByUser(user);
        session.setAttribute("modules",modules);
        return "home/main";
	}

    //退出
    @RequestMapping(value = "/logout",name="用户登出")
    public String logout(){
        SecurityUtils.getSubject().logout();   //登出
        return "forward:login.jsp";
    }

    @RequestMapping("/home")
    public String home(){
	    return "home/home";
    }
}

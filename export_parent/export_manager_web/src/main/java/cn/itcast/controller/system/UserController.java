package cn.itcast.controller.system;

import cn.itcast.controller.BaseController;
import cn.itcast.domain.system.Dept;
import cn.itcast.domain.system.Role;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.DeptService;
import cn.itcast.service.system.RoleService;
import cn.itcast.service.system.UserService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/system/user/")
public class UserController extends BaseController {
    @Autowired
    private UserService userService;
    @Autowired
    private DeptService deptService;
    @Autowired
    private RoleService roleService;
    @RequestMapping(value="/list",name = "显示用户信息")
    public String findAll(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "5") Integer pageSize){
        /*List<User> list = userService.findAll();
        request.setAttribute("list",list);*/
        PageInfo<User> pageInfo = userService.findPage(companyId,page,pageSize);
        request.setAttribute("page",pageInfo);
        return "system/user/user-list";
    }

    @RequestMapping(value = "/toAdd",name = "跳转到添加页面")
    public String toAdd(){
        List<Dept> deptList = deptService.findAll(companyId);
        request.setAttribute("deptList",deptList);
        return "system/user/user-add";
    }

    @RequestMapping(value = "/delete",name = "删除用户信息")
    public String delete(String id){
        userService.deleteById(id);
        return "redirect:/system/user/list.do";
    }

    @RequestMapping(value = "/toUpdate",name = "跳转到添加页面")
    public String toUpdate(String id){
        User user = userService.findById(id);
        request.setAttribute("user",user);

        /*List<User> userList = userService.findAll(companyId);
        request.setAttribute("userList",userList);*/
        List<Dept> deptList = deptService.findAll(companyId);
        request.setAttribute("deptList",deptList);
        return "system/user/user-add";
    }

    @RequestMapping(value = "/edit",name = "保存用户信息方法")
    public String edit(User user){
        if (StringUtils.isEmpty(user.getId())){
            //如果user的id为空的话，说明是添加方法
            //添加需要设置主键id
            user.setId(UUID.randomUUID().toString());
            user.setCompanyId(companyId);
            user.setCompanyName(companyName);
            user.setCreateBy(createBy);
            user.setCreateDept(createDept);
            user.setCreateTime(new Date());
            userService.save(user);
        }else {
            //user的id不为空，是编辑方法
            user.setCompanyId(companyId);
            user.setCompanyName(companyName);
            user.setUpdateBy(createBy);
            user.setUpdateTime(new Date());
            userService.update(user);
        }

        return "redirect:/system/user/list.do";
    }

    @RequestMapping(value = "/roleList",name = "进入用户分配角色界面")
    public String roleList(String id){
        //根据id查询用户
        User user = userService.findById(id);
        request.setAttribute("user",user);

        //查询所有的角色
        List<Role> roleList = roleService.findAll(companyId);
        request.setAttribute("roleList",roleList);

        //查询用户已经拥有的角色的id字符串
        List<String> listId = roleService.findByUserId(id);
        StringBuffer userRoleStr = new StringBuffer();
        for (String s : listId) {
            userRoleStr.append(s).append(",");
        }
        request.setAttribute("userRoleStr",userRoleStr.toString());
        return "system/user/user-role";
    }

    @RequestMapping(value = "changeRole",name = "给用户分配角色方法")
    public String changeRole(String userid,String roleIds){
        userService.changeRole(userid,roleIds);
        return "redirect:/system/user/list.do";
    }
}

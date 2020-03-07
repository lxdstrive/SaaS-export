package cn.itcast.controller.system;

import cn.itcast.controller.BaseController;
import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.ModuleService;
import cn.itcast.service.system.RoleService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.*;

@Controller
@RequestMapping("/system/role/")
public class RoleController extends BaseController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private ModuleService moduleService;
    @RequestMapping(value="/list",name = "角色列表数据展示")
    public String findAll(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "5") Integer pageSize){
        /*List<Role> list = roleService.findAll();
        request.setAttribute("list",list);*/
        PageInfo<Role> pageInfo = roleService.findPage(companyId,page,pageSize);
        request.setAttribute("page",pageInfo);
        return "system/role/role-list";
    }

    @RequestMapping(value = "/toAdd",name = "进入到角色新增页面")
    public String toAdd(){
        List<Role> roleList = roleService.findAll(companyId);
        request.setAttribute("roleList",roleList);
        return "system/role/role-add";
    }

    @RequestMapping(value = "/delete",name = "删除角色信息方法")
    public String delete(String id){
        roleService.deleteById(id);
        return "redirect:/system/role/list.do";
    }

    @RequestMapping(value = "/toUpdate",name = "进入到角色修改页面")
    public String toUpdate(String id){
        Role role = roleService.findById(id);
        request.setAttribute("role",role);

        List<Role> roleList = roleService.findAll(companyId);
        request.setAttribute("roleList",roleList);
        return "system/role/role-add";
    }

    @RequestMapping(value = "/edit",name = "保存角色信息方法")
    public String edit(Role role){
        if (StringUtils.isEmpty(role.getId())){
            role.setId(null);
        }
        if (StringUtils.isEmpty(role.getId())){
            //如果role的id为空的话，说明是添加方法
            //添加需要设置主键id
            role.setId(UUID.randomUUID().toString());
            role.setCompanyId(companyId);
            role.setCompanyName(companyName);
            role.setCreateBy(createBy);
            role.setCreateTime(new Date());
            role.setCreateDept(createDept);
            roleService.save(role);
        }else {
            //role的id不为空，是编辑方法
            role.setCompanyId(companyId);
            role.setCompanyName(companyName);
            role.setUpdateBy(createBy);
            role.setUpdateTime(new Date());
            roleService.update(role);
        }

        return "redirect:/system/role/list.do";
    }

    @RequestMapping(value = "/roleModule",name = "进入角色分配权限页面")
    public String roleModule(String roleid){
        Role role = roleService.findById(roleid);
        request.setAttribute("role",role);
        return "system/role/role-module";
    }

    @RequestMapping(value = "/getZtreeNodes",name = "加载ztree需要的权限数据")
    @ResponseBody
    public List<Map> getZtreeNodes(String roleid){
        //查询该角色已经拥有的module
        List<Module> roleModuleList = moduleService.findByRoleId(roleid);

        //自己的==================查询角色已经拥有的module，只查询module_id
      /*  Map<String,String> myMap = moduleService.findByRoleIdForMy(roleid);*/
        //构建Ztree需要的数据[{id,pId,name}]
        List<Module> modules = moduleService.findAll();
        List<Map> moduleZtreeList = new ArrayList<>();
        for (Module module : modules) {
            Map map = new HashMap();
            map.put("id",module.getId());
            map.put("pId",module.getParentId());
            map.put("name",module.getName());

            if (roleModuleList.contains(module)){//此时的包含判断的是对象的地址，我们想要的是id是否相同就行，改写对象中的equals方法
                map.put("checked",true);// 勾选此角色之前的权限
            }

            //自己的
           /* if (StringUtils.equals(roleid,myMap.get(module.getId()))){
                map.put("checked",true);// 勾选此角色之前的权限
            }*/
            moduleZtreeList.add(map);
        }
        return moduleZtreeList;
    }

    @RequestMapping(value = "/updateRoleModule",name = "保存角色分配权限的数据")
    public String updateRoleModule(String roleid,String moduleIds){
        roleService.updateRoleModule(roleid,moduleIds);
        return "redirect:/system/role/list.do";
    }
}

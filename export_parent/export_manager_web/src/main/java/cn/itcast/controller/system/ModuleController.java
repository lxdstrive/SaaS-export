package cn.itcast.controller.system;

import cn.itcast.controller.BaseController;
import cn.itcast.domain.system.Module;
import cn.itcast.service.system.ModuleService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/system/module")
public class ModuleController extends BaseController {
    @Autowired
    private ModuleService moduleService;
    @RequestMapping(value="/list",name = "模块列表数据展示")
    public String findAll(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "10") Integer pageSize){
        /*List<Module> list = moduleService.findAll();
        request.setAttribute("list",list);*/
        PageInfo<Module> pageInfo = moduleService.findPage(page,pageSize);
        request.setAttribute("page",pageInfo);
        return "/system/module/module-list";
    }

    @RequestMapping(value = "/toAdd",name = "进入到模块新增页面")
    public String toAdd(){
        List<Module> menus = moduleService.findAll();
        request.setAttribute("menus",menus);
        return "/system/module/module-add";
    }

    @RequestMapping(value = "/delete",name = "删除模块信息方法")
    public String delete(String id){
        moduleService.deleteById(id);


        return "redirect:/system/module/list.do";
    }

    @RequestMapping(value = "/toUpdate",name = "进入到模块修改页面")
    public String toUpdate(String id){
        Module module = moduleService.findById(id);
        request.setAttribute("module",module);

        List<Module> menus = moduleService.findAll();
        request.setAttribute("menus",menus);
        return "/system/module/module-add";
    }

    @RequestMapping(value = "/edit",name = "保存模块信息方法")
    public String edit(Module module){
        if (StringUtils.isEmpty(module.getParentId())){
            module.setParentId(null);
        }
        if (StringUtils.isEmpty(module.getId())){
            //如果module的id为空的话，说明是添加方法
            //添加需要设置主键id
            module.setId(UUID.randomUUID().toString());

            moduleService.save(module);
        }else {
            //module的id不为空，是编辑方法
            moduleService.updateById(module);
        }

        return "redirect:/system/module/list.do";
    }
}

package cn.itcast.controller.system;

import cn.itcast.controller.BaseController;
import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/system/dept/")
public class DeptController extends BaseController {
    @Autowired
    private DeptService deptService;

    @RequestMapping(value = "/list", name = "显示公司信息")
    public String findAll(@RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "5") Integer pageSize) {
        /*List<Dept> list = deptService.findAll();
        request.setAttribute("list",list);*/
        PageInfo<Dept> pageInfo = deptService.findPage(companyId, page, pageSize);
        request.setAttribute("page", pageInfo);
        return "system/dept/dept-list";
    }

    @RequestMapping(value = "/toAdd", name = "跳转到添加页面")
    public String toAdd() {
        List<Dept> deptList = deptService.findAll(companyId);
        request.setAttribute("deptList", deptList);
        return "system/dept/dept-add";
    }

    @RequestMapping(value = "/delete", name = "删除客户信息")
    public String delete(String id) {
        deptService.deleteById(id);
        return "redirect:/system/dept/list.do";
    }

    @RequestMapping(value = "/toUpdate", name = "跳转到添加页面")
    public String toUpdate(String id) {
        Dept dept = deptService.findById(id);
        request.setAttribute("dept", dept);

        List<Dept> deptList = deptService.findAll(companyId);
        request.setAttribute("deptList", deptList);
        return "system/dept/dept-add";
    }

    @RequestMapping(value = "/edit", name = "保存部门信息方法")
    public String edit(Dept dept) {
        if (StringUtils.isEmpty(dept.getParent().getId())) {
            dept.getParent().setId(null);
        }

        if (StringUtils.isEmpty(dept.getId())) {
            //如果dept的id为空的话，说明是添加方法
            //添加需要设置主键id
            dept.setId(UUID.randomUUID().toString());
            dept.setCompanyId(companyId);
            dept.setCompanyName(companyName);
            deptService.save(dept);
        } else {
            //dept的id不为空，是编辑方法
            dept.setCompanyId(companyId);
            dept.setCompanyName(companyName);
            deptService.update(dept);
        }

        return "redirect:/system/dept/list.do";
    }
}

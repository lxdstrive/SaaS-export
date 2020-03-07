package cn.itcast.controller.company;

import cn.itcast.controller.BaseController;
import cn.itcast.domain.company.Company;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import cn.itcast.service.company.CompanyService;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@Controller
@RequestMapping("/company")
public class CompanyController extends BaseController {
    @Reference
    private CompanyService companyService;
    @RequestMapping(value="/list",name = "显示公司信息")
    public String findAll(@RequestParam(defaultValue = "1") Integer page,@RequestParam(defaultValue = "5") Integer pageSize){
        /*List<Company> list = companyService.findAll();
        request.setAttribute("list",list);*/
        PageInfo<Company> pageInfo = companyService.findPage(page,pageSize);
        request.setAttribute("page",pageInfo);
        return "company/company-list";
    }

    @RequestMapping(value = "/toAdd",name = "跳转到添加页面")
    public String toAdd(){
        return "company/company-add";
    }

    @RequestMapping(value = "/delete",name = "删除客户信息")
    public String delete(String id){
        companyService.deleteById(id);
        return "redirect:/company/list.do";
    }

    @RequestMapping(value = "/toUpdate",name = "跳转到添加页面")
    public String toUpdate(String id){
        Company company = companyService.findById(id);
        request.setAttribute("/controller",company);
        return "company/company-add";
    }

    @RequestMapping(value = "/edit",name = "添加或者编辑功能")
    public String edit(Company company){
        if (StringUtils.isEmpty(company.getId())){
            //如果company的id为空的话，说明是添加方法
            //添加需要设置主键id
            company.setId(UUID.randomUUID().toString());

            companyService.save(company);
        }else {
            //company的id不为空，是编辑方法
            companyService.updateById(company);
        }

        return "redirect:/company/list.do";
    }
}

package cn.itcast.controller.cargo;

import cn.itcast.controller.BaseController;
import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.ExtCproductService;
import cn.itcast.service.cargo.FactoryService;
import cn.itcast.utils.FileUploadUtil;
import com.alibaba.dubbo.config.annotation.Reference;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/cargo/extCproduct")
public class ExtCproductController extends BaseController {

    @Reference
    private FactoryService factoryService;

    @Reference
    private ContractProductService contractProductService;

    @Reference
    private ExtCproductService extCproductService;

    @Autowired
    private FileUploadUtil fileUploadUtil;

    @RequestMapping(value = "/list",name = "显示货物下附件列表数据")
    public String list(String contractId,String contractProductId, @RequestParam(value = "page",defaultValue = "1") int page, @RequestParam(value = "pageSize",defaultValue = "5")int size){
        //查询生产厂家
        //select * from co_factory where ctype="货物"

        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList",factoryList);


        //查询当前合同下的货物的附件
        //select * from co_contract_product where contract_id=?
        //查询货物下的附件(在映射文件中已经查询)

        ExtCproductExample extCproductExample = new ExtCproductExample();
        extCproductExample.createCriteria().andContractProductIdEqualTo(contractProductId);
        PageInfo pageInfo = extCproductService.findAll(extCproductExample, page, size);

        request.setAttribute("page",pageInfo);
        request.setAttribute("contractId",contractId);//保存附件时需要的合同id
        request.setAttribute("contractProductId",contractProductId);//保存附件时需要的货物id
        return "cargo/extc/extc-list";
    }

    @RequestMapping(value = "/edit",name = "货物下附件的保存")
    public String edit(ExtCproduct extCproduct , MultipartFile productPhoto){
        String imagePath = null;
        try {
             imagePath = fileUploadUtil.upload(productPhoto);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            imagePath="";
        }

        extCproduct.setProductImage(imagePath);//将图片的路径保存到数据库中
        if (StringUtils.isEmpty(extCproduct.getId())){
            //新增
            extCproduct.setId(UUID.randomUUID().toString());
            extCproduct.setCompanyId(companyId);
            extCproduct.setCompanyName(companyName);
            extCproduct.setCreateBy(createBy);
            extCproduct.setCreateTime(new Date());
            extCproductService.save(extCproduct);
        }else {
            extCproduct.setUpdateBy(createBy);
            extCproduct.setUpdateTime(new Date());
            extCproductService.update(extCproduct);
            //编辑
        }
        return "redirect:/cargo/extCproduct/list.do?contractId="+extCproduct.getContractId()+"&contractProductId="+extCproduct.getContractProductId();
    }

    //toUpdate.do?id=2bb34adf-85dc-4ab5-88e8-afe7be472d86&contractId=6768f025-18fb-462a-bf41-fd9072e56485&contractProductId=3356b720-b0bb-437a-995f-6e6cb66ea2d5
    @RequestMapping(value = "/toUpdate",name = "跳转到更新附件界面")
    public String toUpdate(String id,String contractId,String contractProductId){

        ExtCproduct extCproduct = extCproductService.findById(id);
        request.setAttribute("extCproduct",extCproduct);

        //查询生产厂家
        //select * from co_factory where ctype="附件"
        FactoryExample factoryExample = new FactoryExample();
        factoryExample.createCriteria().andCtypeEqualTo("附件");
        List<Factory> factoryList = factoryService.findAll(factoryExample);
        request.setAttribute("factoryList",factoryList);

        request.setAttribute("contractId",contractId); //保存附件时需要的合同id
        request.setAttribute("contractProductId",contractProductId); //保存附件时需要的货物id
        return "cargo/extc/extc-update";
    }

    @RequestMapping(value = "/delete",name = "删除货物信息")
    public String delete(String id,String contractId,String contractProductId){
        extCproductService.delete(id);
        return "redirect:/cargo/extCproduct/list.do?contractId="+contractId+"&contractProductId="+contractProductId;
    }

}

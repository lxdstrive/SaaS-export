package cn.itcast.service.cargo.impl;

import cn.itcast.dao.cargo.*;
import cn.itcast.domain.cargo.*;
import cn.itcast.service.cargo.ContractProductService;
import cn.itcast.service.cargo.ExportService;
import cn.itcast.vo.ExportProductResult;
import cn.itcast.vo.ExportResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;

import java.util.List;
import java.util.Set;

@Service
public class ExportServiceImpl implements ExportService {

    @Autowired
    private ExportDao exportDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private ExportProductDao exportProductDao;

    @Autowired
    private ExtEproductDao extEproductDao;

    @Autowired
    private ContractProductDao contractProductDao;



    @Override
    public Export findById(String id) {
        return exportDao.selectByPrimaryKey(id);
    }

    //保存电子报运
    @Override
    public void save(Export export) {
        //这里需要同时保存
        //      报运单----从页面上获取的 + 合同数据(合同号，货物数量。附件数量)
        //      报运单货物表---》》数据从合同货物表中获取
        //      报运单附件表---》》数据从合同附件表中获取
        String[] contractIds = export.getContractIds().split(",");
        StringBuffer customerNames = new StringBuffer();
        int proNum = 0; //货物数量
        int extNum = 0; //附件数量

        for (String contractId : contractIds) {
            //根据合同id查询合同
            Contract contract = contractDao.selectByPrimaryKey(contractId);
            proNum+=contract.getProNum();
            extNum+=contract.getExtNum();
            customerNames.append(contract.getCustomName()).append(" ");//用来拼接客户名称

            //查询合同的货物 select * from co_contract_product where contract_id=?
            ContractProductExample contractProductExample = new ContractProductExample();
            contractProductExample.createCriteria().andContractIdEqualTo(contractId);
            List<ContractProduct> contractProducts = contractProductDao.selectByExample(contractProductExample);

            for (ContractProduct contractProduct : contractProducts) {
                //有多少个合同货物就应该保存多少个报运单货物
                //                报运报运单的货物
                ExportProduct exportProduct = new ExportProduct();
                BeanUtils.copyProperties(contractProduct,exportProduct);
                exportProduct.setExportId(export.getId());//设置报运单id
                exportProductDao.insertSelective(exportProduct);

                List<ExtCproduct> extCproducts = contractProduct.getExtCproducts();

                for (ExtCproduct extCproduct : extCproducts) {
                    //报运单的附件
                    ExtEproduct extEproduct = new ExtEproduct();
                    BeanUtils.copyProperties(extCproduct,extEproduct);
                    extEproduct.setExportProductId(exportProduct.getId()); //设置报运单货物id
                    extEproduct.setExportId(export.getId());//设计报运单id
                    extEproductDao.insertSelective(extEproduct);
                }
            }
        }

        export.setCustomerContract(customerNames.toString());
        export.setProNum(proNum);
        export.setExtNum(extNum);
        exportDao.insertSelective(export);
    }

    @Override
    public void update(Export export) {
        //同时修改两张表，1.报运单  2.报运单货物  TODO  有坑
        exportDao.updateByPrimaryKeySelective(export);
        List<ExportProduct> exportProducts = export.getExportProducts();
        for (ExportProduct exportProduct : exportProducts) {
            exportProductDao.updateByPrimaryKeySelective(exportProduct);
        }
    }

    @Override
    public void delete(String id) {
        exportDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo findAll(ExportExample example, int page, int size) {
        PageHelper.startPage(page,size);
        List<Export> list = exportDao.selectByExample(example);
        return new PageInfo(list);
    }

    @Override
    public void updateE(ExportResult exportResult) {
        //        报运单 状态
        String exportId = exportResult.getExportId();
        Export export = exportDao.selectByPrimaryKey(exportId);
        export.setState(exportResult.getState());
        export.setRemark(exportResult.getRemark());
        exportDao.updateByPrimaryKeySelective(export);
//        货物
        Set<ExportProductResult> productResults = exportResult.getProducts();
        for (ExportProductResult productResult : productResults) {
            ExportProduct exportProduct = exportProductDao.selectByPrimaryKey(productResult.getExportProductId());
            exportProduct.setTax( productResult.getTax());
            exportProductDao.updateByPrimaryKeySelective(exportProduct);
        }
    }
}

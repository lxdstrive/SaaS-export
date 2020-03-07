package cn.itcast.service.cargo.impl;



import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.dao.cargo.ContractProductDao;
import cn.itcast.dao.cargo.ExtCproductDao;
import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ContractProduct;
import cn.itcast.domain.cargo.ContractProductExample;
import cn.itcast.domain.cargo.ExtCproduct;
import cn.itcast.vo.ContractProductVo;
import cn.itcast.service.cargo.ContractProductService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


/**
 * 业务层接口
 */
@Service
@Transactional
public class ContractProductServiceImpl implements ContractProductService {

    @Autowired
    private ContractProductDao contractProductDao;

    @Autowired
    private ContractDao contractDao;

    @Autowired
    private ExtCproductDao extCproductDao;
	/**
	 * 保存
	 */
    @Override
    public void save(ContractProduct contractProduct) {
        //设置货物的小计
        Double amount = contractProduct.getPrice()*contractProduct.getCnumber();
        contractProduct.setAmount(amount);
        //保存货物信息
        contractProductDao.insertSelective(contractProduct);


        //更新合同的货物种类数
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        contract.setProNum(contract.getProNum()+1);
        //更新合同的总金额
        contract.setTotalAmount(contract.getTotalAmount()+amount);
        // 更新货物信息
        contractDao.updateByPrimaryKeySelective(contract);

    }

    /**
	 * 更新
	 */
    @Override
    public void update(ContractProduct contractProduct) {
        //获得修改之前的货物小计
        ContractProduct contractProduct_old = contractProductDao.selectByPrimaryKey(contractProduct.getId());
        Double amountOld = contractProduct_old.getAmount();

        //设置新的货物小计
        double amountNew = contractProduct.getCnumber()*contractProduct.getPrice();
        contractProduct.setAmount(amountNew);
        contractProductDao.updateByPrimaryKeySelective(contractProduct);

        //更新合同的信息
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        //更新合同的总金额
        contract.setTotalAmount(contract.getTotalAmount()-amountOld+amountNew);
        // 更新合同信息
        contractDao.updateByPrimaryKeySelective(contract);
    }

    /**
     * 删除
     */
    @Override
    public void delete(String id) {

            //删除货物下的附件信息
        ContractProduct contractProduct = contractProductDao.selectByPrimaryKey(id);
        //该货物下的附件
        List<ExtCproduct> extCproducts = contractProduct.getExtCproducts();

        Double totalExtProductAmount = 0.0;
        for (ExtCproduct extCproduct : extCproducts) {

            totalExtProductAmount+=extCproduct.getAmount();

            extCproductDao.deleteByPrimaryKey(extCproduct.getId());
        }

        //修改合同的货物数量     -1
        Contract contract = contractDao.selectByPrimaryKey(contractProduct.getContractId());
        contract.setProNum(contract.getProNum()-1);

        //修改合同上的附件数量
        contract.setExtNum(contract.getExtNum()-extCproducts.size());

            //修改合同上的总金额
        contract.setTotalAmount(contract.getTotalAmount() - contractProduct.getAmount() - totalExtProductAmount);

        contractDao.updateByPrimaryKeySelective(contract);

        // 删除货物信息
        contractProductDao.deleteByPrimaryKey(id);
    }

    /**
     * 根据id查询
     */
    @Override
    public ContractProduct findById(String id) {
        return contractProductDao.selectByPrimaryKey(id);
    }

    /**
     * 分页查询
     */
    @Override
    public PageInfo findAll(ContractProductExample example, int page, int size) {
        PageHelper.startPage(page,size);
        List<ContractProduct> contractProducts = contractProductDao.selectByExample(example);
        return new PageInfo(contractProducts);
    }

    @Override
    public void saveList(List<ContractProduct> productList) {
        for (ContractProduct contractProduct : productList) {
            this.save(contractProduct);
        }
    }

    @Override
    public List<ContractProductVo> findContractProductVoByShipTime(String inputDate, String companyId) {
        return contractProductDao.findContractProductVoByShipTime(inputDate,companyId);
    }
}

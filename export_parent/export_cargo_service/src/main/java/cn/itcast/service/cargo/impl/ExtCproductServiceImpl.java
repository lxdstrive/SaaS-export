package cn.itcast.service.cargo.impl;



import cn.itcast.dao.cargo.ContractDao;
import cn.itcast.dao.cargo.ExtCproductDao;
import cn.itcast.domain.cargo.Contract;
import cn.itcast.domain.cargo.ExtCproduct;
import cn.itcast.domain.cargo.ExtCproductExample;
import cn.itcast.service.cargo.ExtCproductService;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ExtCproductServiceImpl implements ExtCproductService {

	@Autowired
	private ExtCproductDao extCproductDao;

	@Autowired
	private ContractDao contractDao;
	/**
	 * 保存
	 */
	@Override
	public void save(ExtCproduct extCproduct) {
		//1、计算小计金额
		double amount = extCproduct.getCnumber() * extCproduct.getPrice();
		extCproduct.setAmount(amount);
		//修改合同上的附件数 + 1
		Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
		contract.setExtNum(contract.getExtNum()+1);
		////修改合同上的总金额
		contract.setTotalAmount(contract.getTotalAmount()+amount);

		//更新contract
		contractDao.updateByPrimaryKeySelective(contract);
		// 保存附件

		extCproductDao.insertSelective(extCproduct);
	}


	/**
	 * 更新
	 */

	@Override
	public void update(ExtCproduct extCproduct) {
		//        从表中查询之前的小计金额
		ExtCproduct extCproduct_old = extCproductDao.selectByPrimaryKey(extCproduct.getId());
		Double amount_old = extCproduct_old.getAmount();

//        1、重新计算小计金额
		double amount_new = extCproduct.getCnumber() * extCproduct.getPrice();
		extCproduct.setAmount(amount_new);
		String contractId = extCproduct.getContractId();
//        查询合同对象
		Contract contract = contractDao.selectByPrimaryKey(contractId);
//        2、修改合同的总金额    合同的总金额-原小计金额+新的小计金额
		contract.setTotalAmount(contract.getTotalAmount()-amount_old+amount_new);
		contractDao.updateByPrimaryKeySelective(contract);

		extCproductDao.updateByPrimaryKeySelective(extCproduct);

	}

	/**
	 * 删除
	 */
	@Override
	public void delete(String id) {
		ExtCproduct extCproduct = extCproductDao.selectByPrimaryKey(id);
		//获取合同
		Contract contract = contractDao.selectByPrimaryKey(extCproduct.getContractId());
		//修改合同上的附件数目
		contract.setExtNum(contract.getExtNum()-1);

		Double amount = contract.getTotalAmount()-extCproduct.getAmount();

		//修改合同上的总金额
		contract.setTotalAmount(amount);

		//更新合同
		contractDao.updateByPrimaryKeySelective(contract);

		//删除附件
		extCproductDao.deleteByPrimaryKey(id);
	}


	/**
	 * 根据id查询
	 */

	@Override
	public ExtCproduct findById(String id) {
		return extCproductDao.selectByPrimaryKey(id);
	}

	/**
	 * 分页查询
	 */
	@Override
	public PageInfo findAll(ExtCproductExample example, int page, int size) {
		PageHelper.startPage(page,size);
		List<ExtCproduct> list = extCproductDao.selectByExample(example);
		return new PageInfo(list);
	}

}

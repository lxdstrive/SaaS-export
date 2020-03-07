package cn.itcast.service.cargo.impl;



import cn.itcast.dao.cargo.FactoryDao;
import cn.itcast.domain.cargo.Factory;
import cn.itcast.domain.cargo.FactoryExample;
import cn.itcast.service.cargo.FactoryService;
import com.alibaba.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

/**
 */
@Service
public class FactoryServiceImpl implements FactoryService {
	@Autowired
	private FactoryDao factoryDao;
	/**
	 * 保存
	 */

	@Override
	public void save(Factory factory) {

	}

	/**
	 * 更新
	 */
	@Override
	public void update(Factory factory) {

	}


	/**
	 * 删除
	 */
	@Override
	public void delete(String id) {

	}


	/**
	 * 根据id查询
	 */
	@Override
	public Factory findById(String id) {
		return null;
	}


	//查询所有
	@Override
	public List<Factory> findAll(FactoryExample example) {
		return factoryDao.selectByExample(example);
	}


}

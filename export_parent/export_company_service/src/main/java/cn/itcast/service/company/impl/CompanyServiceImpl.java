package cn.itcast.service.company.impl;

import cn.itcast.dao.company.CompanyDao;
import cn.itcast.domain.company.Company;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import cn.itcast.service.company.CompanyService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

@Service
public class CompanyServiceImpl implements CompanyService {

    @Autowired
    private CompanyDao companyDao;
    @Override
    public List<Company> findAll() {
        return companyDao.findAll();
    }

    @Override
    public void save(Company company) {
        companyDao.insert(company);
    }

    @Override
    public void updateById(Company company) {
        companyDao.updateByPrimaryKey(company);
    }

    @Override
    public Company findById(String id) {
        return companyDao.selectByPrimaryKey(id);
    }

    @Override
    public void deleteById(String id) {
        companyDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Company> findPage(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Company> list = companyDao.findAll();

        PageInfo<Company> pageInfo = new PageInfo<>(list, 5);
        return pageInfo;
    }
}

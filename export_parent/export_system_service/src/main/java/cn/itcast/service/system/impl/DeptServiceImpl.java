package cn.itcast.service.system.impl;

import cn.itcast.dao.system.DeptDao;
import cn.itcast.domain.system.Dept;
import cn.itcast.service.system.DeptService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DeptServiceImpl implements DeptService {

    @Autowired
    private DeptDao deptDao;
    @Override
    public List<Dept> findAll(String companyId) {
        return deptDao.findAll(companyId);
    }

    @Override
    public void save(Dept dept) {
        deptDao.insert(dept);
    }

    @Override
    public void update(Dept dept) {
        deptDao.updateByPrimaryKey(dept);
    }

    @Override
    public Dept findById(String id) {
        return deptDao.selectByPrimaryKey(id);
    }

    @Override
    public void deleteById(String id) {
        deptDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo findPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Dept> list = deptDao.findAll(companyId);

        PageInfo<Dept> pageInfo = new PageInfo<>(list, 5);
        return pageInfo;
    }


}

package cn.itcast.service.system.impl;

import cn.itcast.dao.system.RoleDao;
import cn.itcast.domain.system.Role;
import cn.itcast.service.system.RoleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleDao roleDao;

    @Override
    public List<Role> findAll(String companyId) {
        return roleDao.findAll(companyId);
    }

    @Override
    public void save(Role role) {
        roleDao.insert(role);
    }

    @Override
    public void update(Role role) {
        roleDao.updateByPrimaryKey(role);
    }

    @Override
    public Role findById(String id) {
        return roleDao.selectByPrimaryKey(id);
    }

    @Override
    public void deleteById(String id) {
        roleDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo findPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<Role> list = roleDao.findAll(companyId);

        PageInfo<Role> pageInfo = new PageInfo<>(list, 5);
        return pageInfo;
    }

    //    保存角色分配权限的数据
    @Override
    @Transactional
    public void updateRoleModule(String roleid, String moduleIds) {
        //先删除再新增
        roleDao.deleteRoleAndModuleByRoleId(roleid);
        //insert into
        String[] mIds = moduleIds.split(",");
        for (String moduleId : mIds) {
            roleDao.insertRoleAndModule(roleid, moduleId);
        }
    }

    @Override
    public List<String> findByUserId(String id) {
        return roleDao.findByUserId(id);
    }


}

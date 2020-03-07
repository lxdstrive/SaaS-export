package cn.itcast.service.system.impl;

import cn.itcast.dao.system.ModuleDao;
import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.ModuleService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ModuleServiceImpl implements ModuleService {

    @Autowired
    private ModuleDao moduleDao;
    @Override
    public List<Module> findAll() {
        return moduleDao.findAll();
    }

    @Override
    public void save(Module module) {
        moduleDao.insert(module);
    }

    @Override
    public void updateById(Module module) {
        moduleDao.updateByPrimaryKey(module);
    }

    @Override
    public Module findById(String id) {
        return moduleDao.findByPrimaryKey(id);
    }

    @Override
    public void deleteById(String id) {
        moduleDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo<Module> findPage(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<Module> list = moduleDao.findAll();

        PageInfo<Module> pageInfo = new PageInfo<>(list, 5);
        return pageInfo;
    }

    @Override
    public List<Module> findByRoleId(String roleid) {
        List<Module> roleModuleList = moduleDao.findByRoleId(roleid);
        return roleModuleList;
    }

    //自己的
    @Override
    public Map findByRoleIdForMy(String roleid) {
        Map<String,String> map = new HashMap<>();
        List<String> modules = moduleDao.findModuleIdByRoid(roleid);
        for (String module : modules) {
            map.put(module,roleid);
        }
        return map;
    }

    @Override
    public List<Module> findModulesByUser(User user) {
        //如果是saas管理员用户  select * from ss_module where belong = 0;
        List<Module> moduleList;
        if (user.getDegree()==0){
            moduleList = moduleDao.findByBelong(0);
        }else if (user.getDegree()==1){
            moduleList = moduleDao.findByBelong(1);

        }else {
            moduleList= moduleDao.findByUserId(user.getId());
        }
        //如果是企业管理员用户 select * from ss_module where belong = 1
        //如果belong != 1 != 0
      /*
       select m.* from ss_module m,pe_role_module rm,pe_role r,pe_role_user ru,pe_user u where
       m,module_id = rm.module_id and rm.role_id = r.role_id and r.role_id = ru.role_id and ru.user_id = u.user_id and user_id=
      * */

        return moduleList;
    }
}

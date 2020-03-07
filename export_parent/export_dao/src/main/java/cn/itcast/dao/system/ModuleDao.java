package cn.itcast.dao.system;

import cn.itcast.domain.system.Module;

import java.util.List;

public interface ModuleDao {

    List<Module> findAll();

    void insert(Module module);

    void updateByPrimaryKey(Module module);

    Module findByPrimaryKey(String id);

    void deleteByPrimaryKey(String id);

    List<Module> findByRoleId(String roleid);

    //自己的
    List<String> findModuleIdByRoid(String roleid);

    List<Module> findByBelong(int belong);

    List<Module> findByUserId(String userid);
}

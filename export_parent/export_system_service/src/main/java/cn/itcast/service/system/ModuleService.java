package cn.itcast.service.system;

import cn.itcast.domain.system.Module;
import cn.itcast.domain.system.User;
import com.github.pagehelper.PageInfo;

import java.util.List;
import java.util.Map;

public interface ModuleService {
    List<Module> findAll();

    void save(Module module);

    void updateById(Module module);

    Module findById(String id);

    void deleteById(String id);

    PageInfo<Module> findPage(Integer page, Integer pageSize);

    List<Module> findByRoleId(String roleid);

    //自己的
    Map findByRoleIdForMy(String roleid);

    List<Module> findModulesByUser(User user);
}

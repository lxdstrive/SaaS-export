package cn.itcast.dao.system;

import cn.itcast.domain.system.Role;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface RoleDao {

    //角色管理是企业管理员在操作，所以一定要带着companyId进行
    public List<Role> findAll(String companyId);

    void insert(Role role);

    Role selectByPrimaryKey(String id);

    void updateByPrimaryKey(Role role);

    void deleteByPrimaryKey(String id);

    void deleteRoleAndModuleByRoleId(String roleid);

    void insertRoleAndModule(@Param("roleid") String roleid,@Param("moduleId") String moduleId);

    List<String> findByUserId(String id);
}

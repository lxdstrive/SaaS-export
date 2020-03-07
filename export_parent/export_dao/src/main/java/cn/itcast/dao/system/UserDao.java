package cn.itcast.dao.system;

import cn.itcast.domain.system.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserDao {

    //部门管理是企业管理员在操作，所以一定要带着companyId进行
    public List<User> findAll(String companyId);

    void insert(User user);

    User selectByPrimaryKey(String id);

    void updateByPrimaryKey(User user);

    void deleteByPrimaryKey(String id);

    void deleteUserRoleByUserId(String userid);

    void insertUserAndRole(@Param("userid") String userid,@Param("roleId") String roleId);

    User findUserByEmail(String email);
}

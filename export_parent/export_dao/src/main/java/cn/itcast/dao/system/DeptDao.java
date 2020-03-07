package cn.itcast.dao.system;

import cn.itcast.domain.system.Dept;

import java.util.List;

public interface DeptDao {

    //部门管理是企业管理员在操作，所以一定要带着companyId进行
    public List<Dept> findAll(String companyId);

    void insert(Dept dept);

    Dept selectByPrimaryKey(String id);

    void updateByPrimaryKey(Dept dept);

    void deleteByPrimaryKey(String id);
}

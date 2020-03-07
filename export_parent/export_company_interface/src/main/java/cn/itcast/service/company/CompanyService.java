package cn.itcast.service.company;

import cn.itcast.domain.company.Company;
import com.github.pagehelper.PageInfo;

import java.util.List;

public interface CompanyService {
    List<Company> findAll();

    void save(Company company);

    void updateById(Company company);

    Company findById(String id);

    void deleteById(String id);

    PageInfo<Company> findPage(Integer page, Integer pageSize);
}

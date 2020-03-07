package cn.itcast.service.aspect.impl;

import cn.itcast.dao.aspect.SysLogDao;
import cn.itcast.domain.aop.SysLog;
import cn.itcast.service.aspect.SysLogService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SysLogServiceImpl  implements SysLogService {
    @Autowired
    private SysLogDao sysLogDao;

    //保存
    public void save(SysLog sysLog) {
        sysLogDao.save(sysLog);
    }

    //分页查询
    public PageInfo findPage(String companyId, int page, int size) {
        PageHelper.startPage(page, size);
        List<SysLog> list = sysLogDao.findAll(companyId);
        return new PageInfo(list,10);
    }
}

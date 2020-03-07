package cn.itcast.service.system.impl;

import cn.itcast.dao.system.UserDao;
import cn.itcast.domain.system.User;
import cn.itcast.service.system.UserService;
import cn.itcast.service.system.UserService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserDao userDao;
    @Override
    public List<User> findAll(String companyId) {
        return userDao.findAll(companyId);
    }

    @Override
    public void save(User user) {
        String password = user.getPassword();
        String md5Hash = new Md5Hash(password, user.getEmail(), 2).toString();
        user.setPassword(md5Hash);
        userDao.insert(user);
    }

    @Override
    public void update(User user) {
        userDao.updateByPrimaryKey(user);
    }

    @Override
    public User findById(String id) {
        return userDao.selectByPrimaryKey(id);
    }

    @Override
    public void deleteById(String id) {
        userDao.deleteByPrimaryKey(id);
    }

    @Override
    public PageInfo findPage(String companyId, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<User> list = userDao.findAll(companyId);

        PageInfo<User> pageInfo = new PageInfo<>(list, 5);
        return pageInfo;
    }

    @Override
    @Transactional
    public void changeRole(String userid, String roleIds) {
        //先删除用户原有的角色
        userDao.deleteUserRoleByUserId(userid);

        String[] rIds = roleIds.split(",");

        for (String rId : rIds) {
            //添加新的角色
            userDao.insertUserAndRole(userid, rId);
        }
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao.findUserByEmail(email);
    }


}

package com.dyz.myBatis.services;

import com.dyz.myBatis.dao.UserMapper;
import com.dyz.myBatis.daoImp.UserDaoImp;
import com.dyz.myBatis.model.User;
import org.apache.ibatis.session.SqlSessionFactory;

import java.sql.SQLException;

/**
 * Created by kevin on 2016/6/17.
 */
public class UserService {
    private UserMapper um;
    private static UserService userService = new UserService();
    private SqlSessionFactory sqlSessionFactory;
    private UserService(){

    }

    public void initSetSession(SqlSessionFactory sqlSessionFactory){
        um = new UserDaoImp(sqlSessionFactory);
    }

    public static UserService getInstance(){
        return userService;
    }

    public void insertUser(User user) throws SQLException {
        int index = um.insert(user);
        System.out.println("-add index->>"+index);
    }
}

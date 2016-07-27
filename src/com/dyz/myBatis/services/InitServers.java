package com.dyz.myBatis.services;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by kevin on 2016/6/17.
 */
public class InitServers {



    public void initServersFun() throws IOException {
        Reader reader = Resources.getResourceAsReader("myBatisConfig.xml");
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);
        //===============================================================
        //UserService.getInstance().initSetSession(sessionFactory);
        AccountService.getInstance().initSetSession(sessionFactory);
    }

    private static InitServers initServers = new InitServers();

    public static InitServers getInstance(){
        return initServers;
    }
}

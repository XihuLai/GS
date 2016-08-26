package com.dyz.myBatis.services;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.ContactWayMapper;
import com.dyz.myBatis.daoImp.ContactWayDaoImp;
import com.dyz.myBatis.model.ContactWay;

public class ContactWayService {
	
	  private ContactWayMapper contactWayMapper;

	    private static ContactWayService contactWayService = new ContactWayService();

	    public static ContactWayService getInstance(){
	        return contactWayService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	contactWayMapper = new ContactWayDaoImp(sqlSessionFactory);
	    }
	    
	    
	    public ContactWay selectByPrimaryKey(Integer id){
	    	
			return contactWayMapper.selectByPrimaryKey(id);
	    }

}

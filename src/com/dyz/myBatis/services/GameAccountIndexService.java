package com.dyz.myBatis.services;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.GameAccountIndexMapper;
import com.dyz.myBatis.daoImp.GameAccountIndexDaoImp;

public class GameAccountIndexService {

	 private GameAccountIndexMapper gameAccountIndexMap;

	 
	 private static GameAccountIndexService gameService = new GameAccountIndexService();
	 public static GameAccountIndexService getInstance(){
	        return gameService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	gameAccountIndexMap = new GameAccountIndexDaoImp(sqlSessionFactory);
	    }
	    
	    
	    
}

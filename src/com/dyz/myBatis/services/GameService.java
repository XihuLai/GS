package com.dyz.myBatis.services;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.GameMapper;
import com.dyz.myBatis.daoImp.GameDaoImp;

public class GameService {

	 private GameMapper gameMap;

	 
	 private static GameService gameService = new GameService();
	 public static GameService getInstance(){
	        return gameService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	gameMap = new GameDaoImp(sqlSessionFactory);
	    }
	    
	    
	    
}

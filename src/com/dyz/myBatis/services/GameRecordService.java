package com.dyz.myBatis.services;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.GameRecordMapper;
import com.dyz.myBatis.daoImp.GameRecordDaoImp;

public class GameRecordService {

	 private GameRecordMapper gameRecordMap;

	 
	 private static GameRecordService gameService = new GameRecordService();
	 public static GameRecordService getInstance(){
	        return gameService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	gameRecordMap = new GameRecordDaoImp(sqlSessionFactory);
	    }
	    
	    
	    
}

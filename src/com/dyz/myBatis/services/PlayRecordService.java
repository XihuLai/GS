package com.dyz.myBatis.services;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.PlayRecordMapper;
import com.dyz.myBatis.daoImp.PlayRecordImp;
import com.dyz.myBatis.model.PlayRecord;

public class PlayRecordService {

	
	  private PlayRecordMapper playRecordMapper;

	    private static PlayRecordService playRecordService = new PlayRecordService();

	    public static PlayRecordService getInstance(){
	        return playRecordService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	playRecordMapper = new PlayRecordImp(sqlSessionFactory);
	    }
	    
	    
	    
	    public PlayRecord selectByPrimaryKey(Integer id) {
	    	PlayRecord playrecord = playRecordMapper.selectByPrimaryKey(id);
	        return playrecord;
		}
	    
	    public int saveSelective(PlayRecord playRecord){
	    	 int index = playRecordMapper.saveSelective(playRecord);
	         System.out.println("-account insert index->>" + index);
	         return index;
	    }
	    
	    
	    public PlayRecord selectByStandingsDetailId(Integer id) {
	    	PlayRecord playrecord = playRecordMapper.selectByStandingsDetailId(id);
	        return playrecord;
		}
}

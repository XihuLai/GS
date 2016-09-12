package com.dyz.myBatis.services;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.StandingsMapper;
import com.dyz.myBatis.daoImp.StandingsDaoImp;
import com.dyz.myBatis.model.RoomInfo;
import com.dyz.myBatis.model.Standings;

/**
 * 
 * @author LUCK
 *
 */
public class StandingsService {
	  private StandingsMapper standingsMapper;

	    private static StandingsService standingsService = new StandingsService();

	    public static StandingsService getInstance(){
	        return standingsService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	standingsMapper = new StandingsDaoImp(sqlSessionFactory);
	    }
	    
	    
	    public int saveSelective(Standings standings){
	    	 int index = standingsMapper.saveSelective(standings);
	         System.out.println("-account insert index->>" + index);
	         return index;
	    }
	    
	    public Standings selectByPrimaryKey(Integer id){
	    	Standings obj = standingsMapper.selectByPrimaryKey(id);
	         return obj;
	    }
	    public Standings selectByRoomId(Integer roomId){
	    	Standings obj = standingsMapper.selectByRoomId(roomId);
	         return obj;
	    }
}

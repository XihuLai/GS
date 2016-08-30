package com.dyz.myBatis.services;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.StandingsDetailMapper;
import com.dyz.myBatis.daoImp.StandingsDetailDaoImp;
import com.dyz.myBatis.model.StandingsDetail;

/**
 * 
 * @author LUCK
 *
 */
public class StandingsDetailService {
	  private StandingsDetailMapper standingsDetailMapper;

	    private static StandingsDetailService standingsDetailService = new StandingsDetailService();

	    public static StandingsDetailService getInstance(){
	        return standingsDetailService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	standingsDetailMapper = new StandingsDetailDaoImp(sqlSessionFactory);
	    }
	    
	    public int saveSelective(StandingsDetail standingsDetail){
	    	 int index = standingsDetailMapper.saveSelective(standingsDetail);
	         System.out.println("-account insert index->>" + index);
	         return index;
	    }
	    
	    
	    public StandingsDetail selectByPrimaryKey(Integer  id){
	         return standingsDetailMapper.selectByPrimaryKey(id);
	    }
}

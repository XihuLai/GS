package com.dyz.myBatis.services;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.StandingsRelationMapper;
import com.dyz.myBatis.daoImp.StandingsRelationDaoImp;
import com.dyz.myBatis.model.StandingsRelation;

/**
 * 
 * @author LUCK
 *
 */
public class StandingsRelationService {
	  private StandingsRelationMapper standingsRelationMapper;

	    private static StandingsRelationService standingsService = new StandingsRelationService();

	    public static StandingsRelationService getInstance(){
	        return standingsService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	standingsRelationMapper = new StandingsRelationDaoImp(sqlSessionFactory);
	    }
	    
	    
	    public int saveSelective(StandingsRelation standingsRelation){
	    	 int index = standingsRelationMapper.saveSelective(standingsRelation);
	         System.out.println("-account insert index->>" + index);
	         return index;
	    }
	    
	    public int save(StandingsRelation standingsRelation){
	    	 int index = standingsRelationMapper.save(standingsRelation);
	         System.out.println("-account insert index->>" + index);
	         return index;
	    }
	    
	    public List<Integer> selectDetailIdsByStandingsId(Integer standingsId){
	    	 List<Integer> ids = standingsRelationMapper.selectDetailIdsByStandingsId(standingsId);
	         return ids;
	    }
}

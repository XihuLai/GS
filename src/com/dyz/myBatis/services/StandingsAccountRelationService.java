package com.dyz.myBatis.services;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.StandingsAccountRelationMapper;
import com.dyz.myBatis.daoImp.StandingsAccountRelationDaoImp;
import com.dyz.myBatis.model.StandingsAccountRelation;

/**
 * 
 * @author LUCK
 *
 */
public class StandingsAccountRelationService {
	  private StandingsAccountRelationMapper standingsAccountRelationMapper;

	    private static StandingsAccountRelationService standingsService = new StandingsAccountRelationService();

	    public static StandingsAccountRelationService getInstance(){
	        return standingsService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	standingsAccountRelationMapper = new StandingsAccountRelationDaoImp(sqlSessionFactory);
	    }
	    
	    
	    public int saveSelective(StandingsAccountRelation standingsAccountRelation){
	    	 int index = standingsAccountRelationMapper.saveSelective(standingsAccountRelation);
	         System.out.println("-account insert index->>" + index);
	         return index;
	    }
	    
	    public int save(StandingsAccountRelation standingsAccountRelation){
	    	 int index = standingsAccountRelationMapper.save(standingsAccountRelation);
	         System.out.println("-account insert index->>" + index);
	         return index;
	    }
	    
	    
	    public List<Integer> selectNearestStandingsIdByAccountId(Integer accountId){
	    	 List<Integer> standingsIds = standingsAccountRelationMapper.selectNearestStandingsIdByAccountId(accountId);
	         return standingsIds;
	    }
}

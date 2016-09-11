package com.dyz.myBatis.services;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.PrizeRuleMapper;
import com.dyz.myBatis.daoImp.PrizeRuleDaoImp;
import com.dyz.myBatis.model.PrizeRule;

public class PrizeRuleService {
	 private PrizeRuleMapper prizeRuleMapper;

	    private static PrizeRuleService prizeRuleService = new PrizeRuleService();

	    public static PrizeRuleService getInstance(){
	        return prizeRuleService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	prizeRuleMapper = new PrizeRuleDaoImp(sqlSessionFactory);
	    }
	    
	    
	    public PrizeRule selectByPrimaryKey(Integer id) {
	    	PrizeRule prizeRule = prizeRuleMapper.selectByPrimaryKey(id);
	        return prizeRule;
		}
}

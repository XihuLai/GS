package com.dyz.myBatis.daoImp;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.AccountMapper;
import com.dyz.myBatis.dao.PrizeRuleMapper;
import com.dyz.myBatis.model.Account;
import com.dyz.myBatis.model.PrizeRule;

public class PrizeRuleDaoImp implements PrizeRuleMapper {

	 private SqlSessionFactory sqlSessionFactory;
	    public PrizeRuleDaoImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	    }
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(PrizeRule record) {
		 int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	        	PrizeRuleMapper mapper = sqlSession.getMapper(PrizeRuleMapper.class);
	            flag = mapper.insert(record);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return flag;
	}

	@Override
	public int insertSelective(PrizeRule record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PrizeRule selectByPrimaryKey(Integer id) {
		PrizeRule result = null;
         SqlSession sqlSession = sqlSessionFactory.openSession();
         try {
        	 PrizeRuleMapper mapper = sqlSession.getMapper(PrizeRuleMapper.class);
             result = mapper.selectByPrimaryKey(id);
             sqlSession.commit();
         } catch (Exception e) {
             e.printStackTrace();
         }finally {
             sqlSession.close();
         }
        return result;
	}

	@Override
	public int updateByPrimaryKeySelective(PrizeRule record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(PrizeRule record) {
		// TODO Auto-generated method stub
		return 0;
	}

}

package com.dyz.myBatis.daoImp;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.StandingsAccountRelationMapper;
import com.dyz.myBatis.model.StandingsAccountRelation;

public class StandingsAccountRelationDaoImp implements StandingsAccountRelationMapper {
	  private SqlSessionFactory sqlSessionFactory;
	    public StandingsAccountRelationDaoImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	    }
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int save(StandingsAccountRelation StandingsAccountRelation) {
		 int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            StandingsAccountRelationMapper mapper = sqlSession.getMapper(StandingsAccountRelationMapper.class);
	            flag = mapper.save(StandingsAccountRelation);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return flag;
	}

	@Override
	public int saveSelective(StandingsAccountRelation standingsAccountRelation) {
		 int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	        	StandingsAccountRelationMapper mapper = sqlSession.getMapper(StandingsAccountRelationMapper.class);
	            flag = mapper.saveSelective(standingsAccountRelation);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return flag;
	}

	@Override
	public StandingsAccountRelation selectByPrimaryKey(Integer id) {
		StandingsAccountRelation standingsAccountRelation = null;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	        	StandingsAccountRelationMapper mapper = sqlSession.getMapper(StandingsAccountRelationMapper.class);
	            standingsAccountRelation = mapper.selectByPrimaryKey(id);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return standingsAccountRelation;
	}

	@Override
	public int updateByPrimaryKeySelective(StandingsAccountRelation standingsAccountRelation) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(StandingsAccountRelation standingsAccountRelation) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<Integer> selectNearestStandingsIdByAccountId(Integer accountId) {
		 SqlSession sqlSession = sqlSessionFactory.openSession();
		 StandingsAccountRelationMapper mapper = sqlSession.getMapper(StandingsAccountRelationMapper.class);
		return mapper.selectNearestStandingsIdByAccountId(accountId);
	}

}

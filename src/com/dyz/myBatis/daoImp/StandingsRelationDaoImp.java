package com.dyz.myBatis.daoImp;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.StandingsRelationMapper;
import com.dyz.myBatis.model.StandingsRelation;

public class StandingsRelationDaoImp implements StandingsRelationMapper {
	  private SqlSessionFactory sqlSessionFactory;
	    public StandingsRelationDaoImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	    }
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int save(StandingsRelation standingsRelation) {
		 int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            StandingsRelationMapper mapper = sqlSession.getMapper(StandingsRelationMapper.class);
	            flag = mapper.save(standingsRelation);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return flag;
	}

	@Override
	public int saveSelective(StandingsRelation standingsRelation) {
		 int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	        	StandingsRelationMapper mapper = sqlSession.getMapper(StandingsRelationMapper.class);
	            flag = mapper.saveSelective(standingsRelation);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return flag;
	}

	@Override
	public StandingsRelation selectByPrimaryKey(Integer id) {
		StandingsRelation standingsRelation = null;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	        	StandingsRelationMapper mapper = sqlSession.getMapper(StandingsRelationMapper.class);
	            standingsRelation = mapper.selectByPrimaryKey(id);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return standingsRelation;
	}

	@Override
	public int updateByPrimaryKeySelective(StandingsRelation standingsRelation) {
		return 0;
	}

	@Override
	public int updateByPrimaryKey(StandingsRelation standingsRelation) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public List<Integer> selectDetailIdsByStandingsId(Integer standingsId) {
		List<Integer> ids = null;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
        	StandingsRelationMapper mapper = sqlSession.getMapper(StandingsRelationMapper.class);
        	ids = mapper.selectDetailIdsByStandingsId(standingsId);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            sqlSession.close();
        }
        return ids;
	}

}

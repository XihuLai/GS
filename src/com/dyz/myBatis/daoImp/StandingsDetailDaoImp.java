package com.dyz.myBatis.daoImp;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.StandingsDetailMapper;
import com.dyz.myBatis.dao.StandingsMapper;
import com.dyz.myBatis.model.Standings;
import com.dyz.myBatis.model.StandingsDetail;

public class StandingsDetailDaoImp implements StandingsDetailMapper {
	  private SqlSessionFactory sqlSessionFactory;
	    public StandingsDetailDaoImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	    }
		@Override
		public int deleteByPrimaryKey(Integer id) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int save(StandingsDetail record) {
			 int flag = 0;
		        SqlSession sqlSession = sqlSessionFactory.openSession();
		        try {
		            StandingsDetailMapper mapper = sqlSession.getMapper(StandingsDetailMapper.class);
		            flag = mapper.save(record);
		            sqlSession.commit();
		        } catch (Exception e) {
		            e.printStackTrace();
		        }finally {
		            sqlSession.close();
		        }
		        return flag;
		}
		@Override
		public int saveSelective(StandingsDetail record) {
			int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            StandingsDetailMapper mapper = sqlSession.getMapper(StandingsDetailMapper.class);
	            flag = mapper.saveSelective(record);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return flag;
		}
		@Override
		public StandingsDetail selectByPrimaryKey(Integer id) {
			StandingsDetail standingsDetail = null;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            StandingsDetailMapper mapper = sqlSession.getMapper(StandingsDetailMapper.class);
	            standingsDetail = mapper.selectByPrimaryKey(id);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return standingsDetail;
		}
		@Override
		public int updateByPrimaryKeySelective(StandingsDetail record) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int updateByPrimaryKey(StandingsDetail record) {
			// TODO Auto-generated method stub
			return 0;
		}
	

}

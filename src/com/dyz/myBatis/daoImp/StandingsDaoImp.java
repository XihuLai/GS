package com.dyz.myBatis.daoImp;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.AccountMapper;
import com.dyz.myBatis.dao.RoomInfoMapper;
import com.dyz.myBatis.dao.StandingsMapper;
import com.dyz.myBatis.model.RoomInfo;
import com.dyz.myBatis.model.Standings;

public class StandingsDaoImp implements StandingsMapper {
	  private SqlSessionFactory sqlSessionFactory;
	    public StandingsDaoImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	    }
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int save(Standings record) {
		 int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            StandingsMapper mapper = sqlSession.getMapper(StandingsMapper.class);
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
	public int saveSelective(Standings record) {
		 int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            StandingsMapper mapper = sqlSession.getMapper(StandingsMapper.class);
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
	public Standings selectByPrimaryKey(Integer id) {
		 Standings standings = null;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            StandingsMapper mapper = sqlSession.getMapper(StandingsMapper.class);
	            standings = mapper.selectByPrimaryKey(id);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return standings;
	}

	@Override
	public int updateByPrimaryKeySelective(Standings record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(Standings record) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public Standings selectByRoomId(Integer roomId) {
		 Standings standings = null;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            StandingsMapper mapper = sqlSession.getMapper(StandingsMapper.class);
	            standings = mapper.selectByRoomId(roomId);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return standings;
	}


}

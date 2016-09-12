package com.dyz.myBatis.daoImp;

import java.util.Date;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.RoomInfoMapper;
import com.dyz.myBatis.dao.StandingsMapper;
import com.dyz.myBatis.model.RoomInfo;

public class RoomInfoDaoImp implements RoomInfoMapper {
	 private SqlSessionFactory sqlSessionFactory;
	    public RoomInfoDaoImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	    }
		@Override
		public int deleteByPrimaryKey(Integer id) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int insert(RoomInfo record) {
			int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            RoomInfoMapper mapper = sqlSession.getMapper(RoomInfoMapper.class);
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
		public int insertSelective(RoomInfo record) {
			int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            RoomInfoMapper mapper = sqlSession.getMapper(RoomInfoMapper.class);
	            flag = mapper.insertSelective(record);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return flag;
		}
		@Override
		public RoomInfo selectByPrimaryKey(Integer id) {
			RoomInfo flag = null ;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            RoomInfoMapper mapper = sqlSession.getMapper(RoomInfoMapper.class);
	            flag = mapper.selectByPrimaryKey(id);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return flag;
		}
		@Override
		public int updateByPrimaryKeySelective(RoomInfo record) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int updateByPrimaryKey(RoomInfo record) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int selectCount() {
			int roomCount = 0 ;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            RoomInfoMapper mapper = sqlSession.getMapper(RoomInfoMapper.class);
	            roomCount = mapper.selectCount();
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return roomCount;
		}
		@Override
		public int selectTodayCount(Date date) {
			int roomCount = 0 ;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            RoomInfoMapper mapper = sqlSession.getMapper(RoomInfoMapper.class);
	            roomCount = mapper.selectTodayCount(date);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return roomCount;
		}
		@Override
		public RoomInfo selectRoomId(Integer roomid) {
			RoomInfo roomCount = null;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	            RoomInfoMapper mapper = sqlSession.getMapper(RoomInfoMapper.class);
	            roomCount = mapper.selectRoomId(roomid);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return roomCount;
		}
		

	

}

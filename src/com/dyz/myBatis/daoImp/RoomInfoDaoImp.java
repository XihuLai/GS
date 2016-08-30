package com.dyz.myBatis.daoImp;

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
			// TODO Auto-generated method stub
			return null;
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

	

}

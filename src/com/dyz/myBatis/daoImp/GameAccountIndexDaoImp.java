package com.dyz.myBatis.daoImp;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.GameAccountIndexMapper;
import com.dyz.myBatis.model.GameAccountIndex;

public class GameAccountIndexDaoImp implements GameAccountIndexMapper {
	 private SqlSessionFactory sqlSessionFactory;
	    public GameAccountIndexDaoImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	    }
		@Override
		public int deleteByPrimaryKey(Integer id) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int insert(GameAccountIndex record) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int insertSelective(GameAccountIndex record) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public GameAccountIndex selectByPrimaryKey(Integer id) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public int updateByPrimaryKeySelective(GameAccountIndex record) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int updateByPrimaryKey(GameAccountIndex record) {
			// TODO Auto-generated method stub
			return 0;
		}

	

}

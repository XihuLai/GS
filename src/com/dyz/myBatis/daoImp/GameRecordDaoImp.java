package com.dyz.myBatis.daoImp;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.gameserver.gamerecord.GameRecord;
import com.dyz.myBatis.dao.GameRecordMapper;

public class GameRecordDaoImp implements GameRecordMapper {
	 private SqlSessionFactory sqlSessionFactory;
	    public GameRecordDaoImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	    }
		@Override
		public int deleteByPrimaryKey(Integer id) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int insert(GameRecord record) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int insertSelective(GameRecord record) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public GameRecord selectByPrimaryKey(Integer id) {
			// TODO Auto-generated method stub
			return null;
		}
		@Override
		public int updateByPrimaryKeySelective(GameRecord record) {
			// TODO Auto-generated method stub
			return 0;
		}
		@Override
		public int updateByPrimaryKey(GameRecord record) {
			// TODO Auto-generated method stub
			return 0;
		}

	

}

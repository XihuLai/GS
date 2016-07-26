package com.dyz.myBatis.daoImp;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.GameMapper;
import com.weipai.model.Game;

public class GameDaoImp implements GameMapper {
	 private SqlSessionFactory sqlSessionFactory;
	    public GameDaoImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	    }

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return 0;
	}

	@Override
	public int insert(Game record) {
		int flag = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            GameMapper mapper = sqlSession.getMapper(GameMapper.class);
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
	public int insertSelective(Game record) {
		return 0;
	}

	@Override
	public Game selectByPrimaryKey(Integer id) {
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(Game record) {
		return 0;
	}

	@Override
	public int updateByPrimaryKey(Game record) {
		return 0;
	}

}

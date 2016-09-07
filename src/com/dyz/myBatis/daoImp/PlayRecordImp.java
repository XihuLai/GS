package com.dyz.myBatis.daoImp;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.PlayRecordMapper;
import com.dyz.myBatis.model.PlayRecord;

public class PlayRecordImp implements PlayRecordMapper {

	 private SqlSessionFactory sqlSessionFactory;
	    public PlayRecordImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	    }

	@Override
	public int deleteByPrimaryKey(Integer id) {
		int flag = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PlayRecordMapper mapper = sqlSession.getMapper(PlayRecordMapper.class);
            flag = mapper.deleteByPrimaryKey(id);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            sqlSession.close();
        }
        return flag;
	}

	@Override
	public int save(PlayRecord record) {
		int flag = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PlayRecordMapper mapper = sqlSession.getMapper(PlayRecordMapper.class);
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
	public int updateByPrimaryKey(PlayRecord record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int saveSelective(PlayRecord record) {
		int flag = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PlayRecordMapper mapper = sqlSession.getMapper(PlayRecordMapper.class);
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
	public PlayRecord selectByPrimaryKey(Integer id) {
		PlayRecord flag = null;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PlayRecordMapper mapper = sqlSession.getMapper(PlayRecordMapper.class);
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
	public int updateByPrimaryKeySelective(PlayRecord record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKeyWithBLOBs(PlayRecord record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public PlayRecord selectByStandingsDetailId(Integer id) {
		PlayRecord flag = null;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            PlayRecordMapper mapper = sqlSession.getMapper(PlayRecordMapper.class);
            flag = mapper.selectByStandingsDetailId(id);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            sqlSession.close();
        }
        return flag;
	}


}

package com.dyz.myBatis.daoImp;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.WinnersInfoMapper;
import com.dyz.myBatis.model.WinnersInfo;

public class WinnersInfoDaoImp implements WinnersInfoMapper {
	private SqlSessionFactory sqlSessionFactory;
    public WinnersInfoDaoImp(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int save(WinnersInfo record) {
		int flag = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            WinnersInfoMapper mapper = sqlSession.getMapper(WinnersInfoMapper.class);
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
	public int saveSelective(WinnersInfo record) {
		int flag = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            WinnersInfoMapper mapper = sqlSession.getMapper(WinnersInfoMapper.class);
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
	public WinnersInfo selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(WinnersInfo record) {
		int flag = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
            WinnersInfoMapper mapper = sqlSession.getMapper(WinnersInfoMapper.class);
            flag = mapper.updateByPrimaryKeySelective(record);
            sqlSession.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            sqlSession.close();
        }
        return flag;
	}

	@Override
	public int updateByPrimaryKey(WinnersInfo record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<WinnersInfo> selectWinnersInfoByMap(String status) {
		// TODO Auto-generated method stub
		return null;
	}

}

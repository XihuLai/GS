package com.dyz.myBatis.daoImp;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.TechargerecordMapper;
import com.dyz.myBatis.dao.WinnersInfoMapper;
import com.dyz.myBatis.model.Techargerecord;

public class TechargerecordDaoImp implements TechargerecordMapper{

	private SqlSessionFactory sqlSessionFactory;
    public TechargerecordDaoImp(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }
	
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int save(Techargerecord record) {
		int flag = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
        	TechargerecordMapper mapper = sqlSession.getMapper(TechargerecordMapper.class);
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
	public int saveSelective(Techargerecord record) {
		int flag = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
        	TechargerecordMapper mapper = sqlSession.getMapper(TechargerecordMapper.class);
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
	public Techargerecord selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int updateByPrimaryKeySelective(Techargerecord record) {
		int flag = 0;
        SqlSession sqlSession = sqlSessionFactory.openSession();
        try {
        	TechargerecordMapper mapper = sqlSession.getMapper(TechargerecordMapper.class);
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
	public int updateByPrimaryKey(Techargerecord record) {
		// TODO Auto-generated method stub
		return 0;
	}

}

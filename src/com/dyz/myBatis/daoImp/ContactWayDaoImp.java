package com.dyz.myBatis.daoImp;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.ContactWayMapper;
import com.dyz.myBatis.model.ContactWay;

public class ContactWayDaoImp implements ContactWayMapper {
	  private SqlSessionFactory sqlSessionFactory;
	   public ContactWayDaoImp(SqlSessionFactory sqlSessionFactory){
	        this.sqlSessionFactory = sqlSessionFactory;
	   }
	    
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int insert(ContactWay record) {
		  int flag = 0;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	        	ContactWayMapper mapper = sqlSession.getMapper(ContactWayMapper.class);
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
	public int insertSelective(ContactWay record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ContactWay selectByPrimaryKey(Integer id) {
		ContactWay contactWay = null;
	        SqlSession sqlSession = sqlSessionFactory.openSession();
	        try {
	        	ContactWayMapper mapper = sqlSession.getMapper(ContactWayMapper.class);
	        	contactWay = mapper.selectByPrimaryKey(id);
	            sqlSession.commit();
	        } catch (Exception e) {
	            e.printStackTrace();
	        }finally {
	            sqlSession.close();
	        }
	        return contactWay;
	}

	@Override
	public int updateByPrimaryKeySelective(ContactWay record) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int updateByPrimaryKey(ContactWay record) {
		// TODO Auto-generated method stub
		return 0;
	}

}

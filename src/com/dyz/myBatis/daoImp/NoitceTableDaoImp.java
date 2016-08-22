package com.dyz.myBatis.daoImp;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.NoticeTableMapper;
import com.dyz.myBatis.model.NoticeTable;

/**
 * Created by kevin on 2016/6/21.
 */
public class NoitceTableDaoImp implements NoticeTableMapper {
    private SqlSessionFactory sqlSessionFactory;
    public NoitceTableDaoImp(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }
	@Override
	public int deleteByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int insert(NoticeTable record) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int insertSelective(NoticeTable record) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public NoticeTable selectByPrimaryKey(Integer id) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public int updateByPrimaryKeySelective(NoticeTable record) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public int updateByPrimaryKey(NoticeTable record) {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public NoticeTable selectRecentlyObject() {
		 SqlSession sqlSession = sqlSessionFactory.openSession();
		 NoticeTable noticeTable = null;
         try {
        	 NoticeTableMapper noticeTableMapper = sqlSession.getMapper(NoticeTableMapper.class);
             noticeTable =  noticeTableMapper.selectRecentlyObject();
         } catch (Exception e) {
                 e.printStackTrace();
         }finally {
             sqlSession.close();
         }
		return noticeTable;
	}

}

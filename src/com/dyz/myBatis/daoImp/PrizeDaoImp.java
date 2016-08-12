package com.dyz.myBatis.daoImp;

import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.PrizeMapper;
import com.dyz.myBatis.dao.UserMapper;
import com.dyz.myBatis.model.Prize;
import com.ibatis.sqlmap.client.SqlMapClient;

public class PrizeDaoImp implements PrizeMapper {
    private SqlSessionFactory sqlSessionFactory;
    public PrizeDaoImp(SqlSessionFactory sqlSessionFactory){
        this.sqlSessionFactory = sqlSessionFactory;
    }

	@Override
	public int deleteByPrimaryKey(Integer id) {
		return 0;
	}

	@Override
	public int insert(Prize record) {
		return 0;
	}

	@Override
	public int insertSelective(Prize record) {
		return 0;
	}

	@Override
	public Prize selectByPrimaryKey(Integer id) {
		  SqlSession sqlSession = sqlSessionFactory.openSession();
		  Prize prize = null;
          try {
              PrizeMapper prizeMapper = sqlSession.getMapper(PrizeMapper.class);
              prize =  prizeMapper.selectByPrimaryKey(id);
          } catch (Exception e) {
                  e.printStackTrace();
          }finally {
              sqlSession.close();
          }
		return prize;
	}

	@Override
	public int updateByPrimaryKeySelective(Prize record) {
		return 0;
	}

	@Override
	public int updateByPrimaryKey(Prize record) {
		return 0;
	}

	@Override
	public List<Prize> selectAllPrizes() {
            SqlSession sqlSession = sqlSessionFactory.openSession();
            List<Prize> list = new ArrayList<Prize>();
            try {
                PrizeMapper prizeMapper = sqlSession.getMapper(PrizeMapper.class);
                list = prizeMapper.selectAllPrizes();
            } catch (Exception e) {
                    e.printStackTrace();
            }finally {
                sqlSession.close();
            }
        return list;
	}

}

package com.dyz.myBatis.services;

import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.PrizeMapper;
import com.dyz.myBatis.daoImp.PrizeDaoImp;
import com.dyz.myBatis.model.Prize;

public class PrizeService {

	 private PrizeMapper prizeMap;

	    private static PrizeService prizeService = new PrizeService();

	    public static PrizeService getInstance(){
	        return prizeService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	prizeMap = new PrizeDaoImp(sqlSessionFactory);
	    }
	    
	    
	    /**
	     * 获取所有的奖品信息
	     * @return
	     */
	   public  List<Prize> selectAllPrizes(){
		   List<Prize> list = null ;
		  try {
			list =prizeMap.selectAllPrizes();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
	    	return list;
	    }
	   
	   public Prize selectByPrimaryKey(int id){
		   
		   return prizeMap.selectByPrimaryKey(id);
	   }
}

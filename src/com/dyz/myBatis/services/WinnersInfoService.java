package com.dyz.myBatis.services;

import java.sql.SQLException;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.WinnersInfoMapper;
import com.dyz.myBatis.daoImp.WinnersInfoDaoImp;
import com.dyz.myBatis.model.WinnersInfo;

public class WinnersInfoService {
	private WinnersInfoMapper winnersInfoMapper;

    private static WinnersInfoService winnersInfoService = new WinnersInfoService();

    public static WinnersInfoService getInstance(){
        return winnersInfoService;
    }

    public void initSetSession(SqlSessionFactory sqlSessionFactory){
    	winnersInfoMapper = new WinnersInfoDaoImp(sqlSessionFactory);
    }

    /**
     *
     * @param account
     * @throws SQLException
     */
    public void saveSelective(WinnersInfo winnersInfo) {
        try {
            int index = winnersInfoMapper.saveSelective(winnersInfo);
            System.out.println("===index====> "+index);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    /**
    *
    * @param account
    * @throws SQLException
    */
   /*public void updateWinnersInfo(WinnersInfo winnersInfo) {
       try {
           int index = winnersInfoMapper.updateByPrimaryKey(winnersInfo);
           System.out.println("===index====> "+index);
       }catch (Exception e){
           System.out.println(e.getMessage());
       }
   }*/
   /**
   *
   * @param account
   * @throws SQLException
   */
  public void save(WinnersInfo winnersInfo) {
      try {
          int index = winnersInfoMapper.save(winnersInfo);
          System.out.println("===index====> "+index);
      }catch (Exception e){
          System.out.println(e.getMessage());
      }
  }
}

package com.dyz.myBatis.services;

import java.sql.SQLException;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.TechargerecordMapper;
import com.dyz.myBatis.daoImp.TechargerecordDaoImp;
import com.dyz.myBatis.model.Techargerecord;

public class TechargerecordService {
	private TechargerecordMapper techargerecordMapper;

    private static TechargerecordService techargerecordService = new TechargerecordService();

    public static TechargerecordService getInstance(){
        return techargerecordService;
    }

    public void initSetSession(SqlSessionFactory sqlSessionFactory){
    	techargerecordMapper = new TechargerecordDaoImp(sqlSessionFactory);
    }

    /**
     *
     * @param account
     * @throws SQLException
     */
    public void saveSelective(Techargerecord techargerecord) {
        try {
            int index = techargerecordMapper.saveSelective(techargerecord);
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
   public void updateWinnersInfoSelective(Techargerecord techargerecord) {
       try {
           int index = techargerecordMapper.updateByPrimaryKeySelective(techargerecord);
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
  public void save(Techargerecord techargerecord) {
      try {
          int index = techargerecordMapper.save(techargerecord);
          System.out.println("===index====> "+index);
      }catch (Exception e){
          System.out.println(e.getMessage());
      }
  }
}

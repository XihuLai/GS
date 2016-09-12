package com.dyz.myBatis.services;

import java.util.Date;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.myBatis.dao.RoomInfoMapper;
import com.dyz.myBatis.daoImp.RoomInfoDaoImp;
import com.dyz.myBatis.model.RoomInfo;

public class RoomInfoService {

	 private RoomInfoMapper roomInfoMap;

	 
	 private static RoomInfoService gameService = new RoomInfoService();
	 public static RoomInfoService getInstance(){
	        return gameService;
	    }

	    public void initSetSession(SqlSessionFactory sqlSessionFactory){
	    	roomInfoMap = new RoomInfoDaoImp(sqlSessionFactory);
	    }
	    
	    /**
	     * 创建roomInfo
	     * @param roomInfo
	     * @return
	     */
	    public int createRoomInfo(RoomVO roomVO){
	    	//创建信息的同事创建其关联表
	        RoomInfo room = new RoomInfo();
	        room.setIshong(roomVO.getHong()?"1":"0");
	        room.setGametype(roomVO.getRoomType()+"");
	        room.setMa(roomVO.getMa());
	        room.setRoomid(roomVO.getRoomId());
	        room.setSevendouble(roomVO.getSevenDouble()?"1":"0");;
	        room.setXiayu(roomVO.getXiaYu());
	        room.setZimo(roomVO.getZiMo()==0?"0":"1");
	        room.setName(roomVO.getName());
	        room.setAddwordcard(roomVO.isAddWordCard()?"1":"0");
	        room.setCreateTime(new Date());
	        room.setCardNumb(roomVO.getRoundNumber()/4);
	    	//创建RoomInfo表
	        int index = roomInfoMap.insertSelective(room);
	        roomVO.setId(room.getId());
	        //创建表 gameaccountindex记录某个游戏记录中4个玩家对应的索引
	        //GameAccountIndex gameAccountIndex;
	        //gameAccountIndex.setAccountId(accountId);
	       // List<AvatarVO> avatarVos = roomVO.getPlayerList();
	       // for (int i = 0 ; i<avatarVos.size();i++) {
	        //	gameAccountIndex = new GameAccountIndex();
	        	//int [] pai = avatarVos.get(i).getPaiArray()[0];
	        	//gameAccountIndex.setAccountId(avatarVo.getAccount().getId());
	        //	gameAccountIndex.setAccountindex(i);
			//}
	        
	        
	        //gameaccountindex记录某个游戏记录中4个玩家对应的索引
	        
	        System.out.println("-RoomInfo insert index->>" + index);
	        return index;
	    }
	    
	    public RoomInfo selectByPrimaryKey(Integer id){
			return roomInfoMap.selectByPrimaryKey(id);
	    }
	    public RoomInfo selectRoomId(Integer roomId){
	    	return roomInfoMap.selectRoomId(roomId);
	    }
	    public int selectCount(){
	    	return roomInfoMap.selectCount();
	    }
	    public int selectTodayCount(Date date){
	    	return roomInfoMap.selectTodayCount(date);
	    }
}

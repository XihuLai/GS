package com.dyz.gameserver.manager;

import java.util.HashMap;
import java.util.Map;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.pojo.RoomVO;

/**
 * Created by kevin on 2016/6/18.
 */
public class RoomManager {

    Map<Integer,RoomLogic> roomList;

    public RoomManager(){
        roomList = new HashMap<Integer, RoomLogic>();
    }

    private static RoomManager roomManager;

    public static RoomManager getInstance(){
        if(roomManager == null){
            roomManager = new RoomManager();
        }
        return roomManager;
    }

    /**
     * 创建房间
     * @param avatar
     */
    public void createRoom(Avatar avatar, RoomVO roomVO){
        int roomId = randRoomId();
        roomVO.setRoomId(roomId);
        RoomLogic roomLogic = new RoomLogic(roomVO);
        roomLogic.CreateRoom(avatar);
        //这里需要统计创建房间个数****
        roomList.put(roomId,roomLogic);
    }
    /**
     * 销毁房间/通知房间里面的玩家退出房间
     * @param avatar
     */
    public void destroyRoom(RoomVO roomVO){
    	
    	if(roomList.get(roomVO.getRoomId()) != null){
    		roomList.remove(roomVO.getRoomId());
//    		for (Avatar avatar : avatars) {
//    			//avatar.getSession().sendMsg(new OutRoomResponse(1, roomVO));
//			}
    	}
    }

    /**
     * 获取room
     * @param roomId 房间号
     * @return
     */
    public RoomLogic getRoom(int roomId){
        RoomLogic resultObj = null;
        resultObj = roomList.get(roomId);
        return resultObj;
    }

    /**
     * 随机获取房间ID
     * @return
     */
    public int randRoomId(){
        int roomId = (int)(899999 * Math.random());
        if(roomId < 100000){
            roomId += 300000;
        }

        RoomLogic temp = getRoom(roomId);
        if(temp == null){
            return roomId;
        }else{
            roomId = randRoomId();
        }
        return roomId;
    }


    /**
     * 检查房间号是否被使用了
     * @param key
     * @return
     */
    public boolean checkRoomIdIsUsed(String key){
        RoomLogic resultObj = roomList.get(key);
        if(resultObj != null){
            return false;
        }
        return  true;
    }
}

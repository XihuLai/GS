package com.dyz.gameserver.msg.response.joinroom;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.persist.util.JsonUtilTool;

import java.io.IOException;

public class JoinRoomResponse extends ServerResponse {

	public JoinRoomResponse(int status,RoomVO roomVO) {
		super(status,ConnectAPI.JOIN_ROOM_RESPONSE);
		try {
			if(status>0){
					output.writeUTF(JsonUtilTool.toJson(roomVO));
			}
			else{
				output.writeUTF(roomVO.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
       	 output.close();
		}
		//entireMsg();
	}

}

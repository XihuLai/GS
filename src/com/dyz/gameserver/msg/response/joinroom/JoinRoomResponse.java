package com.dyz.gameserver.msg.response.joinroom;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.persist.util.JsonUtilTool;

public class JoinRoomResponse extends ServerResponse {

	public JoinRoomResponse(int status,Object roomVO) {
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
		}
		entireMsg();
	}

}

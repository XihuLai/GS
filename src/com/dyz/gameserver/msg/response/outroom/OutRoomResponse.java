package com.dyz.gameserver.msg.response.outroom;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.persist.util.JsonUtilTool;

import java.io.IOException;

public class OutRoomResponse extends ServerResponse {

	public OutRoomResponse(int status,String str) {
		super(status,ConnectAPI.OUT_ROOM_RESPONSE);
		if(status>0){
			try {
				output.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
           	 output.close();
			}
		}
		//entireMsg();
	}

}

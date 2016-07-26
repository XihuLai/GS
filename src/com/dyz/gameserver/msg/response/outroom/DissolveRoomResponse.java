package com.dyz.gameserver.msg.response.outroom;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

public class DissolveRoomResponse extends ServerResponse {

	public DissolveRoomResponse(int status, String str) {
		super(status, ConnectAPI.DISSOLVE_ROOM_RESPONSE);
		if(status>0){
			try {
				output.writeUTF(str);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
           	 output.close();
			}
		}
	}

}

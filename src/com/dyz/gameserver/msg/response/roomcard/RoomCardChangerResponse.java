package com.dyz.gameserver.msg.response.roomcard;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import net.sf.json.JSONObject;

public class RoomCardChangerResponse extends ServerResponse{

	public RoomCardChangerResponse(int status, int msgCode) {
		 super(status, ConnectAPI.ROOMCARDCHANGER_RESPONSE);
	        if(status >0){
	            try {
	                output.writeUTF(msgCode+"");
	            } catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	           	 output.close();
				}
	        }
		
	}

}

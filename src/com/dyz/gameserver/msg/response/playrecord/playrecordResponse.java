package com.dyz.gameserver.msg.response.playrecord;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

public class playrecordResponse extends ServerResponse{

	public playrecordResponse(int status, String msgCode) {
		super(status, ConnectAPI.PLAYRECORD_RESPONSE);
		if(status>0){
			try {
	            output.writeUTF(msgCode);
	        } catch (IOException e) {
	            e.printStackTrace();
	        }
		}
	}

}

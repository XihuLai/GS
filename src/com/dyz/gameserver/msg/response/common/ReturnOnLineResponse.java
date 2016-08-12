package com.dyz.gameserver.msg.response.common;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

public class ReturnOnLineResponse extends ServerResponse {

	public ReturnOnLineResponse(int status, String str) {
		
		super(status, ConnectAPI.RETURN_ONLINE_RESPONSE);
		 if(status >0){
	            try {
	            	//格式
	                output.writeUTF(str);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	}

}

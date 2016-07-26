package com.dyz.gameserver.msg.response.login;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;

public class SignUpResponse extends ServerResponse{

	public SignUpResponse(int status,boolean isSuccess) {
		super(status,ConnectAPI.SIGNUP_RESPONSE);
		try {
			output.writeBoolean(isSuccess);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
       	 output.close();
		}
	}

}

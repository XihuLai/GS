package com.dyz.gameserver.msg.response.login;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
/**
 * 通知前端 返回到登录界面
 * @author luck
 *
 */
public class BreakLineResponse extends ServerResponse{

	
	
	public BreakLineResponse(int status) {
		super(status,ConnectAPI.BREAK_LINE_RESPONSE);
		try {
			if(status > 0) {
				output.writeUTF("1");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
       	 output.close();
		}
	}
	
}

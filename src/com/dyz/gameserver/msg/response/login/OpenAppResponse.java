package com.dyz.gameserver.msg.response.login;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

import java.io.IOException;
/**
 * 
 *
 * @author  daiyongzhi
 * @date 2015年1月28日 下午2:12:34
 * @version V1.0
 */
public class OpenAppResponse extends ServerResponse{

	public OpenAppResponse(int status, String initWord){
		super(status,ConnectAPI.OPENAPP_RESPONSE);
		try {
			output.writeUTF(initWord);
		} catch (IOException e) {
			
			e.printStackTrace();
		} finally {
       	 output.close();
		}
	}
}

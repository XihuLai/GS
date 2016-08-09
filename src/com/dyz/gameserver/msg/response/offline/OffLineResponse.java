package com.dyz.gameserver.msg.response.offline;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

/**
 * 断线消息
 * @author luck
 *
 */
public class OffLineResponse extends ServerResponse  {

	public OffLineResponse(int status, String msgCode) {
		super(status, ConnectAPI.OFF_LINE_RESPONSE);
		if(status>0){
			try {
				output.writeUTF(msgCode);
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
           	 output.close();
			}
		}
	}

}

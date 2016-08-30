package com.dyz.gameserver.msg.response.standings;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;


/**
 * 返回某个房间里面的每局游戏的战绩
 * @author luck
 *
 */
public class StandingsDetailResponse extends ServerResponse {
	public StandingsDetailResponse(int status,String str) {
		super(status,ConnectAPI.MSG_STANDINGSDETAIL_RESPONSE);
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

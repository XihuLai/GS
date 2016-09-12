package com.dyz.gameserver.msg.response.standings;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;


/**
 * 返回某个房间的战绩
 * @author luck
 *
 */
public class StandingsDetailResponseSearch extends ServerResponse {
	public StandingsDetailResponseSearch(int status,String str) {
		super(status,ConnectAPI.MSG_STANDINGS_RESPONSE);
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

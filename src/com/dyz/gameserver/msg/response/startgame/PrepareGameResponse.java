package com.dyz.gameserver.msg.response.startgame;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * Created by kevin on 2016/6/22.
 */
public class PrepareGameResponse extends ServerResponse {
	/**
	 *
	 * @param status
	 * @param paiArray 自己的牌数组
	 * @param bankerId 庄家ID
     */
	public PrepareGameResponse(int status, int[][] paiArray,int bankerId) {
		super(status, ConnectAPI.STARTGAME_RESPONSE);
		try {
			//String str = JsonUtilTool.toJson(paiArray);
			JSONObject json = new JSONObject();
			json.put("paiArray",paiArray);
			json.put("bankerId",bankerId);
			output.writeUTF(json.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
       	 output.close();
		}
		//entireMsg();
	}
}

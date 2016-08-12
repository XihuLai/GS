package com.dyz.gameserver.msg.response.draw;

import java.io.IOException;

import com.alibaba.fastjson.JSONObject;
import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;

/**
 * 抽奖返回
 * @author luck
 *
 */
public class DrawResponse extends ServerResponse {
	
	public DrawResponse(int status, JSONObject json) {
		//type   0：获取奖品信息    1：获取随机获得奖品id   
		super(status, ConnectAPI.DRAw_RESPONSE);
		  if(status > 0){
			  try {
	                output.writeUTF(json.toString());
	            } catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	           	 output.close();
				}
	        }
		
	}

}

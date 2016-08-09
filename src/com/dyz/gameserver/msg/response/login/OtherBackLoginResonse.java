package com.dyz.gameserver.msg.response.login;

import java.io.IOException;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.persist.util.JsonUtilTool;

public class OtherBackLoginResonse extends ServerResponse {

	public OtherBackLoginResonse(int status, String uuid) {
		super(status,ConnectAPI.OTHER_BACK_LOGIN_RESPONSE);
		try {
			if(status>0){
					output.writeUTF(uuid);
			}
			else{
				//output.writeUTF(roomVO.toString());
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
       	 output.close();
		}
	}

}

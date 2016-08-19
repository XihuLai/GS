package com.dyz.gameserver.msg.response.host;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import com.dyz.gameserver.pojo.GangBackVO;
import com.dyz.persist.util.JsonUtilTool;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 
 * @author luck
 * 后台更新公告后发送给前段
 *
 */
public class HostNoitceResponse extends ServerResponse {
	public HostNoitceResponse(int status, String noitce) {
        super(status, ConnectAPI.HOST_SEND_RESPONSE);
        if(status >0){
            try {
            	
                output.writeUTF(noitce);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
           	 output.close();
			}
        }
       // entireMsg();
    }
}

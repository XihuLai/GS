package com.dyz.gameserver.msg.response.contact;

import com.context.ConnectAPI;
import com.dyz.gameserver.commons.message.ServerResponse;
import net.sf.json.JSONObject;

import java.io.IOException;

/**
 * app端获取充卡联系人信息
 * @author luck
 *
 */
public class ContactResponse extends ServerResponse {
	
	
    public ContactResponse(int status, String content) {
        super(status, ConnectAPI.HOST_ADDROOMCARD_RESPONSE);
        if(status >0){
            try {
                output.writeUTF(content);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
           	 output.close();
			}
        }
       // entireMsg();
    }
}

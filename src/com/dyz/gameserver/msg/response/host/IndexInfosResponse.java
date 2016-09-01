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
 * 后台获取了游戏信息之后发送给前段
 *
 */
public class IndexInfosResponse extends ServerResponse {
	public IndexInfosResponse(int status, String content) {
        super(status, ConnectAPI.HOST_INDEXINFOS_RESPONSE);
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

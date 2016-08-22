package com.dyz.gameserver.msg.processor.host;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.host.HostNoitceResponse;
import com.dyz.gameserver.msg.response.roomcard.RoomCardChangerResponse;
import com.dyz.myBatis.model.Account;
import com.dyz.myBatis.model.NoticeTable;
import com.dyz.myBatis.model.PrizeRule;
import com.dyz.myBatis.services.AccountService;
import com.dyz.myBatis.services.NoticeTableService;
import com.dyz.myBatis.services.PrizeRuleService;

/**
 * 
 * @author luck
 *
 */
public class HostNoitceProcessor extends MsgProcessor implements
        INotAuthProcessor {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
    	
    }
	@Override
	public void handle(GameSession gameSession, ClientRequest request) {
		try {
			String type = request.getString();    
			//system.out.println(type);
	    	if(type.equals("notice")){
	    		//说明后台更新了公告，需要发送给每个玩家
	    		NoticeTable notice = null;
	    		try {
	    			 notice = NoticeTableService.getInstance().selectRecentlyObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
	    		String content = notice.getContent();
	    		List<GameSession> gamesssions =GameSessionManager.getInstance().getAllSession();
	    		Map<String,GameSession> map= GameSessionManager.getInstance().sessionMap;
	    		if(!map.isEmpty() && !gamesssions.isEmpty()){
	    			for (GameSession gamession : gamesssions) {
	    				gamession.sendMsg(new HostNoitceResponse(1, content));
	    			}
	    		}
	    		else{
	    			//system.out.println("当前没有玩家在线");
	    			
	    		}
	    	}
	    	else if(type.indexOf(",") != -1){
	    		//说明需要更新房卡，传入id和房卡数量 ,后台已经修改了表数据
	    		String [] strs = type.split(",");
	    		//RoomCardChangerResponse
	    		int id = Integer.parseInt(strs[0]);
	    		int roomCard = Integer.parseInt(strs[1]);
	    		Account account = AccountService.getInstance().selectByPrimaryKey(id);
	    		Map<String,GameSession> map= GameSessionManager.getInstance().sessionMap;
	    		if(!map.isEmpty()){
	    			GameSession gamesession = GameSessionManager.getInstance().sessionMap.get("uuid_"+account.getUuid());
	    			if(gamesession != null){
	    				gamesession.sendMsg(new RoomCardChangerResponse(1, roomCard));
	    				//修改当前玩家缓存account里面的房卡数量
	    				Avatar avatar = gamesession.getRole(Avatar.class);
	    				avatar.avatarVO.getAccount().setRoomcard(roomCard);
	    			}
	    			else{
	    				//system.out.println("玩家当前不在线!");
	    			}
	    		}
	    		else{
	    			//system.out.println("sessionMap为空");
	    		}
	    	}
	    	/*else if(type.equals("prizerule")){
	    		PrizeRule PrizeRuleService.getInstance().selectByPrimaryKey(1);
	    		//后台管理 修改抽奖规则之后发送给游戏端
	    		
	    		
	    		
	    	}*/
	    	
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    
    
}

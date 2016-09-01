package com.dyz.gameserver.msg.processor.host;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;


import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.host.IndexInfosResponse;
import com.dyz.myBatis.services.AccountService;
import com.dyz.myBatis.services.RoomInfoService;
import com.dyz.persist.util.DateUtil;


/**
 * 后台管理员登录后，返回在线人数，注册用户数量，开放数量等信息
 * @author luck
 *
 */
public class IndexInfosProcessor extends MsgProcessor implements
INotAuthProcessor{

	@Override
	public void process(GameSession gameSession, ClientRequest request) throws Exception {
		
	}
	@Override
	public void handle(GameSession gameSession, ClientRequest request) {
		try {
			String type = request.getString();  
			//下面就是从数据库表中取后台管理界面需要的数据
			//1:房间总数  （管理后台直接取）
			//	int AllRoomsCount = RoomInfoService.getInstance().selectCount();
			
			//2:当前在线房间总数
				int onlineRoomsCount = RoomManager.getInstance().getRoomsCount();
				
			//3:今天创建房间总数   （管理后台直接取)
//				String currentTime = DateUtil.toDefineString(new Date(), DateUtil.maskE);
//				int todayRoomsCount = 0;
//				try {
//					Date date = DateUtil.toDefineMaskDate(currentTime, DateUtil.maskC);
//					todayRoomsCount = RoomInfoService.getInstance().selectTodayCount(date);
//				} catch (ParseException e) {
//					e.printStackTrace();
//				}
				
			//4:总房卡数(玩家房卡总数额/代理商房卡总数) （管理后台直接取)
				
			//5:今日售出(今天充值-今天退卡)（管理后台直接取)
				
				
			//6:本周售出(本周充值-本周退卡)（管理后台直接取)
				
			
			//7:当前在线人数
				int onlineAccountCount = GameSessionManager.getInstance().sessionMap.size();
			
			//8:今日新增用户（管理后台直接取)
			
			//String str = "onlineRoomsCount:"+onlineRoomsCount+",onlineAccountCount:"+onlineAccountCount;
			String str = onlineRoomsCount+","+onlineAccountCount+","+GameSessionManager.topOnlineAccountCount;
			
			gameSession.sendMsg(new IndexInfosResponse(1,str));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
}

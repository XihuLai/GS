package com.dyz.gameserver.msg.processor.login;

import java.util.Date;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.initial.Params;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.context.GameServerContext;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.host.HostNoitceResponse;
import com.dyz.gameserver.msg.response.hu.HuPaiResponse;
import com.dyz.gameserver.msg.response.login.LoginResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.LoginVO;
import com.dyz.myBatis.model.Account;
import com.dyz.myBatis.model.NoticeTable;
import com.dyz.myBatis.services.AccountService;
import com.dyz.myBatis.services.NoticeTableService;
import com.dyz.persist.util.JsonUtilTool;
import com.dyz.persist.util.TimeUitl;

public class LoginMsgProcessor extends MsgProcessor implements INotAuthProcessor{

	@Override
	public  void process(GameSession gameSession, ClientRequest request)
			throws Exception {
		String message = request.getString();
		LoginVO loginVO = JsonUtilTool.fromJson(message,LoginVO.class);
		Account account = AccountService.getInstance().selectAccount(loginVO.getOpenId());
		if(account==null){
			//创建新用户并登录
			account = new Account();
			account.setOpenid(loginVO.getOpenId());
			account.setUuid(AccountService.getInstance().selectMaxId()+135797);
			account.setRoomcard(Params.initialRoomCard);
			account.setHeadicon(loginVO.getHeadIcon());
			account.setNickname(loginVO.getNickName());
			System.out.println("新用户传来=========="+loginVO.getNickName()+"======");   
			account.setCity(loginVO.getCity());
			account.setProvince(loginVO.getProvince());
			account.setSex(loginVO.getSex());
			account.setUnionid(loginVO.getUnionid());
			account.setPrizecount(Params.initialPrizeCount);
			account.setCreatetime(new Date());
			account.setActualcard(Params.initialRoomCard);
			account.setTotalcard(Params.initialRoomCard);
			account.setStatus("0");
			account.setIsGame("0");

			if(AccountService.getInstance().createAccount(account) == 0){
				gameSession.sendMsg(new LoginResponse(0,null));
				TimeUitl.delayDestroy(gameSession,1000);
			}else{
				Avatar tempAva = new Avatar();
				AvatarVO tempAvaVo = new AvatarVO();
				tempAvaVo.setAccount(account);
				tempAvaVo.setIP(loginVO.getIP());
				tempAva.avatarVO = tempAvaVo;

				loginAction(gameSession,tempAva);
				//把session放入到GameSessionManager
				GameSessionManager.getInstance().putGameSessionInHashMap(gameSession,tempAva.getUuId());
				//公告发送给玩家
				Thread.sleep(3000);
				NoticeTable notice = null;
				try {
					 notice = NoticeTableService.getInstance().selectRecentlyObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
				String content = notice.getContent();
				gameSession.sendMsg(new HostNoitceResponse(1, content));
			}
		}else{
			//如果玩家是掉线的，则直接从缓存(GameServerContext)中取掉线玩家的信息
			//判断用户是否已经进行断线处理(如果前端断线时间过短，后台则可能还未来得及把用户信息放入到离线map里面，就已经登录了，所以取出来就会是空)
			Avatar avatar = GameServerContext.getAvatarFromOn(account.getUuid());
			if(avatar == null){
				avatar =  GameServerContext.getAvatarFromOff(account.getUuid());
			}
			if(avatar == null){
				GameSession gamesession = GameSessionManager.getInstance().getAvatarByUuid("uuid_"+account.getUuid());
				if(gamesession != null){
					avatar =  gamesession.getRole(Avatar.class);
				}
			}
			if(avatar == null) {
					//判断微信昵称是否修改过，若修改过昵称，则更新数据库信息
					if(!loginVO.getNickName().equals(account.getNickname())){
						System.out.println("玩家掉线后昵称修改的处理===============");
						account.setNickname(loginVO.getNickName());
						AccountService.getInstance().updateByPrimaryKeySelective(account);
					}
					//断线超过时间后，自动退出
					avatar = new Avatar();
					AvatarVO avatarVO = new AvatarVO();
					avatarVO.setAccount(account);
					avatarVO.setIP(loginVO.getIP());
					avatar.avatarVO = avatarVO;
					//把session放入到GameSessionManager
					GameSessionManager.getInstance().putGameSessionInHashMap(gameSession,avatar.getUuId());
					loginAction(gameSession,avatar);
					Thread.sleep(3000);
					//公告发送给玩家
					NoticeTable notice = null;
					try {
						notice = NoticeTableService.getInstance().selectRecentlyObject();
					} catch (Exception e) {
						e.printStackTrace();
					}
					String content = notice.getContent();
					gameSession.sendMsg(new HostNoitceResponse(1, content));
			}else{
				//断线重连
				System.out.println("断线重连===============");
				GameServerContext.add_onLine_Character(avatar);
				GameServerContext.remove_offLine_Character(avatar);
				avatar.avatarVO.setIsOnLine(true);
				avatar.avatarVO.setAccount(account);
				avatar.avatarVO.setIP(loginVO.getIP());
				TimeUitl.stopAndDestroyTimer(avatar);
				avatar.setSession(gameSession);
				//system.out.println("用户回来了，断线重连，中止计时器");
				//返回用户断线前的房间信息******
				gameSession.setLogin(true);
				gameSession.setRole(avatar);
				returnBackAction(gameSession ,avatar);
				//把session放入到GameSessionManager,并且移除以前的session
				GameSessionManager.getInstance().putGameSessionInHashMap(gameSession,avatar.getUuId());
				//公告发送给玩家
				Thread.sleep(3000);
				NoticeTable notice = null;
				try {
					 notice = NoticeTableService.getInstance().selectRecentlyObject();
				} catch (Exception e) {
					e.printStackTrace();
				}
				String content = notice.getContent();
				gameSession.sendMsg(new HostNoitceResponse(1, content));
				
			}
		}
		System.out.println(account.getUuid()+"  :登录游戏");
	}

	/**
	 * 登录操作
	 * @param gameSession
	 * @param avatar
     */
	public void loginAction(GameSession gameSession,Avatar avatar){
		gameSession.setRole(avatar);
		gameSession.setLogin(true);
		avatar.setSession(gameSession);
		avatar.avatarVO.setIsOnLine(true);
		GameServerContext.add_onLine_Character(avatar);
		gameSession.sendMsg(new LoginResponse(1,avatar.avatarVO));
	}
	
	/**
	 *玩家断线重连操作
	 * @param
	 * @param avatar
     */
	public void returnBackAction(GameSession gameSession ,Avatar avatar){
		
		
		if(avatar.avatarVO.getRoomId() != 0){
			RoomLogic roomLogic = RoomManager.getInstance().getRoom(avatar.avatarVO.getRoomId());
			if(roomLogic !=null){
				//如果用户是在玩游戏/在房间的时候断线，且返回时房间还未被解散，则需要返回游戏房间其他用户信息，牌组信息
				roomLogic.returnBackAction(avatar);
				try {
					Thread.sleep(1000);
					if(avatar.overOff){
						//在某一句结算时断线，重连时返回结算信息
//						System.out.println("returnBackAction HuPaiResponse " + avatar.avatarVO.getAccount().getOpenid());
						avatar.getSession().sendMsg(new HuPaiResponse(1,avatar.oneSettlementInfo));
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			else{
				//如果是在游戏时断线,但是返回的时候，游戏房间已经被解散，则移除该用户的房间信息
				AvatarVO avatarVO = new AvatarVO(); 
				avatarVO.setAccount(avatar.avatarVO.getAccount());
				GameSession gamesession = avatar.getSession();
				avatar = new Avatar();
				avatar.avatarVO = avatarVO;
				avatar.setSession(gamesession);
				avatar.avatarVO.setIsOnLine(true);
				gamesession.setRole(avatar);
				gamesession.setLogin(true);
				GameServerContext.add_onLine_Character(avatar);
				gamesession.sendMsg(new LoginResponse(1, avatar.avatarVO));
			}
		}
		else{
			//如果不是在游戏时断线，则直接返回个人用户信息avatar
			avatar.getSession().sendMsg(new LoginResponse(1, avatar.avatarVO));
		}
		
	}

}

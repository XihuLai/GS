package com.dyz.gameserver.msg.processor.common;

import com.context.ConnectAPI;
import com.dyz.gameserver.msg.processor.chi.ChiMsgProcessor;
import com.dyz.gameserver.msg.processor.chupai.ChuPaiMsgProcessor;
import com.dyz.gameserver.msg.processor.contact.ContactMsgProcssor;
import com.dyz.gameserver.msg.processor.createroom.CreateRoomMsgProcssor;
import com.dyz.gameserver.msg.processor.draw.DrawProcessor;
import com.dyz.gameserver.msg.processor.gang.GangMsgProcessor;
import com.dyz.gameserver.msg.processor.heartbeat.HeadMsgProcessor;
import com.dyz.gameserver.msg.processor.host.HostNoitceProcessor;
import com.dyz.gameserver.msg.processor.host.IndexInfosProcessor;
import com.dyz.gameserver.msg.processor.hu.HuPaiMsgProcessor;
import com.dyz.gameserver.msg.processor.joinroom.JoinRoomMsgProcessor;
import com.dyz.gameserver.msg.processor.login.LoginMsgProcessor;
import com.dyz.gameserver.msg.processor.login.LoginReturnInfoMsgProcessor;
import com.dyz.gameserver.msg.processor.login.LogoutMsgProcessor;
import com.dyz.gameserver.msg.processor.login.OpenAppMsgProcessor;
import com.dyz.gameserver.msg.processor.login.SignUpMsgProcessor;
import com.dyz.gameserver.msg.processor.messageBox.MessageBoxMsgProcessor;
import com.dyz.gameserver.msg.processor.outroom.DissolveRoomMsgProcessor;
import com.dyz.gameserver.msg.processor.outroom.OutRoomMsgProcessor;
import com.dyz.gameserver.msg.processor.pass.GaveUpMsgProcessor;
import com.dyz.gameserver.msg.processor.peng.PengMsgProcessor;
import com.dyz.gameserver.msg.processor.pickcard.PickCardMsgProcessor;
import com.dyz.gameserver.msg.processor.playrecord.PlayRecordMsgProcessor;
import com.dyz.gameserver.msg.processor.remotecontrol.RemoteControlProcessor;
import com.dyz.gameserver.msg.processor.standings.StandingsMsgProcessor;
import com.dyz.gameserver.msg.processor.standings.StandingsMsgProcessorSearch;
import com.dyz.gameserver.msg.processor.startgame.PrepareGameMSGProcessor;


/**
 * 消息处理器注册类，所有的消息处理器，都在此注册实例化
 * @author dyz
 *
 */
public enum MsgProcessorRegister {
	/**用户打开app*/
	openApp(ConnectAPI.OPENAPP_REQUEST,new OpenAppMsgProcessor()),
	/**登陆处理器*//**断线重连**/
	login(ConnectAPI.LOGIN_REQUEST,new LoginMsgProcessor()),
	/**登陆处理器*//**断线重连**/
	loginReturnInfo(ConnectAPI.RETURN_ONLINE_REQUEST,new LoginReturnInfoMsgProcessor()),
	/**用户注册处理器*/
	signUp(ConnectAPI.SIGNUP_REQUEST,new SignUpMsgProcessor()),
	/**创建 房间*/
	createRoom(ConnectAPI.CREATEROOM_REQUEST,new CreateRoomMsgProcssor()),
	/**进入游戏房间*/
	joinRoom(ConnectAPI.JOIN_ROOM_REQUEST,new JoinRoomMsgProcessor()),
	/**摸牌*/
	pickPai(ConnectAPI.PICKCARD_REQUEST,new PickCardMsgProcessor()),
	/**出牌*/
	chuPai(ConnectAPI.CHUPAI_REQUEST,new ChuPaiMsgProcessor()),
	/**退出房间*/
	outRoom(ConnectAPI.OUT_ROOM_REQUEST,new OutRoomMsgProcessor()),
	/**申请解散房间*/
	dissolveRoom(ConnectAPI.DISSOLVE_ROOM_REQUEST,new DissolveRoomMsgProcessor()),
	/**吃牌*/
	chiPai(ConnectAPI.CHIPAI_REQUEST,new ChiMsgProcessor()),
	/**碰牌*/
	pengPai(ConnectAPI.PENGPAI_REQUEST,new PengMsgProcessor()),
	/**碰牌*/
	gangPai(ConnectAPI.GANGPAI_REQUEST,new GangMsgProcessor()),
	/**放弃操作*/
	gaveUp(ConnectAPI.GAVEUP_REQUEST,new GaveUpMsgProcessor()),
	/*胡牌**/
	hupai(ConnectAPI.HUPAI_REQUEST,new HuPaiMsgProcessor()),
	/**与前段握手*/
	successRerunMsg(ConnectAPI.SUCCESS_RETURN_MSG_RESPONSE,new SuccessReturnMsgProcessor()),
	/**游戏开始前准备*/
	prepareGame(ConnectAPI.PrepareGame_MSG_REQUEST,new PrepareGameMSGProcessor()),
	/**退出游戏*/
	loginOutGame(ConnectAPI.LOGINOUTGAME_MSG_REQUEST,new LogoutMsgProcessor()),
	
	messageBox(ConnectAPI.MessageBox_Request,new MessageBoxMsgProcessor()),
	/**心跳协议*/
	head(ConnectAPI.head,new HeadMsgProcessor()),
	/**抽奖*/
	draw(ConnectAPI.DRAw_REQUEST,new DrawProcessor()),
	/**后台发送消息*/
	hostSendMessage(ConnectAPI.HOST_SEND_REQUEST,new HostNoitceProcessor()),
	/**后台发送消息获取首页所有信息(当前在线任务，当前房间数量等等)*/
	indexInfosMessage(ConnectAPI.HOST_INDEXINFOS_REQUEST,new IndexInfosProcessor()),
	/**前段请求发布的充卡联系人信息*/
	contactMessage(ConnectAPI.HOST_ADDROOMCARD_REQUEST,new ContactMsgProcssor()),
	/**远程请求*/
	romoteControlMessage(ConnectAPI.HOST_ROMOTECONTROL_REQUEST,new RemoteControlProcessor()),
	/**战绩请求*/
	standingsMessage(ConnectAPI.MSG_STANDINGS_REQUEST,new StandingsMsgProcessor()),
	/**room战绩请求*/
	standingsMessageRoom(ConnectAPI.MSG_STANDINGSSEAREH_REQUEST,new StandingsMsgProcessorSearch()),
	/**游戏回放*/
	PlayRecordMessage(ConnectAPI.PLAYRECORD_REQUEST,new PlayRecordMsgProcessor());
	
	
	
	private int msgCode;
	private MsgProcessor processor;

	/**
	 * 不允许外部创建
	 * @param msgCode
	 * @param processor
     */
	private MsgProcessorRegister(int msgCode,MsgProcessor processor){
		this.msgCode = msgCode;
		this.processor = processor;
	}

	/**
	 * 获取协议号
	 * @return
     */
	public int getMsgCode(){
		return this.msgCode;
	}

	/**
	 * 获取对应的协议解晰类对象
	 * @return
     */
	public MsgProcessor getMsgProcessor(){
		return this.processor;
	}
}

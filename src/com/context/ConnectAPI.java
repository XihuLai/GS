package com.context;

public class ConnectAPI {

	public ConnectAPI() {
		// TODO Auto-generated constructor stub
	}


	public static int LOGIN_REQUEST = 0x000001;
	public static int LOGIN_RESPONSE = 0x000002;
	
	public static int BACK_LOGIN_REQUEST = 0x001001;
	public static int BACK_LOGIN_RESPONSE = 0x001002;
	
	
	public static int RETURN_ONLINE_RESPONSE = 0x001003;//断线重连返回打牌逻辑消息
	public static int RETURN_ONLINE_REQUEST = 0x001004;//断线重连打牌逻辑请求
	
	
	public static int OTHER_BACK_LOGIN_RESPONSE = 0x001111;//断线玩家重连之后 其他三家人接收信息

	public static int JOIN_ROOM_REQUEST  = 0x000003;
	public static int JOIN_ROOM_RESPONSE  = 0x000004;
	public static int JOIN_ROOM_NOICE = 0x10a004;
	

	public static int SIGNUP_REQUEST = 0x000005;
	public static int SIGNUP_RESPONSE = 0x000006;

	public static int OPENAPP_REQUEST = 0x000007;
	public static int OPENAPP_RESPONSE =0x000008;

	public static int CREATEROOM_REQUEST = 0x00009;
	public static int CREATEROOM_RESPONSE = 0x00010;
	//开始游戏
	public static int STARTGAME_RESPONSE = 0x00012;
	//解散房间
	public static int DISSOLVE_ROOM_REQUEST  = 0x000113;
	public static int DISSOLVE_ROOM_RESPONSE  = 0x000114;
	//退出房间
	public static int OUT_ROOM_REQUEST  = 0x000013;
	public static int OUT_ROOM_RESPONSE  = 0x000014;
    //离线通知 
	public static int OFF_LINE_RESPONSE  = 0x000015;
	public static int ON_LINE_RESPONSE  = 0x000016;
	
	//心跳协议
	public static int head = 0x000030;
	public static int headRESPONSE = 0x000031;

	//出牌
	public static int CHUPAI_REQUEST = 0x100001;
	public static int CHUPAI_RESPONSE = 0x100002;
	//摸牌
	public static int PICKCARD_REQUEST = 0x100003;
	public static int PICKCARD_RESPONSE = 0x100004;
	//碰牌
	public static int PENGPAI_REQUEST = 0x100005;
	public static int PENGPAI_RESPONSE = 0x100006;
	//杠牌
	public static int GANGPAI_REQUEST = 0x100007;
	public static int GANGPAI_RESPONSE = 0x100008;
	public static int OTHER_GANGPAI_NOICE = 0x10a008;
	//胡牌
	public static int HUPAI_REQUEST = 0x100009;
	public static int HUPAI_RESPONSE = 0x100010;
	public static int HUPAIALL_RESPONSE = 0x100110;
	//吃
	public static int CHIPAI_REQUEST = 0x100011;
	public static int CHIPAI_RESPONSE = 0x100012;
	//其它人摸牌了
	public static  int OTHER_PICKCARD_RESPONSE = 0x100014;

	//放弃请求
	public static int GAVEUP_REQUEST = 0x100015;
	//有吃碰杠胡需要操作的返回信息/一个房间次数用完之后返回全局信息
	public static int RETURN_INFO_RESPONSE =  0x100000;
	
	//游戏错误码返回
	public static int ERROR_RESPONSE = 0xffff09;
	
	//游戏关闭返回
	public static int CLOSE_RESPONSE = 0x000000;
	
	//后台与前段握手信息返回，(前段接收到信息自后返回，调用接口)
	public static int SUCCESS_RETURN_MSG_REQUEST = 0x111111;
	public static int SUCCESS_RETURN_MSG_RESPONSE = 0x222222;
	
	//准备游戏
	public static int  PrepareGame_MSG_REQUEST = 0x333333;
	public static int PrepareGame_MSG_RESPONSE = 0x444444;
	//退出游戏
	public static int  LOGINOUTGAME_MSG_REQUEST = 0x555555;
	//固定语音盒子协议
	public static int MessageBox_Request = 203;
	public static int MessageBox_Notice = 204;
	//玩家房卡变化协议
	public static  int ROOMCARDCHANGER_RESPONSE = 0x777777;
	
	//抽奖协议
	public static  int DRAw_REQUEST = 0x888888;
	public static  int DRAw_RESPONSE = 0x999999;
	//被跟庄通知
	public static int Game_FollowBanker_Notice = 0x100016;
	//通知前端进行断线操作
	public static int BREAK_LINE_RESPONSE = 0x211211;
	
	//后台广告链接通知/后台充卡链接通知  公用request  不公用response
	public static int HOST_SEND_REQUEST = 0x158888;
	public static int HOST_SEND_RESPONSE = 0x157777;
	//后台管理官登录获取主页面 信息
	public static int HOST_INDEXINFOS_REQUEST = 0x154444;
	public static int HOST_INDEXINFOS_RESPONSE = 0x153333;

	//app点+号返回充卡联系信息
	public static int HOST_ADDROOMCARD_REQUEST = 0x156666;
	public static int HOST_ADDROOMCARD_RESPONSE = 0x155555;
	
	//每天充值抽奖信息之后，通知在线玩家
	public static int HOST_UPDATEDRAW_RESPONSE = 0x010111;
		
	//战绩
	public static int MSG_STANDINGS_REQUEST = 0x002001;//请求
	public static int MSG_STANDINGSSEAREH_REQUEST = 0x002004;//请求房间战绩
	public static int MSG_STANDINGS_RESPONSE = 0x002002;//返回房间战绩
	public static int MSG_STANDINGSDETAIL_RESPONSE = 0x002003;//返回具体某个房间对应的详细每局战绩
	
	//游戏回放请求
	public static int PLAYRECORD_REQUEST = 0x003001;
	public static int PLAYRECORD_RESPONSE = 0x003002;
	
		
	//远程请求
	public static int HOST_ROMOTECONTROL_REQUEST = 0x999999;
	
}

package com.dyz.gameserver.bootstrap;

import java.io.IOException;
import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyz.gameserver.commons.message.MsgDispatcher;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.context.ExecutorServiceManager;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.gameserver.msg.response.common.CloseGameResponse;
import com.dyz.gameserver.net.MinaHostMsgHandler;
import com.dyz.gameserver.net.MinaMsgHandler;
import com.dyz.gameserver.net.NetManager;
import com.dyz.myBatis.services.InitServers;
import com.dyz.persist.util.PrizeProbability;

public class GameServer {
	
	private static final Logger logger = LoggerFactory.getLogger(GameServer.class);

	private static int port = 10122;
	private static int hostPort = 10123;
	
	private static GameServer instance=new GameServer();
	
	public static MsgDispatcher msgDispatcher = new MsgDispatcher();;
	
	private static NetManager netManager;
	
	private GameServer(){
		netManager = new NetManager();
	}
	
	public static GameServer getInstance(){
		return instance;
	}
	
	public static void main(String[] args) throws IOException {
           startUp();
	}
	public static void startUp(){
		try {
			logger.info("开始启动服务器 ...");
			ExecutorServiceManager.getInstance().initExecutorService();
			logger.info("初始化服务器线程池完成");
			//DBUtil.initAllSqlMapClient();
			InitServers.getInstance().initServersFun();
			logger.info("数据库连接初始化完成");
			netManager.startListner(new MinaMsgHandler(), port);//前段监听端口
			netManager.startHostListner(new MinaHostMsgHandler(),hostPort);//后台数据链接的时候再开一个listner
			logger.info("服务器监听端口:{}完成",port);
			logger.info("game server started...");
			PrizeProbability.initPrizesProbability();
			logger.info("初始化奖品概率");
		} catch (Exception e) {
			logger.error("服务器启动失败");
			e.printStackTrace();
		}
	}
	
	public static void stop() {
		try {
			//关闭服务器前，向所有线程玩家发送关闭消息
			Collection<GameSession> list = GameSessionManager.getInstance().sessionMap.values();
			for (GameSession gameSession : list) {
				gameSession.sendMsg(new CloseGameResponse(1, "closeGame"));
			}
			logger.info("准备关闭服务器...");
			netManager.stop();
			logger.info("服务器停止网络监听");
			ExecutorServiceManager.getInstance().stop();
			logger.info("服务器线程池关闭完成");
			logger.info("服务器关闭完成");
		} catch (Exception e) {
			logger.info("服务器关闭异常");
			e.printStackTrace();
		}
	}
}

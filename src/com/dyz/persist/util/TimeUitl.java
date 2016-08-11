package com.dyz.persist.util;


import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.context.GameServerContext;
import com.dyz.gameserver.sprite.base.GameObj;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by kevin on 2016/6/20.
 */
public class TimeUitl {

    static Map<GameObj,Timer> gameObjMap = new HashMap<GameObj,Timer>();

    /**
     *  延迟销毁对象
     * @param gobj
     * @param delayTime
     */
    public static  void delayDestroy(GameObj gobj,int delayTime){
        final Timer timer = new Timer();
        gameObjMap.put(gobj,timer);
        getGameObjMapSize();
        TimerTask tt=new TimerTask() {
            @Override
            public void run() {
                gameObjMap.remove(gobj);
                timer.cancel();
                if(gobj instanceof Avatar){
                    System.out.println("用户掉线超时，删除用户数据 UserId "+ ((Avatar) gobj).getUuId());
                    GameServerContext.remove_offLine_Character((Avatar) gobj);
                }else{
                    System.out.println("到点啦！移除 gameSession");
                }
                gobj.destroyObj();
            }
        };
        timer.schedule(tt, delayTime);
    }
    /**
     *  心跳,//10秒钟没有接收到前段传入的心跳，则断线
     * @param gobj
     * @param delayTime
     */
    public synchronized static  void heartbeat(GameSession gamesession){
    	if(getTimer(gamesession) != null){
    		//System.out.println("进入");
    		getTimer(gamesession).cancel();
    		gameObjMap.remove(gamesession);
    	}
    		final Timer t = new Timer();
    		gameObjMap.put(gamesession,t);
    		//getGameObjMapSize();
    		//这里设置timer = 100 如果 10秒之后取得time还是10，表示前端已经断线
    		TimerTask tt=new TimerTask() {
    			@Override
    			public void run() {
    				//System.out.println("run:"+gamesession.getTime());
    				if(gamesession.getTime() >= 10){
    				//	System.out.println("run:"+gamesession.getTime());
    					//10秒钟没有接收到前段传入的心跳，则调用断线
    					gamesession.destroyObj();
    					t.cancel();
    					gameObjMap.remove(gamesession);
    				}
    			}
    		};
    		t.schedule(tt, 30000);
    		gamesession.addTime(10);
    }
    /**
     * 停止并销毁计时器
     * @param obj
     */
    public static void stopAndDestroyTimer(GameObj obj){
        Timer timer = getTimer(obj);
        if(timer != null){
            gameObjMap.remove(obj);
            getGameObjMapSize();
            timer.cancel();
            timer = null;
        }
    }

    public static void getGameObjMapSize(){
       System.out.println("计时器 gameObjMap.size() = "+gameObjMap.size());
    }

    /**
     * 获取计时器
     * @param obj
     * @return
     */
    private static Timer getTimer(GameObj obj){
        return gameObjMap.get(obj);
    }
}

package com.dyz.gameserver.manager;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.session.GameSession;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kevin on 2016/6/20.
 */
public class GameSessionManager {

    public Map<String,GameSession> sessionMap = new HashMap<String,GameSession>();

    private static GameSessionManager gameSessionManager;

    public GameSessionManager(){

    }

    /**
     *
     * @return
     */
    public static GameSessionManager getInstance(){
        if(gameSessionManager == null){
            gameSessionManager = new GameSessionManager();
        }
        return gameSessionManager;
    }

    /**
     * 存放GAMESESSION
     * @param gameSession
     * @return
     */
    public boolean putGameSessionInHashMap(GameSession gameSession,int useId){
        //Avatar avatar = gameSession.getRole(Avatar.class);
        boolean result = checkSessionIsHava(useId);
        if(result){
            System.out.println("这个用户已登录了");
        }else{
        	System.out.println("denglu");
            sessionMap.put("uuid_"+useId,gameSession);
        }
        return !result;
    }

    public int getVauleSize(){
        return sessionMap.size();
    }

    /**
     * 通过用户得到session
     * @param avatar
     * @return
     */
    public GameSession getGameSessionFromHashMap(Avatar avatar){
        return sessionMap.get("uuid_"+avatar.getUuId());
    }

    /**
     *
     * @param avatar
     */
    public void removeGameSession(Avatar avatar){
        GameSession gameSession =  sessionMap.remove("uuid_"+avatar.getUuId());
        if(gameSession != null){
        	gameSession.destroyObj();
        }
        gameSession = null;
    }

    /**
     * 检测用户session是否存在
     * @param uuid
     * @return
     */
    private boolean checkSessionIsHava(int uuid){
    	//可以用来判断是否在线****等功能
        GameSession gameSession = sessionMap.get("uuid_"+uuid);
        if(gameSession != null){
            return true;
        }
        return false;
    }

}

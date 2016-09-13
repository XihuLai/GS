package com.dyz.gameserver.manager;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.context.GameServerContext;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.login.BreakLineResponse;
import com.dyz.persist.util.TimeUitl;

import java.io.IOException;
import java.util.*;

/**
 * Created by kevin on 2016/6/20.
 */
public class GameSessionManager {

    public Map<String,GameSession> sessionMap = new HashMap<String,GameSession>();
    
    public static int topOnlineAccountCount = 0;
    
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
        System.out.println(" result ==> "+result);
        if(result){
           System.out.println("这个用户已登录了,更新session"); 
            try {
				sessionMap.get("uuid_"+useId).sendMsg(new ErrorResponse(ErrorCode.Error_000022));
				sessionMap.get("uuid_"+useId).sendMsg(new BreakLineResponse(1));
				Thread.sleep(300);
                sessionMap.get("uuid_"+useId).close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            Avatar avatar = gameSession.getRole(Avatar.class);
            GameServerContext.add_onLine_Character(avatar);
            GameServerContext.remove_offLine_Character(avatar);
            TimeUitl.stopAndDestroyTimer(avatar);
        	sessionMap.put("uuid_"+useId,gameSession);
        	if(sessionMap.size() > topOnlineAccountCount){
                topOnlineAccountCount = sessionMap.size();
            }
        }else{
        	//System.out.println("denglu");
            sessionMap.put("uuid_"+useId,gameSession);
            if(sessionMap.size() > topOnlineAccountCount){
            	topOnlineAccountCount = sessionMap.size();
            }
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
     * @param
     * @return
     */
    public GameSession getAvatarByUuid(String uuid){
        return sessionMap.get(uuid);
    }
    /**
     *
     * @param avatar
     */
    public void removeGameSession(Avatar avatar){
        //System.out.println("removeForMap");
        GameSession gameSession =  sessionMap.get("uuid_"+avatar.getUuId());
        if(gameSession != null){
        	GameServerContext.add_offLine_Character(avatar);
        	GameServerContext.remove_onLine_Character(avatar);
        	sessionMap.remove("uuid_"+avatar.getUuId());
        }
    }

    public List<GameSession> getAllSession(){
        List<GameSession> result = null;
        if(getVauleSize() >0) {
            result = new ArrayList<GameSession>();
            Collection<GameSession> connection = sessionMap.values();
            Iterator<GameSession> iterator = connection.iterator();
            while (iterator.hasNext()) {
                result.add(iterator.next());
            }
        }
        return result;
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

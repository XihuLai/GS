package com.dyz.persist.util;


import com.dyz.gameserver.Avatar;
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
        //getGameObjMapSize();
        TimerTask tt=new TimerTask() {
            @Override
            public void run() {
                gameObjMap.remove(gobj);
                timer.cancel();
                if(gobj instanceof Avatar){
                    System.out.println("用户掉线超时，删除用户数据 UserId "+ ((Avatar) gobj).getUuId());
                    GameServerContext.remove_offLine_Character((Avatar) gobj);
                    //清除 对象所有数据
                    
                }else{
                    System.out.println("到点啦！移除 gameSession");
                }
                gobj.destroyObj();
            }
        };
        timer.schedule(tt, delayTime);
    }

    /**
     * 停止并销毁计时器
     * @param obj
     */
    public static void stopAndDestroyTimer(GameObj obj){
        Timer timer = getTimer(obj);
        if(timer != null){
            gameObjMap.remove(obj);
           // getGameObjMapSize();
            timer.cancel();
            timer = null;
        }
    }

    public static void getGameObjMapSize(){
       //System.out.println("计时器 gameObjMap.size() = "+gameObjMap.size());
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

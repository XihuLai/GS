package com.dyz.persist.util;

import com.context.ErrorCode;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.response.ErrorResponse;

import java.io.IOException;

/**
 * Created by kevin on 2016/6/23.
 */
public class GlobalUtil {

    public static boolean checkIsLogin(GameSession session){
        if(session.isLogin() == false){
            System.out.println("账户未登录或已经掉线!");
            try {
                session.sendMsg(new ErrorResponse(ErrorCode.Error_000002));
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }
        return true;
    }

    public static int getRandomUUid(){
        double d = Math.random();
        //System.out.println(d);
        String subStr = String.valueOf(d).substring(7,13);
        int result = Integer.parseInt(subStr);
        return result;
    }

    public static int[] CloneIntList(int[] List) {
        int[] result = new int[List.length];
        for(int i=0;i<List.length;i++){
            result[i] = List[i];
        }
        return result;
    }
}

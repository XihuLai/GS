package com.dyz.persist.util;

import com.dyz.gameserver.pojo.CheckObjectVO;

import java.util.*;

/**
 * Created by kevin on 2016/6/29.
 */
public class Naizi {
    private static HashMap<Integer,List<CheckObjectVO>> shengCard = new HashMap<>();
    public static  boolean testHuiPai(int [][] paiList ){
        int[] pai =GlobalUtil.CloneIntList(paiList[0]);
        for(int i=0;i<paiList[0].length;i++){
            if(paiList[1][i] == 1 && pai[i] >= 3) {
                pai[i] -= 3;
            }else if(paiList[1][i] == 2 && pai[i] == 4){
                pai[i]  -= 4;
            }
        }
       return getNeedHunNum(pai);
    }

    /**
     * 得到胡牌需要的赖子数
     * type 将在哪里
     * @return
     */
    private static boolean getNeedHunNum(int[] paiList){
    	int zhong = paiList[31];
        int[] wan_arr = new int[9];
        int[] tiao_arr = new int[9];
        int[] tong_arr = new int[9];
        int needNum = 0;
        int index = 0;
        for(int i=0;i<27;i++){
            if(i<9){
                wan_arr[index] = paiList[i];
                index++;
            }
            if(i>=9 && i<18){
                if(i == 9){
                    index = 0;
                }
                tiao_arr[index] = paiList[i];
                index++;
            }
            if(i>=18){
                if(i == 18){
                    index = 0;
                }
                tong_arr[index] = paiList[i];
                index++;
            }
        }
        needNum = getNumWithJiang(wan_arr.clone())+ getNorNumber(tiao_arr.clone()) + getNorNumber(tong_arr.clone());
        if(needNum <= zhong){
        	return true;
        }
        else {
        	needNum = getNorNumber(wan_arr.clone()) +getNumWithJiang(tiao_arr.clone()) + getNorNumber(tong_arr.clone());
        	if(needNum <= zhong){
        		return true;
        	}
        	else{
        		needNum = getNorNumber(wan_arr.clone()) + getNorNumber(tiao_arr.clone())+getNumWithJiang(tong_arr.clone()) ;
        		if(needNum <= zhong){
        			return true;
        		}
        		else{
        			return false;
        		}
        	}
        }
    }

    static int Jiang = 0;

    private static int  getNumWithJiang(int[] temp_arr){
        int result = 999999;
        Jiang = 0;
        shengCard.clear();
        if(checkCanbeGroup(temp_arr.clone())){
                result = 0;
        }else{
            Set<Integer> ints =  shengCard.keySet();
            ints.size();
            if(ints.iterator().hasNext()){
                int key = ints.iterator().next();
                List<CheckObjectVO> objectVOList = shengCard.get(key);
                objectVOList = deleteSameItemForList(objectVOList);
                for(int k = 0;k<objectVOList.size();k++) {
                    CheckObjectVO objectVO = objectVOList.get(k);
                    if (objectVO.isJiang == 0) {
                        for (int i = 0; i < 9; i++) {
                            if (objectVO.paiArray[i] > 0) {
                                int tempInt = getJiangNumber(objectVO.paiArray.clone(), i);
                                if (tempInt < result) {
                                    result = tempInt;
                                }
                            }
                        }
                    } else {
                        int tempInt = getNumber(objectVO.paiArray.clone());
                        if (tempInt < result) {
                            result = tempInt;
                        }
                    }
                }
            }
        }

        //System.out.println("getNumWithJiang ===>  "+result+"  ==>> ");
       // for(int a = 0;a<temp_arr.length;a++){
            //system.out.print(temp_arr[a]+",");
       // }
       // System.out.println();
        return  result;

    }
    private static boolean isjiang;
    private static int getJiangNumber(int[] temp_arr,int index){
        int result = 0;
        if(temp_arr[index] >= 2){
            temp_arr[index] -= 2;
        }else if(temp_arr[index] == 1){
            temp_arr[index] = 0;
            result++;
        }
        for(int i=0;i<9;i++){
            if(temp_arr[i]>0){
                if(i<7){
                    if(temp_arr[i+1] == 0 && temp_arr[i+2] == 0){
                        result += 3-temp_arr[i];
                        temp_arr[i] = 0;
                    }
                    else if(temp_arr[i+1] > 0 && temp_arr[i+2] == 0){
                        temp_arr[i]--;
                        temp_arr[i+1]--;
                        result++;
                        i--;
                    }
                    else if(temp_arr[i+1] == 0 && temp_arr[i+2] > 0){
                        temp_arr[i]--;
                        temp_arr[i+2]--;
                        result++;
                        i--;
                    }
                }else{
                    if(i == 7) {
                        if (temp_arr[i + 1] == 0) {
                            result += 3 - temp_arr[i];
                            temp_arr[i] = 0;
                        } else if (temp_arr[i + 1] > 0) {
                            result++;
                            temp_arr[i]--;
                            temp_arr[i + 1]--;
                            i--;
                        }
                    }else{
                        result += 3 - temp_arr[i];
                        temp_arr[i] = 0;
                    }
                }
            }
        }
        return result;
    }

    private static int getNorNumber(int[] temp_arr){
        int result = 99999;
        shengCard.clear();
        if(checkNormalGroup(temp_arr.clone())){
            result = 0;
        }else{
            Set<Integer> ints =  shengCard.keySet();
            ints.size();
            if(ints.iterator().hasNext()) {
                int key = ints.iterator().next();
                List<CheckObjectVO> objectVOList = shengCard.get(key);
                objectVOList = deleteSameItemForList(objectVOList);
                for (int k = 0; k < objectVOList.size(); k++) {
                    CheckObjectVO objectVO = objectVOList.get(k);
                    int tempInt = getNumber(objectVO.paiArray.clone());
                    if (tempInt < result) {
                        result = tempInt;
                    }
                }
            }
        }
        //System.out.println("getNorNumber======>>   "+result);
        return result;
    }

    private static boolean checkCanbeGroup(int[] temp_arr){
        int resultNum = Remain(temp_arr);
        if (resultNum == 0) {
            return true;           //   递归退出条件：如果没有剩牌，则胡牌返回。
        }else{
            CheckObjectVO objectVO = new CheckObjectVO();
            objectVO.isJiang = Jiang;
            objectVO.paiArray = temp_arr.clone();
            List<CheckObjectVO> tempList = shengCard.get(resultNum);
            if(tempList == null){
                tempList = new ArrayList<>();
                tempList.add(objectVO);
                shengCard.put(resultNum,tempList);
            }else{
                tempList.add(objectVO);
            }
        }
        for (int i = 0;  i < temp_arr.length; i++) {//   找到有牌的地方，i就是当前牌,   PAI[i]是个数
            //   跟踪信息
            //   4张组合(杠子)
            if(temp_arr[i] != 0){
                if (temp_arr[i] == 4)                               //   如果当前牌数等于4张
                {
                    temp_arr[i] = 0;                                     //   除开全部4张牌
                    if (checkCanbeGroup(temp_arr)) {
                        return true;             //   如果剩余的牌组合成功，和牌
                    }
                    temp_arr[i] = 4;                                     //   否则，取消4张组合
                }
                //   3张组合(大对)
                if (temp_arr[i] >= 3)                               //   如果当前牌不少于3张
                {
                    temp_arr[i] -= 3;                                   //   减去3张牌
                    if (checkCanbeGroup(temp_arr)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    temp_arr[i] += 3;                                   //   取消3张组合
                }
                //   2张组合(将牌)
                if (Jiang ==0 && temp_arr[i] >= 2)           //   如果之前没有将牌，且当前牌不少于2张
                {
                    Jiang = 1;                                       //   设置将牌标志
                    temp_arr[i] -= 2;                                   //   减去2张牌
                    if (checkCanbeGroup(temp_arr)) return true;             //   如果剩余的牌组合成功，胡牌
                    temp_arr[i] += 2;                                   //   取消2张组合
                    Jiang = 0;                                       //   清除将牌标志
                }
                if   ( i> 27){
                    return   false;               //   “东南西北中发白”没有顺牌组合，不胡
                }
                //   顺牌组合，注意是从前往后组合！
                //   排除数值为8和9的牌
                if (i<7 && temp_arr[i+1]!=0 && temp_arr[i+2]!=0)             //   如果后面有连续两张牌
                {
                    temp_arr[i]--;
                    temp_arr[i + 1]--;
                    temp_arr[i + 2]--;                                     //   各牌数减1
                    if (checkCanbeGroup(temp_arr)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    temp_arr[i]++;
                    temp_arr[i + 1]++;
                    temp_arr[i + 2]++;                                     //   恢复各牌数
                }
            }
        }
        //   无法全部组合
      /* System.out.println("无法全部组合");
        for(int a = 0;a<temp_arr.length;a++){
            System.out.print(temp_arr[a]+",");
        }
        System.out.println("");*/
        return false;
    }

    private static boolean checkNormalGroup(int[] temp_arr){
        int resultNum = Remain(temp_arr);
        if (resultNum == 0) {
            return true;           //   递归退出条件：如果没有剩牌，则胡牌返回。
        }else{
            CheckObjectVO objectVO = new CheckObjectVO();
            objectVO.isJiang = Jiang;
            objectVO.paiArray = temp_arr.clone();
            List<CheckObjectVO> tempList = shengCard.get(resultNum);
            if(tempList == null){
                tempList = new ArrayList<>();
                tempList.add(objectVO);
                shengCard.put(resultNum,tempList);
            }else{
                tempList.add(objectVO);
            }
        }
        for (int i = 0;  i < temp_arr.length; i++) {//   找到有牌的地方，i就是当前牌,   PAI[i]是个数
            //   跟踪信息
            //   4张组合(杠子)
            if(temp_arr[i] != 0){
                if (temp_arr[i] == 4)                               //   如果当前牌数等于4张
                {
                    temp_arr[i] = 0;                                     //   除开全部4张牌
                    if (checkCanbeGroup(temp_arr)) {
                        return true;             //   如果剩余的牌组合成功，和牌
                    }
                    temp_arr[i] = 4;                                     //   否则，取消4张组合
                }
                //   3张组合(大对)
                if (temp_arr[i] >= 3)                               //   如果当前牌不少于3张
                {
                    temp_arr[i] -= 3;                                   //   减去3张牌
                    if (checkCanbeGroup(temp_arr)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    temp_arr[i] += 3;                                   //   取消3张组合
                }
                if   ( i> 27){
                    return   false;               //   “东南西北中发白”没有顺牌组合，不胡
                }
                //   顺牌组合，注意是从前往后组合！
                //   排除数值为8和9的牌
                if (i<7 && temp_arr[i+1]!=0 && temp_arr[i+2]!=0)             //   如果后面有连续两张牌
                {
                    temp_arr[i]--;
                    temp_arr[i + 1]--;
                    temp_arr[i + 2]--;                                     //   各牌数减1
                    if (checkCanbeGroup(temp_arr)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    temp_arr[i]++;
                    temp_arr[i + 1]++;
                    temp_arr[i + 2]++;                                     //   恢复各牌数
                }
            }
        }
        //   无法全部组合
     /* System.out.println("无法全部组合");
        for(int a = 0;a<temp_arr.length;a++){
            System.out.print(temp_arr[a]+",");
        }
        System.out.println("");*/
        return false;
    }

    //   检查剩余牌数
    static int Remain(int[] paiList) {
        int sum = 0;
        for (int i = 0; i < paiList.length; i++) {
            sum += paiList[i];
        }
        return sum;
    }

    private static int getNumber(int[] temp_arr){
        int result = 0;
        for(int i=0;i<9;i++) {
            if(temp_arr[i] > 0) {
                if(temp_arr[i] >= 3){
                    temp_arr[i] -= 3;
                    i--;
                }else {
                    if(temp_arr[i]==2){
                        temp_arr[i] = 0;
                        result++;
                    }else {
                        if (i < 7) {
                            if (temp_arr[i + 1] > 0 && temp_arr[i + 2] > 0) {
                                temp_arr[i]--;
                                temp_arr[i + 1]--;
                                temp_arr[i + 2]--;
                                i--;
                            } else if (temp_arr[i + 1] > 0 && temp_arr[i + 2] == 0) {
                                temp_arr[i]--;
                                temp_arr[i + 1]--;
                                result++;
                                i--;
                            } else if (temp_arr[i + 1] == 0 && temp_arr[i + 2] > 0) {
                                temp_arr[i]--;
                                temp_arr[i + 2]--;
                                result++;
                                i--;
                            } else if (temp_arr[i + 1] == 0 && temp_arr[i + 2] == 0) {
                                if (temp_arr[i] == 2) {
                                    temp_arr[i] = 0;
                                    result++;
                                } else {
                                    temp_arr[i] = 0;
                                    result += 2;
                                }
                            }
                        } else {
                            if (i == 7) {
                                if (temp_arr[i] > 0 && temp_arr[i + 1] > 0) {
                                    temp_arr[i]--;
                                    temp_arr[i + 1]--;
                                    result++;
                                    i--;
                                } else if (temp_arr[i] > 0 && temp_arr[i + 1] == 0) {
                                    result = result + 3 - temp_arr[i];
                                    temp_arr[i] = 0;
                                }
                            } else {
                                result = result + 3 - temp_arr[i];
                                temp_arr[i] = 0;
                            }
                        }
                    }
                }
            }
        }

      // System.out.println("getNumber ===>  "+result+"  ==>> ");
        /*for(int a = 0;a<temp_arr.length;a++){
            System.out.print(temp_arr[a]+",");
        }
        System.out.println();*/

        return result;
    }

    public static void main(String[] args){
        List<int[]> tempList = new ArrayList<>();
      tempList.add(new int[]{2,1,2,0,0,0,0,0,0,     2,0,0,3,0,0,0,0,0,     0,0,1,1,1,0,0,0,0,   0,0,0,0,1,0,0});
       //tempList.add(new int[]{1,0,0,0,0,0,0,1,0,     1,2,1,1,0,0,0,0,0,     1,0,1,0,0,0,1,1,0,   0,0,0,0,3,0,0});
      // tempList.add(new int[]{0,0,0,0,0,0,1,1,1,     0,0,2,0,3,1,1,1,0,     0,0,1,1,1,0,0,0,0,   0,0,0,0,0,0,0});
      //tempList.add(new int[]{0,1,0,1,0,0,0,2,0,     0,1,1,0,0,0,1,1,1,     0,0,0,0,0,3,0,0,0,   0,0,0,0,2,0,0});

       // int [] test = new int[]{0,1,0,1,0,0,0,2,0,     0,1,1,0,0,0,1,1,1,     0,0,0,0,0,3,0,0,0,   0,0,0,0,2,0,0};
        for(int i=0;i<tempList.size();i++){
           boolean fal = getNeedHunNum(tempList.get(i));
            if(fal){
                System.out.println("HU LE");
            }else{
                System.out.print("====================================== ");
                for(int a = 0;a<tempList.get(i).length;a++){
                    System.out.print(tempList.get(i)[a]+",");
                }
                System.out.println();
            }
        }
       // getNeedHunNum(test);
    }

    private static List<CheckObjectVO> deleteSameItemForList(List<CheckObjectVO> tempList){
        HashMap<String,CheckObjectVO> tempMap = new HashMap<>();
        List<CheckObjectVO> result = new ArrayList<>();
        for(int i=0;i<tempList.size();i++){
           tempMap.put(tempList.get(i).ToString(),tempList.get(i));
        }
        Object[] temp =  tempMap.values().toArray();
        for (int i=0;i<temp.length;i++){
            result.add((CheckObjectVO)temp[i]);
        }
        return result;
    }
}

package com.dyz.persist.util;

/**
 * Created by kevin on 2016/7/30.
 */
public class NormalHuPai {

    /**
     * //   将牌标志，即牌型“三三三三二”中的“二”
     */
    private int JIANG = 0;
    
    
    
    public int getJIANG() {
		return JIANG;
	}


	public void setJIANG(int jIANG) {
		JIANG = jIANG;
	}


	public static void main(String[] args){
    	int[] pai = new int[]{0,0,0,3,0,0,1,1,1,    0, 1, 1,1,0,0,0,0,0,    0,0,2,0,0,0,1,1,1};
    	//int [] pai = new int[]{0,0,0,0,0,0,1,1,1,     0,0,2,0,3,1,1,1,0,     0,0,1,1,1,0,0,0,0,   0,0,0,0,0,0,0};
    	NormalHuPai normalHuPai = new NormalHuPai();
    	boolean flag = normalHuPai.isHuPai(pai);
    	System.out.println(flag);
    }
    
    
    
    
    public  boolean checkHu(int[][] paiList){//加入吃牌的逻辑
        JIANG = 0;
        int[] pai =GlobalUtil.CloneIntList(paiList[0]);
        for(int i=0;i<paiList[0].length&&i<34;i++){
            if(paiList[1][i] == 1 && pai[i] >= 3) {
                pai[i] -= 3;
            }else if((paiList[1][i] == 2||paiList[1][i] == 6) && pai[i] == 4){
                pai[i]  -= 4;
            }else if(paiList[1][i]/4>0&&paiList[1][i]%4==0 && pai[i] > 0){//吃牌的标识是4，吃几次扣几次
            	int times = paiList[1][i]/4;
            	pai[i] = pai[i]-times;
            }else if(paiList[1][i] == 5 && pai[i] == 4){//碰一次并且吃一次
                pai[i]  -= 4;
            }
        }
        return isHuPai(pai);
    }
    
    

    
    public boolean isHuPai(int[] paiList){
    	if (Remain(paiList) == 0&&JIANG==1) {
            return true;           //   递归退出条件：如果没有剩牌，则胡牌返回。
        }
        for (int i = 0;  i < paiList.length&&i<34; i++) {//   找到有牌的地方，i就是当前牌,   PAI[i]是个数
            //   跟踪信息
            //   4张组合(杠子)
            if(paiList[i] != 0){
                if (paiList[i] == 4)                               //   如果当前牌数等于4张
                {
                    paiList[i] = 0;                                     //   除开全部4张牌
                    if (isHuPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，和牌
                    }
                    paiList[i] = 4;                                     //   否则，取消4张组合
                }
                //   3张组合(大对)
                if (paiList[i] >= 3)                               //   如果当前牌不少于3张
                {
                    paiList[i] -= 3;                                   //   减去3张牌
                    if (isHuPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    paiList[i] += 3;                                   //   取消3张组合
                }
                //   2张组合(将牌)
                if (JIANG ==0 && paiList[i] >= 2)           //   如果之前没有将牌，且当前牌不少于2张
                {
                    JIANG = 1;                                       //   设置将牌标志
                    paiList[i] -= 2;                                   //   减去2张牌
                    if (isHuPai(paiList)){
                    	JIANG = 0; 
                    	return true;         
                    }//   如果剩余的牌组合成功，胡牌
                    paiList[i] += 2;                                   //   取消2张组合
                    JIANG = 0;                                       //   清除将牌标志
                }
                if   ( i> 26&& i < 34){
                    return   false;               //   “东南西北中发白”没有顺牌组合，不胡
                }
                //   顺牌组合，注意是从前往后组合！
                //   排除数值为8和9的牌
                if (i<27&&i %9!=7 && i%9 != 8 && paiList[i+1]!=0 && paiList[i+2]!=0)             //   如果后面有连续两张牌
                {
                    paiList[i]--;
                    paiList[i + 1]--;
                    paiList[i + 2]--;                                     //   各牌数减1
                    if (isHuPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    paiList[i]++;
                    paiList[i + 1]++;
                    paiList[i + 2]++;                                     //   恢复各牌数
                }
            }

        }
        //   无法全部组合，不胡！
        return false;
    }
    
    //   检查剩余牌数
    int Remain(int[] paiList) {
        int sum = 0;
        for (int i = 0; i < paiList.length&&i<34; i++) {
            sum += paiList[i];
        }
        return sum;
    }
}

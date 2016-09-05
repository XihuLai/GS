package com.dyz.persist.util;

/**
 * Created by kevin on 2016/7/30.
 */
public class NormalHuPai {

    /**
     * //   将牌标志，即牌型“三三三三二”中的“二”
     */
    private int JIANG = 0;
    
    
    
    public static void main(String[] args){
    	int[] pai = new int[]{0,0,0,3,1,0,1,1,1, 0, 0, 0,0,0,0,0,0,0,0,0,1,2,0,1,1,1,1};
    	//int [] pai = new int[]{0,0,0,0,0,0,1,1,1,     0,0,2,0,3,1,1,1,0,     0,0,1,1,1,0,0,0,0,   0,0,0,0,0,0,0};
    	NormalHuPai normalHuPai = new NormalHuPai();
    	boolean flag = normalHuPai.isZZHuPai(pai);
    	System.out.println(flag);
    }
    
    
    /**
     * 判断转转麻将普通胡牌
     * @param paiList
     * @return
     */
    public  boolean checkZZHu(int[][] paiList){
        JIANG = 0;
        int[] pai =GlobalUtil.CloneIntList(paiList[0]);
        for(int i=0;i<paiList[0].length;i++){
            if(paiList[1][i] == 1 && pai[i] >= 3) {
                pai[i] -= 3;
            }else if(paiList[1][i] == 2 && pai[i] == 4){
                pai[i]  -= 4;
            }
        }
        return isZZHuPai(pai);
    }
    /**
     * 判断划水麻将普通胡牌方法
     * @param paiList
     * @return
     */
    public  boolean checkHSHu(int[][] paiList , boolean feng){
        JIANG = 0;
        int[] pai =GlobalUtil.CloneIntList(paiList[0]);
        int twoFengCount = 0;
        if(feng){
			for (int i = 0; i < paiList[0].length; i++) {
				if(i >= 27){
					if(paiList[0][i] == 2){//获取风牌成对的个数
						twoFengCount ++;
					}
					if(paiList[0][i] == 1){
						feng = false;
						i = 100;
					}
					else if(JIANG == 0 && paiList[0][i] == 2){
						JIANG = 1;
						pai[i] -= 2;
					}
					else if(paiList[1][i] == 1 && pai[i] == 3) {
	        			pai[i] -= 3;
	        		}
					else if(paiList[1][i] == 2 && pai[i] == 4){
	        			pai[i]  -= 4;
	        		}
				}
				else{
					 if(paiList[1][i] == 1 && pai[i] >= 3) {
			                pai[i] -= 3;
			            }else if(paiList[1][i] == 2 && pai[i] == 4){
			                pai[i]  -= 4;
			            }
				}
				//风牌适合胡规则时，才进行普通牌的判断
			}
			if(twoFengCount >=2){
				//风牌有对牌个数超过2个时，不能胡
				feng = false;
			}
			if(feng){
				return isHSHuPai(pai);
			}
			else{
				return feng;
			}
		}
        else{
        	for(int i=0;i<paiList[0].length;i++){
        		if(paiList[1][i] == 1 && pai[i] >= 3) {
        			pai[i] -= 3;
        		}else if(paiList[1][i] == 2 && pai[i] == 4){
        			pai[i]  -= 4;
        		}
        	}
        	return isHSHuPai(pai);
        }
        
    }
    /**
     * 转转麻将普通胡牌算法
     * @param paiList
     * @return
     */
    public boolean isZZHuPai(int[] paiList) {

        if (Remain(paiList) == 0) {
            return true;           //   递归退出条件：如果没有剩牌，则胡牌返回。
        }
        for (int i = 0;  i < paiList.length; i++) {//   找到有牌的地方，i就是当前牌,   PAI[i]是个数
            //   跟踪信息
            //   4张组合(杠子)
            if(paiList[i] != 0){
                if (paiList[i] == 4)                               //   如果当前牌数等于4张
                {
                    paiList[i] = 0;                                     //   除开全部4张牌
                    if (isZZHuPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，和牌
                    }
                    paiList[i] = 4;                                     //   否则，取消4张组合
                }
                //   3张组合(大对)
                if (paiList[i] >= 3)                               //   如果当前牌不少于3张
                {
                    paiList[i] -= 3;                                   //   减去3张牌
                    if (isZZHuPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    paiList[i] += 3;                                   //   取消3张组合
                }
                //   2张组合(将牌)
                if (JIANG ==0 && paiList[i] >= 2)           //   如果之前没有将牌，且当前牌不少于2张
                {
                    JIANG = 1;                                       //   设置将牌标志
                    paiList[i] -= 2;                                   //   减去2张牌
                    if (isZZHuPai(paiList)) return true;             //   如果剩余的牌组合成功，胡牌
                    paiList[i] += 2;                                   //   取消2张组合
                    JIANG = 0;                                       //   清除将牌标志
                }
                if   ( i> 27){
                    return   false;               //   “东南西北中发白”没有顺牌组合，不胡
                }
                //   顺牌组合，注意是从前往后组合！
                //   排除数值为8和9的牌
                if (i %9!=7 && i%9 != 8 && paiList[i+1]!=0 && paiList[i+2]!=0)             //   如果后面有连续两张牌
                {
                    paiList[i]--;
                    paiList[i + 1]--;
                    paiList[i + 2]--;                                     //   各牌数减1
                    if (isZZHuPai(paiList)) {
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
    
    /**
     * 划水麻将普通胡牌算法
     * @param paiList
     * @return
     */
    public boolean isHSHuPai(int[] paiList) {

        if (Remain(paiList) == 0) {
            return true;           //   递归退出条件：如果没有剩牌，则胡牌返回。
        }
        for (int i = 0;  i < paiList.length; i++) {//   找到有牌的地方，i就是当前牌,   PAI[i]是个数
            //   跟踪信息
            //   4张组合(杠子)
            if(paiList[i] != 0){
                if (paiList[i] == 4)                               //   如果当前牌数等于4张
                {
                    paiList[i] = 0;                                     //   除开全部4张牌
                    if (isHSHuPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，和牌
                    }
                    paiList[i] = 4;                                     //   否则，取消4张组合
                }
                //   3张组合(大对)
                if (paiList[i] >= 3)                               //   如果当前牌不少于3张
                {
                    paiList[i] -= 3;                                   //   减去3张牌
                    if (isHSHuPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    paiList[i] += 3;                                   //   取消3张组合
                }
                //   2张组合(将牌)
                if (JIANG ==0 && paiList[i] >= 2)           //   如果之前没有将牌，且当前牌不少于2张
                {
                    JIANG = 1;                                       //   设置将牌标志
                    paiList[i] -= 2;                                   //   减去2张牌
                    if (isHSHuPai(paiList)) return true;             //   如果剩余的牌组合成功，胡牌
                    paiList[i] += 2;                                   //   取消2张组合
                    JIANG = 0;                                       //   清除将牌标志
                }
               /* if( i> 27){                                      //   “东南西北中发白"  如果有将了 则必须是4 3 组合，如果没将，则可以构成一个将
                	if(JIANG ==0 && paiList[i] >= 2){
                		  JIANG = 1;                                     
                          paiList[i] -= 2;                                   //   减去2张牌
                          if (isHSHuPai(paiList)) {
                        	  return true;             //   如果剩余的牌组合成功，胡牌
                          }
                          paiList[i] += 2;                                   //   取消2张组合
                          JIANG = 0;  
                	}
                }*/
                //   顺牌组合，注意是从前往后组合！
                //   排除数值为8,9的牌和风
                if ( i < 27 && i %9!=7 && i%9 != 8 && paiList[i+1]!=0 && paiList[i+2]!=0)             //   如果后面有连续两张牌
                {
                    paiList[i]--;
                    paiList[i + 1]--;
                    paiList[i + 2]--;                                     //   各牌数减1
                    if (isHSHuPai(paiList)) {
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
        for (int i = 0; i < paiList.length; i++) {
            sum += paiList[i];
        }
        return sum;
    }
}

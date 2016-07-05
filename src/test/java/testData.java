package test.java;

import com.dyz.gameserver.pojo.RoomVO;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kevin on 2016/6/21.
 */
public class testData  {
    public int juCount;
    public int ziMo;
    public boolean hong;
    public boolean sevenDui;
    public int ma;

    public List<RoomVO> getObjList() {
        return objList;
    }

    public void setObjList(List<RoomVO> objList) {
        this.objList = objList;
    }

    public List<RoomVO> objList;
    
    public testData(){
        objList = new ArrayList<>();
        for(int i = 0;i<4;i++){
            RoomVO object = new RoomVO();
            object.name = Math.random()+"";
            objList.add(object);
        }
    }

    public int getJuCount() {
        return juCount;
    }

    public void setJuCount(int juCount) {
        this.juCount = juCount;
    }

    public int getZiMo() {
        return ziMo;
    }

    public void setZiMo(int ziMo) {
        this.ziMo = ziMo;
    }

    public boolean isHong() {
        return hong;
    }

    public void setHong(boolean hong) {
        this.hong = hong;
    }

    public boolean isSevenDui() {
        return sevenDui;
    }

    public void setSevenDui(boolean sevenDui) {
        this.sevenDui = sevenDui;
    }

    public int getMa() {
        return ma;
    }

    public void setMa(int ma) {
        this.ma = ma;
    }
}

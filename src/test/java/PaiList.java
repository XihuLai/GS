package test.java;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PaiList {
    public static ArrayList<Integer> listCard = new ArrayList<>();
    public static int paiCount = 42;


    public static int[] p1 = {0,1,2,3,3,4,5,6,7,7,10,11,12};
    public static int[] p2 = {0,1,2,3,4,5,6,8,8,8,13,13,13};
    public static int[] p3 = {0,1,2,4,5,6,8,13,15,15,16,16,17};
    public static int[] p4 = {0,1,2,4,5,6,15,15,16,17,18,18,19};

    public static ArrayList<Integer> getListCard() {
        listCard.clear();
        ArrayList<Integer> lc = new ArrayList<>();

        for (int i = 0; i < paiCount; i++) {
            for (int k = 0; k < 4; k++) {
                if(i < 34) {
                    lc.add(i);
                }
                else {
                    lc.add(i);
                    break;
                }
            }
        }

        Collections.shuffle(lc);
        Collections.shuffle(lc);

        System.out.println(lc.size());
        System.out.println(p1.length);
        System.out.println(p2.length);
        System.out.println(p3.length);
        System.out.println(p4.length);

        Integer v;
        for (int i = 0; i < p1.length; i++) {
            v = p1[i];
            lc.remove(v);
            listCard.add(v);

            v = p2[i];
            lc.remove(v);
            listCard.add(v);

            v = p3[i];
            lc.remove(v);
            listCard.add(v);

            v = p4[i];
            lc.remove(v);
            listCard.add(v);
        }

        System.out.println(lc.size());
        System.out.println(listCard.size());

        listCard.addAll(lc);

        System.out.println(listCard.size());
        return listCard;
    }

    public static void main(String[] args) {
        getListCard();
    }
}
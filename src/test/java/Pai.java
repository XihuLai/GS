package test.java;
//import com.alibaba.fastjson.JSONObject;

public class Pai {
    final private static String[] pai = {
            "一 万",
            "二 万",
            "三 万",
            "四 万",
            "五 万",
            "六 万",
            "七 万",
            "八 万",
            "九 万",

            "一 条",
            "二 条",
            "三 条",
            "四 条",
            "五 条",
            "六 条",
            "七 条",
            "八 条",
            "九 条",

            "一 筒",
            "二 筒",
            "三 筒",
            "四 筒",
            "五 筒",
            "六 筒",
            "七 筒",
            "八 筒",
            "九 筒",

            "东 风",
            "南 风",
            "西 风",
            "北 风",
            "红 中",
            "发 财",
            "白 板",

            "春 花",
            "夏 花",
            "秋 花",
            "冬 花",
            "梅 花",
            "兰 花",
            "竹 花",
            "菊 花",
    };

    public  static String getCard(int idx) {
        return pai[idx];
    }

    public static void printCards(int[][] pa, int avatarIdx) {
        String[] pc = new String[22];
        String[] pt = new String[22];
        String s;
        int idx = 0;

        for (int i = 0; i < 42; i++) {
            int len = pa[0][i];
            if (i < 34) {
                for (int j = 0; j < len; j++) {
                    s = Pai.getCard(i);
                    String[] ss = s.split(" ");
                    pc[idx] = ss[0];
                    pt[idx] = ss[1];
                    idx++;
                }
            } else {
                if (len == avatarIdx) {
                    s = Pai.getCard(i);
                    String[] ss = s.split(" ");
                    pc[idx] = ss[0];
                    pt[idx] = ss[1];
                    idx++;
                }
            }
        }

        for (int i = 0; i < idx; i++) {
            System.out.print(pc[i] + " ");
        }

        System.out.print('\n');
        for (int i = 0; i < idx; i++) {
            System.out.print(pt[i] + " ");
        }
        System.out.print('\n');

    }

    public static int getCardPoint(String name) {
//        System.out.println(name);
        String s;
        int i;
        for (i = 0; i < pai.length; i++) {
            s = pai[i].replaceAll("\\s", "");
            if (s.equals(name)) {
                break;
            }
        }

        if (i == pai.length) {
            System.out.println("wrong: 数组越界");
        }

        return i;
    }
//
//    public static void main(String[] args) {
//        int[][] paiArray = new int[2][42];
//
//        for(int i = 0; i < 42; i++){
//            paiArray[0][i] = i;
//            paiArray[1][i] = 0;
//        }
//
//        JSONObject json = new JSONObject();
//        json.put("paiArray",paiArray);
//        json.put("bankerId",1);
//        System.out.println(json.toString());
//    }
}
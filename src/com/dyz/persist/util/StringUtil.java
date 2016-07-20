package com.dyz.persist.util;


import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;


/**
 * 
 * @author luck
 *
 */
public class StringUtil {

	/**
	 * 获取字符串的字节数
	 * 
	 * @Title: getBytesLength
	 * @Description: TODO
	 * @param @param str
	 * @param @return
	 * @return int
	 * @throws
	 */
	public static int getBytesLength(String str) {
		if (isEmpty(str)) {
			return 0;
		}
		return str.getBytes().length;
	}

	/**
	 * 判断字符串是为空
	 * 
	 * @Title: isEmpty
	 * @Description: TODO
	 * @param @param str
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim())) {
			return true;
		}

		return false;
	}

	/**
	 * 判断字符串非空
	 * 
	 * @Title: isNotEmpty
	 * @Description: TODO
	 * @param @param str
	 * @param @return
	 * @return boolean
	 * @throws
	 */
	public static boolean isNotEmpty(String str) {
		return !isEmpty(str);
	}

	/**
	 * 根据正则表达式获取匹配的字符串 只获取括号中的第一个
	 * 
	 * @Title: getRegexStr
	 * @Description: TODO
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String getRegexStr(String sourceStr, String regex) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(sourceStr);
		if (m.find()) {
			return m.group(1);
		}

		return null;
	}

	/**
	 * 
	* @Title: getCurrentTime 
	* @Description: 得到当天日期
	* @param @return    设定文件 
	* @return String    返回类型 
	* @throws
	 */
    public static String getCurrentTime(){
        SimpleDateFormat f = new SimpleDateFormat("yyyyMMdd");  
        String time = f.format(new Date()); 
        return time;
    }

	/**
	 * 
	* @Title: removeEndChar 
	* @Description: 移除字符串中最后一个字符为 cha 的字符
	* @param @return    设定文件 
	* @return Object    返回类型 
	* @throws
	 */
    public static Object  removeEndChar(String object , String cha){
    	while(isNotEmpty(object) && object.endsWith(cha)){
    		object = object.substring(0 , object.length()-1);
    	}
    	return object;
    }
	/**
	 * 
	 * @Title: getRegexStrs
	 * @Description: 根据正则返回字符串匹配的所有集合
	 * @param @param sourceStr
	 * @param @param regex
	 * @param @return 设定文件
	 * @return Set<String> 返回类型
	 * @throws
	 */
	public static List<String> getRegexStrs(String sourceStr, String regex) {
		List<String> list = new ArrayList<String>();
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(sourceStr);
		while (m.find()) {
			list.add(m.group(1));
		}
		return list;
	}

	/**
	 * 合并空格用某个字符
	 * 
	 * @Title: mergeSpaceUseChar
	 * @Description: TODO
	 * @param @param str
	 * @param @param replace
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String mergeSpaceUseChar(String str,char src, String replacement) {
		// 例如:我的 你的 他的→我的,你的,他的
		if (isEmpty(str) || isEmpty(str.trim())) {
			return str;
		}
		str = str.trim();
		String dest = "";
		char[] arr = str.toCharArray();
		for (int i = 0; i < arr.length; i++) {
			if (arr[i] == src) {
				if (dest.endsWith(src+"")) {
					continue;
				}
			}
			dest = dest + arr[i];
		}

		return dest.replaceAll(src+"", replacement);
	}

	/**
	 * json过滤
	 * 
	 * @Title: jsonFilter
	 * @Description: TODO
	 * @param @param str
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String jsonFilter(String str) {

		if (isEmpty(str)) {
			return str;
		}
		try {
			//判断是否为标准的json格式
			JSON.parse(str);
		} catch (JSONException e) {
			//使用正则快速替换一般的转义标签
			str = str.replaceAll("\"[\\{]", "{").replaceAll("[\\}]\"", "}")
					.replaceAll("\\\\", "").replaceAll("(\r\n|\r|\n|\n\r)", "").replaceAll(" ", "");
			try {
				//判断是否为标准的json格式
				JSON.parse(str);
			} catch (JSONException e2) {
				//判断json特殊符号
				StringBuffer sb = new StringBuffer(str);
				//排除第一个左大括号
				for (int i = 1; i < sb.length() - 1; i++) {
					char c2;
					char c3;
					char c = sb.charAt(i);
					char c1 = sb.charAt(i + 1);
					//冒号
					if (c==':'||c == '\"'||c == '{'||c == '}'||c == '['||c == ']') {
						c2 = sb.charAt(i - 1);
						if(c==':'){
							c3 = sb.charAt(i - 2);
							if ((c2 >= 0x4e00)&&(c2 <= 0x9fbb)||(c3 >= 0x4e00)&&(c3 <= 0x9fbb)) {
								sb.replace(i, i+1, "：");
								//如果前面一位是引号就替换为中文引号
								if(c2=='"'){
									sb.replace(i-1, i, "“");
								}
						     }
						}else if(c=='"'){
							if (!(c1 == ',' || c1 == '{' || c1 == '}'
								|| c1 == '[' || c1 == ']' || c1 == ':'
								|| c2 == ',' || c2 == '{'
								|| c2 == '}' || c2 == '[' || c2 == ']' || c2 == ':')) {
								sb.replace(i, i+1, "”");
					     	}
						}else{
							if (!(c1 == ',' || c1 == '{' || c1 == '}'
								|| c1 == '[' || c1 == ']' || c1 == ':'
								|| c2 == ',' || c2 == '{'
								|| c2 == '}' || c2 == '[' || c2 == ']' || c2 == ':')) {
								sb.replace(i, i+1, "");
					     	}
						}

					}

				}
				//如果这时候还是不能转为标准json，这放弃这也评
				str = sb.toString();			
				System.out.println(str);
			}
		}
		return str;
	}

	/**
	 * 过滤内容中的引号
	 * 
	 * @Title: jsonString
	 * @Description: TODO
	 * @param @param s
	 * @param @return
	 * @return String
	 * @throws
	 */
/*	private static String dealQuot(String s) {
		// [["1"2"3","4","5"]]
		// ["a":{}]
		char[] temp = s.toCharArray();
		int n = temp.length;
		for (int i = 0; i < n; i++) {
			if ((i > 0 && temp[i - 1] == '"') && temp[i] == ':'
					&& temp[i + 1] == '"') {
				// {"a":"a","b":"b"}
				for (int j = i + 2; j < n; j++) {
					if (temp[j] == '"') {
						if (temp[j + 1] != '}' && temp[j - 1] != '\\'
								&& dealJsonSpec1(s, j)) {
							temp[j] = '”';
						} else if (temp[j + 1] == ',' || temp[j + 1] == '}') {
							break;
						}
					}
				}
			}
		}
		return new String(temp);
	}
*/
	/**
	 * index为引号的位置 是否需要替换" 内容用含有",
	 */
	@SuppressWarnings("unused")
	private static boolean dealJsonSpec1(String s, int index) {
		char[] temp = s.toCharArray();
		String tmp = s;
		int nextQuot = -1;
		if (temp[index + 1] == ',') {
			tmp = tmp.substring(index + 2);
			nextQuot = tmp.indexOf("\",");
			if (nextQuot > -1) {
				tmp = tmp.substring(0, nextQuot);
				if (!tmp.contains(":")) {
					return true;
				}
			}
		} else {
			return true;
		}
		return false;

	}

	/**
	 * 去空行
	 * 
	 * @Title: removeSpaceLine
	 * @Description: TODO
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String removeSpaceLine(String str) {

		if (isEmpty(str)) {
			return str;
		}
		String dest = "";
		for (String each : str.split("\n")) {
			if (isNotEmpty(each.trim())) {
				dest = dest + each + "\n";
			}
		}
		
		if(dest.startsWith("\n")){
			dest = dest.substring(1);
		}else if(dest.endsWith("\n")){
			dest = dest.substring(0,dest.length()-1);
		}
		
		return dest;
	}

	/**
	 * 处理冒号
	 * 
	 * @Title: dealColon
	 * @Description: TODO
	 * @param
	 * @return void
	 * @throws
	 */
	@SuppressWarnings("unused")
	private static String dealJsonColon(String json) {
		char[] temp = json.toCharArray();
		for (int i = 0; i < temp.length; i++) {
			// 找左边
			if (temp[i] == ':') {
				for (int j = i-1; j >=0; i--) {
					if(temp[j] == ' ' || temp[j] == '\r'
						|| temp[j] == '\n' || temp[j] == '\t'){
						continue;
					}else if( temp[j] == '"'){
						break;
					}else{
						temp[i] ='：';
						break;
					}
				}
				
				for (int j = i+1; j < temp.length; j++) {
					if(temp[j] == ' ' || temp[j] == '\r'
						|| temp[j] == '\n' || temp[j] == '\t'){
						continue;
					}else if(temp[j] == 't'  ||temp[j] == 'f' ||temp[j] == 'n' || temp[j] == '"' || temp[j] == '[' || temp[j] =='{' || (temp[j]+"").matches("\\d{1}")){
						break;
					}else{
						temp[i] ='：';
						break;
					}
				}

			}
		}
		
		return new String(temp);
	}

	/**
	 * 处理引号
	 * 
	 * @Title: dealJsonQuot
	 * @Description: TODO
	 * @param @param json
	 * @param @return
	 * @return String
	 * @throws
	 */
	public static String dealJsonQuot(String json) {
		String zuodakuohao = "{";
		String zuozhongkuohao = "[";
		String youdakuohao = "}";
		String youzhongkuohao = "]";
		String douhao = ",";
		String maohao = ":";
		char yinhao = '\"';
		Map<String, List<String>> map = new HashMap<String, List<String>>();

		// 与之对应的右边
		List<String> zuodakuohaoList = new LinkedList<String>();
		List<String> maohaoList = new LinkedList<String>();
		List<String> douhaoList = new LinkedList<String>();
		List<String> zuozhongkuohaoList = new LinkedList<String>();
		List<String> leftList = new LinkedList<String>();

		zuodakuohaoList.add(maohao);

		maohaoList.add(youdakuohao);
		maohaoList.add(douhao);

		douhaoList.add(maohao);
		douhaoList.add(youzhongkuohao);

		zuozhongkuohaoList.add(douhao);
		zuozhongkuohaoList.add(youzhongkuohao);

		map.put(zuodakuohao, zuodakuohaoList);
		map.put(maohao, maohaoList);
		map.put(douhao, douhaoList);
		map.put(zuozhongkuohao, zuozhongkuohaoList);

		leftList.add(zuodakuohao);
		leftList.add(maohao);
		leftList.add(douhao);
		leftList.add(zuozhongkuohao);

		// 开始处理
		char[] temp = json.toCharArray();
		String left = null;
		for (int i = 0; i < temp.length; i++) {
			if (left == null) {
				// 找左边
				if (temp[i] == yinhao) {
					for (int j = i - 1; j >= 0; j--) {
						if (temp[j] == ' ' || temp[j] == '\r'
								|| temp[j] == '\n' || temp[j] == '\t') {
							continue;
						} else if (leftList.contains(temp[j] + "")) {
							left = temp[j] + "";
							break;
						} else {
							temp[i] = '”';
							break;
						}
					}
				}
			} else {
				// 找右边
				out: for (int j = i; j < temp.length; j++) {
					if (temp[j] == yinhao) {
						// 如果找到引号
						for (int k = j + 1; k < temp.length; k++) {
							// 往后查找有没有与之匹配的右边
							if (map.get(left).contains(temp[k] + "")) {
								i = k;
								// 可能找到
								left = null;
								break out;
							} else if (temp[k] == ' ' || temp[k] == '\n'
									|| temp[k] == '\r' || temp[k] == '\t') {
								continue;
							} else {
								temp[j] = '”';
								break;
							}

						}
					}
				}
			}

		}

		return new String(temp);

	}
	
	/**
	 * 某个字符串不够多少位在前面补字符
	* @Title: fillPre 
	* @Description: TODO
	* @param @param c
	* @param @return    
	* @return String   
	* @throws
	 */
	public static String fillPre(String s,String r,int c){
		if(isEmpty(s)){
			return s;
		}
		int length = s.length();
		
		for (int i = 0; i < c - length; i++) {
			s = r + s;
		}
		
		return s;
	}
	
	
	/**
	 * unicode转中文
	* @Title: fromEncodedUnicode 
	* @Description: TODO
	* @param @param in
	* @param @param off
	* @param @param len
	* @param @return    
	* @return String   
	* @throws
	 */
	
	public static String fromEncodedUnicode(char[] in, int off, int len) {

        char aChar;

        char[] out = new char[len]; // 只短不长

        int outLen = 0;

        int end = off + len;

 


        while (off < end) {

            aChar = in[off++];

            if (aChar == '\\') {

                aChar = in[off++];

                if (aChar == 'u') {

                    // Read the xxxx

                    int value = 0;

                    for (int i = 0; i < 4; i++) {

                        aChar = in[off++];

                        switch (aChar) {

                        case '0':

                        case '1':

                        case '2':

                        case '3':

                        case '4':

                        case '5':

                        case '6':

                        case '7':

                        case '8':

                        case '9':

                            value = (value << 4) + aChar - '0';

                            break;

                        case 'a':

                        case 'b':

                        case 'c':

                        case 'd':

                        case 'e':

                        case 'f':

                            value = (value << 4) + 10 + aChar - 'a';

                            break;

                        case 'A':

                        case 'B':

                        case 'C':

                        case 'D':

                        case 'E':

                        case 'F':

                            value = (value << 4) + 10 + aChar - 'A';

                            break;

                        default:

                            throw new IllegalArgumentException("Malformed \\uxxxx encoding.");

                        }

                    }

                    out[outLen++] = (char) value;

                } else {

                    if (aChar == 't') {

                        aChar = '\t';

                    } else if (aChar == 'r') {

                        aChar = '\r';

                    } else if (aChar == 'n') {

                        aChar = '\n';

                    } else if (aChar == 'f') {

                        aChar = '\f';

                    }
                    out[outLen++] = '\\';
                    out[outLen++] = aChar;

                }

            } else {

                out[outLen++] = (char) aChar;

            }

        }

        return new String(out, 0, outLen);

    }
     /**
      * MD5加密
      * @param sourceStr
      * @return
      */
	public static String MD5(String sourceStr) {
        String result = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(sourceStr.getBytes());
            byte b[] = md.digest();
            int i;
            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }
            result = buf.toString();
        } catch (NoSuchAlgorithmException e) {
            System.out.println(e);
        }
        return result;
    }
	/**
	 * 判断 传入参数是否能转换成 int类型，并且在范围内，0,0则没有范围//判断传入game的id是否在范围内(暂时game类型只有3种)，
	 * @param str 需要转换的字符串
	 * @param big  范围大
	 * @param small  范围小
	 * @return boolean
	 */
	public static boolean isInteger(String str , Integer big , Integer small ) {
		boolean returnb ;
		if(isNotEmpty(str)){
			try {
				int intStr = Integer.parseInt(str);
				returnb = true;
				if(big == 0 && small ==0  ){
					//不需要判sourceStr转换成int类型后的范围
					returnb = true;
				}
				else{
					if(intStr <= big && intStr >= small){
						returnb = true;
					}
					else{
						returnb = false;
					}
					
				}
			} catch (Exception e) {
				returnb = false;
			}
		}
		else{
			returnb = false;
		}
		
		return returnb;
		
	}
	/**
	 * 判断手机号码是否合法
	 * @param telephone
	 * @return boolean
	 */
	public static boolean isPhone(String telephone){
		boolean b;
		if(isEmpty(telephone)){
			b = false;
		}
		else{
			Pattern p = Pattern.compile("^[1]([3|5|4|7|8][0-9]{1})[0-9]{8}$");
			Matcher m = p.matcher(telephone);
			b = m.matches();
		}
		return b;
	}
	
	public static void  main(String[] args) throws Exception {
		
		System.out.println(MD5("123"));
	}
}
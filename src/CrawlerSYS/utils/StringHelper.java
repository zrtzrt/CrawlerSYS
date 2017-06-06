package CrawlerSYS.utils;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.apache.log4j.Logger;


public class StringHelper {
	private static Logger logger = Logger.getLogger(StringHelper.class);  

	public static String getStringTime(String Format){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat(Format);
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
		return sdf.format(date);
	}

	public static boolean validateString(String s){
		if(s != null && s.trim().length() > 0){
			return true;
		}
		return false;
	}

	public static boolean validateFloat(Float f){
		try {
			if(f != null && f > 0){
				return true;
			}
		} catch (Exception e) {}
		return false;
	}

	public static String createId(){
		StringBuffer sb = new StringBuffer();
		sb.append(getStringTime("yyyyMMddHHmmssSSSS"));
		sb.append("_");
		for (int i = 0; i < 2; i++) {
			sb.append(new Random().nextInt(9));
		}
		return sb.toString();
	}
	
	public static String currentlyTime() {
		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL);
		return dateFormat.format(date);
	}
	
	public static int toInt(String str){
		if(str==null)
			return 0;
		if(str.contains("."))
			str=str.split("\\.", 2)[0];
		String str2 = "";
		for(int i=0;i<str.length();i++)
			if(str.charAt(i)>=48 && str.charAt(i)<=57)
				str2+=str.charAt(i);
		if(str2.length()==0)
			return -1;
		return Integer.parseInt(str2);
	}
	
	public static boolean isDate(String str){
		boolean res=false;
		String str2 = "";
		for(int i=0;i<str.length();i++)
			if(str.charAt(i)>=48 && str.charAt(i)<=57)
				str2+=str.charAt(i);
		if(str2.length()>4)
			res=true;
		return res;
	}
	
	public static double similarity(String str1,String str2){
		if (validateString(str1)&&validateString(str2)) {
			int len1 = str1.length(),len2 = str2.length(),temp;
			//建立上面说的数组，比字符长度大一个空间  
			int[][] dif = new int[len1 + 1][len2 + 1];  
			//赋初值，步骤B。  
			for (int a = 0; a <= len1; a++)  
				dif[a][0] = a; 
			for (int a = 0; a <= len2; a++) 
				dif[0][a] = a;
			//计算两个字符是否一样，计算左上的值 
			for (int i = 1; i <= len1; i++) 
				for (int j = 1; j <= len2; j++) {  
					if (str1.charAt(i - 1) == str2.charAt(j - 1)){
						temp = 0;
					}else 
						temp = 1;
					//取三个值中最小的  
					dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,  
							dif[i - 1][j] + 1);  
				}
			return 1-(double)dif[len1][len2]/Math.max(str1.length(), str2.length());  
		}
		return 0;
	}
	
	private static int min(int... is) {  
        int min = Integer.MAX_VALUE;
        for (int i : is)
            if (min > i) 
                min = i;
        return min;  
    }
	
	public static String similarity(List<String> strList,String str,double filter){
		double max = 0;
		String res = null;
		for (int i = 0; i < strList.size(); i++) {
			double same=similarity(res,str);
			if(same<filter)
				if(max<same){
					max=same;
					res=strList.get(i);
				}
		}
		return res;
	}
	
	public static String sqlCREATE(String table,String[] lable,int[] length){
		String sql="CREATE TABLE `"+table+"` (id int(5) NOT NULL AUTO_INCREMENT, ";
		for (int i = 0; i < lable.length; i++) {
			sql=sql+"`"+lable[i]+"` varchar("+length[i]+"), ";
		}
		return sql+"PRIMARY KEY (`id`) ) ENGINE=INNODB AUTO_INCREMENT=0 DEFAULT CHARSET=utf8;";
		
//(
//列名称1 数据类型,
//列名称2 数据类型,
//列名称3 数据类型,
//....
//)";
	}
	
	public static String sqlSELECT(String table,String[] lable,String[] condition){
		String sql="SELECT ";
		for (int i = 0; i < lable.length; i++) {
			sql=sql+lable[i];
			if(i<lable.length-1)
				sql=sql+",";
		}
		sql=sql+" FROM "+table;
		if(condition!=null&&condition.length>0){
			sql=sql+" WHERE";
			for (int i = 0; i < condition.length; i++) {
				sql=sql+condition[i];
				if(i<condition.length-1)
					sql=sql+" AND ";
			}
		}
		return sql;
	}
	
	public static String sqlINSERT(String table,String[] lable){
		String sql="INSERT INTO `"+table+"`(";
		for (int i = 0; i < lable.length; i++) {
			sql=sql+lable[i];
			if(i<lable.length-1)
				sql=sql+",";
		}
		sql=sql+") VALUES(";
		for (int i = 0; i < lable.length; i++) {
			sql=sql+"?";
			if(i<lable.length-1)
				sql=sql+",";
		}
		return sql+")";
	}
	
	public static String sqlINSERT(String table,String[] lable,String[] data){
		String sql="INSERT INTO `"+table+"`(";
		for (int i = 0; i < lable.length; i++) {
			if(validateString(data[i])){
				if(i>0)
					sql=sql+",";
				sql=sql+lable[i];
			}
		}
		sql=sql+") VALUES(\"";
		for (int i = 0; i < lable.length; i++) {
			if(validateString(data[i])){
				if(i>0)
					sql=sql+"\" , \"";
				sql=sql+data[i];
			}
		}
		return sql+"\")";
	}
	
	public static String sqlDate(Date date){
		if(date==null)
			return null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}
	
	public static String toGBK(String str){
		String res = null;
		if(!validateString(str))
			return str;
		try {
			res=new String(str.getBytes("ISO-8859-1"),"GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		}
		return res;
	}
	
	public static String[][] spArr(String[] arr,int num){
		int per = arr.length/num, indexX = 0;
		String[][] res = new String[num][per];
		String[] t = new String[per];;
		for (int i = 0; i < arr.length; i++) {
			int indexY = i%per;
			if(indexY==0)
				t = new String[per];
			t[indexY]=arr[i];
			if(indexY==per-1)
				res[indexX++] = t;
		}
		return res;
	}
	
	public static Map<String,String> mapAdd(List<String> key, List<String> val){
		Map<String,String> res = new HashMap<String,String>();
		if (key.size()==val.size()) 
			for (int i = 0; i < key.size(); i++) 
				res.put(key.get(i), val.get(i));
		return res;
		
	}
}

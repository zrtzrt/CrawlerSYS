package CrawlerSYS.utils;

//import java.io.BufferedReader;
import java.io.IOException;
//import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
//import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.log4j.Logger;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import us.codecraft.xsoup.Xsoup;

public class WebCrawler {
	private static Logger logger = Logger.getLogger(WebCrawler.class);  
	
	public static Connection getConnect(String url, Map<String,String> header, Map<String,String> cookie){
		Connection con;
//		Document doc = null;
		url = url.trim();
		con = Jsoup.connect(url);
		con.ignoreContentType(true);
//		if(!url.startsWith("http:/")){
//			return null;
//		}
		if(url.split(":", 2)[0].equals("https")){
			con.validateTLSCertificates(false);
		}
		if(!(header==null||header.isEmpty())){
			for(Object key : header.keySet()){
				con.header((String) key, header.get(key));
			}
		}else{
			con.header("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
		}
		if(!(cookie==null||cookie.isEmpty()))
			con.cookies(cookie);
//		try {
//			doc = con.get();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//		e.printStackTrace();logger.error("Exception",e);
//		}
		con.ignoreHttpErrors(true).ignoreContentType(true);
		return con;
	}
	
	public static void send(String url){
		URL realUrl;
		try {
			realUrl = new URL(url);
			// 打开和URL之间的连接
			// 建立实际的连接
			URLConnection con = realUrl.openConnection();
			 con.connect();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		} catch (IOException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		}
	}
	
	public static String get(String url, Map<String,String> header, String encode){
		String result = "";
		try {
			result = getConnect(url, header, null).execute().body();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//        BufferedReader in = null;
//        try {
//            String urlNameString = url;
//            URL realUrl = new URL(urlNameString);
//            // 打开和URL之间的连接
//            URLConnection con = realUrl.openConnection();
//            // 建立实际的连接
//            con.setRequestProperty("Accept-Charset", "utf-8");
//            con.setRequestProperty("Content-type", "application/x-www-form-urlencoded;charset=UTF-8");
//            if(!(header==null||header.isEmpty()))
//    			for(Object key : header.keySet())
//    				con.setRequestProperty((String) key, header.get(key));
//    		else
//    			con.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//            con.connect();
////            GZIPInputStream gzip = new GZIPInputStream(con.getInputStream());
//            if(encode!=null)
//            	in = new BufferedReader(new InputStreamReader(con.getInputStream(),encode));
//            else in = new BufferedReader(new InputStreamReader(con.getInputStream()));
//            String line;
//            while ((line = in.readLine()) != null)
//                result += line;
//        } catch (Exception e) {
//            System.err.println("发送GET请求出现异常！" + e);
//e.printStackTrace();logger.error("Exception",e);
//        }
//        finally {
//            try {
//                if (in != null)
//                    in.close();
//            } catch (Exception e) {
//e.printStackTrace();logger.error("Exception",e);
//            }
//        }
        return result; 
	}
	public static List<List<String>> getByXpath(Document doc, List<String> xpath, int sleepTime){
		List<List<String>> res = new ArrayList<List<String>>();
		for (int i = 0; i < xpath.size(); i++) {
//			res.add(Xsoup.select(doc,xpath.get(i)).list().toString());
			List<String> t = Xsoup.select(doc,xpath.get(i)).list();
				res.add(t);
//			Object[] get =doc.select(xpath.get(i)).toArray();
//			if(get.length==1)
//				res.add((String)get[0]);
//			else res.add(get.toString());
		}
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		}
		return res;
	}
	
	public static String[] getByXpath(Document doc, String[] xpath, int sleepTime){
		List<String> res = new ArrayList<String>();
		for (int i = 0; i < xpath.length; i++) {
			Object[] get =doc.select(xpath[i]).toArray();
			if(get.length==1)
				res.add((String)get[0]);
			else res.add(null);
		}
		try {
			Thread.sleep(sleepTime);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
		}
		return (String[]) res.toArray();
	}
	
	private static void trustAllHttpsCertificates() throws Exception {
		TrustManager[] trustAllCerts = new TrustManager[1];
		TrustManager tm = new miTM();
		trustAllCerts[0] = tm;
		SSLContext sc = SSLContext.getInstance("SSL");
		sc.init(null, trustAllCerts, null);
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	}
	static class miTM implements TrustManager,X509TrustManager {
		public X509Certificate[] getAcceptedIssuers() {
			return null;
		}
		public boolean isServerTrusted(X509Certificate[] certs) {
			return true;
		}
		public boolean isClientTrusted(X509Certificate[] certs) {
			return true;
		}
		public void checkServerTrusted(X509Certificate[] certs, String authType)
				throws CertificateException {
			return;
		}
		public void checkClientTrusted(X509Certificate[] certs, String authType)
				throws CertificateException {
			return;
		}
	}
	/**
	 * 忽略HTTPS请求的SSL证书，必须在openConnection之前调用
	 * @throws Exception
	 */
	public static void ignoreSsl() throws Exception{
		HostnameVerifier hv = new HostnameVerifier() {
			@Override
			public boolean verify(String urlHostName, SSLSession session) {
				System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
				logger.info("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
				return true;
			}
		};
		trustAllHttpsCertificates();
		HttpsURLConnection.setDefaultHostnameVerifier(hv);
	}
}

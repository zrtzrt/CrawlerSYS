package test;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import CrawlerSYS.crawler.Crawler;
import CrawlerSYS.main.MainNodeControler;

import com.alibaba.fastjson.JSONPath;

public class test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		List<String> xpath = new ArrayList<String>();
//		xpath.add("//ul[@id='performList']/li[1]/div/h2/a/text()");
//		System.out.println(webSpider.getByXpath(webSpider.getDocument("https://www.damai.cn/projectlist.do?mcid=1",
//				null, null), xpath, 3000));
		
//		String a = "[\"a\",\"b\"]";
//		String[] b=a.substring(1, a.length()-1).split(",");
//		System.out.println(Arrays.asList(b).toString()+Arrays.asList(b).size());
		
//		String[] u = {"http://news.baidu.com/?tn=news"},
//				//*[@id="performList"]/li[1]/div/h2/a
//				x = {"//title/text()"},
//				nd = {"127.0.0.1:6543"};
//		new Crawler(u,x).recursion(null, 30).startServer().run();
		
		
//		SpiderSocket s = new SpiderSocket(6543);
//		s.start();
//		MainNodeControler n = new MainNodeControler(u,x,nd,null);
//		n.setHeader(null);
//		n.setCookie(null);
//		n.setThread(3);
//		n.setUrlLimit(9);
//		n.setAllLimit(30);
//		boolean relink = true;
//		n.setRelink(relink);
//		String then = "1";
//		n.setthen(then);
//		n.run();
		
		
//		if (relink) {
//			String table = JsonPath.read(message,"$.table"),dbip = JsonPath.read(message,"$.dbip"),
//					user = JsonPath.read(message,"$.user"),pwd = JsonPath.read(message,"$.pwd");
//			String[] lable = JsonPath.read(message,"$.lable");
//			n.setTable(table);
//			n.setDbip(dbip);
//			n.setUser(user);
//			n.setPwd(pwd);
//			n.setLable(lable);
//			
//		}
		
//		String[] u = {"http://news.baidu.com/?tn=news"};
//				x = {"//div[@class='zhmaincontent']/h1/text()",
//				"//div[@class='zhxxcontent']/div/p/strong/text()",
//				"//div[@class='zhtitlepic']/img/@src",
//				"//div[@class='zhxxcontent']/p[1]/text()",
//				"//div[@class='zhxxcontent']/p[2]/a[1]/text()",
//				"//div[@class='zhxxcontent']/p[2]/text()",
//				"//div[@class='zhxxcontent']/p[3]/a/text()",
//				"//div[@class='zhxxcontent']/p/text()"};
		
//		Map<String,String> xpath = new LinkedHashMap<String,String>();
//		List<String> url = new ArrayList<String>();
//			xpath.put("url","//ul[@id='performList']/li/div/h2/a/@href");
//			xpath.put("name","//ul[@id='performList']/li/div/h2/a/text()");
//			xpath.put("info","//ul[@id='performList']/li/div/p/text()");
//			xpath.put("picture","//ul[@id='performList']/li/p/a/img/@src");
//			xpath.put("date","//ul[@id='performList']/li/div/p[@class='mt5']/text()");
//			xpath.put("locat","//ul[@id='performList']/li/div/p[@class='mt5']/span[@class='ml20']/a/text()");
//			xpath.put("price","//ul[@id='performList']/li/div/p[3]/span[@class='price-sort']/text()");
//		xpath.put("last","//div[@class='pagination']/span[@class='ml10']/text()");
//		for (int ind = 1; ind < 7; ind++) {
//			url.add("https://www.damai.cn/projectlist.do?mcid="+ind);
//		}
//		new Crawler(url.toArray(new String[url.size()]),xpath).custom(1).startServer().run();
		
		String message = 
				"{\"urls\":[{\"url\":\"http://www.xinhuanet.com\"}],\"xpaths\":[{\"xpath\":\"//title/@text\"}],\"nodes\":[{\"node\":\"127.0.0.1:6543\"}],\"dbip\":\"127.0.0.1:3306/test\",\"user\":\"root\",\"pw\":\"root\",\"table\":\"www.xinhuanet.com\",\"then\":\"auto\",\"relink\":true}";
		
		List<String> url =(List<String>) JSONPath.read(message,"$.urls.url");
		List<String> node =(List<String>) JSONPath.read(message,"$.nodes.node");
			String then = (String)JSONPath.read(message,"$.then");
//			System.out.println(then);
			List<String> xpath = null ;
			if (then==null||!then.equalsIgnoreCase("auto")) {
				xpath =(List<String>) JSONPath.read(message,"$.xpaths.xpath");
			}
			List<String> header =(List<String>) JSONPath.read(message,"$.header.key");
			List<String> cookie =(List<String>) JSONPath.read(message,"$.cookie.key");
			List<String> headerV =(List<String>) JSONPath.read(message,"$.header.val");
			List<String> cookieV =(List<String>) JSONPath.read(message,"$.cookie.val");
			
			String timer = (String) JSONPath.read(message,"$.timer");
			int inv = 0;
			String t = (String) JSONPath.read(message,"$.interval");
			if(t!=null)
				inv=Integer.parseInt(t);
			
			if(node==null||node.size()==0){
				node=new ArrayList<String>();
				node.add("127.0.0.1:6543");
			}
			MainNodeControler n = new MainNodeControler(url,xpath,node.toArray(new String[0]));
			t = (String) JSONPath.read(message,"$.thread");
			if(t!=null)
				n.setThread(Integer.parseInt(t));
			t = (String) JSONPath.read(message,"$.urlLimit");
			if(t!=null)
				n.setUrlLimit(Integer.parseInt(t));
//			t = (String) JSONPath.read(message,"$.relink");
			boolean relink=false;
//			if(t!=null)
			relink = (Boolean) JSONPath.read(message,"$.relink");
			n.setRelink(relink);
			t = (String) JSONPath.read(message,"$.stime");
			if(t!=null)
				n.setSleeptime(Integer.parseInt(t));
			t = (String) JSONPath.read(message,"$.allLimit");
			if(t!=null)
				n.setAllLimit(Integer.parseInt(t));
			if(then!=null)
				n.setthen(then);
			if (relink)
				n.setLinkpath((String) JSONPath.read(message,"$.linkpath"));
			if (then!=null&&(then.contains("save")||then.contains("auto"))) {
				String table = (String) JSONPath.read(message,"$.table"),dbip = (String) JSONPath.read(message,"$.dbip"),
						user = (String) JSONPath.read(message,"$.user"),pwd = (String) JSONPath.read(message,"$.pw");
				System.out.println(dbip+user+pwd+table);
				List<String> lable = (List<String>) JSONPath.read(message,"$.lables.lable");
				n.setTable(table);
				n.setDbip(dbip);
				n.setUser(user);
				n.setPwd(pwd);
				if(lable!=null)
					n.setLable(lable.toArray(new String[1]));
			}
			Crawler c = new Crawler(n);
			c.header(header,headerV).cookie(cookie,cookieV).startServer();
			if(timer!=null)
				c.fixTimeRun(timer);
			else if(inv>0)
				c.periodRun(inv);
			else c.run();
	}

}

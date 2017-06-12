package CrawlerSYS.main;

import org.apache.log4j.Logger;

import net.minidev.json.JSONObject;

public class MainAfterReceive extends Thread{
	private MainNodeControler nc;
	private JSONObject j;
	public MainAfterReceive(MainNodeControler nc, JSONObject j) {
		super();
		this.nc = nc;
		this.j = j;
	}
	public void run() {
//		JSONObject dnj = (JSONObject) j.get("done");
		Logger logger = Logger.getLogger ( MainAfterReceive.class );
		String dn = (String) j.get("done");
		if(dn!=null)
			for (int i = 0; i < nc.getNode().length; i++) {
				if (nc.getNode()[i].equalsIgnoreCase(dn)||nc.getNode()[i].split(":", 2)[0].equalsIgnoreCase("127.0.0.1")) {
					nc.getSumUrl()[i]++;
					if(nc.getConn()!=null)
						nc.getConn().send("{\"ip\":\""+dn+"\",\"done\":\""+(nc.getSumUrl()[i]*nc.getUrlLimit()*100)/nc.getUrl().size()+"\"}");
//					System.out.println(dn+"/"+(nc.getSumUrl()[i]*100)/nc.getUrl().size());
					logger.info(dn+"/"+(nc.getSumUrl()[i]*nc.getUrlLimit()*100)/nc.getUrl().size());
					return;
				}
			}
		String fn = (String) j.get("finish");
		if(fn==null)
			return;
//		if(nc.getConn()!=null)
//			nc.getConn().send("{\"ip\":\""+fn+"\",\"finsh\":true}");
		String[] link = (String[]) j.get("link");
		if (link!=null) {
			for (int i = 0; i < link.length; i++) {
				if (!nc.getBan().contains(link[i])&&nc.getUrl().add(link[i].trim())&&nc.getUrl().size()<=nc.getAllLimit()){
					nc.getNewUrl().add(link[i].trim().toLowerCase());
				}else{
					if(nc.getUrl().size()>=nc.getAllLimit()){
						nc.setRelink(false);
						nc.over();
					}
				}
			}
//			System.out.println("---------got new url:"+nc.getNewUrl().size()+"\n"+nc.getNewUrl()+"---------");
			logger.info("got new url:"+nc.getNewUrl().size()+"\n");
			if (nc.getNewUrl().size()<nc.getUrlLimit()) {
				nc.send(nc.getNewUrl().toArray(new String[0]), nc.getIndexOfNode());
				if(nc.getConn()!=null)
					nc.getConn().send(nc.getNewUrl().size()+"@"+nc.getNode()[nc.getIndexOfNode()]);
//				System.out.println(nc.getNewUrl().size()+"@"+nc.getNode()[nc.getIndexOfNode()]+"\n"+nc.getNewUrl());
				logger.info(nc.getNewUrl().size()+"@"+nc.getNode()[nc.getIndexOfNode()]);
				if(nc.getIndexOfNode()<nc.getNode().length-1)
					nc.setIndexOfNode(nc.getIndexOfNode()+1);
				else nc.setIndexOfNode(0);
				nc.getNewUrl().clear();
			}else {
				for (int i = 0; i < nc.getNewUrl().size()/nc.getUrlLimit(); i++) {
					String[] u = new String[nc.getUrlLimit()];
					for (int k = 0; k < u.length; k++)
						u[k] = nc.getNewUrl().get(i*u.length+k);
					if(nc.getConn()!=null)
						nc.getConn().send("{\"ip\":\""+nc.getNode()[nc.getIndexOfNode()]+"\",\"newUrl\":\""+nc.getUrlLimit()+"\"}");
//					System.out.println(nc.getUrlLimit()+"@"+nc.getNode()[nc.getIndexOfNode()]+"\n"+Arrays.asList(u));
					logger.info(nc.getUrlLimit()+"@"+nc.getNode()[nc.getIndexOfNode()]);
					nc.send(u, nc.getIndexOfNode());
					if(nc.getIndexOfNode()<nc.getNode().length-1)
						nc.setIndexOfNode(nc.getIndexOfNode()+1);
					else nc.setIndexOfNode(0);
					for (int k = 0; k < u.length; k++)
						nc.getNewUrl().remove(i*u.length);
					try {
						Thread.sleep(nc.getSleeptime());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
e.printStackTrace();logger.error("Exception",e);
					}
				}
			}
		}
//		else System.out.println("link=null");
//		if (nc.getthen().equalsIgnoreCase("auto")) {
//			List<List<List<String>>> res = (List<List<List<String>>>) j.get("res");
//			List<List<String>> r = new ArrayList<List<String>>();
//			for (int i = 0; i < res.size(); i++) {
//				r.add(res.get(i).get(0));
//			}
//			nc.getRes().addAll(r);
//			
//////			List<List<String>> alltext = res.get(0);
//////			List<List<String>> structure = new ArrayList<List<String>>();
////			LinkedHashSet<String> text = new LinkedHashSet<String>();
////			int max = 0;
////			for (int i = 0; i < res.size(); i++) {
////				int t = res.get(i).get(0).size();
////				if(t>max)
////					max=t;
////			}
////			for (int i = 0; i < max; i++) {
//////				text.addAll(Arrays.asList(alltext.get(i).substring(1, alltext.get(i).get(0).length()-1).split(",")));
//////				structure.add(new ArrayList<String>());
////				List<String> structure = new ArrayList<String>();
////				if(res.get(i).get(0).size()<=i){
////					for (int j = 0; j < res.size(); j++) {
////						if(text.add(res.get(j).get(0).get(i)))
////							structure.add(res.get(j).get(0).get(i));
////						else structure.clear();
////					}
////					if(structure!=null)
////						saveaslist();
////				}
////			}
//			
//		}
//		if(!nc.getthen().equalsIgnoreCase("save")&&!nc.getthen().equalsIgnoreCase("saveaslist")){
//			List<List<String>> res = (List<List<String>>) j.get("res");
//			System.out.println(res);
//		}
		nc.setSend(nc.getSend()-1);
		if(nc.getSend()==0){
			nc.over();
			if(nc.getConn()!=null)
				nc.getConn().send("{\"ip\":\""+fn+"\",\"finsh\":true}");
		}
		
	}
}

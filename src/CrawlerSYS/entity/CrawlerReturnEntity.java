package CrawlerSYS.entity;

import java.util.List;
import java.util.Map;

public class CrawlerReturnEntity {
	private String url;
	private List<String> link,err;
	private List<List<String>> res;
	private Map<String, List<String>> resMap;
	
	public CrawlerReturnEntity(String url, List<List<String>> res, List<String> link,
			List<String> err) {
		super();
		this.url = url;
		this.res = res;
		this.link = link;
		this.err = err;
	}
	public CrawlerReturnEntity(String url2, Map<String, List<String>> resMap,
			List<String> link2, List<String> err2) {
		super();
		this.url = url2;
		this.setResMap(resMap);
		this.link = link2;
		this.err = err2;
	}
	public CrawlerReturnEntity() {
		
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public List<List<String>> getRes() {
		return res;
	}
	public void setRes(List<List<String>> res) {
		this.res = res;
	}
	public List<String> getLink() {
		return link;
	}
	public void setLink(List<String> link) {
		this.link = link;
	}
	public List<String> getErr() {
		return err;
	}
	public void setErr(List<String> err) {
		this.err = err;
	}
	public Map<String, List<String>> getResMap() {
		return resMap;
	}
	public void setResMap(Map<String, List<String>> resMap) {
		this.resMap = resMap;
	}
	
}

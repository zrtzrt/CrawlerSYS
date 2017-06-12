package CrawlerSYS.node;

import com.zaxxer.hikari.HikariDataSource;

import CrawlerSYS.entity.CrawlerReturnEntity;

public interface Dispose {
	public void init();
	public CrawlerReturnEntity resultDispose(CrawlerReturnEntity back,HikariDataSource ds);
	public boolean saveProcess(String[] res,HikariDataSource ds);
}

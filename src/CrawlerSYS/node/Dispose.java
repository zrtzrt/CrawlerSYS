package CrawlerSYS.node;

import com.zaxxer.hikari.HikariDataSource;

import CrawlerSYS.entity.CrawlerReturnEntity;

public interface Dispose {
	public CrawlerReturnEntity resultDispose(CrawlerReturnEntity back,HikariDataSource ds);
	public boolean saveProcess(String[] res);
}

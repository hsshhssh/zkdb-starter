package org.hssh.common;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.zkclient.ZkClient;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import com.google.common.collect.Maps;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 动态数据源
 * Created by hssh on 2017/2/16.
 */
public class ZkDynamicDataSource extends AbstractRoutingDataSource{

    public ZkDynamicDataSource() {
        super();
    }

    @Autowired
    private ZkdbcpConfigProperties properties;

    public void init(ZkdbcpConfigProperties properties) throws SQLException {
        ZkClient zk = new ZkClient(System.getenv("spring.cloud.zookeeper.connect-string"));
        this.init(zk, properties);
    }


    public void init(ZkClient zk, ZkdbcpConfigProperties properties) throws SQLException {
        Preconditions.checkArgument(zk!=null && properties!=null, "zk is not init");
        Map<Object, Object> dataSourceMap = Maps.newHashMap();
        this.properties = properties;
        PropertiesConfiguration dbConfig = ZkUtils.byteToProper(zk.readData("/config/zkdb/db.conf"));
        PropertiesConfiguration bizConfig = ZkUtils.byteToProper(zk.readData("/config/zkdb/" + properties.getBizName()));
        for(String s : DataSourceName.allName) {
            Preconditions.checkNotNull(dbConfig, "load zk dbConfig error");
            DruidDataSource dataSource = new DruidDataSource();
            List<String> bizConfigList = Splitter.on(" ").splitToList(bizConfig.getString(s));
            Preconditions.checkArgument(bizConfigList != null && bizConfigList.size()>=2, "zkdb bizConfig error");

            String url = String.format(dbConfig.getString(s + ".url"), bizConfigList.get(0));
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUrl(url);
            dataSource.setUsername(bizConfigList.get(1));
            dataSource.setPassword(bizConfigList.size()==2 ? "" : bizConfigList.get(2));
            dataSource.init();
            dataSourceMap.put(s, dataSource);
        }
        this.setTargetDataSources(dataSourceMap);
        this.setDefaultTargetDataSource(dataSourceMap.get(DataSourceName.master));
    }

    @Override
    protected Object determineCurrentLookupKey() {
        return ContextHolder.getHolder();
    }
}

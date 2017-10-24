package org.hssh.common;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.zkclient.ZkClient;
import com.google.common.base.Preconditions;
import com.google.common.base.Splitter;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

/**
 * Created by hssh on 2017/2/17.
 */
@Configuration
@EnableConfigurationProperties(ZkdbcpConfigProperties.class)
@EnableTransactionManagement
public class ZkdbcpConfig {


    @Autowired
    private ZkdbcpConfigProperties properties;

    @Autowired(required = false)
    private ZkClient zkClient;

    @Bean
    SwitchStrategy switchStrategy() {
        return new SwitchStrategy();
    }

    /**
     * 开启事务
     * @param dataSource
     * @return
     */
    @Bean
    public PlatformTransactionManager txManager( DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

    //@Bean
    //public JdbcTemplate jdbcTemplate(@Qualifier("zKDynamicDataSource")DataSource dataSource) {
    //    return new JdbcTemplate(dataSource);
    //}

    @Bean(name = "dataSource")
    public DataSource zKDynamicDataSource() throws SQLException {
        ZkDynamicDataSource zkDynamicDataSource = new ZkDynamicDataSource();

        if(null == zkClient) {
            zkDynamicDataSource.init(properties);
        }
        else {
            zkDynamicDataSource.init(zkClient, properties);
        }

        return zkDynamicDataSource;
    }

    /**
     * 获取主从数据源
     */
    public DataSource createDataSource(String dataSourceName) throws SQLException
    {
        ZkClient zk;
        if(null == zkClient)
        {
            zk = new ZkClient(System.getenv("ZK_HOST"));
        }
        else
        {
            zk = zkClient;
        }

        PropertiesConfiguration dbConfig = ZkUtils.byteToProper(zk.readData("/config/zkdb/" + getConfName(this.properties)));
        PropertiesConfiguration bizConfig = ZkUtils.byteToProper(zk.readData("/config/zkdb/" + properties.getBizName()));

        Preconditions.checkNotNull(dbConfig, "load zk dbConfig error");
        DruidDataSource dataSource = new DruidDataSource();
        List<String> bizConfigList = Splitter.on(" ").splitToList(bizConfig.getString(dataSourceName));
        Preconditions.checkArgument(bizConfigList != null && bizConfigList.size()>=2, "zkdb bizConfig error");

        String url = String.format(dbConfig.getString(dataSourceName + ".url"), bizConfigList.get(0));
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(bizConfigList.get(1));
        dataSource.setPassword(bizConfigList.size()==2 ? "" : bizConfigList.get(2));
        dataSource.init();

        return dataSource;
    }

    /**
     * 获取备份数据数据源
     */
    public DataSource createBkDataSource() throws SQLException
    {
        final String backupDataSourceName = "backup";

        ZkClient zk;
        if(null == zkClient)
        {
            zk = new ZkClient(System.getenv("ZK_HOST"));
        }
        else
        {
            zk = zkClient;
        }

        PropertiesConfiguration backupDbConfig = ZkUtils.byteToProper(zk.readData("/config/zkdb/" + getConfName(this.properties)));
        PropertiesConfiguration bizConfig = ZkUtils.byteToProper(zk.readData("/config/zkdb/" + properties.getBizName()));

        Preconditions.checkNotNull(backupDbConfig, "load zk dbConfig error");
        DruidDataSource dataSource = new DruidDataSource();
        List<String> bizConfigList = Splitter.on(" ").splitToList(bizConfig.getString(backupDataSourceName));
        Preconditions.checkArgument(bizConfigList != null && bizConfigList.size()>=2, "zkdb bizConfig error");

        String url = backupDbConfig.getString(backupDataSourceName + ".url");
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(bizConfigList.get(1));
        dataSource.setPassword(bizConfigList.size()==2 ? "" : bizConfigList.get(2));
        dataSource.init();

        return dataSource;
    }

    public static String getConfName(ZkdbcpConfigProperties properties)
    {
        return properties.getConfName() == null ? "db.conf" : properties.getConfName();

    }

}

package org.hssh.common;

import com.github.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;

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
    public AbstractRoutingDataSource zKDynamicDataSource() throws SQLException {
        ZkDynamicDataSource zkDynamicDataSource = new ZkDynamicDataSource();

        if(null == zkClient) {
            zkDynamicDataSource.init(properties);
        }
        else {
            zkDynamicDataSource.init(zkClient, properties);
        }

        return zkDynamicDataSource;
    }

}

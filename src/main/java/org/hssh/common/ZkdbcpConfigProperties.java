package org.hssh.common;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by hssh on 2017/2/18.
 */
@ConfigurationProperties(prefix = ZkdbcpConfigProperties.ZKDB_PREFIX)
public class ZkdbcpConfigProperties {

    public static final String ZKDB_PREFIX = "zkdb";

    /**
     * 业务名称
     */
    public String bizName;

    public String getBizName() {
        return bizName;
    }

    public void setBizName(String bizName) {
        this.bizName = bizName;
    }
}

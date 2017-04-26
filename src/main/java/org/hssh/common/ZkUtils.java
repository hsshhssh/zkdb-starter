package org.hssh.common;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import java.io.ByteArrayInputStream;

/**
 * Created by hssh on 2017/2/17.
 */
public class ZkUtils {

    /**
     * byte[] => PropertiesCOnfiguration
     * @param bytes
     * @return
     */
    public static PropertiesConfiguration byteToProper(byte[] bytes) {
        PropertiesConfiguration config = new PropertiesConfiguration();
        try {
            config.load(new ByteArrayInputStream(bytes));
        } catch (ConfigurationException e) {
            e.printStackTrace();
            return null;
        }
        return config;
    }

}

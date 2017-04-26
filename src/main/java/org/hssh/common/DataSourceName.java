package org.hssh.common;

import java.lang.annotation.*;

/**
 * Created by hssh on 2017/2/17.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DataSourceName {

    String value() default master;

    String master = "master";

    String salve = "salve";

    String allName[] = {master, salve};

}

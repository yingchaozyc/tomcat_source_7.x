/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.catalina.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;


/**
 * Utility class to read the bootstrap Catalina configuration.
 * 
 * 工具类。负责读取Catalina的配置文件。
 *
 * @author Remy Maucherat
 * @version $Id: CatalinaProperties.java 1364151 2012-07-21 19:00:28Z markt $
 */
public class CatalinaProperties {

    private static final org.apache.juli.logging.Log log=
        org.apache.juli.logging.LogFactory.getLog( CatalinaProperties.class );

    private static Properties properties = null;


    static {
        loadProperties();
    }


    /**
     * Return specified property value.
     * 
     * 提供给外部的一个属性值获取方法。 catalina.properties。
     */
    public static String getProperty(String name) {
        return properties.getProperty(name);
    }


    /**
     * Load properties.
     * 
     * 加载properties。静态快初始化开始做，优先级高。
     */
    private static void loadProperties() {

        InputStream is = null;
        Throwable error = null;

        try {
        	// 我们可以自己定义环境变量catalina.config来定义配置文件的地址。
            // 但是一般情况下我们都不会这么定义的这么详细。
            String configUrl = getConfigUrl();
            if (configUrl != null) {
            	// 如果真的配置了，尝试将这个配置文件转化成流准备处理
                is = (new URL(configUrl)).openStream();
            }
        } catch (Throwable t) { 
            handleThrowable(t);
        }

        // 如果找不到，则去CATALINA_HOME下的conf文件夹下找catalina.properties.
        if (is == null) {
            try {
                File home = new File(Bootstrap.getCatalinaBase());
                File conf = new File(home, "conf");
                File propsFile = new File(conf, "catalina.properties");
                is = new FileInputStream(propsFile);
            } catch (Throwable t) {
                handleThrowable(t);
            }
        }

        // 如果还找不到，就需要用默认值了。在tomcat自身的路径里
        if (is == null) {
            try {
            	// TODO  这种写法和CatalinaProperties.getClass() 相比有什么不同和好处。。？
                is = CatalinaProperties.class.getResourceAsStream
                    ("/org/apache/catalina/startup/catalina.properties");
            } catch (Throwable t) {
                handleThrowable(t);
            }
        }

        // 正常情况。拿到了配置文件流，转换成properties，方便读取。
        if (is != null) {
            try {
            	// *** 学习点，从流读取到properties。***
                properties = new Properties();
                properties.load(is);
                is.close();
            } catch (Throwable t) {
                handleThrowable(t);
                error = t;
            }
        }

        // 异常情况。warn级别日志，同时new一个新的Properties。
        if ((is == null) || (error != null)) {
            // Do something
            log.warn("Failed to load catalina.properties", error);
            // That's fine - we have reasonable defaults.
            properties=new Properties();
        }

        // Register the properties as system properties
        // 把配置文件中的key-value全部注册到环境变量中。方便以后读取。
        // *** 学习点， 普通的遍历properties的方式 ***
        Enumeration<?> enumeration = properties.propertyNames();
        while (enumeration.hasMoreElements()) {
            String name = (String) enumeration.nextElement();
            String value = properties.getProperty(name);
            if (value != null) {
                System.setProperty(name, value);
            }
        }
    }


    /**
     * Get the value of the configuration URL.
     * 
     * 我们可以自己定义环境变量catalina.config来定义配置文件的地址。
     * 但是一般情况下我们都不会这么定义的这么详细。
     */
    private static String getConfigUrl() {
        return System.getProperty("catalina.config");
    }


    // Copied from ExceptionUtils since that class is not visible during start
    // 同样这个方法是我们从ExecptionUtil拷贝而来的。原理同Bootstrap。
    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
        // All other instances of Throwable will be silently swallowed
    }
}

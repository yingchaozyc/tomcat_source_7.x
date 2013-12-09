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
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.catalina.Globals;
import org.apache.catalina.security.SecurityClassLoad;
import org.apache.catalina.startup.ClassLoaderFactory.Repository;
import org.apache.catalina.startup.ClassLoaderFactory.RepositoryType;
import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogFactory;


/**
 * Bootstrap loader for Catalina.  This application constructs a class loader
 * for use in loading the Catalina internal classes (by accumulating all of the
 * JAR files found in the "server" directory under "catalina.home"), and
 * starts the regular execution of the container.  The purpose of this
 * roundabout approach is to keep the Catalina internal classes (and any
 * other classes they depend on, such as an XML parser) out of the system
 * class path and therefore not visible to application level classes.
 * 
 * Bootstrap是核心Catalina的引导类加载器。它构造了一个类加载器去加载Catalina的核心类以及
 * CatalinaHome下的jar文件。同时启动容器。 用了这种迂回的方式去加载Catalina内部的类(包括
 * 他依赖的类)是为了对上层应用保持不可见。是一种安全性的体现。
 * 
 * @author Craig R. McClanahan
 * @author Remy Maucherat
 * @version $Id: Bootstrap.java 1529816 2013-10-07 10:55:18Z markt $
 */
public final class Bootstrap {

    private static final Log log = LogFactory.getLog(Bootstrap.class);

    /**
     * Daemon object used by main.
     */
    private static Bootstrap daemon = null;

    private static final File catalinaBaseFile;
    private static final File catalinaHomeFile;

    private static final Pattern PATH_PATTERN = Pattern.compile("(\".*?\")|(([^,])*)");

    static {
        // Will always be non-null
    	// 当前工作目录。永远不为空的值。
        String userDir = System.getProperty("user.dir");

        // Home first 
        String home = System.getProperty(Globals.CATALINA_HOME_PROP);
        File homeFile = null;
        
        // 如果能拿到CATALINA_HOME做如下的处理
        if (home != null) {
            File f = new File(home);
            try {
            	// 先获取对应系统下的绝对路径, 再获取对应系统下的规范路径名称。做一些对应于不同OS的特殊处理。比如
            	// 移除多余的名称（比如 "." 和 ".."）、分析符号连接（对于 UNIX 平台），以及将驱动器名转换成标准大小写形式（对于 Microsoft Windows 平台）。
                homeFile = f.getCanonicalFile();
            } catch (IOException ioe) {
            	// 只是获取对应系统下的绝对路径。
                homeFile = f.getAbsoluteFile();
            }
        }

        // 如果CATALINA_HOME的绝对路径是无效的做如下处理
        if (homeFile == null) {
            // First fall-back. See if current directory is a bin directory
            // in a normal Tomcat install
        	// 容错处理，拿到bootstrap.jar的路径准备去定位
            File bootstrapJar = new File(userDir, "bootstrap.jar");

            // 如果bootstrap.jar存在， 用它的上一级作为CATALINA_HOME. 
            if (bootstrapJar.exists()) { 
                File f = new File(userDir, "..");
                try {
                    homeFile = f.getCanonicalFile();
                } catch (IOException ioe) {
                    homeFile = f.getAbsoluteFile();
                }
            }
        }

        // 实在取不到这个CATALINA_HOME， 就临时用工作目录user.dir来代替了
        if (homeFile == null) {
            // Second fall-back. Use current directory
            File f = new File(userDir);
            try {
                homeFile = f.getCanonicalFile();
            } catch (IOException ioe) {
                homeFile = f.getAbsoluteFile();
            }
        }

        // 到这里CATALINA_HOME是肯定有值了，最起码也是user.dir! 当然我们如果自己设置的话也应该是user.dir...
        catalinaHomeFile = homeFile;
        
        // 更改环境变量CATALINA_HOME。
        System.setProperty(Globals.CATALINA_HOME_PROP, catalinaHomeFile.getPath());

        // Then base
        // OK，下来是环境变量CONTALINA_BASE的设置。相对步骤简单很多。
        // 如果我们在环境变量中有设置过那么就直接取这个值。如果没有设置，则直接使用CATALINA_HOME
        // 同时将最后拿到的值塞到运行时的系统变量中。
        String base = System.getProperty(Globals.CATALINA_BASE_PROP);
        if (base == null) {
            catalinaBaseFile = catalinaHomeFile;
        } else {
            File baseFile = new File(base);
            try {
                baseFile = baseFile.getCanonicalFile();
            } catch (IOException ioe) {
                baseFile = baseFile.getAbsoluteFile();
            }
            catalinaBaseFile = baseFile;
        }
        System.setProperty(
                Globals.CATALINA_BASE_PROP, catalinaBaseFile.getPath());
    }

    // -------------------------------------------------------------- Variables


    /**
     * Daemon reference.
     */
    private Object catalinaDaemon = null;


    protected ClassLoader commonLoader = null;
    protected ClassLoader catalinaLoader = null;
    protected ClassLoader sharedLoader = null;


    // -------------------------------------------------------- Private Methods

    // 类加载器初始化
    private void initClassLoaders() {
        try {
        	// 创建common类加载器
            commonLoader = createClassLoader("common", null);
            if( commonLoader == null ) {
                // no config file, default to this loader - we might be in a 'single' env.
            	// 没有配置文件或者有傻逼乱删了这个配置项的话
                commonLoader=this.getClass().getClassLoader();
            }
            catalinaLoader = createClassLoader("server", commonLoader);
            sharedLoader = createClassLoader("shared", commonLoader);
        } catch (Throwable t) {
        	// TODO 没有明白这里的异常处理方案。为什么要这么做。
            handleThrowable(t);
            log.error("Class loader creation threw exception", t);
            
            // 类加载器创建异常，直接退出
            System.exit(1);
        }
    }


    /**
     * 类加载器创建。
     * 
     * @param name		标识名称。定义在catalina.properties. 有common，server，share三种。但是实际上在tomcat7中只用到了common。
     * @param parent	父类加载器。如果没有定义的话则用这个。
     * @return
     * @throws Exception
     */
    private ClassLoader createClassLoader(String name, ClassLoader parent)
        throws Exception {  
    	
    	// common.loader = "${catalina.base}/lib","${catalina.base}/lib/*.jar","${catalina.home}/lib","${catalina.home}/lib/*.jar"
    	// server.loader = ''
    	// share.loader  = ''
        String value = CatalinaProperties.getProperty(name + ".loader");
        if ((value == null) || (value.equals("")))
            return parent;
        
        // 环境变量替换。 比如上边的common.loader会被替换成
        // "D:\software\opensource-code\tomcat_source_7.x/lib","D:\software\opensource-code\tomcat_source_7.x/lib/*.jar",
        // "D:\software\opensource-code\tomcat_source_7.x/lib","D:\software\opensource-code\tomcat_source_7.x/lib/*.jar"
        value = replace(value);

        List<Repository> repositories = new ArrayList<>();

        // 转换成字符List。
        String[] repositoryPaths = getPaths(value);

        for (String repository : repositoryPaths) {
            // Check for a JAR URL repository
        	// 判断类加载器路径是否是URL类型的，这样的判断其实有点怪异。如果发生异常忽略继续
            try {
                @SuppressWarnings("unused")
                URL url = new URL(repository);
                repositories.add(new Repository(repository, RepositoryType.URL));
                continue;
            } catch (MalformedURLException e) {
                // Ignore
            }

            // Local repository
            if (repository.endsWith("*.jar")) {
            	// 如果是GLOB类型。则只保留他的目录即可。BLOB其实也就是这个意思。
                repository = repository.substring(0, repository.length() - "*.jar".length());
                repositories.add(new Repository(repository, RepositoryType.GLOB));
            } else if (repository.endsWith(".jar")) {
                repositories.add(new Repository(repository, RepositoryType.JAR));
            } else {
                repositories.add(new Repository(repository, RepositoryType.DIR));
            }
        }
        
        // 创建类加载器。
        return ClassLoaderFactory.createClassLoader(repositories, parent);
    }


    /**
     * System property replacement in the given string.
     *  
     * 当前方法会将环境变量替换为真实的绝对路径。 
     *
     * @param str The original string
     * @return the modified string
     */
    protected String replace(String str) {
        // Implementation is copied from ClassLoaderLogManager.replace(),
        // but added special processing for catalina.home and catalina.base.
        String result = str;
        int pos_start = str.indexOf("${");
        if (pos_start >= 0) {
            StringBuilder builder = new StringBuilder();
            int pos_end = -1;
            while (pos_start >= 0) {
                builder.append(str, pos_end + 1, pos_start);
                pos_end = str.indexOf('}', pos_start + 2);
                if (pos_end < 0) {
                    pos_end = pos_start - 1;
                    break;
                }
                String propName = str.substring(pos_start + 2, pos_end);
                String replacement;
                if (propName.length() == 0) {
                    replacement = null;
                } else if (Globals.CATALINA_HOME_PROP.equals(propName)) {
                    replacement = getCatalinaHome();
                } else if (Globals.CATALINA_BASE_PROP.equals(propName)) {
                    replacement = getCatalinaBase();
                } else {
                    replacement = System.getProperty(propName);
                }
                if (replacement != null) {
                    builder.append(replacement);
                } else {
                    builder.append(str, pos_start, pos_end + 1);
                }
                pos_start = str.indexOf("${", pos_end + 1);
            }
            builder.append(str, pos_end + 1, str.length());
            result = builder.toString();
        }
        return result;
    }


    /**
     * Initialize daemon.
     * 
     * 初始化daemon， 也就是Bootstrap。
     */
    public void init() throws Exception {

    	// 初始化类加载器。
        initClassLoaders();

        // 没明白下来两步设置类加载器的用意 TODO
        Thread.currentThread().setContextClassLoader(catalinaLoader);

        SecurityClassLoad.securityClassLoad(catalinaLoader);

        // Load our startup class and call its process() method
        if (log.isDebugEnabled())
            log.debug("Loading startup class");
        
        // 准备反射调用，拿到Catalina的class实例
        Class<?> startupClass =
            catalinaLoader.loadClass
            ("org.apache.catalina.startup.Catalina");
        Object startupInstance = startupClass.newInstance();

        // Set the shared extensions class loader
        if (log.isDebugEnabled())
            log.debug("Setting startup class properties");
        
        // 调用Catalina的setParentClassLoader方法设置Catalina的父加载器为shareLoader。
        // TODO 这里需要思考为什么
        String methodName = "setParentClassLoader";
        Class<?> paramTypes[] = new Class[1];
        paramTypes[0] = Class.forName("java.lang.ClassLoader");
        Object paramValues[] = new Object[1];
        paramValues[0] = sharedLoader;
        Method method = startupInstance.getClass().getMethod(methodName, paramTypes);
        method.invoke(startupInstance, paramValues);

        catalinaDaemon = startupInstance;
    }


    /**
     * Load daemon.
     * 
     * 调用Catalina的load方法。
     */
    private void load(String[] arguments)
        throws Exception {

        // Call the load() method
        String methodName = "load";
        Object param[];
        Class<?> paramTypes[];
        if (arguments==null || arguments.length==0) {
            paramTypes = null;
            param = null;
        } else {
            paramTypes = new Class[1];
            paramTypes[0] = arguments.getClass();
            param = new Object[1];
            param[0] = arguments;
        }
        Method method =
            catalinaDaemon.getClass().getMethod(methodName, paramTypes);
        if (log.isDebugEnabled())
            log.debug("Calling startup class " + method);
        method.invoke(catalinaDaemon, param);

    }


    /**
     * getServer() for configtest
     */
    private Object getServer() throws Exception {

        String methodName = "getServer";
        Method method =
            catalinaDaemon.getClass().getMethod(methodName);
        return method.invoke(catalinaDaemon);

    }


    // ----------------------------------------------------------- Main Program


    /**
     * Load the Catalina daemon.
     */
    public void init(String[] arguments)
        throws Exception {

        init();
        load(arguments);

    }


    /**
     * Start the Catalina daemon.
     * 
     * 启动Catalina。 实际就是调用Catalina的start方法。
     */
    public void start()
        throws Exception {
        if( catalinaDaemon==null ) init();

        // 这里的两个将null转型为Class[]的意义在哪里？ TODO
        Method method = catalinaDaemon.getClass().getMethod("start", (Class [] )null);
        method.invoke(catalinaDaemon, (Object [])null);

    }


    /**
     * Stop the Catalina Daemon.
     */
    public void stop()
        throws Exception {

        Method method = catalinaDaemon.getClass().getMethod("stop", (Class [] ) null);
        method.invoke(catalinaDaemon, (Object [] ) null);

    }


    /**
     * Stop the standalone server.
     */
    public void stopServer()
        throws Exception {

        Method method =
            catalinaDaemon.getClass().getMethod("stopServer", (Class []) null);
        method.invoke(catalinaDaemon, (Object []) null);

    }


   /**
     * Stop the standalone server.
     */
    public void stopServer(String[] arguments)
        throws Exception {

        Object param[];
        Class<?> paramTypes[];
        if (arguments==null || arguments.length==0) {
            paramTypes = null;
            param = null;
        } else {
            paramTypes = new Class[1];
            paramTypes[0] = arguments.getClass();
            param = new Object[1];
            param[0] = arguments;
        }
        Method method =
            catalinaDaemon.getClass().getMethod("stopServer", paramTypes);
        method.invoke(catalinaDaemon, param);

    }


    /**
     * Set flag.
     * 
     * 反射调用Catalina的setAwait方法
     */
    public void setAwait(boolean await)
        throws Exception {

        Class<?> paramTypes[] = new Class[1];
        paramTypes[0] = Boolean.TYPE;
        Object paramValues[] = new Object[1];
        paramValues[0] = Boolean.valueOf(await);
        Method method =
            catalinaDaemon.getClass().getMethod("setAwait", paramTypes);
        method.invoke(catalinaDaemon, paramValues);

    }

    /**
     * 获取Catalina的await状态
     * 
     * @return
     * @throws Exception
     */
    public boolean getAwait()
        throws Exception
    {
        Class<?> paramTypes[] = new Class[0];
        Object paramValues[] = new Object[0];
        Method method =
            catalinaDaemon.getClass().getMethod("getAwait", paramTypes);
        Boolean b=(Boolean)method.invoke(catalinaDaemon, paramValues);
        return b.booleanValue();
    }


    /**
     * Destroy the Catalina Daemon.
     */
    public void destroy() {

        // FIXME

    }


    /**
     * Main method and entry point when starting Tomcat via the provided
     * scripts.
     *
     * 启动主入口
     * 
     * @param args Command line arguments to be processed
     */
    public static void main(String args[]) { 
    	// tomcat开始启动
        if (daemon == null) {
            // Don't set daemon until init() has completed 
            Bootstrap bootstrap = new Bootstrap();
            try {
            	// bootstrap初始化
                bootstrap.init();
            } catch (Throwable t) {
                handleThrowable(t);
                t.printStackTrace();
                return;
            }
            daemon = bootstrap;
        } else {
            // When running as a service the call to stop will be on a new
            // thread so make sure the correct class loader is used to prevent
            // a range of class not found exceptions.
        	//
        	// tomcat已经在启动中。 此时调用main方法可能是一些其他的操作，比如说，关闭。
        	// 关闭时候需要保持ClassLoader的一致，所以设置当前线程的上下文类加载器为
        	// 之前dameon的类加载器，防止抛出class not found。
            Thread.currentThread().setContextClassLoader(daemon.catalinaLoader);
        }

        try {
            String command = "start";
            if (args.length > 0) {
                command = args[args.length - 1];
            }

            if (command.equals("startd")) {
                args[args.length - 1] = "start";
                daemon.load(args);
                daemon.start();
            } else if (command.equals("stopd")) {
                args[args.length - 1] = "stop";
                daemon.stop();
            } else if (command.equals("start")) { 
            	// 关注点 启动
                daemon.setAwait(true);		// 这里不明。	TODO
                daemon.load(args);			 
                daemon.start();
            } else if (command.equals("stop")) { 
            	// 关注点 停止
                daemon.stopServer(args);
            } else if (command.equals("configtest")) {
                daemon.load(args);
                if (null==daemon.getServer()) {
                    System.exit(1);
                }
                System.exit(0);
            } else {
                log.warn("Bootstrap: command \"" + command + "\" does not exist.");
            }
        } catch (Throwable t) {
            // Unwrap the Exception for clearer error reporting
            if (t instanceof InvocationTargetException &&
                    t.getCause() != null) {
                t = t.getCause();
            }
            handleThrowable(t);
            t.printStackTrace();
            System.exit(1);
        }

    }


    /**
     * Obtain the name of configured home (binary) directory. Note that home and
     * base may be the same (and are by default).
     */
    public static String getCatalinaHome() {
        return catalinaHomeFile.getPath();
    }


    /**
     * Obtain the name of the configured base (instance) directory. Note that
     * home and base may be the same (and are by default). If this is not set
     * the value returned by {@link #getCatalinaHome()} will be used.
     */
    public static String getCatalinaBase() {
        return catalinaBaseFile.getPath();
    }


    /**
     * Obtain the configured home (binary) directory. Note that home and
     * base may be the same (and are by default).
     */
    public static File getCatalinaHomeFile() {
        return catalinaHomeFile;
    }


    /**
     * Obtain the configured base (instance) directory. Note that
     * home and base may be the same (and are by default). If this is not set
     * the value returned by {@link #getCatalinaHomeFile()} will be used.
     */
    public static File getCatalinaBaseFile() {
        return catalinaBaseFile;
    }


    // Copied from ExceptionUtils since that class is not visible during start
    // 异常处理方法，从ExceptionUtils拷贝而来。因为在初始化的时候还没有加载这个类，所以暂时这个类
    // 的方法对于Bootstrap是不可见的。
    //
    // 只对于极度错误的情况才会抛出异常信息。比如线程僵死，OOM， 栈溢出等等。
    // 其他情况下异常将直接被吞掉。并且不会告诉调用方。
    private static void handleThrowable(Throwable t) {
        if (t instanceof ThreadDeath) {
            throw (ThreadDeath) t;
        }
        if (t instanceof VirtualMachineError) {
            throw (VirtualMachineError) t;
        }
        // All other instances of Throwable will be silently swallowed
    }


    // Protected for unit testing
    // 将类加载器地址转换为地址列表。
    protected static String[] getPaths(String value) {

        List<String> result = new ArrayList<>();
        Matcher matcher = PATH_PATTERN.matcher(value);

        while (matcher.find()) {
            String path = value.substring(matcher.start(), matcher.end());

            path = path.trim();

            if (path.startsWith("\"") && path.length() > 1) {
                path = path.substring(1, path.length() - 1);
                path = path.trim();
            }

            if (path.length() == 0) {
                continue;
            }

            result.add(path);
        }
        return result.toArray(new String[result.size()]);
    }
}

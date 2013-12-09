package com.alibaba.tomcat.test.jmx;
 
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.swing.JDialog;

public class MBeanTest {
	 public static void main(String[] args) throws Exception{    
	    MBeanServer mbs = MBeanServerFactory.createMBeanServer();//不能在jconsole中使用  
	    // MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();//可在jconsole中使用  
        // 创建MBean  
        HelloWorldMBean helloworld = new HelloWorld();  
        //将MBean注册到MBeanServer中  
        mbs.registerMBean(helloworld, new ObjectName("HelloWorldMBean:name=helloworld"));   
          
        //由于是为了演示保持程序处于运行状态，创建一个图形窗口  
        javax.swing.JDialog dialog = new JDialog();  
        dialog.setName("jmx test");  
        dialog.setVisible(true);  
    }  
}

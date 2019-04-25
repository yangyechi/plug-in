package swust.yang.util;

import java.util.Properties;

public class SystemProperty {
	
	private static Properties props;
	
	static{
        props = System.getProperties();
    }
	
	/**
	 * 
	 * @return 返回当前操作系统的名字
	 */
	public static String getOSName(){
        return props.getProperty("os.name");
    }
	
	/**
	 * 
	 * @return 返回当前系统环境下的文件分隔符
	 */
	public static String getFileSeparator(){
        return props.getProperty("file.separator");
    }
	
	/**
	 * 
	 * @param propertyName 属性名
	 * @return 根据传入的属性名返回对应的值
	 */
	public static String getValue(String propertyName) {
		return props.getProperty(propertyName);
	}
}

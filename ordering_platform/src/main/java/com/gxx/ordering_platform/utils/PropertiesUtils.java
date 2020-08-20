package com.gxx.ordering_platform.utils;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Properties;

public class PropertiesUtils {

	public static String get(String fileName, String propName) {
		String value = null;
		Properties prop = new Properties();
		try (InputStream inputStream = PropertiesUtils.class.getClassLoader().getResourceAsStream(fileName + ".properties");) {
			prop.load(inputStream);
			Iterator<String> it = prop.stringPropertyNames().iterator();
			while (it.hasNext()) {
				if (it.next().equals(propName)) {
					value = prop.getProperty(propName);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return value;
	}
}

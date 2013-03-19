package com.plugin.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class PropertyReader {
	private Properties prop;
	private static String propertyFile = Constants.FILEPROPERTY_NAME.getName();
	
	public PropertyReader() {
		FileInputStream propis;
		try {
			propis = new FileInputStream(PropertyReader.propertyFile);
			prop.load(propis);
		} catch (FileNotFoundException e) {
			System.out.println("FileNotFoundException");
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IOException");
			e.printStackTrace();
		}
	}
	
	public String getProperty(String name) {
		String property = prop.getProperty(name);
		if(property == null) {
			System.out.println(name + "was not setted");
		}
		return property;
	}
}

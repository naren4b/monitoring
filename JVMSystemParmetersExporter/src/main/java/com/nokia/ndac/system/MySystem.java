package com.nokia.ndac.system;

import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nokia.ndac.bean.SystemParameter;

public class MySystem implements MonitoredSystem {

	Map<String, Object> stat = new HashMap<String, Object>();
	private List<SystemParameter> systemParameters;

	public MySystem(List<SystemParameter> systemParameters) {
		this.systemParameters = systemParameters;
		stat.put("name", getName());
		stat.put("description", getDescription());
		stat.put("systemTime", getSystemTime());
		stat.put("location", getLocation());
		stat.put("hostIp", getHostIp());
		stat.put("availableProcessors", Runtime.getRuntime().availableProcessors());
		stat.put("freeMemory", Runtime.getRuntime().freeMemory());
		stat.put("maxMemory", Runtime.getRuntime().maxMemory());
		stat.put("totalMemory", Runtime.getRuntime().totalMemory());

	}

	@Override
	public List<SystemParameter> stat(List<SystemParameter> parameterNames) {
		for (SystemParameter systemParameter : systemParameters) {
			if (parameterNames.contains(systemParameter))
				systemParameter.setValue(stat.get(systemParameter.getName()));
		}

		return systemParameters;
	}

	public static String getName() {
		return "Java System";
	}

	public static String getLocation() {
		return "IN-BLR-NOKIA-L5";
	}

	public static String getSystemTime() {
		Instant instant = Instant.now();
		return instant.toString();
	}

	public static String getHostIp() {
		try {
			return Inet4Address.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			return "UnknownHost";
		}
	}

	public static String getDescription() {
		return "Simple Application running in Local JVM";
	}

}

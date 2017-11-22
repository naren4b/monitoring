package com.nokia.ndac.system;

import java.util.List;

import com.nokia.ndac.bean.SystemParameter;

public interface MonitoredSystem {

	public List<SystemParameter> stat(List<SystemParameter> systemParams);

}

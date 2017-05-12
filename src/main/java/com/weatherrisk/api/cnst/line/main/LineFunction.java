package com.weatherrisk.api.cnst.line.main;

import java.util.List;

import com.weatherrisk.api.cnst.line.sub.LineSubFunction;

public interface LineFunction {
	public List<String> getKeywords();
	
	public String getSubItemName();
	
	public String getSubMenuTitle();

	public String getSubMenuText();
	
	public String getSubAltText();

	public String getSubImagePath();

	public LineSubFunction[] getLineSubFuncs();
}

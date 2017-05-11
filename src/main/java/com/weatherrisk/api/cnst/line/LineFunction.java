package com.weatherrisk.api.cnst.line;

import java.util.List;

public interface LineFunction {
	public List<String> getKeywords();
	
	public String getSubItemName();
	
	public String getSubMenuTitle();

	public String getSubMenuText();
	
	public String getSubAltText();

	public String getSubImagePath();

	public LineSubFunction[] getLineSubFuncs();
}

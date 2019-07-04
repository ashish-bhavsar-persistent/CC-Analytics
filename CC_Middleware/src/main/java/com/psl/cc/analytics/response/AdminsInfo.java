package com.psl.cc.analytics.response;

public class AdminsInfo {
	private String label;
	private long count;

	public String getLabel() {
		return label;
	}

	public long getCount() {
		return count;
	}

	public AdminsInfo(String label, long count) {
		super();
		this.label = label;
		this.count = count;
	}

}

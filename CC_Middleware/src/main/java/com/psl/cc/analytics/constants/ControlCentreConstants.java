package com.psl.cc.analytics.constants;

public class ControlCentreConstants {

	public static final String ACCOUNTS_URL = "/api/v1/accounts";
	public static final String DEVICES_URL = "/api/v1/devices";
	public static final int NUMBER_OF_THREADS = 5;
	public static final String STATUS_SUCCESS = "Success";
	public static final int PAGE_SIZE = 10000;
	public static final String STATUS_FAIL = "Error";

	public static final String DATEFORMAT_DEVICESURL = "yyyy-MM-dd'T'HH:mm:ssXXX";
	public static final String DATEFORMAT_DEVICES = "yyyy-MM-dd HH:mm:ss.SSSZ";
	public static final String DEVICE_RATE_PLAN = "ratePlan";
	public static final String DEVICE_COMM_PLAN = "communicationPlan";
	public static final String ACCOUNT_RATE_PLAN = "defaultRatePlan.defaultRatePlanName";
	public static final String ACCOUNT_COMM_PLAN = "commPlanDetails.defaultCommPlan";
	public static final String NOT_FOUND_MESSAGE = ": Not Found";
	public static final int FETCH_DETAILS_FROM_YEAR = 1970;

	private ControlCentreConstants() {

	}
}

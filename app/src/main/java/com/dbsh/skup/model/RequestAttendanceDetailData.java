package com.dbsh.skup.model;

public class RequestAttendanceDetailData {
	/**
	 * payload.put("MAP_ID", "education.ual.UAL_04004_T.select");
	 * payload.put("SYS_ID", "AL");
	 * payload.put("accessToken", token);
	 * payload.put("parameter", parameter);
	 * payload.put("path", "common/selectlist");
	 * payload.put("programID", "UAL_04004_T");
	 * payload.put("userID", id);
	 */
	private String MAP_ID;
	private String SYS_ID;
	private String accessToken;
	private String path;
	private String programID;
	private String userID;
	private RequestAttendanceDetailParameterData parameter;

	public RequestAttendanceDetailData(String MAP_ID, String SYS_ID, String accessToken, String path, String programID, String userID, RequestAttendanceDetailParameterData parameter) {
		this.MAP_ID = MAP_ID;
		this.SYS_ID = SYS_ID;
		this.accessToken = accessToken;
		this.path = path;
		this.programID = programID;
		this.userID = userID;
		this.parameter = parameter;
	}
}
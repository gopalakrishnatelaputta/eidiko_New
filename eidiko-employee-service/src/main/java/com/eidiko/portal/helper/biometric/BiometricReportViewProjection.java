package com.eidiko.portal.helper.biometric;

public interface BiometricReportViewProjection {
	Long getemp_id();
	String getavg_working_hours();
    Long getis_late_count();
    Long getvery_late_count();
    Long getno_late_count();
    String getmonth();
    Long getyear();
}

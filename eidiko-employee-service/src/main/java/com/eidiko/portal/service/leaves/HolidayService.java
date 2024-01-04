package com.eidiko.portal.service.leaves;

import com.eidiko.portal.dto.leaves.HolidayDto;
import com.eidiko.portal.entities.biometric.HolidayList;

import java.util.Map;

public interface HolidayService {
    Map<String, Object> addHolidays(HolidayList holidayList);

    Map<String, Object> getHolidayList(int year);

    Map<String, Object> updateHoliday(HolidayDto holidayDto, int holidayId);

    Map<String, Object> deleteHoliday(int id);
}

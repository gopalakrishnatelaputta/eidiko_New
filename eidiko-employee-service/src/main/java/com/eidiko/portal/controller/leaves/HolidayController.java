package com.eidiko.portal.controller.leaves;

import com.eidiko.portal.dto.leaves.HolidayDto;
import com.eidiko.portal.entities.biometric.HolidayList;
import com.eidiko.portal.helper.employee.AuthAssignedConstants;
import com.eidiko.portal.service.leaves.HolidayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/holiday")
public class HolidayController {

    @Autowired
    private HolidayService holidayService;

    @PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
    @PostMapping("/add-holiday")
    public Map<String, Object> addHolidays(@RequestBody HolidayList holidayList) {
        return this.holidayService.addHolidays(holidayList);
    }

    @PreAuthorize(AuthAssignedConstants.EMPLOYEE_LEVEL_ACCESS)
    @GetMapping("/get-holiday-list-by-year")
    public Map<String, Object> getHolidayList(@RequestParam("year") int year) {
        return this.holidayService.getHolidayList(year);
    }

    @PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
    @PutMapping("/update-holiday")
    public Map<String, Object> updateHoliday(@RequestBody HolidayDto holidayDto, @RequestParam("holidayId") int holidayId) {
        return this.holidayService.updateHoliday(holidayDto,holidayId);
    }
    @PreAuthorize(AuthAssignedConstants.HR_LEVEL_ACCESS)
    @DeleteMapping("/delete-holiday/{id}")
    public Map<String, Object> deleteHoliday (@PathVariable("id") int id) {
        return this.holidayService.deleteHoliday(id);
    }
}

package com.eidiko.portal.serviceimpl.leavesimpl;

import com.eidiko.portal.dto.leaves.HolidayDto;
import com.eidiko.portal.entities.biometric.HolidayList;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.repo.leaves.HolidayRepository;
import com.eidiko.portal.service.leaves.HolidayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class HolidayServiceImpl implements HolidayService {

    @Autowired
    private HolidayRepository holidayRepository;
    Map<String, Object> map = new HashMap<>();
    public Map<String, Object> addHolidays(HolidayList holidayList) {
        holidayRepository.save(holidayList);
        map.put(ConstantValues.MESSAGE,ConstantValues.SUCCESS_MESSAGE);
        map.put(ConstantValues.STATUS_CODE,HttpStatus.CREATED);
        map.put(ConstantValues.STATUS_TEXT, HttpStatus.CREATED.value());
        log.info("Added successfully: {}", map);
        return map;
    }

    @Override
    public Map<String, Object> getHolidayList(int year) {
        List<HolidayList> holidayList = this.holidayRepository.findByHolidayYear(year);
        map.put(ConstantValues.STATUS_CODE,ConstantValues.SUCCESS_MESSAGE);
        map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
        map.put(ConstantValues.RESULT, holidayList);
        log.info("Holidays by year: {}", map);
        return map;
    }

    @Override
    public Map<String, Object> updateHoliday(HolidayDto holidayDto, int holidayId) {
        HolidayList holidays = this.holidayRepository.findById(holidayId).get();
        int year = holidayDto.getHolidayDate().getYear()+1900;
        holidays.setHolidayDesc(holidayDto.getHolidayDesc());
        holidays.setHolidayDate(holidayDto.getHolidayDate());
        holidays.setHolidayYear(year);
        holidays.setCreatedBy(holidays.getCreatedBy());
        this.holidayRepository.save(holidays);
        map.put(ConstantValues.MESSAGE,ConstantValues.UPDATED);
        map.put(ConstantValues.STATUS_CODE,HttpStatus.OK);
        map.put(ConstantValues.STATUS_TEXT,HttpStatus.OK.value());
        log.info("updated successfully: {}", map);
        return map;
    }

    @Override
    public Map<String, Object> deleteHoliday(int id) {
        this.holidayRepository.deleteById(id);
        map.put(ConstantValues.MESSAGE,ConstantValues.DELETE);
        map.put(ConstantValues.STATUS_CODE,HttpStatus.OK);
        map.put(ConstantValues.STATUS_TEXT,HttpStatus.OK.value());
        log.info("deleted successfully: {}", map);
        return map;
    }
}

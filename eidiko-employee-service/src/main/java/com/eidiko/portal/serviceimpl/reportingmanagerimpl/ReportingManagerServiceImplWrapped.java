package com.eidiko.portal.serviceimpl.reportingmanagerimpl;

import com.eidiko.portal.dto.reportingmanager.ReportingManagerResDto;
import com.eidiko.portal.entities.reportingmanager.DailyStatusReportReportingManager;
import com.eidiko.portal.entities.reportingmanager.EmployeeLeaveStatusReportingManager;
import com.eidiko.portal.entities.reportingmanager.EmployeeReportingManager;
import com.eidiko.portal.entities.reportingmanager.ReportingManager_RM;
import com.eidiko.portal.entities.taskstatus.EmpSkillsTracking;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.helper.biometric.BiometricReportProjection;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.repo.reportingmanager.DailyStatusReportRepoRM;
import com.eidiko.portal.repo.reportingmanager.EmployeeLeaveStatusRepoRM;
import com.eidiko.portal.repo.reportingmanager.EmployeeRepoReportingManager;
import com.eidiko.portal.repo.reportingmanager.ReportingManagerRepoRM;
import com.eidiko.portal.repo.taskstatus.EmpSkillsTrackingRepository;
import com.eidiko.portal.service.employee.EmployeeService;
import com.eidiko.portal.service.reportingmanager.ReportingManagerServiceWrapped;
import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
public class ReportingManagerServiceImplWrapped implements ReportingManagerServiceWrapped {

    @Autowired
    private WebClient client;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ReportingManagerRepoRM reportingManagerRepo;

    @Autowired
    private EmployeeRepoReportingManager employeeRepo;

    @Autowired
    private EmployeeLeaveStatusRepoRM employeeLeaveStatusRepo;

    @Autowired
    private DailyStatusReportRepoRM dailyStatusReportRepo;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmpSkillsTrackingRepository skillsTrackingRepository;
    private String employeeServiceUrl = ConstantValues.EMPLOYEE_SERVICE_BASE_URL;

    public Date getDateFromatFromString(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            java.util.Date parsedDate = dateFormat.parse(date);
            return new java.sql.Date(parsedDate.getTime());
        } catch (Exception e) {
            logger.error("Exception occurred : ",e);
        }

        return null;
    }

    public java.time.LocalDate getLocalDateFromString(String date) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date parsedDate = dateFormat.parse(date);
        return parsedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private Set<ReportingManager_RM> getReportingManagerHierarchy(long managerId, Set<ReportingManager_RM> result) {

        List<ReportingManager_RM> findAllByManagerId = this.reportingManagerRepo.findAllByManagerId(managerId);

        for (ReportingManager_RM r : findAllByManagerId) {
            if (managerId == r.getEmpId()) {
                continue;
            }
            if (result.contains(r)) continue;

            result.add(r);
            getReportingManagerHierarchy(r.getEmpId(), result);
        }
        return result;
    }

    @Override
    public Map<String, Object> getReportingEmployees(long managerId, String fromDate, String toDate) {
        Map<String, Object> map = new HashMap<>();
        try {
            LocalDate fromDateLocalDate = getLocalDateFromString(fromDate);
            LocalDate toDateLocalDate = getLocalDateFromString(toDate);

            Set<ReportingManager_RM> reportingEmployeeList = getReportingManagerHierarchy(managerId, new HashSet<>());
            Set<ReportingManagerResDto> reportingEmployeeListBetweenDates = new HashSet<>();
            List<Long> employeesId = this.employeeRepo.getAllNonDeletedEmployees();


            reportingEmployeeList.forEach(rm -> {
                if (((rm.getStartDate().toLocalDate().isEqual(fromDateLocalDate) || rm.getStartDate().toLocalDate().isAfter(fromDateLocalDate)) && (rm.getStartDate().toLocalDate().isEqual(toDateLocalDate) || rm.getStartDate().toLocalDate().isBefore(toDateLocalDate))) && (rm.getEndDate() == null || (rm.getEndDate().toLocalDate().isEqual(fromDateLocalDate) || rm.getEndDate().toLocalDate().isAfter(fromDateLocalDate)) || (rm.getEndDate().toLocalDate().isEqual(toDateLocalDate) || rm.getEndDate().toLocalDate().isBefore(toDateLocalDate))) && (employeesId.contains(rm.getEmpId()))) {

                    reportingEmployeeListBetweenDates.add(new ReportingManagerResDto(rm.getReportingManagerId(), rm.getModifiedDate(), rm.getStartDate(), rm.getEndDate(), rm.getModifiedBy(), rm.getEmpId(), this.employeeRepo.getNameFromEmployee(rm.getEmpId()), rm.getManagerId(), this.employeeRepo.getNameFromEmployee(rm.getManagerId())));
                }

            });

            logger.info("<----- reportingEmployeeListBetweenDates ------>: {}", reportingEmployeeListBetweenDates.size());


            map.put("messae", "Data fetched successfully ");
            map.put("status", HttpStatus.OK.value());
            map.put("statusMessage", "success");
            map.put("managerId", managerId);
            map.put("managerName", this.employeeRepo.getNameFromEmployee(managerId));
            map.put("result", reportingEmployeeListBetweenDates);

        } catch (ParseException e) {
            logger.error("Exception occurred : ",e);
        }

        return map;
    }

    @Override
    public Map<String, Object> getReportingEmployeesDetailedInformation(long empId, long managerId, int year, String token) {

        String fromDate = (year - 1) + "-12-26";
        String toDate = year + "-12-25";
        Map<String, Object> map = new HashMap<>();

        String url = employeeServiceUrl + "/employee/get-employee/" + empId;
        try {
            this.employeeRepo.findById(empId).orElseThrow(() -> new Exception("employee not found"));
            EmployeeReportingManager manager = this.employeeRepo.findById(managerId).orElseThrow(() -> new Exception("manager not found"));

            // get employee Data
//            String responseFinal = client.get().uri(url).header("Authorization", token).exchange().flatMap(clientResponse -> {
//                if (clientResponse.statusCode().is5xxServerError()) {
//                    clientResponse.body((clientHttpResponse, context) -> {
//                        return clientHttpResponse.getBody();
//                    });
//                    return clientResponse.bodyToMono(String.class);
//                } else return clientResponse.bodyToMono(String.class);
//            }).block();
//
//             new ObjectMapper().readValue(responseFinal, HashMap.class);
            Map<String, Object> empData = this.employeeService.getEmployeeById(empId);
            // Bio report
            BiometricReportProjection lateCount = this.employeeRepo.findByisLatetrue(fromDate, toDate, empId);
            BiometricReportProjection noLateCount = this.employeeRepo.findByisLatefalse(fromDate, toDate, empId);
            BiometricReportProjection workingGreaterThan9Hrs = this.employeeRepo.findBytotalworkingtime(fromDate, toDate, empId);
            BiometricReportProjection workingLessThan9Hrs = this.employeeRepo.findBytotalworkingtimeLess9hrs(fromDate, toDate, empId);
            BiometricReportProjection isVeryLateCount = this.employeeRepo.findByisLatetrue(fromDate, toDate, empId);

            // Leave Report

            List<EmployeeLeaveStatusReportingManager> empLeaveStatuslist = employeeLeaveStatusRepo.findAllByEmpIdInThisYear(fromDate, toDate, empId);

            // Task Status Report
            List<DailyStatusReportReportingManager> taskStatusList = this.dailyStatusReportRepo.findTaskStatusReportByEmpIdBetweenDates(empId, fromDate, toDate);


            // Reporting Employees

            Map<String, Object> reportingEmployees = getReportingEmployees(empId, fromDate, toDate);

            map.put("managerId", managerId);
            map.put("managerName", manager.getEmpName());
            map.put("employeeLateCount", lateCount);
            map.put("employeeNoLateCount", noLateCount);
            map.put("workingGreaterThan9Hrs", workingGreaterThan9Hrs);
            map.put("workingLessThan9Hrs", workingLessThan9Hrs);
            map.put("empLeaveStatuslist", empLeaveStatuslist);
            map.put("taskStatusList", taskStatusList);
            map.put("reportingEmployees", reportingEmployees.get("result"));
            map.put("employeeInfo", empData);
            map.put("isVeryLateCount", isVeryLateCount);
            map.put("empWorkingOnSkill", getEmployeeWorkingSkill(empId));

        } catch (Exception e) {
            logger.error("exception occurred : ", e);
        }
        Map<String, Object> resMap = new HashMap<>();
        resMap.put("message", "Data fetched Successfully");
        resMap.put("status", HttpStatus.OK.value());
        resMap.put("statusMessage", "success");
        resMap.put("result", map);
        return resMap;
    }

    public List<EmpSkillsTracking> getEmployeeWorkingSkill(long empId) {
        List<EmpSkillsTracking> filteredList = new ArrayList<>();
        List<EmpSkillsTracking> list = this.skillsTrackingRepository.findAllByEmpId(empId);
        Date todayDate = new Date(System.currentTimeMillis());
        list.forEach(l -> {

            if ((l.getStartDate().before(todayDate) || DateUtils.isSameDay(l.getStartDate(), todayDate)) && (l.getEndDate() == null || l.getEndDate().after(todayDate) || DateUtils.isSameDay(l.getEndDate(), todayDate))) {
                filteredList.add(l);
            }
        });
        return filteredList;
    }

    @Override
    public Map<String, Object> getAllEmployeesWithoutManager() {
        try {
            List<Long> employees = this.employeeRepo.getAllNonDeletedEmployees();
            Set<EmployeeReportingManager>list=new HashSet<>();
            if(!employees.isEmpty()){
                for(Long e:employees){
                    List<ReportingManager_RM> manager= this.reportingManagerRepo.findAllByEmpId(e);
                    if(manager.isEmpty()){
                        EmployeeReportingManager employeeManager=this.employeeRepo.findById(e).orElseThrow();
                        list.add(employeeManager);
                    }
                }
            }
            logger.info("Employees without reporting managers : {} ", list);
            Map<String,Object>map=new HashMap<>();
            map.put("message", "Data fetched Successfully");
            map.put("status", HttpStatus.OK.value());
            map.put("statusMessage", "success");
            map.put("result", list);
            return map;
        }
        catch(Exception e){
            logger.error("Exception encountered : ",e);
            throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
        }
    }

}
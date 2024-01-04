package com.eidiko.portal.serviceimpl.leavesbetaimpl;

import com.eidiko.portal.config.employee.SecurityUtil;
import com.eidiko.portal.dto.leaves.LeaveStatusResponseDto;
import com.eidiko.portal.dto.leavesbeta.LeavesBetaReqDto;
import com.eidiko.portal.entities.employee.Employee;
import com.eidiko.portal.entities.employee.ResignedEmployee;
import com.eidiko.portal.entities.leaves.EmployeeLeaveStatusLeaves;
import com.eidiko.portal.entities.leaves.LeavesAsPerBand;
import com.eidiko.portal.entities.leavesbeta.EmpLeavesBetaAuditStatus;
import com.eidiko.portal.entities.leavesbeta.LeaveTypesBeta;
import com.eidiko.portal.entities.leavesbeta.LeavesBeta;
import com.eidiko.portal.entities.reportingmanager.ReportingManager_RM;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.exception.reportingmanager.UserNotFoundException;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.repo.employee.EmployeeRepo;
import com.eidiko.portal.repo.employee.ResignedEmpRepo;
import com.eidiko.portal.repo.leaves.EmployeeLeaveStatusRepo;
import com.eidiko.portal.repo.leaves.LeavesAsPerBandRepo;
import com.eidiko.portal.repo.leavesbeta.EmpLeavesBetaAuditStatusRepo;
import com.eidiko.portal.repo.leavesbeta.LeaveTypesBetaRepo;
import com.eidiko.portal.repo.leavesbeta.LeavesBetaRepo;
import com.eidiko.portal.repo.reportingmanager.ReportingManagerRepoRM;
import com.eidiko.portal.service.leavesbeta.LeavesBetaService;
import com.eidiko.portal.serviceimpl.reportingmanagerimpl.CautionMailServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.tika.Tika;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.security.sasl.AuthenticationException;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LeavesBetaServiceImpl implements LeavesBetaService {

    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private EmployeeRepo employeeRepo;
    @Autowired
    private LeavesBetaRepo leavesBetaRepo;
    @Autowired
    private LeaveTypesBetaRepo leaveTypesBeta;
    @Autowired
    private ReportingManagerRepoRM reportingManagerRepoRM;
    @Autowired
    private CautionMailServiceImpl cautionMailService;
    @Autowired
    private EmpLeavesBetaAuditStatusRepo auditStatusRepo;
    @Autowired
    private ResignedEmpRepo resignedRepo;
    @Autowired
    private LeavesAsPerBandRepo asPerBandRepo;

    @Autowired
    private EmployeeLeaveStatusRepo empLeavesRepo;
    @Value("${files.storage}")
    public String folderLocation;
    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    Map<String, Object> map = new HashMap<>();
    private static final String LEAVES_MAIL_ID = ConstantValues.LEAVES_MAIL_ID;
    private static final String HR_TEAM_MAIL_ID = ConstantValues.HR_TEAM_MAIL_ID;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void applyLeaveByEmployee(LeavesBetaReqDto betaReqDto) throws IOException {
        Long empId = SecurityUtil.getCurrentUserDetails().getEmpId();
        List<LeavesBeta> b = this.leavesBetaRepo.findByEmpIdAndLeaveDateBetween(empId, betaReqDto.getFromDate(), betaReqDto.getToDate());

        if (b.stream().anyMatch(bb -> !bb.getStatus().equalsIgnoreCase("Declined")))
            throw new ResourceNotProcessedException(ConstantValues.LEAVE_APPLIED + betaReqDto.getFromDate());
        Employee employee = this.employeeRepo.findById(empId)
                .orElseThrow(() -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
        String attachments = null;
        if (betaReqDto.getAttachments() != null)
            attachments = this.saveAttachments(betaReqDto.getAttachments(), folderLocation, empId);

        Date currentDate = new Date();
        long randomNo = this.generateRandomNumber();

        List<LeavesBeta> betaList = new ArrayList<>();
        LocalDate fromDate = betaReqDto.getFromDate();
        LocalDate toDate = betaReqDto.getToDate();
        if (fromDate.isAfter(toDate)) throw new ResourceNotProcessedException(ConstantValues.DATE_MISMATCH);
        while (!fromDate.isAfter(toDate)) {
            LeavesBeta leavesBeta = new LeavesBeta();
            leavesBeta.setLeaveType(betaReqDto.getLeaveType());
            leavesBeta.setEmpId(empId);
            leavesBeta.setLeaveDate(fromDate);
            leavesBeta.setReason(betaReqDto.getReason());
            leavesBeta.setCreatedBy(empId);
            leavesBeta.setAttachments(attachments);
            leavesBeta.setStatus("pending");
            leavesBeta.setLeaveId(randomNo);
            leavesBeta.setHalfDay(betaReqDto.getIsHalfDay() == 1);
            betaList.add(leavesBeta);
            fromDate = fromDate.plusDays(1);
        }
        List<ReportingManager_RM> rm = this.reportingManagerRepoRM.findAllByEmpId(empId);
        Employee employee1 = null;
        for (ReportingManager_RM manager_rm : rm) {

            if (manager_rm.getEndDate() == null || manager_rm.getEndDate().after(currentDate) || currentDate.equals(manager_rm.getEndDate())) {
                employee1 = this.employeeRepo.findById(manager_rm.getManagerId()).orElseThrow(() -> new ResourceNotProcessedException(ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT));
                LOGGER.info("reporting manager email: {}", employee1.getEmailId());
            }
        }
        LeaveTypesBeta beta = this.leaveTypesBeta.findByLeaveCode(betaReqDto.getLeaveType());
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mail, true);

            String text = betaReqDto.getReason();
            String managerMailId;
            if (employee1 != null && employee1.getEmailId() != null) managerMailId = employee1.getEmailId();
            else managerMailId = HR_TEAM_MAIL_ID;
            String[] cc = new String[]{employee.getEmailId(), managerMailId};

            String htmlTemplate = cautionMailService.readingMailTemplateFromText(ConstantValues.APPLY_LEAVE_MAIL_TXT);
            String contactNo = (employee.getContactNo() != null) ? employee.getContactNo() : "1234567890";
            String processedTemplate = htmlTemplate.replace("@reason", text)
                    .replace("@empName", employee.getEmpName()).replace("@contact", contactNo);

            message.setTo("mahesh.durgadara@eidiko-india.com");
            //message.setCc(cc);
            message.setCc("nikhil.korukoppula@eidiko-india.com");
            message.setSubject(beta.getLeaveName());
            message.setText(processedTemplate, true);

            if (betaReqDto.getAttachments() != null && !betaReqDto.getAttachments().isEmpty()) {
                for (MultipartFile attachment : betaReqDto.getAttachments()) {
                    if (attachment != null && !attachment.isEmpty()) {
                        message.addAttachment(Objects.requireNonNull(attachment.getOriginalFilename()), new ByteArrayResource(attachment.getBytes()));
                        LOGGER.info("attachments: {}", attachment);
                    }
                }
            }
            javaMailSender.send(mail);
            LOGGER.info("mail sent successfully: {},{}", beta.getLeaveName(),managerMailId);
            leavesBetaRepo.saveAll(betaList);
            LOGGER.info("leave applied by employee: {}", betaList);
        } catch (Exception e) {
            LOGGER.error("Exception occurred : ", e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void applyLeaveByHrteam(LeavesBetaReqDto betaReqDto) throws AuthenticationException {

        Long empId = SecurityUtil.getCurrentUserDetails().getEmpId();
        List<LeavesBeta> b = this.leavesBetaRepo.findByEmpIdAndLeaveDateBetween(betaReqDto.getEmpId(), betaReqDto.getFromDate(), betaReqDto.getToDate());

        if (b.stream().anyMatch(bb -> !bb.getStatus().equalsIgnoreCase("Declined")))
            throw new ResourceNotProcessedException(ConstantValues.LEAVE_APPLIED + betaReqDto.getFromDate());
        Employee emp = this.employeeRepo.findById(betaReqDto.getEmpId())
                .orElseThrow(() -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));

        String attachments = null;
        if (betaReqDto.getAttachments() != null)
            attachments = this.saveAttachments(betaReqDto.getAttachments(), folderLocation, betaReqDto.getEmpId());
        Date currentDate = new Date();

        List<LeavesBeta> betaList = new ArrayList<>();
        long randomNo = this.generateRandomNumber();
        LocalDate fromDate = betaReqDto.getFromDate();
        LocalDate toDate = betaReqDto.getToDate();
        if (fromDate.isAfter(toDate)) throw new ResourceNotProcessedException(ConstantValues.DATE_MISMATCH);
        while (!fromDate.isAfter(toDate)) {
            LeavesBeta leavesBeta = new LeavesBeta();
            leavesBeta.setLeaveType(betaReqDto.getLeaveType());
            leavesBeta.setEmpId(betaReqDto.getEmpId());
            leavesBeta.setLeaveDate(fromDate);
            leavesBeta.setReason(betaReqDto.getReason());
            leavesBeta.setCreatedBy(empId);
            leavesBeta.setStatus("pending");
            leavesBeta.setAttachments(attachments);
            leavesBeta.setLeaveId(randomNo);
            leavesBeta.setHalfDay(betaReqDto.getIsHalfDay() == 1);
            betaList.add(leavesBeta);
            fromDate = fromDate.plusDays(1);
        }

        List<ReportingManager_RM> rm = this.reportingManagerRepoRM.findAllByEmpId(betaReqDto.getEmpId());
        Employee employee1 = null;
        for (ReportingManager_RM manager_rm : rm) {

            if (manager_rm.getEndDate() == null || manager_rm.getEndDate().after(currentDate) || currentDate.equals(manager_rm.getEndDate())) {
                employee1 = this.employeeRepo.findById(manager_rm.getManagerId())
                        .orElseThrow(()-> new UserNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID+manager_rm.getManagerId()));
                LOGGER.info("reporting manager email: {}", employee1.getEmailId());
            }
        }
        LeaveTypesBeta beta = this.leaveTypesBeta.findByLeaveCode(betaReqDto.getLeaveType());
        try {
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mail, true);

            String text = betaReqDto.getReason();
            String managerMailId;
            if (employee1 != null && employee1.getEmailId() != null) managerMailId = employee1.getEmailId();
            else managerMailId = HR_TEAM_MAIL_ID;
            String[] cc = new String[]{emp.getEmailId(), managerMailId};

            String htmlTemplate = cautionMailService.readingMailTemplateFromText(ConstantValues.APPLY_LEAVE_MAIL_TXT);
            String contactNo = (emp.getContactNo() != null) ? emp.getContactNo() : ConstantValues.DEFAULT_CONTACT_NO;
            String processedTemplate = htmlTemplate.replace("@reason", text).replace("@empName", emp.getEmpName()).replace("@contact", contactNo);

            message.setFrom(emp.getEmailId());
            message.setTo("mahesh.durgadara@eidiko-india.com");
            //message.setCc(cc);
            message.setCc("nikhil.korukoppula@eidiko-india.com");
            message.setSubject(beta.getLeaveName());
            message.setText(processedTemplate, true);

            if (betaReqDto.getAttachments() != null && !betaReqDto.getAttachments().isEmpty()) {
                for (MultipartFile attachment : betaReqDto.getAttachments()) {
                    if (attachment != null && !attachment.isEmpty()) {
                        message.addAttachment(Objects.requireNonNull(attachment.getOriginalFilename()), new ByteArrayResource(attachment.getBytes()));
                        LOGGER.info("attachments: {}", attachment);
                    }
                }
            }
            javaMailSender.send(mail);
            LOGGER.info("mail sent successfully: {}", beta.getLeaveName());
            leavesBetaRepo.saveAll(betaList);
            LOGGER.info("leave applied by hr-team: {}", betaList);
        } catch (Exception e) {
            LOGGER.error("Exception occurred :", e);
        }
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void updateAppliedLeave(long leavesBetaId, LeavesBetaReqDto betaReqDto) throws AuthenticationException {
        Long empId = SecurityUtil.getCurrentUserDetails().getEmpId();

       /* List<LeavesBeta> b = this.leavesBetaRepo.findByEmpIdAndLeaveDateBetween(betaReqDto.getEmpId(), betaReqDto.getFromDate(), betaReqDto.getToDate());
        if (b.stream().anyMatch(bb -> !bb.getStatus().equalsIgnoreCase("Declined")))
            throw new ResourceNotProcessedException(ConstantValues.LEAVE_APPLIED + betaReqDto.getFromDate());*/
        Employee emp = this.employeeRepo.findById(betaReqDto.getEmpId()).orElseThrow(
                () -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + betaReqDto.getEmpId()));
        LeaveTypesBeta beta = this.leaveTypesBeta.findByLeaveCode(betaReqDto.getLeaveType());
        LeavesBeta leavesBeta = this.leavesBetaRepo.findById(leavesBetaId).orElseThrow();
        List<LeavesBeta> betaList = this.leavesBetaRepo.findByLeaveId(leavesBeta.getLeaveId());
        List<ReportingManager_RM> rm = this.reportingManagerRepoRM.findAllByEmpId(betaReqDto.getEmpId());

        Date currentDate = new Date();

        Employee employee = null;
        for (ReportingManager_RM manager_rm : rm) {

            if (manager_rm.getEndDate() == null || manager_rm.getEndDate().after(currentDate) || currentDate.equals(manager_rm.getEndDate())) {
                employee = this.employeeRepo.findById(manager_rm.getManagerId()).orElseThrow(
                        ()-> new UserNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID+manager_rm.getManagerId())
                );
                LOGGER.info("reporting manager email: {}", employee.getEmailId());
            }
        }
        String attachments = null;
        if (betaReqDto.getAttachments() != null)
            attachments = this.saveAttachments(betaReqDto.getAttachments(), folderLocation, betaReqDto.getEmpId());
        List<LeavesBeta> listForSave = new ArrayList<>();
        long randomNo = this.generateRandomNumber();
        LocalDate fromDate = betaReqDto.getFromDate();
        LocalDate toDate = betaReqDto.getToDate();
        if (fromDate.isAfter(toDate)) throw new ResourceNotProcessedException(ConstantValues.DATE_MISMATCH);
        while (!fromDate.isAfter(toDate)) {
            LeavesBeta leavesBeta1 = new LeavesBeta();
            leavesBeta1.setEmpId(betaReqDto.getEmpId());
            leavesBeta1.setLeaveType(betaReqDto.getLeaveType());
            leavesBeta1.setReason(betaReqDto.getReason());
            leavesBeta1.setLeaveDate(fromDate);
            leavesBeta1.setCreatedBy(empId);
            leavesBeta1.setAttachments(attachments);
            leavesBeta1.setStatus("pending");
            leavesBeta1.setLeaveId(randomNo);
            leavesBeta1.setHalfDay(betaReqDto.getIsHalfDay() == 1);
            listForSave.add(leavesBeta1);
            fromDate = fromDate.plusDays(1);
        }
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            this.leavesBetaRepo.deleteAll(betaList);

            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mail, true);

            String text = betaReqDto.getReason();
            String managerMailId;
            if (employee != null && employee.getEmailId() != null) managerMailId = employee.getEmailId();
            else managerMailId = HR_TEAM_MAIL_ID;
            String[] cc = new String[]{emp.getEmailId(), managerMailId};

            String htmlTemplate = this.cautionMailService.readingMailTemplateFromText(ConstantValues.APPLY_LEAVE_MAIL_TXT);
            String contactNo = (emp.getContactNo() != null) ? emp.getContactNo() : ConstantValues.DEFAULT_CONTACT_NO;
            String processedTemplate = htmlTemplate.replace("@reason", text).replace("@empName", emp.getEmpName())
                    .replace("@contact", contactNo);

            message.setFrom(emp.getEmailId());
            message.setTo("mahesh.durgadara@eidiko-india.com");
            //message.setCc(cc);
            message.setCc("nikhil.korukoppula@eidiko-india.com");
            message.setSubject(beta.getLeaveName() + " (Leave Update)");
            message.setText(processedTemplate, true);

            if (betaReqDto.getAttachments() != null && !betaReqDto.getAttachments().isEmpty()) {
                for (MultipartFile attachment : betaReqDto.getAttachments()) {
                    if (attachment != null && !attachment.isEmpty()) {
                        message.addAttachment(Objects.requireNonNull(attachment.getOriginalFilename()), new ByteArrayResource(attachment.getBytes()));
                        LOGGER.info("attachments: {}", attachment);
                    }
                }
            }
            javaMailSender.send(mail);
            LOGGER.info("mail sent successfully: {}", beta.getLeaveName());

            this.leavesBetaRepo.saveAll(listForSave);
            for (LeavesBeta beta1 : listForSave) {
                this.updateAuditDetails(objectMapper.writeValueAsString(beta), beta1.getEmpId(),
                        "Updated Applied Leave with leaveId :" + randomNo, "Leave updated");
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred : ", e);
        }
    }

    @Override
    public Map<String, Object> getLeavesByEmpId(long empId, int year) {
        LocalDate fromDate = LocalDate.of(year - 1, 12, 26);
        LocalDate toDate = LocalDate.of(year, 12, 25);
        List<LeavesBeta> betaList = this.leavesBetaRepo.findByEmpIdAndLeaveDateBetween(empId, fromDate, toDate);
        List<EmployeeLeaveStatusLeaves> employeeLeaveStatusList = this.empLeavesRepo.findAllByEmpIdInThisYear(fromDate.toString(), toDate.toString(), empId);
        Set<Map<String, Object>> set = new HashSet<>();
        boolean resignedEmployee = this.resignedRepo.existsByempId(empId);
        if ((!resignedEmployee) && (!betaList.isEmpty())) {
            for (LeavesBeta leave : betaList) {
                List<LeavesBeta> ll = this.leavesBetaRepo.findByLeaveId(leave.getLeaveId());
                Map<String, Object> map1 = new HashMap<>();
                ll.get(0).setCount(ll.size());
                map1.put("leaveId", leave.getLeaveId());
                map1.put("leaveData", ll);
                set.add(map1);
            }
        }
        employeeLeaveStatusList.forEach(empLeaves -> {
            boolean leaveDateMatched = false;
            for (LeavesBeta ll : betaList) {
                if (empLeaves.getLeaveDate().toLocalDate().equals(ll.getLeaveDate())) {
                    leaveDateMatched = true;
                    break; // Exit the inner loop once a match is found
                }
            }
            if (!leaveDateMatched) {
                List<LeavesBeta>list=new ArrayList<>();
                Map<String, Object> map = new HashMap<>();
                LeavesBeta empLeavesCount = new LeavesBeta();
                // empLeavesCount.setLeavesBetaId(empLeaves.getLeaveStatusId());
                empLeavesCount.setLeaveDate(empLeaves.getLeaveDate().toLocalDate());
                empLeavesCount.setEmpId(empLeaves.getEmpId());
                empLeavesCount.setLeaveType(String.valueOf(empLeaves.getEStatus()));
                empLeavesCount.setModifiedBy(empLeaves.getModifiedBy());
                empLeavesCount.setLeaveId(empLeaves.getLeaveStatusId());
                Timestamp time=new Timestamp(empLeaves.getLeaveDate().getTime());
                empLeavesCount.setCreatedDate(time);
                empLeavesCount.setStatus("Approved");
                list.add(empLeavesCount);
                list.get(0).setCount(list.size());
                map.put("leaveId", empLeavesCount.getLeaveId());
                map.put("leaveData", list);
                set.add(map);
            }
        });
        map.put(ConstantValues.RESULT, set);
        map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
        map.put(ConstantValues.MESSAGE, ConstantValues.SUCCESS_MESSAGE);
        return map;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public void deleteAppliedLeave(long leaveBetaId, String comments) throws AuthenticationException {
        long empId = SecurityUtil.getCurrentUserDetails().getEmpId();
        Employee employee = this.employeeRepo.findById(empId).orElseThrow(
                () -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + empId));
        List<ReportingManager_RM> rm = this.reportingManagerRepoRM.findAllByEmpId(employee.getEmpId());
        LeavesBeta byId = leavesBetaRepo.findById(leaveBetaId).orElseThrow(
                () -> new UserNotFoundException(ConstantValues.LEAVE_NOT_FOUND + leaveBetaId));

        List<LeavesBeta> leavesBeta = this.leavesBetaRepo.findByLeaveId(byId.getLeaveId());
        Date currentTime = new Date();
        try {

            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mail, true);
            Employee manager = null;
            for (ReportingManager_RM managerRm : rm) {
                if (managerRm.getEndDate() == null || currentTime.before(managerRm.getEndDate()) || currentTime.equals(managerRm.getEndDate())) {
                    manager = this.employeeRepo.findById(managerRm.getManagerId()).orElseThrow(
                            () -> new UserNotFoundException(ConstantValues.USER_NOT_FOUND_WITH_THIS_ID + managerRm.getManagerId()));
                }
            }
            String managerMailId;
            if (manager != null && manager.getEmailId() != null) managerMailId = manager.getEmailId();
            else managerMailId = HR_TEAM_MAIL_ID;
            String[] cc = new String[]{employee.getEmailId(), managerMailId};
            LeaveTypesBeta leaves = this.leaveTypesBeta.findByLeaveCode(byId.getLeaveType());

            String formattedTimestamp = byId.getCreatedDate().toLocalDateTime()
                    .format(DateTimeFormatter.ofPattern("yyyy MMM dd 'at' HH:mm"));

            String fullFileName = folderLocation + ConstantValues.TEMPLATES + ConstantValues.APPLY_LEAVE_MAIL_TXT;
            File file = ResourceUtils.getFile(fullFileName);
            String s = new String(Files.readAllBytes(file.toPath()));

            String htmlTemplate = this.cautionMailService.readingMailTemplateFromText(ConstantValues.DELETE_LEAVE_MAIL_TXT);
            String processedTemplate = htmlTemplate.replace("@comments", comments)
                    .replace("@leaveDate", formattedTimestamp)
                    .replace("@to", employee.getEmailId()).replace("@applyLeaveTemplate", s)
                    .replace("@empName", employee.getEmpName()).replace("@contact", employee.getContactNo())
                    .replace("@reason", byId.getReason());

            message.setTo("mahesh.durgadara@eidiko-india.com");
            //message.setCc(cc);
            message.setCc("nikhil.korukoppula@eidiko-india.com");
            message.setSubject("Re: " + leaves.getLeaveName() + " (Leave Revoke)");
            message.setText(processedTemplate, true);
            javaMailSender.send(mail);
            LOGGER.info("Mail sent successfully : {} ", leaves.getLeaveName());

            leavesBetaRepo.deleteAll(leavesBeta);
            LOGGER.info("leave deleted: {}", byId.getLeaveId());
            for (LeavesBeta beta : leavesBeta) {
                updateAuditDetails("leaveBetaId :" + beta.getLeavesBetaId() + " leave date :" + beta.getLeaveDate() + " comments : "
                        + comments, beta.getEmpId(), "Deleted on Date  : " + new Timestamp(System.currentTimeMillis()), "leave deleted");
            }
        } catch (Exception e) {
            LOGGER.error("Exception in delete leave : ",e);
            throw new ResourceNotProcessedException("not deleted ...");
        }
    }

    @Override
    public Map<String, Object> showAttachments(String filename) {
        try {
            Path filePath = Paths.get(folderLocation + ConstantValues.ATTACHMENTS).resolve(filename);
            byte[] fileData = Files.readAllBytes(filePath);

            Tika tika = new Tika();
            String contentType = tika.detect(filePath.toFile());

            String base64String = Base64.getEncoder().encodeToString(fileData);

            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.FILE_NAME, filename);
            map.put(ConstantValues.EXTENSION, StringUtils.getFilenameExtension(filename));
            map.put(ConstantValues.CONTENT_TYPE, contentType);
            map.put(ConstantValues.RESULT, base64String);
        } catch (IOException e) {
            LOGGER.error("Error reading or processing the file", e);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return map;
    }

    @Override
    public Map<String, Object> getEmpLeaveReport(int year) {
        LocalDate fromDate = LocalDate.of(year - 1, 12, 26);
        LocalDate toDate = LocalDate.of(year, 12, 25);
        List<Employee> employee = employeeRepo.findAll();
        char leaveStatus = 'L';

        List<EmployeeLeaveStatusLeaves> listLeaves = this.empLeavesRepo.findAllByLeaveDateBetween(fromDate, toDate);
        List<LeavesBeta> leavesBeta = this.leavesBetaRepo.empLeavesReport(fromDate, toDate);

        List<LeavesAsPerBand> asPerBands = this.asPerBandRepo.findAllByYear(year);
        List<ResignedEmployee> resgnEmpList = this.resignedRepo.findAll();
        Set<Long> resignedEmpIds = resgnEmpList.stream()
                .map(ResignedEmployee::getEmpId)
                .collect(Collectors.toSet());

        List<LeaveStatusResponseDto> responseList = new ArrayList<>();
        for (Employee emp : employee) {
            LeaveStatusResponseDto responseDto = new LeaveStatusResponseDto();
            responseDto.setEmpId(emp.getEmpId());
            responseDto.setEmpName(emp.getEmpName());

            asPerBands.forEach(band -> {
                if (emp.getEmpId() == band.getEmpId()) {
                    responseDto.setBand(band.getBand());
                    responseDto.setTotalLeavesAsPerband(band.getLeaves());
                }
            });
            Set<LocalDate> set = new HashSet<>();
            for (EmployeeLeaveStatusLeaves leaveDate : listLeaves) {
                if (emp.getEmpId() == leaveDate.getEmpId() && leaveDate.getEStatus() == leaveStatus) {
                    set.add(leaveDate.getLeaveDate().toLocalDate());
                }
            }
            for (LeavesBeta ll : leavesBeta) {
                if (emp.getEmpId() == ll.getEmpId()) {
                    set.add(ll.getLeaveDate());
                }
            }
            responseDto.setLeavesCount(set.size());
            if (resignedEmpIds.contains(emp.getEmpId())) {
                continue;
            }
            responseList.add(responseDto);
        }
        map.put(ConstantValues.RESULT, responseList);
        map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
        map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
        return map;
    }

    private void updateAuditDetails(String payload, long empId, String description, String status) {

        long updatedBy;
        try {
            updatedBy = SecurityUtil.getCurrentUserDetails().getEmpId();
        } catch (AuthenticationException e) {
            throw new RuntimeException(e);
        }

        EmpLeavesBetaAuditStatus auditStatus = new EmpLeavesBetaAuditStatus();

        auditStatus.setUpdatedDate(new Timestamp(System.currentTimeMillis()));
        auditStatus.setEmpId(empId);
        auditStatus.setUpdatedBy(updatedBy);
        auditStatus.setDescription(description);
        auditStatus.setPayLoad(payload);
        auditStatus.setStatus(status);
        try {
            this.auditStatusRepo.save(auditStatus);
            LOGGER.info("Leaves beta audit table updated: {}", auditStatus);
        } catch (Exception e) {
            LOGGER.error("Error updating audit details", e);
            throw new ResourceNotProcessedException("not updated or processed ...");
        }
    }

    private Long generateRandomNumber() {
        int min = 0;
        int max = 999999;
        return new Random().nextLong(max - min + 1) + min;
    }

    private String saveAttachments(List<MultipartFile> multipartFiles, String folderLocation, Long empId) {
        List<String> fileNames = new ArrayList<>();
        List<String> allowedExtensions = Arrays.asList("pdf","jpg","jpeg","png","docx");
        LocalDate date = LocalDate.now();
        for (MultipartFile file : multipartFiles) {
            String fileExtension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            String filename = file.getOriginalFilename();
            String fileSaveName;
            if (!allowedExtensions.contains(fileExtension)) {
                throw new ResourceNotProcessedException(ConstantValues.NOT_ALLOWED_EXTENSIONS);
            }

            try {
                final Path path = Paths.get(folderLocation + ConstantValues.ATTACHMENTS);
                if ("docx".equalsIgnoreCase(fileExtension)) {
                    InputStream docFile =file.getInputStream();
                    XWPFDocument doc = new XWPFDocument(docFile);

                    // Create a ByteArrayOutputStream to store the PDF content
                    ByteArrayOutputStream pdfOutputStream = new ByteArrayOutputStream();

                    PdfOptions pdfOptions = PdfOptions.create();
                    PdfConverter.getInstance().convert(doc, pdfOutputStream, pdfOptions);

                    // Save the PDF content to a file
                    String pdfFileName = filename.substring(0, filename.lastIndexOf('.')) + ".pdf";
                    fileSaveName = empId + "_" + date + "_" + pdfFileName;
                    Files.write(path.resolve(fileSaveName), pdfOutputStream.toByteArray());

                    fileNames.add(fileSaveName);

                    doc.close();
                    pdfOutputStream.close();
                } else {
                    fileSaveName = empId + "_" + date + "_" + filename;
                    Files.copy(file.getInputStream(),
                            path.resolve(fileSaveName),
                            StandardCopyOption.REPLACE_EXISTING);

                    fileNames.add(fileSaveName);
                }
            } catch (IOException e) {
                LOGGER.error("Error reading or processing the file", e);
            }
        }
        return String.join(",", fileNames);
    }


    @Override
    public Map<String, Object> getPendingLeavesById(long leaveApplyId) {
        try {
            LeavesBeta leavesBeta = this.leavesBetaRepo.findById(leaveApplyId).orElseThrow();
            map.put(ConstantValues.RESULT, leavesBeta);
            map.put(ConstantValues.MESSAGE, ConstantValues.PROCESSED_SUCCESSFULLY);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Exception : ", e);
            throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
        }
        return map;
    }

    @Override
    public Map<String, Object> getAllPendingLeavesToManager() {
        try {
            Date currentDate = new Date();
            Long empId = SecurityUtil.getCurrentUserDetails().getEmpId();
            List<ReportingManager_RM> reportingManagerRm = this.reportingManagerRepoRM.findAllByManagerId(empId);
            Map<Object, Object> list = new HashMap<>();
            for (ReportingManager_RM employee : reportingManagerRm) {
                if (employee.getEndDate() == null || employee.getEndDate().equals(currentDate) || employee.getEndDate().after(currentDate)) {
                    String status = "Pending";
                    boolean resignedEmployee = this.resignedRepo.existsByempId(employee.getEmpId());
                    List<LeavesBeta> leaves = this.leavesBetaRepo.findAllByEmpIdAndStatusIgnoreCase(employee.getEmpId(), status);
                    if ((!resignedEmployee) && (!leaves.isEmpty())) {
                        leaves.get(0).setCount(leaves.size());
                        list.put(leaves.get(0).getLeaveId(), leaves);
                    }
                }
            }
            LOGGER.info("List of Pending Leave Applications : {} ", list);
            if (!list.isEmpty()) {
                map.put(ConstantValues.RESULT, list);
                map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
                map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
                map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
            } else {
                map.put(ConstantValues.RESULT, list);
                map.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
                map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
                map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.error("Exception :  ", e);
            throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
        }
        return map;
    }

    @Override
    public Map<String, Object> getAllEmployeesPendingLeaves() {
        try {
            String status = "Pending";
            List<LeavesBeta> allLeaves = this.leavesBetaRepo.findAllByStatusIgnoreCase(status);
            Map<Object, Object> list = new HashMap<>();
            for (LeavesBeta empLeaves : allLeaves) {
                List<LeavesBeta> leavesBetas = this.leavesBetaRepo.findAllByLeaveId(empLeaves.getLeaveId());
                leavesBetas.get(0).setCount(leavesBetas.size());
                list.put(leavesBetas.get(0).getLeaveId(), leavesBetas);
            }
            LOGGER.info("List of Pending Leave Applications : {} ", allLeaves);
            if (!allLeaves.isEmpty()) {
                map.put(ConstantValues.RESULT, list);
                map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
                map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
                map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
            } else {
                map.put(ConstantValues.RESULT, list);
                map.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
                map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
                map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred : ", e);
            throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
        }
        return map;
    }

    @Override
    @Transactional(rollbackOn = Exception.class)
    public Map<String, Object> approveLeaves(long leaveId, String status, String comments) {
        try {
            final Long empId = SecurityUtil.getCurrentUserDetails().getEmpId();
            Date currentTime = new Date();
            System.out.println(leaveId);
            LeavesBeta leavesBeta = this.leavesBetaRepo.findById(leaveId).orElseThrow();
            List<LeavesBeta> empLeaves = this.leavesBetaRepo.findAllByLeaveId(leavesBeta.getLeaveId());
            List<LeavesBeta> saveLeave = new ArrayList<>();
            for (LeavesBeta leaves : empLeaves) {
                leaves.setStatus(status);
                leaves.setComments(comments);
                leaves.setModifiedBy(empId);
                saveLeave.add(leaves);
            }
            MimeMessage mail = javaMailSender.createMimeMessage();
            MimeMessageHelper message = new MimeMessageHelper(mail, true);
            Employee employee = this.employeeRepo.findById(leavesBeta.getEmpId()).orElseThrow();
            List<ReportingManager_RM> rm = this.reportingManagerRepoRM.findAllByEmpId(employee.getEmpId());
            Employee manager = null;
            for (ReportingManager_RM managerRm : rm) {
                if (managerRm.getEndDate() == null || currentTime.before(managerRm.getEndDate()) || currentTime.equals(managerRm.getEndDate())) {
                    manager = this.employeeRepo.findById(managerRm.getManagerId()).orElseThrow();
                }
            }
           // assert manager != null;
            String managerMailId;
            String managerName;
            if (manager!=null && manager.getEmailId() != null){
                managerMailId = manager.getEmailId();
                managerName=manager.getEmpName();
            }
            else {
                managerMailId = HR_TEAM_MAIL_ID;
                managerName=HR_TEAM_MAIL_ID;
            }
            String[] leavesCc = new String[]{ConstantValues.LEAVES_MAIL_ID,managerMailId};//Leaves@eidiko.com and reporting manager
            System.out.println(Arrays.toString(leavesCc));
            LeaveTypesBeta leaves = this.leaveTypesBeta.findByLeaveCode(leavesBeta.getLeaveType());

            String formattedTimestamp = leavesBeta.getCreatedDate().toLocalDateTime().format(DateTimeFormatter.ofPattern("yyyy MMM dd 'at' HH:mm"));

            String fullFileName = folderLocation + ConstantValues.TEMPLATES + ConstantValues.APPLY_LEAVE_MAIL_TXT;
            File file = ResourceUtils.getFile(fullFileName);
            String s = new String(Files.readAllBytes(file.toPath()));

            String htmlTemplate = this.cautionMailService.readingMailTemplateFromText(ConstantValues.APPROVE_LEAVE_MAIL_TXT);

            assert manager != null;
            String processedTemplate = htmlTemplate.replace("@status", status)
                    .replace("@comments", comments)
                    .replace("@managerName", managerName).replace("@leaveDate", formattedTimestamp)
                    .replace("@to", employee.getEmailId()).replace("@applyLeaveTemplate", s)
                    .replace("@empName", employee.getEmpName()).replace("@contact", employee.getContactNo())
                    .replace("@reason", leavesBeta.getReason());
            message.setTo(employee.getEmailId());
            //message.setCc(leavesCc);
            message.setCc("nikhil.korukoppula@eidiko-india.com");
            message.setSubject("Re: " + leaves.getLeaveName());
            message.setText(processedTemplate, true);
            leavesBetaRepo.saveAll(saveLeave);
            LOGGER.info("Employee leave application status : {} ", leavesBeta);
            for (LeavesBeta beta : saveLeave) {
                String payload = "{leavesBetaId :" + beta.getLeavesBetaId() + " ,empId :" + beta.getEmpId()
                        + " ,approved by :" +empId + " ,managerName :" + managerName + " ,leaveType :" + beta.getLeaveType() + "}";
                this.updateAuditDetails(payload, beta.getEmpId(), status + " employee leave application with id : " + beta.getLeavesBetaId(), status);
            }
            javaMailSender.send(mail);
            LOGGER.info("Mail sent successfully : {} ", leaves.getLeaveName());
            map.put(ConstantValues.MESSAGE, ConstantValues.MAIL_SENT);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
        } catch (Exception e) {
            LOGGER.error("Exception occurred : ", e);
            throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
        }
        return map;
    }

    @Override
    public Map<String, Object> getEmpLeaveReportToManager(int month, int year, String status) {

        LocalDate fromDate;
        LocalDate toDate;
        if (month == 0) {
            fromDate = LocalDate.of(year - 1, 12, 26);
            toDate = LocalDate.of(year, month + 1, 25);
        } else if (month == 12) {  //12 equal to all months
            fromDate = LocalDate.of(year - 1, month, 26);
            toDate = LocalDate.of(year, month, 25);
        } else {
            fromDate = LocalDate.of(year, month, 26);
            toDate = LocalDate.of(year, month + 1, 25);
        }
        try {
            Date currentTime = new Date();
            Long manager = SecurityUtil.getCurrentUserDetails().getEmpId();
            Set<ReportingManager_RM> managerRm = getReportingManagerHierarchy(manager, new HashSet<>());
            // List<ReportingManager_RM> managerRm=this.reportingManagerRepoRM.findAllByManagerId(manager);
            Set<Map<String, Object>> set = new HashSet<>();
            if (!managerRm.isEmpty()) {
                for (ReportingManager_RM rm : managerRm) {
                    if (rm.getEndDate() == null || rm.getEndDate().after(currentTime) ||
                            rm.getEndDate().equals(currentTime)) {
                        boolean resignedEmployee = this.resignedRepo.existsByempId(rm.getEmpId());
                        List<LeavesBeta> leavesBeta;
                        if (status.equalsIgnoreCase("All")) {
                            leavesBeta = this.leavesBetaRepo.findByEmpIdAndLeaveDateBetween(rm.getEmpId(), fromDate, toDate);
                        } else {
                            leavesBeta = this.leavesBetaRepo.findByEmpIdAndLeaveDateBetweenAndStatusIgnoreCase(rm.getEmpId(), fromDate, toDate, status);
                        }
                        if ((!leavesBeta.isEmpty() && !resignedEmployee)) {
                            for (LeavesBeta ll : leavesBeta) {
                                Map<String, Object> map = new HashMap<>();
                                List<LeavesBeta> leavesData = this.leavesBetaRepo.findAllByLeaveId(ll.getLeaveId());
                                leavesData.get(0).setCount(leavesData.size());
                                // list.put(leavesData.get(0).getLeaveId(),leavesData);
                                map.put("leaveId", leavesData.get(0).getLeaveId());
                                map.put("leaveData", leavesData);
                                set.add(map);
                            }
                        }
                    }
                }
                LOGGER.info("All Reporting Employees leaves between given month and year: {} ", set);
                if (!set.isEmpty()) {
                    map.put(ConstantValues.RESULT, set);
                    map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
                    map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
                    map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
                } else {
                    map.put(ConstantValues.RESULT, set);
                    map.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
                    map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
                    map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
                }
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred :", e);
            throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
        }
        return map;
    }

    private Set<ReportingManager_RM> getReportingManagerHierarchy(long managerId, Set<ReportingManager_RM> result) {
        List<ReportingManager_RM> findAllByManagerId = this.reportingManagerRepoRM.findAllByManagerId(managerId);

        for (ReportingManager_RM r : findAllByManagerId) {
            if (managerId == r.getEmpId()) { //730
                continue;
            }
            if (result.contains(r))
                continue;

            result.add(r);
            getReportingManagerHierarchy(r.getEmpId(), result);
        }
        return result;
    }

    @Override
    public Map<String, Object> getAllEmpReport(int month, int year, String status) {

        LocalDate fromDate;
        LocalDate toDate;
        if (month == 0) {
            fromDate = LocalDate.of(year - 1, 12, 26);
            toDate = LocalDate.of(year, month + 1, 25);
        } else if (month == 12) {  //12 equal to all months
            fromDate = LocalDate.of(year - 1, month, 26);
            toDate = LocalDate.of(year, month, 25);
        } else {
            fromDate = LocalDate.of(year, month, 26);
            toDate = LocalDate.of(year, month + 1, 25);
        }
        try {
            List<LeavesBeta> leavesBeta;
            if (status.equalsIgnoreCase("All")) {
                leavesBeta = this.leavesBetaRepo.findAllByLeaveDateBetween(fromDate, toDate);
            } else {
                leavesBeta = this.leavesBetaRepo.findAllByLeaveDateBetweenAndStatusIgnoreCase(fromDate, toDate, status);
            }
            Set<Map<String, Object>> set = new HashSet<>();
            for (LeavesBeta leaves : leavesBeta) {
                Map<String, Object> map = new HashMap<>();
                List<LeavesBeta> allEmpList = this.leavesBetaRepo.findAllByLeaveId(leaves.getLeaveId());
                allEmpList.get(0).setCount(allEmpList.size());
                map.put("leaveId", allEmpList.get(0).getLeaveId());
                map.put("leaveData", allEmpList);
                set.add(map);
            }
            LOGGER.info("All Employees leaves between given month and year : {} ", set);
            if (!set.isEmpty()) {
                map.put(ConstantValues.RESULT, set);
                map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
                map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
                map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
            } else {
                map.put(ConstantValues.RESULT, set);
                map.put(ConstantValues.MESSAGE, ConstantValues.NO_DATA_FETCHED_SUCCESS_TEXT);
                map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
                map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
            }
        } catch (Exception e) {
            LOGGER.error("Exception occurred :",e);
            throw new ResourceNotProcessedException(ConstantValues.NOT_PROCESSED);
        }
        return map;
    }

    @Override
    public Map<String, Object> getAllYearlyEmpReport(long empId, int year) {
        LocalDate fromDate = LocalDate.of(year - 1, 12, 26);
        LocalDate toDate = LocalDate.of(year, 12, 25);
        List<LeavesBeta> leaves = this.leavesBetaRepo.findByEmpIdAndStatusAndLeaveDateBetween(empId, fromDate, toDate);
        List<EmployeeLeaveStatusLeaves> employeeLeaveStatusList = this.empLeavesRepo.findAllByEmpIdInThisYear(fromDate.toString(), toDate.toString(),empId);
        employeeLeaveStatusList.forEach(empLeaves->{

            boolean leaveDateMatched = false;

            for (LeavesBeta ll : leaves) {
                if (empLeaves.getLeaveDate().toLocalDate().equals(ll.getLeaveDate())) {
                    leaveDateMatched = true;
                    break; // Exit the inner loop once a match is found
                }
            }
            if (!leaveDateMatched) {
                LeavesBeta empLeavesCount=new LeavesBeta();
                empLeavesCount.setLeavesBetaId(empLeaves.getLeaveStatusId());
                empLeavesCount.setLeaveDate(empLeaves.getLeaveDate().toLocalDate());
                empLeavesCount.setEmpId(empLeaves.getEmpId());
                empLeavesCount.setLeaveType(String.valueOf(empLeaves.getEStatus()));
                empLeavesCount.setModifiedBy(empLeaves.getModifiedBy());
                leaves.add(empLeavesCount);
            }
        });
        map.put(ConstantValues.RESULT, leaves);
        map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
        map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
        map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
        return map;
    }

    @Override
    public Map<String,Object> reportingEmployeesReport(int year){
        LocalDate fromDate = LocalDate.of(year - 1, 12, 26);
        LocalDate toDate = LocalDate.of(year, 12, 25);
        char leaveStatus='L';
        try{
            Date currentTime = new Date();
            Long manager = SecurityUtil.getCurrentUserDetails().getEmpId();
            List<EmployeeLeaveStatusLeaves> listLeaves = this.empLeavesRepo.findAllByLeaveDateBetween(fromDate, toDate);

            List<LeavesAsPerBand> asPerBands = this.asPerBandRepo.findAllByYear(year);

            Set<ReportingManager_RM> managerRm = getReportingManagerHierarchy(manager, new HashSet<>());
            Set<LeaveStatusResponseDto> responseList = new HashSet<>();
            if (!managerRm.isEmpty()) {
                for (ReportingManager_RM rm : managerRm) {
                    if (rm.getEndDate() == null || rm.getEndDate().after(currentTime) ||
                            rm.getEndDate().equals(currentTime)) {
                        boolean resignedEmployee = this.resignedRepo.existsByempId(rm.getEmpId());
                        if ( !resignedEmployee) {
                            List<LeavesBeta> leavesBeta = this.leavesBetaRepo.reportingEmpLeavesReport(rm.getEmpId(),fromDate, toDate);
                            Employee emp=this.employeeRepo.findById(rm.getEmpId()).orElseThrow();
                            LeaveStatusResponseDto responseDto = new LeaveStatusResponseDto();
                            responseDto.setEmpId(emp.getEmpId());
                            responseDto.setEmpName(emp.getEmpName());

                            asPerBands.forEach(band -> {
                                if (rm.getEmpId() == band.getEmpId()) {
                                    responseDto.setBand(band.getBand());
                                    responseDto.setTotalLeavesAsPerband(band.getLeaves());
                                }
                            });
                            Set<LocalDate> set = new HashSet<>();
                            for (EmployeeLeaveStatusLeaves leaveDate : listLeaves) {
                                if (emp.getEmpId() == leaveDate.getEmpId() && leaveDate.getEStatus() == leaveStatus) {
                                    set.add(leaveDate.getLeaveDate().toLocalDate());
                                }
                            }
                            for (LeavesBeta ll : leavesBeta) {
                                if (emp.getEmpId() == ll.getEmpId()) {
                                    set.add(ll.getLeaveDate());
                                }
                            }
                            responseDto.setLeavesCount(set.size());
                            responseList.add(responseDto);
                        }
                    }
                }
            }
            map.put(ConstantValues.RESULT, responseList);
            map.put(ConstantValues.MESSAGE, ConstantValues.DATA_FETCHED_SUCCESS_TEXT);
            map.put(ConstantValues.STATUS_TEXT, HttpStatus.OK.value());
            map.put(ConstantValues.STATUS_CODE, ConstantValues.SUCCESS_MESSAGE);
        }
        catch (AuthenticationException ex) {
            throw new RuntimeException(ex);
        }
        return map;
    }
}

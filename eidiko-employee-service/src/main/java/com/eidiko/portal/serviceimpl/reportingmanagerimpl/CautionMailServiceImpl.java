package com.eidiko.portal.serviceimpl.reportingmanagerimpl;

import com.eidiko.portal.dto.employee.CautionDto;
import com.eidiko.portal.dto.reportingmanager.CautionMailResponseDto;
import com.eidiko.portal.entities.employee.ResignedEmployee;
import com.eidiko.portal.entities.reportingmanager.CautionMail;
import com.eidiko.portal.entities.reportingmanager.EmployeeReportingManager;
import com.eidiko.portal.entities.reportingmanager.interfaces.CautionMailProjection;
import com.eidiko.portal.exception.reportingmanager.DataNotFoundException;
import com.eidiko.portal.exception.reportingmanager.MailNotSentException;
import com.eidiko.portal.exception.reportingmanager.UserNotFoundException;
import com.eidiko.portal.repo.employee.ResignedEmpRepo;
import com.eidiko.portal.repo.reportingmanager.CautionMailRepo;
import com.eidiko.portal.repo.reportingmanager.EmployeeRepoReportingManager;
import com.eidiko.portal.service.reportingmanager.CautionMailService;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CautionMailServiceImpl implements CautionMailService {
	@Autowired
	private EmployeeRepoReportingManager employeeRepo;
	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private ResourceLoader resourceLoader;

	@Value("${spring.mail.username}")
	private String sender;
	@Autowired
	private CautionMailRepo cautionMailRepo;
	
	@Autowired
	private ResignedEmpRepo resignedEmpRepo;

	private static final String HR_TEAM_MAIL_ID = "hrteam@eidiko.com";
	
	@Value("${files.storage}")
	public String folderLocation;

	public String readingMailTemplateFromText(String fileName) throws Exception {

		try {
			String fullFileName = folderLocation+"templates/" + fileName;
			File file = ResourceUtils.getFile(fullFileName);
			return new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			e.printStackTrace();

			throw new Exception("Mail not sent");
		}
	}


	@Override
	public Map<String, Object> cautionMailWithAttchments(long empId1, CautionDto cautionDto) throws Exception {
		this.employeeRepo.findById(empId1)
				.orElseThrow(() -> new UserNotFoundException("User not found with the given ID " + empId1));
		EmployeeReportingManager employee = this.employeeRepo.findById(cautionDto.getEmpId())
				.orElseThrow(() -> new UserNotFoundException("User not found with the empId " + cautionDto.getEmpId()));

		MimeMessage mail = javaMailSender.createMimeMessage();
		MimeMessageHelper message = new MimeMessageHelper(mail, true);

		String to = employee.getEmailId();
		String subject = cautionDto.getTitle();
		String text = cautionDto.getDescription();
		String name = employee.getEmpName();

		String htmlTemplate = readingMailTemplateFromText("warningMail.html");
		String processedTemplate = htmlTemplate.replace("{{DESCRIPTION}}", text).replace("@empName", name);

		message.setFrom(sender);
		message.setTo(to);
		message.setCc(HR_TEAM_MAIL_ID);
		message.setSubject(subject);
		message.setText(processedTemplate, true);

		if (cautionDto.getAttachments()!= null && !cautionDto.getAttachments().isEmpty()) {
			for (MultipartFile attachment : cautionDto.getAttachments()) {
				if (attachment != null && !attachment.isEmpty()) {
					message.addAttachment(attachment.getOriginalFilename(), new ByteArrayResource(attachment.getBytes()));
					System.out.println(attachment);
				}
			}
		}
		//message.setText(text);


		javaMailSender.send(mail);
		CautionMail cautionMail=new CautionMail();
		cautionMail.setEmpId(cautionDto.getEmpId());
		cautionMail.setTitle(cautionDto.getTitle());
		cautionMail.setDescription(cautionDto.getDescription());
		cautionMail.setWarningLevel(cautionDto.getWarningLevel());
		cautionMail.setWarningDate(cautionDto.getWarningDate());
		cautionMail.setSentDate(new Timestamp(System.currentTimeMillis()));
		cautionMail.setWarnedBy(empId1);
		cautionMail.setSeverityLevel(cautionDto.getSeverityLevel());
		this.cautionMailRepo.save(cautionMail);

		Map<String, Object> map = new HashMap<>();
		map.put("message", "Mail sent successfully");
		map.put("status", HttpStatus.OK.value());
		map.put("statusMessage", "Success");
		System.out.println("sent ggggggggggggggggg");
		return map;
	}

	@Override
	public Map<String, Object> cautionMail(long empId1, CautionDto cautionDto) throws Exception {
		this.employeeRepo.findById(empId1)
				.orElseThrow(() -> new UserNotFoundException("User not found with the given ID " + empId1));
		EmployeeReportingManager employee = this.employeeRepo.findById(cautionDto.getEmpId())
				.orElseThrow(() -> new UserNotFoundException("User not found with the empId " + cautionDto.getEmpId()));

		MimeMessage mail = javaMailSender.createMimeMessage();
		MimeMessageHelper message = new MimeMessageHelper(mail, true);

		String to = employee.getEmailId();
		String subject = cautionDto.getTitle();
		String text = cautionDto.getDescription();
		String name = employee.getEmpName();

		String htmlTemplate = readingMailTemplateFromText("warningMail.html");
		String processedTemplate = htmlTemplate.replace("{{DESCRIPTION}}", text).replace("@empName", name);

		message.setFrom(sender);
		message.setTo(to);
		message.setCc(HR_TEAM_MAIL_ID);
		message.setSubject(subject);
		message.setText(processedTemplate, true);


		try {
			javaMailSender.send(mail);
			CautionMail cautionMail=new CautionMail();
			cautionMail.setEmpId(cautionDto.getEmpId());
			cautionMail.setTitle(cautionDto.getTitle());
			cautionMail.setDescription(cautionDto.getDescription());
			cautionMail.setWarningDate(cautionDto.getWarningDate());
			cautionMail.setSentDate(new Timestamp(System.currentTimeMillis()));
			cautionMail.setWarnedBy(empId1);
			cautionMail.setWarningLevel(cautionDto.getWarningLevel());
			cautionMail.setSeverityLevel(cautionDto.getSeverityLevel());
			this.cautionMailRepo.save(cautionMail);
		} catch (Exception e) {
			throw new MailNotSentException("Mail not sent");
		}

		Map<String, Object> map = new HashMap<>();
		map.put("message", "Mail sent successfully");
		map.put("status", HttpStatus.OK.value());
		map.put("statusMessage", "Success");

		return map;

	}


	@Override
	public Map<String, Object> getDescriptionsByEmpId(long empId, Pageable pageable) throws DataNotFoundException {
		Page<CautionMail> list = cautionMailRepo.findAllByEmpId(empId,pageable);
		List<CautionMailResponseDto> list1=new ArrayList<>();
		for(CautionMail cm:list) {
			CautionMailResponseDto cmd=new CautionMailResponseDto();
			cmd.setEmpId(cm.getEmpId());
			cmd.setDescription(cm.getDescription());
			cmd.setSentDate(cm.getSentDate());
			cmd.setWarnedBy(cm.getWarnedBy());
			cmd.setTitle(cm.getTitle());
			cmd.setWarningMailId(cm.getWarningMailId());
			cmd.setEmpName(this.employeeRepo.findById(empId).get().getEmpName());
			cmd.setWarnedByName(this.employeeRepo.findById(cm.getWarnedBy()).get().getEmpName());
			cmd.setWarningDate(cm.getWarningDate());
			cmd.setWarningLevel(cm.getWarningLevel());
			cmd.setSeverityLevel(cm.getSeverityLevel());
			list1.add(cmd);
		}
		Map<String, Object> map = new HashMap<>();
		if (list1!= null) {
			map.put("message", "success");
			map.put("result", list1);
			map.put("status", HttpStatus.OK.value());
		} else {
			map.put("message", "success");
			map.put("result", list);
			map.put("status", HttpStatus.OK.value());
		}
		map.put("current page", list.getNumber());
		map.put("total items", list.getTotalElements());
		map.put("total pages", list.getTotalPages());
		return map;
	}

	@Override
	public Map<String, Object> getDataByMonthAndYear(int year, int month, Pageable pageable) {
		LocalDate startOfMonth = LocalDate.of(year, month, 1);
		LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());
		LocalDateTime startDateTime = LocalDateTime.of(startOfMonth, LocalTime.MIN);
		LocalDateTime endDateTime = LocalDateTime.of(endOfMonth, LocalTime.MAX);
		Page<CautionMail> list = this.cautionMailRepo.findBySentDateBetween(startDateTime, endDateTime,pageable);
		List<CautionMailResponseDto> list1=new ArrayList<>();
		for(CautionMail cm:list) {
			CautionMailResponseDto cmd=new CautionMailResponseDto();
			cmd.setEmpId(cm.getEmpId());
			cmd.setDescription(cm.getDescription());
			cmd.setSentDate(cm.getSentDate());
			cmd.setWarnedBy(cm.getWarnedBy());
			cmd.setTitle(cm.getTitle());
			cmd.setWarningMailId(cm.getWarningMailId());
			cmd.setEmpName(this.employeeRepo.findById(cm.getEmpId()).get().getEmpName());
			cmd.setWarnedByName(this.employeeRepo.findById(cm.getWarnedBy()).get().getEmpName());
			cmd.setWarningDate(cm.getWarningDate());
			cmd.setWarningLevel(cm.getWarningLevel());
			cmd.setSeverityLevel(cm.getSeverityLevel());
			list1.add(cmd);
		}
		Map<String, Object> map = new HashMap<>();
		if (list1!= null) {
			map.put("message", "success");
			map.put("result", list1);
			map.put("status", HttpStatus.OK.value());
		} else {
			map.put("message", "success");
			map.put("result", list);
			map.put("status", HttpStatus.OK.value());
		}
		map.put("current page", list.getNumber());
		map.put("total items", list.getTotalElements());
		map.put("total pages", list.getTotalPages());
		return map;
	}

	@Override
	public Map<String, Object> findBySentDate(int year, Pageable pageable) {
		Page<CautionMail> list = cautionMailRepo.findByYear(year,pageable);
		List<CautionMailResponseDto> list1=new ArrayList<>();
		for(CautionMail cm:list) {
			CautionMailResponseDto cmd=new CautionMailResponseDto();
			cmd.setEmpId(cm.getEmpId());
			cmd.setDescription(cm.getDescription());
			cmd.setSentDate(cm.getSentDate());
			cmd.setWarnedBy(cm.getWarnedBy());
			cmd.setTitle(cm.getTitle());
			cmd.setWarningMailId(cm.getWarningMailId());
			cmd.setEmpName(this.employeeRepo.findById(cm.getEmpId()).get().getEmpName());
			cmd.setWarnedByName(this.employeeRepo.findById(cm.getWarnedBy()).get().getEmpName());
			cmd.setWarningDate(cm.getWarningDate());
			cmd.setWarningLevel(cm.getWarningLevel());
			cmd.setSeverityLevel(cm.getSeverityLevel());
			list1.add(cmd);
		}
		Map<String, Object> map = new HashMap<>();
		if (list1!= null) {
			map.put("message", "success");
			map.put("result", list1);
			map.put("status", HttpStatus.OK.value());
		} else {
			map.put("message", "success");
			map.put("result", list);
			map.put("status", HttpStatus.OK.value());
		}
		map.put("current page", list.getNumber());
		map.put("total items", list.getTotalElements());
		map.put("total pages", list.getTotalPages());
		return map;
	}


	@Override
	public Map<String, Object> getCautionReportByDates(LocalDate fromDate,LocalDate toDate) {
		
		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
		        .map(ResignedEmployee::getEmpId)
		        .collect(Collectors.toSet());
		
		List<CautionMailProjection> filteredCautionReports = new ArrayList<>();
		
		LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN).truncatedTo(ChronoUnit.SECONDS);
		LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX).truncatedTo(ChronoUnit.SECONDS);



		List<CautionMailProjection> cautionReportsBetweenDates = cautionMailRepo.findCautionReportsBetweenDates(fromDateTime, toDateTime);
		
		for (CautionMailProjection cautionMailProjection : cautionReportsBetweenDates) {
			System.out.println("!!!!!!!!!!!"+cautionMailProjection.getemp_id());
			Long getemp_id = cautionMailProjection.getemp_id();
			if(resignedEmpIds.contains(getemp_id)){
				System.out.println("INNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNNN");
//				 System.out.println("Saving: " + cautionMailProjection.getemp_id());
//			      filteredCautionReports.add(cautionMailProjection);
				continue;
			}
			System.out.println("Saving: " + cautionMailProjection.getemp_id());
			filteredCautionReports.add(cautionMailProjection);
			System.out.println("MMMNNNMMMMMM"+cautionMailProjection.getemp_id());
			
			
		}
		
		
		
		
		Map<String, Object> map = new HashMap<>();
		if (cautionReportsBetweenDates != null) {
			map.put("message", "success");
			map.put("result", filteredCautionReports);
			map.put("status", HttpStatus.OK.value());
		} else {
			map.put("message", "success");
			map.put("result", filteredCautionReports);
			map.put("status", HttpStatus.OK.value());
		}
		return map;
	}

	@Override
	public Map<String, Object> getCautionReportByDatesAndEmpId(LocalDate fromDate, LocalDate toDate,long empId) {
		LocalDateTime fromDateTime = fromDate.atTime(LocalTime.MIN).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime toDateTime = toDate.atTime(LocalTime.MAX).truncatedTo(ChronoUnit.SECONDS);
        
        CautionMailProjection cautionReportsBetweenDatesByEmpId = cautionMailRepo.findCautionReportsBetweenDatesByEmpId(fromDateTime, toDateTime, empId);
        Map<String, Object> map = new HashMap<>();
		if (cautionReportsBetweenDatesByEmpId != null) {
			map.put("message", "success");
			map.put("result", cautionReportsBetweenDatesByEmpId);
			map.put("status", HttpStatus.OK.value());
		} else {
			map.put("message", "success");
			map.put("result", cautionReportsBetweenDatesByEmpId);
			map.put("status", HttpStatus.OK.value());
		}
		return map;
	}

}
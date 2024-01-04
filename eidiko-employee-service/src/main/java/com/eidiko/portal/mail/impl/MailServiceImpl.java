package com.eidiko.portal.mail.impl;

import com.eidiko.portal.dto.employee.EmailDetailsDto;
import com.eidiko.portal.entities.employee.ResignedEmployee;
import com.eidiko.portal.entities.taskstatus.EmpLowRatingAudit;
import com.eidiko.portal.entities.taskstatus.EmpReviewRating;
import com.eidiko.portal.entities.taskstatus.EmpSkillsTracking;
import com.eidiko.portal.entities.taskstatus.EmployeeStatusReport;
import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import com.eidiko.portal.helper.employee.ConstantValues;
import com.eidiko.portal.mail.MailService;
import com.eidiko.portal.repo.employee.ResignedEmpRepo;
import com.eidiko.portal.repo.taskstatus.EmpLowRatingRepository;
import com.eidiko.portal.repo.taskstatus.EmpSkillsTrackingRepository;
import com.eidiko.portal.repo.taskstatus.EmployeeRepository;
import com.eidiko.portal.repo.taskstatus.RatingRepository;
import com.eidiko.portal.serviceimpl.reportingmanagerimpl.CautionMailServiceImpl;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MailServiceImpl implements MailService {

	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	private CautionMailServiceImpl cautionMailService;
	@Autowired
	private EmpSkillsTrackingRepository skillsTrackingRepository;
	@Autowired
	private EmployeeRepository employeeRepository;
	@Autowired
	private ResignedEmpRepo resignedEmpRepo;
	@Autowired
	private RatingRepository ratingRepository;
 	@Autowired
    private EmpLowRatingRepository lowRatingRepository;
	@Value("${spring.mail.username}")
	private String sender;
	private final Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public boolean sendSimpleMail(EmailDetailsDto details) {
		try {
			// Creating a simple mail message
			MimeMessage message = javaMailSender.createMimeMessage();
			// Setting up necessary details

			message.setFrom(sender);
			message.setRecipients(MimeMessage.RecipientType.TO, details.getRecipient());
			message.setSubject(details.getSubject());

			message.setContent(details.getMsgBody(), "text/html; charset=utf-8");
			javaMailSender.send(message);
			this.logger.info("<<<<<<<<<<<<<<<<<<<" + ConstantValues.MAIL_SENT + ">>>>>>>>>>>>>>>>>>>>");
			return true;
		} catch (MessagingException | MailException e) {
			throw new ResourceNotProcessedException(e.getMessage());
		}
	}

	private List<Long> getMissingRatingEmployees() {
		int month = LocalDate.now().getMonth().getValue()-2;
		String working = "Training";
		List<EmpSkillsTracking> empSkillsTracking = this.skillsTrackingRepository.findAllByWorkingIgnoreCase(working);
		List<Long> empIds = new ArrayList<>();
		Date date = new Date(System.currentTimeMillis());
		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
				.map(ResignedEmployee::getEmpId)
				.collect(Collectors.toSet());
		for (EmpSkillsTracking est : empSkillsTracking) {
			if (date.after(est.getStartDate()) &&
					(est.getEndDate() == null || date.before(est.getEndDate())
							|| date.equals(est.getEndDate()))) {
				List<EmpReviewRating> ratings = this.ratingRepository.findAllByEmpIdAndMonth(est.getEmpId(), month);
				if (ratings.isEmpty()) {
					if (resignedEmpIds.contains(est.getEmpId())) continue; // Skip processing resigned employees
					empIds.add(est.getEmpId());
				}
			}
		}
		return empIds;
	}

	@Override
	@Scheduled(cron = "0 0 10 11 * ?") // Run on the 10th day of every month
	public void sendEmailToRatingMissingEmployees() throws MessagingException {
		List<EmployeeStatusReport> employee = this.employeeRepository.findAllById(getMissingRatingEmployees());
		try {
			MimeMessage mail = javaMailSender.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mail, true);
			String currentMonth = LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM yyyy"));
			String previousMonth = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MMMM yyyy"));
			for(EmployeeStatusReport report : employee ) {
				String to = report.getEmailId();
				String subject = "No Monthly Review Conducted";
				String name = report.getEmpName();
				String htmlTemplate = cautionMailService.readingMailTemplateFromText("rating-missing-mail.txt");
				String processedTemplate = htmlTemplate.replace("@previousMonth", previousMonth).replace("@empName", name).replace("@currentMonth",currentMonth);
				message.setFrom(sender);
				message.setTo(to);
				//message.setCc("sindhuja.a@eidiko.com");
				message.setSubject(subject);
				message.setText(processedTemplate, true);
				javaMailSender.send(mail);
				logger.info("mail sent successfully: {}",report.getEmailId());
			}
		}catch (Exception e) {
			logger.error("An error occurred ",e);
		}

	}

	private List<Long> getLowRatingEmployees() {
		List<Long> list = new ArrayList<>();
		String workingValue = "Training";
		List<EmpSkillsTracking> empSkillsTrackings = this.skillsTrackingRepository.findAllByWorkingIgnoreCase(workingValue);
		java.sql.Date date = new Date(System.currentTimeMillis());
		List<ResignedEmployee> resgnEmpList = resignedEmpRepo.findAll();
		Set<Long> resignedEmpIds = resgnEmpList.stream()
				.map(ResignedEmployee::getEmpId)
				.collect(Collectors.toSet());
		for (EmpSkillsTracking skillsTracking : empSkillsTrackings) {
			if (date.after(skillsTracking.getStartDate()) &&
					(skillsTracking.getEndDate() == null || date.before(skillsTracking.getEndDate())
							|| date.equals(skillsTracking.getEndDate()))) {
				EmpReviewRating rating = this.ratingRepository.findByEmpIdAndMonth(skillsTracking.getEmpId(),date.getMonth()-1);
				if (rating!=null && (rating.getTechnicalRating() < 2.5 || rating.getCommunicationRating() < 2.5)) {
					if (resignedEmpIds.contains(skillsTracking.getEmpId())) {
						continue; // Skip processing resigned employees
					}
					list.add(rating.getEmpId());
				}
			}
		}
		return list;
	}

	@Override
	@Scheduled(cron = "0 0 19 1-10 * ?") // Run on 1 - 10 days of every month at 7 pm
	public void sendEmailToLowRatingEmployees() throws MessagingException {
		List<EmployeeStatusReport> employee = this.employeeRepository.findAllById(getLowRatingEmployees());
		int currentMonth = LocalDate.now().getMonth().getValue()-1;
		try {
			MimeMessage mail = javaMailSender.createMimeMessage();
			MimeMessageHelper message = new MimeMessageHelper(mail, true);
			String previousMonth = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MMMM yyyy"));
			for (EmployeeStatusReport report:employee)
			{
				String to = report.getEmailId();
				String subject = "Below 2.5 Monthly Rating";
				String name = report.getEmpName();
				String htmlTemplate = cautionMailService.readingMailTemplateFromText("below-2.5-rating-mail.txt");
				String processedTemplate = htmlTemplate.replace("@previousMonth", previousMonth).replace("@empName", name);
				message.setFrom(sender);
				message.setTo(to);
				//message.setCc("sindhuja.a@eidiko.com");
				message.setSubject(subject);
				message.setText(processedTemplate, true);
				EmpLowRatingAudit empLowRatingAudit = this.lowRatingRepository.findByEmpIdAndMonth(report.getEmpId(),currentMonth);
				if(empLowRatingAudit==null) {
					javaMailSender.send(mail);
				    logger.info("Mail sent successfully: {}",report.getEmailId());
					EmpLowRatingAudit audit = new EmpLowRatingAudit();
					audit.setMonth(currentMonth);
					audit.setEmpId(report.getEmpId());
					audit.setUpdatedOn(LocalDate.now());
					this.lowRatingRepository.save(audit);
					logger.info("low rating audit table updated: {}",audit);
				}
			}
		}catch (Exception e) {
			logger.error("An error occurred ",e);
		}
	}
}

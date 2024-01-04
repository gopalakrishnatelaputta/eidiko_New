package com.eidiko.portal.mail;

import com.eidiko.portal.dto.employee.EmailDetailsDto;
import jakarta.mail.MessagingException;

public interface MailService {
	boolean sendSimpleMail(EmailDetailsDto details);
	void sendEmailToRatingMissingEmployees() throws MessagingException;
	void sendEmailToLowRatingEmployees() throws MessagingException;
}

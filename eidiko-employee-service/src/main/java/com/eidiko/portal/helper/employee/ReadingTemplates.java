package com.eidiko.portal.helper.employee;

import com.eidiko.portal.exception.employee.ResourceNotProcessedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Service
public class ReadingTemplates {

	private Logger logger = LoggerFactory.getLogger(getClass());
	
	
	@Value("${files.storage}")
	public String folderLocation;
	
	
	private String readingMailTemplateFromText(String fileName) {

		try {
			String fullFileName = folderLocation+ConstantValues.DOCUMENTS_EIDIKO_PORTAL + fileName + ".txt";
			File file = ResourceUtils.getFile(fullFileName);
			return new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e) {
			logger.error(ConstantValues.EMAIL_TEMPLATE_MAIL_FILE_FOR_FORGOT_PASSWORD_NOT_FOUND);
			throw new ResourceNotProcessedException(ConstantValues.SOMETHING_WENT_WRONG_PLEASE_TRY_AGAIN_AFTER_SOMETIME);
		}
	}

	public String generateForgotPasswordMailtext(String fileName, String name, String password) {
		String mailText = readingMailTemplateFromText(fileName);
		mailText = mailText.replace(ConstantValues.EMP_NAME, name);
		mailText = mailText.replace(ConstantValues.PASSWORD_TEXT, password);
		
		return mailText;
	}

}

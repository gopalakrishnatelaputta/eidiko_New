package com.eidiko.portal.service.biometric;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface BiometricService {
	
	public Map<String, Object> uploadFile(MultipartFile file);

}

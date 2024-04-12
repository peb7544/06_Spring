package edu.kh.project.email.model.service;

import java.util.Map;

public interface EmailService {

	/** 이메일 보내기
	 * @param string
	 * @param email
	 * @return
	 */
	String sendEmail(String string, String email);

	/** 이메일 인증번호 보내기
	 * @param map
	 * @return
	 */
	int checkAuthKey(Map<String, Object> map);

}

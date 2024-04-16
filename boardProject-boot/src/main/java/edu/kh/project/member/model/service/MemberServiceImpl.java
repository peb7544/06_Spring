package edu.kh.project.member.model.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.kh.project.member.model.dto.Member;
import edu.kh.project.member.model.mapper.MemberMapper;
import lombok.extern.slf4j.Slf4j;

@Transactional(rollbackFor=Exception.class) // 해당 클래스 메서드 종료 시 까지
               // - 예외(RuntimeException)가 발생하지 않으면 commit
               // - 예외(RuntimeException)가 발생하면 rollback
@Service //비즈니스 로직 처리 역할 명시 + Bean등록
@Slf4j
public class MemberServiceImpl implements MemberService {
	
	// 등록된 bean 중에서 같은 타입 또는 상속관계인 bean을
	// 자동으로 의존성 주입(DI)
	@Autowired
	private MemberMapper mapper;

	// BCrypt 암호화 객체 의존성 주입(SecurityConfig 참고)
	@Autowired
	private BCryptPasswordEncoder bcrypt;
	
	// 로그인 서비스
	@Override
	public Member login(Member inputMember) {
		
		// 테스트
		
		// bcrypt.encode(문자열) : 문자열을 암호화하여 반환
		//String bcryptPassword = bcrypt.encode(inputMember.getMemberPw() /*평문 비밀번호*/);
		//log.debug("bcryptPassword : " + bcryptPassword);
		
		//boolean result = bcrypt.matches(inputMember.getMemberPw(), bcryptPassword);
		//log.debug("result : " + result);
		
		// 1. 이메일이 일치하면서 탈퇴하지 않은 회원 조회
		Member loginMember = mapper.login(inputMember.getMemberEmail());
		
		// 2. 만약에 일치하는 이메일이 없어서 조회 결과가 null인 경우
		if(loginMember == null) return null;
		
		// 3. 입력받은 비밀번호(inputMember.getMemberPw() 평문)와
		//    암호화된 비밀번호(loginMember.getMemberPw()) 
		//    두 비밀번호가 일치하는지 확인
		
		// 일치하지 않으면
		if(!bcrypt.matches(inputMember.getMemberPw(), loginMember.getMemberPw())) {
			
			return null;
		}
		
		// 로그인 결과에서 비민번호 제거
		loginMember.setMemberPw(null);
		
		return loginMember;
	}

	// 이메일 중복검사
	@Override
	public int checkEmail(String memberEmail) {
		// TODO Auto-generated method stub
		return mapper.checkEmail(memberEmail);
	}

	// 닉네임 중복검사
	@Override
	public int checkNickname(String memberNickname) {
		// TODO Auto-generated method stub
		return mapper.checkNickname(memberNickname);
	}

	// 회원가입 서비스
	@Override
	public int signup(Member inputMember, String[] memberAddress) {
		
		// 주소가 입력되지 않으면 
		// inputMember.getMemberAddress() -> ",,"
		// memberAddress -> [,,]
		
		// 주소가 입력된 경우
		if( !inputMember.getMemberAddress().equals(",,") ) {
			
			// String.join("구분자", 배열)
			// -> 배열의 모든 요소 사이에 "구분자"를 추가하여
			//    하나의 문자열로 만들어 반환하는 메서드
			
			// 구분자로 "^^^" 쓴 이유 :
			// -> 주소, 상세주소에 없는 특수문자 작성
			// -> 나중에 다시 3분할 할 때 구분자로 이용할 예정
			String address = String.join("^^^", memberAddress);
			
			// inputMember 주소로 합쳐진 주소를 세팅
			inputMember.setMemberAddress(address);
			
		} else { // 주소 입력 X
			
			inputMember.setMemberAddress(null); // null 저장
			
		}
		
		// 이메일, 비밀번호, 닉네임, 전화번호, 주소
		// 비밀번호를 암호화하여 inputMember에 세팅
		String encPw = bcrypt.encode(inputMember.getMemberPw());
		
		inputMember.setMemberPw(encPw);
		
		// 회원가입 매퍼 메서드 호출
		return mapper.signup(inputMember);
	}

	// 빠른 로그인
	@Override
	public Member quickLogin(String memberEmail) {
		
		Member loginMember = mapper.login(memberEmail);
		
		// 탈퇴 or 없는 회원
		if(loginMember == null) return null;
		
		// 조회된 비밀번호 null 변경
		loginMember.setMemberPw(null);
		
		return loginMember;
	}

	// 회원 목록
	@Override
	public List<Member> selectMemberList() {
		
		return mapper.selectMemberList();
	}

	// 특정 회원 비밀번호 초기화
	@Override
	public int resetPw(int inputNo) {
		
		// pass01! -> 암호화
		String encPw = bcrypt.encode("pass01!");
		
		Map<String, Object> map = new HashMap<>();
		map.put("inputNo", inputNo);
		map.put("encPw", encPw);
		
		return mapper.resetPw(map);
	}

	// 특정 회원(회원번호) 탈퇴 복구
	@Override
	public int restoration(int memberNo) {
		
		return mapper.restoration(memberNo);
	}

	
	
	
	/* BCrypt 암호화(Spring Security 제공)
	 * 
	 * - 입력된 문자열(비밀번호)에 salt를 추가한 후 암호화
	 * 
	 * ex) A 회원 : 1234 -> $12!asdfg
	 *     B 회원 : 1234 -> $12!dfhfd
	 *     
	 * - 비밀번호 확인 방법
	 *   -> BCryptPasswordEncoder.matches(평문 비밀번호, 암호화된 비밀번호)
	 *   -> 평문 비밀번호와 암호화된 비밀번호가 같은 경우 true 아니면 false 반환
	 * 
	 * * 로그인 / 비밀번호 변경 / 탈퇴 등 비밀번호가 입력되는 경우
	 *   DB에 저장된 암호화된 비밀번호를 조회해서
	 *   matches() 메서드로 비교해야 한다!
	 * 
	 * (참고)
	 * sha 방식 암호화
	 * ex) A 회원 : 1234 -> 암호화 : abcd
	 *     B 회원 : 1234 -> 암호화 : abcd
	 * (암호화 시 변경된 내용이 같음)
	 * 
	 * */

}

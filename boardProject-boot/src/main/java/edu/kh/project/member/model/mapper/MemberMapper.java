package edu.kh.project.member.model.mapper;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Mapper;

import edu.kh.project.member.model.dto.Member;

@Mapper
public interface MemberMapper {

	/** 로그인 SQL 실행
	 * @param memberEmail
	 * @return loginMember
	 */
	Member login(String memberEmail);

	/** 이메일 중복검사
	 * @param memberEmail
	 * @return count
	 */
	int checkEmail(String memberEmail);

	/** 닉네임 중복검사
	 * @param memberNickname
	 * @return count
	 */
	int checkNickname(String memberNickname);

	/** 회원가입 SQL 실행
	 * @param inputMember
	 * @return result
	 */
	int signup(Member inputMember);

	/** 회원 목록
	 * @return memberList
	 */
	List<Member> selectMemberList();

	/** 특정 회원 비밀번호 초기화
	 * @param member
	 * @return
	 */
	int resetPw(Map<String, Object> map);

	/** 특정 회원(회원번호) 탈퇴 복구
	 * @param memberNo
	 * @return
	 */
	int restoration(int memberNo);
	
}

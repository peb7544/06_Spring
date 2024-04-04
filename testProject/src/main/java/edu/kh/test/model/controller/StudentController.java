package edu.kh.test.model.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import edu.kh.test.dto.Student;
import jakarta.servlet.http.HttpServletRequest;

@Controller
public class StudentController {
	
	@PostMapping("student/select")
	public String selectStudent(HttpServletRequest req, @ModelAttribute Student student) {
		
		/*String name = req.getParameter("name");
		int age = Integer.parseInt(req.getParameter("age"));
		String addr = req.getParameter("addr");
		
		student = new Student(name, age, addr);*/
		
		
		req.setAttribute("stdName", student.getStdName());

		req.setAttribute("stdAge", student.getStdAge()); //  Integer.parseInt(student.getStdAge())

		req.setAttribute("stdAddress", student.getStdAddress());

		return "student/select";
	}
}

package com.spring.jpabasic.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Optional;

import org.hibernate.DuplicateMappingException;

import com.spring.jpabasic.entity.User;
import com.spring.jpabasic.service.UserService;
import com.spring.jpabasic.utils.EMFUtils;

public class UserMain {
	private static UserService userService = new UserService();
	public static void main (String[] args) throws IOException {
		EMFUtils.init();

		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));

		while (true) {
			System.out.println("명령어를 입력하세요");
			String line = bf.readLine();
			String[] commands = line.split(" ");
			if(commands[0].equalsIgnoreCase("exit")){
				System.out.println("종료합니다.");
				break;
			} else if(commands[0].equalsIgnoreCase("join")){
				handleJoinCommand(commands);
			} else if(commands[0].equalsIgnoreCase("view")){
				handleViewCommand(commands);
			} else if(commands[0].equalsIgnoreCase("list")){

			} else if(commands[0].equalsIgnoreCase("changeName")){

			} else if(commands[0].equalsIgnoreCase("withdraw")){

			} else {
				System.out.println("올바른 명령어를 입력하세요");
			}
			System.out.println("-------------------");
		}

		EMFUtils.close();
	}

	private static void handleViewCommand (String[] commands) {
		if(commands.length !=2) {
			System.out.println("명령어가 올바르지 않습니다.");
			System.out.println("사용법 view 이메일");
			return;
		}

		Optional<User> userOpt = userService.getUser(commands[1]);
		if(userOpt.isPresent()) {
			User user = userOpt.get();
			System.out.println("이름 : " + user.getName());
		} else {
			System.out.println("존재하지 않습니다.");
		}
	}

	private static void handleJoinCommand (String[] commands) {
		if(commands.length != 3) {
			System.out.println("명령어가 올바르지 않습니다.");
			System.out.println("사용법: Join 이메일 이름");
			return;
		}
		try {
			userService.join(new User(commands[1], commands[2], new Date()));
			System.out.println("가입 요청을 처리했습니다.");
		} catch (DuplicateMappingException e) {
			System.out.println("이미 같은 이메일을 가진 사용자가 존재합니다.");
		}
	}

}

package com.exam.examBbs.controller;

import com.exam.examBbs.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;
/*

    @PostMapping("/join")
        public ResponseEntity<String> join(@RequestBody MemberJoinRequest dto){
            memberService.join(dto.getName(), dto.getPassword(), dto.getEmail());
            return ResponseEntity.ok().body("회원가입 성공");
        }

    @PostMapping("/login")
        public ResponseEntity<String> login(@RequestBody MemberLoginRequest dto) {
            return ResponseEntity.ok().body(memberService.login(dto.getEmail(), dto.getPassword()));
        }*/

}

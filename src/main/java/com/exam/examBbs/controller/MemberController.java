package com.exam.examBbs.controller;

import com.exam.examBbs.domain.dto.MemberJoinRequest;
import com.exam.examBbs.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;

    @GetMapping
    public String test(){
        return "test!!";
    }

    @PostMapping("/join")
        public ResponseEntity<String> join(@RequestBody MemberJoinRequest dto){
            memberService.join(dto.getName(), dto.getPassword(), dto.getEmail());
            return ResponseEntity.ok().body("회원가입 성공");
        }

    @PostMapping("/login")
        public ResponseEntity<String> login() {
            return ResponseEntity.ok().body(memberService.login("",""));
        }

}
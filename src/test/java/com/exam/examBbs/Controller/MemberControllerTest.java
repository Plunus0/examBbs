package com.exam.examBbs.Controller;

import com.exam.examBbs.Service.MemberService;
import com.exam.examBbs.domain.dto.MemberJoinRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest

class MemberControllerTest {
    @Autowired
    MockMvc mockMvc;

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 성공")
    void join() throws Exception {
        String name = "회원가입 테스트1";
        String password = "1234";
        String email = "testemail@naver.com";

        mockMvc.perform(post("/api/v1/members/join")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(new MemberJoinRequest(name, password, email))))
                .andDo(print())
                .andExpect(status().isOk());


    }


    @Test
    @DisplayName("회원가입 실패 - email 중복")
    void join_fail() throws Exception {
        String name = "회원가입 테스트1";
        String password = "1234";
        String email = "testemail@naver.com";

        mockMvc.perform(post("/api/v1/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(new MemberJoinRequest(name, password, email))))
                .andDo(print())
                .andExpect(status().isConflict());


    }
}
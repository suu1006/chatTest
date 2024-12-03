package com.example.chattest.controller;

import com.example.chattest.domain.*;
import com.example.chattest.domain.MessageDTO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class ChatbotController { // {{#webhook.text}}

    @PostMapping(value = "/message", headers = {"Accept=application/json"})
    public MessageDTO message(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(params);
            System.out.println("jsonInString >> " + jsonInString);

            log.info("Received params: {}");
            MessageDTO messageDTO = new MessageDTO();
            messageDTO.setRole("user");
            messageDTO.setContent("안녕하세요");

            return messageDTO;
        } catch (Exception e) {
            log.error("Error occurred while processing request: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 응답
            return null;
        }
    }

    @PostMapping(value = "/button", headers = {"Accept=application/json"})
    public ButtonDTO button(@RequestBody Map<String, Object> params, HttpServletRequest request, HttpServletResponse response) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            String jsonInString = mapper.writeValueAsString(params);
            System.out.println("jsonInString >> " + jsonInString);

            log.info("Received params: {}");
            ButtonDTO buttonDTO = new ButtonDTO();
            buttonDTO.setType("buttons");
            String[] buttons = {"첫번째 버튼", "두번째 버튼", "세번째 버튼"};
            buttonDTO.setButtons(buttons);

            return buttonDTO;
        } catch (Exception e) {
            log.error("Error occurred while processing request: ", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // 500 응답
            return null;
        }
    }


    @RequestMapping(value = "/simpleText")
    public SimpleText simpleText() {
        SimpleText simpleText = new SimpleText("꾸꾸꾸꾸");
        return simpleText;
    }

    @RequestMapping(value = "/monday")
    public SimpleText monday() {
        SimpleText simpleText = new SimpleText("월요일이당");
        return simpleText;
    }

    // 카드 선택
    @RequestMapping(value = "/cardText")
    public TextCardDTO cardText() {
        TextCardDTO textCardDTO = new TextCardDTO();
        textCardDTO.setTitle("퇴근하고");
        textCardDTO.setDescription("어떤거할래");
        return textCardDTO;
    }





}

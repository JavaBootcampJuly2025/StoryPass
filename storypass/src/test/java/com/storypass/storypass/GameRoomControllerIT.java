package com.storypass.storypass;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.storypass.storypass.controller.GameRoomController;
import com.storypass.storypass.dto.CreateRoomRequest;
import com.storypass.storypass.dto.LoginRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = {StorypassApplication.class, GameRoomControllerIT.class})
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class GameRoomControllerIT
{
    @Autowired
    private MockMvc mvc;

    @Autowired
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public static void setup() {
        System.setProperty("spring.profiles.active", "test");
    }



    MockHttpServletRequestBuilder request;

    @Test
    public void shouldLogin() throws Exception{
        mvc = MockMvcBuilders.standaloneSetup(new GameRoomController(null)).build();

        LoginRequest loginReq = new LoginRequest("Wolf", "trash_talker");
        request = post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginReq));

        mvc.perform(request).andExpect(status().isOk());
    }


}

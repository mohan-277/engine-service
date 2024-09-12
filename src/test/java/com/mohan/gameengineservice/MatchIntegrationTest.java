package com.mohan.gameengineservice;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
public class MatchIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testCurrentScoreEndpoint() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/matches/1/score")) // Adjust the URL as needed
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("150/4 - 120/6")); // Expected result
    }
}

package com.seasun.management.controller;

import com.seasun.management.Application;
import org.apache.tools.ant.taskdefs.condition.IsTrue;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.client.match.MockRestRequestMatchers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.test.web.servlet.result.*;

import javax.servlet.http.Cookie;

import static com.seasun.management.constant.SessionAttribute.token;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(Application.class)
@WebAppConfiguration
@ActiveProfiles("local")
public class UserSalaryChangeServiceMokMvcTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Before
    public void setupMockMvc(){
        mockMvc= MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void testCharacter() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                .get("/apis/auth/app/user-salary-change/identity").header(token,"9oYGgb3HTGiv"))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testSubSalaryChange() throws  Exception{
        mockMvc.perform(MockMvcRequestBuilders.get("/apis/auth/app/user-salary-change/sub-salary-change")
                .header(token,"9oYGgb3HTGiv")
                .param("workGroupId","309").param("year","2016").param("quarter","4")
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.data.submitFlag",is(true)));
    }

}

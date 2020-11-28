package com.example.demo;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.dto.UserLoginDTO;
import com.example.demo.utils.TokenUtil;
import com.example.demo.web.controller.LoginController;

import javax.servlet.http.Cookie;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * 登录Controller单元测试
 *
 * @author : ssk
 * @date : 2020/3/4
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = DemoApplication.class)
public class LoginControllerTest {

    /**
     * mock
     */
    private MockMvc mockMvc;

    /**
     * mockResponse
     */
    private MockHttpServletResponse response;

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private LoginController loginController;

    @Before
    public void init() {
        // 初始化Mock相关对象
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
        response = new MockHttpServletResponse();
    }

    /**
     * 登录：主流程测试1：单登录
     */
    @Test
    public void testLogin() throws Exception {
        // 构建入参
        UserLoginDTO userLoginDTO = UserLoginDTO.builder().userId(1)
                .userName("rider")
                .password("1234").build();

        // 模拟post请求登录
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(JSONObject.toJSONString(userLoginDTO));

        ResultActions resultActions = mockMvc.perform(content)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().string("ok"));

        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertTrue("请求成功！", response.getStatus() == 200);
    }

    /**
     * 登录：主流程测试2：单注销
     */
    @Test
    public void testLogout() throws Exception {
        // 模拟post请求注销
        MockHttpServletRequestBuilder content = MockMvcRequestBuilders.post("/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(content)
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.content().string("ok"));

        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertTrue("请求成功！", response.getStatus() == 200);
    }

    /**
     * 登录：主流程测试3：登录+注销（带cookie请求）
     */
    @Test
    public void testLoginAndLogout() throws Exception {
        // 构建入参
        UserLoginDTO userLoginDTO = UserLoginDTO.builder().userId(1)
                .userName("rider")
                .password("1234").build();

        // 生成token
        String token = TokenUtil.generateToken(userLoginDTO, 9000);

        // 模拟post请求注销---带cookie
        MockHttpServletRequestBuilder content2 = MockMvcRequestBuilders.post("/logout")
                .cookie(new Cookie("login_token", token))
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);

        ResultActions resultActions = mockMvc.perform(content2)
                .andDo(MockMvcResultHandlers.print());

        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse response = mvcResult.getResponse();
        Assert.assertTrue("请求成功！", response.getStatus() == 200);
    }

    /**
     * 登录：异常分支1：用户ID校验
     */
    @Test
    public void testLoginForParamCheckID() {
        // 构建入参
        UserLoginDTO userLoginDTO = UserLoginDTO.builder()
                .userName("rider")
                .password("1234").build();

        loginController.login(userLoginDTO, response);
    }

    /**
     * 登录：异常分支2：用户名校验
     */
    @Test
    public void testLoginForParamCheckUserName() {
        // 构建入参
        UserLoginDTO userLoginDTO = UserLoginDTO.builder().userId(1)
//        .userName("rider")
                .password("1234").build();

        loginController.login(userLoginDTO, response);
    }

    /**
     * 登录：异常分支1：用户密码校验
     */
    @Test
    public void testLoginForParamCheckPassword() {
        // 构建入参
        UserLoginDTO userLoginDTO = UserLoginDTO.builder().userId(1)
                .userName("rider").build();

        loginController.login(userLoginDTO, response);
    }

}

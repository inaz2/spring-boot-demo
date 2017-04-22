package com.example;

import static org.hamcrest.CoreMatchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.anonymous;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerTests {

    @Autowired
    private WebApplicationContext context;

    private MockMvc mvc;

    @Autowired
    private UserService userService;

    @Before
    public void setup() {
        mvc = MockMvcBuilders
                .webAppContextSetup(context)
                .apply(springSecurity())
                .build();

        userService.registerUser("testuser", "iamatestuser", "testuser@localhost");
    }

    @Test
    public void shouldAnonymousBeRedirected() throws Exception {
        this.mvc.perform(get("/messages").with(anonymous()))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("http://localhost/login"));
    }

    @Test
    public void shouldNewUserBeAbleToSignupAndLogin() throws Exception {
        this.mvc.perform(post("/signup").with(csrf())
                                        .param("username", "testuser2")
                                        .param("password", "iamatestuser")
                                        .param("mailAddress", "testuser2@localhost"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/messages"));

        this.mvc.perform(post("/login").with(csrf())
                .param("username", "testuser2")
                .param("password", "iamatestuser"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/"));

        this.mvc.perform(get("/messages").with(user("testuser2")))
                .andExpect(status().isOk())
                .andExpect(view().name("messages"));
    }

    @Test
    public void shouldSignupWithDuplicateUsernameFail() throws Exception {
        this.mvc.perform(post("/signup").with(csrf())
                                        .param("username", "testuser")
                                        .param("password", "duplicateusername")
                                        .param("mailAddress", "foobar@localhost"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("already used")));
    }

    @Test
    public void shouldSignupWithDuplicateMailAddressFail() throws Exception {
        this.mvc.perform(post("/signup").with(csrf())
                                        .param("username", "testuser2")
                                        .param("password", "duplicatemailaddress")
                                        .param("mailAddress", "testuser@localhost"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("already used")));
    }

    @Test
    public void shouldLoginWithNonExistUserFail() throws Exception {
        this.mvc.perform(post("/login").with(csrf())
                                       .param("username", "nonexistuser")
                                       .param("password", "iamatestuser"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login-error"));
    }

    @Test
    public void shouldLoginWithWrongPasswordFail() throws Exception {
        this.mvc.perform(post("/login").with(csrf())
                                       .param("username", "testuser")
                                       .param("password", "wrongpassword"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login-error"));
    }

    @Test
    public void shouldLoginWithJPQLInjectionFail() throws Exception {
        this.mvc.perform(post("/login").with(csrf())
                                       .param("username", "testuser' OR ''='")
                                       .param("password", "jpqlinjection"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/login-error"));
    }

}

package com.example.socialapi;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/** End-to-end tests: real Spring context, real SQLite file, simulated HTTP via MockMvc. */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class SocialApiFlowTests {

    @Autowired MockMvc mvc;

    private String json(String... kv) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < kv.length; i += 2) {
            if (i > 0) sb.append(',');
            sb.append('"').append(kv[i]).append("\":\"").append(kv[i + 1]).append('"');
        }
        return sb.append('}').toString();
    }

    private void register(String username, String password) throws Exception {
        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content(json("username", username, "password", password)))
           .andExpect(status().isCreated());
    }

    private String login(String username, String password) throws Exception {
        String body = mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json("username", username, "password", password)))
           .andExpect(status().isOk())
           .andReturn().getResponse().getContentAsString();
        return "Bearer " + JsonPath.read(body, "$.token");
    }

    private String uniqueName() { return "u" + UUID.randomUUID().toString().substring(0, 8); }

    @Test
    void registerLoginPostListAndDelete() throws Exception {
        String name = uniqueName();
        register(name, "password123");
        String auth = login(name, "password123");

        mvc.perform(get("/api/users/me").header("Authorization", auth))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$.username").value(name))
           .andExpect(jsonPath("$.passwordHash").doesNotExist());

        String created = mvc.perform(post("/api/posts").header("Authorization", auth)
                .contentType(MediaType.APPLICATION_JSON).content(json("content", "hello world")))
           .andExpect(status().isCreated())
           .andExpect(jsonPath("$.authorUsername").value(name))
           .andReturn().getResponse().getContentAsString();
        int postId = JsonPath.read(created, "$.id");

        mvc.perform(get("/api/posts").header("Authorization", auth))
           .andExpect(status().isOk())
           .andExpect(jsonPath("$[0].content").value("hello world"));

        mvc.perform(delete("/api/posts/" + postId).header("Authorization", auth))
           .andExpect(status().isNoContent());
        mvc.perform(get("/api/posts/" + postId).header("Authorization", auth))
           .andExpect(status().isNotFound());
    }

    @Test
    void cannotDeleteSomeoneElsesPost() throws Exception {
        String alice = uniqueName(), bob = uniqueName();
        register(alice, "password123");
        register(bob, "password123");
        String aliceAuth = login(alice, "password123"), bobAuth = login(bob, "password123");

        String created = mvc.perform(post("/api/posts").header("Authorization", aliceAuth)
                .contentType(MediaType.APPLICATION_JSON).content(json("content", "mine")))
           .andReturn().getResponse().getContentAsString();
        int postId = JsonPath.read(created, "$.id");

        mvc.perform(delete("/api/posts/" + postId).header("Authorization", bobAuth))
           .andExpect(status().isForbidden());
    }

    @Test
    void protectedEndpointsRequireToken() throws Exception {
        mvc.perform(get("/api/posts")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/users/me")).andExpect(status().isUnauthorized());
    }

    @Test
    void badLoginAndDuplicateAndValidation() throws Exception {
        String name = uniqueName();
        register(name, "password123");

        mvc.perform(post("/api/auth/login").contentType(MediaType.APPLICATION_JSON)
                .content(json("username", name, "password", "wrong-password")))
           .andExpect(status().isUnauthorized());

        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content(json("username", name, "password", "password123")))
           .andExpect(status().isConflict());

        mvc.perform(post("/api/users").contentType(MediaType.APPLICATION_JSON)
                .content(json("username", "ab", "password", "short")))
           .andExpect(status().isBadRequest())
           .andExpect(jsonPath("$.errors.username").exists())
           .andExpect(jsonPath("$.errors.password").exists());
    }
}

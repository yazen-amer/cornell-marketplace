package com.yazen.cornellmarketplace;

import com.yazen.cornellmarketplace.entities.Users;
import com.yazen.cornellmarketplace.repositories.UserRepository;
import com.yazen.cornellmarketplace.services.EmailService;
import com.yazen.cornellmarketplace.services.S3Service;
import com.yazen.cornellmarketplace.repositories.ListingRepository;
import org.springframework.mock.web.MockMultipartFile;
import java.util.Date;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:h2:mem:marketplace;MODE=PostgreSQL;DB_CLOSE_DELAY=-1",
    "spring.datasource.driver-class-name=org.h2.Driver",
    "spring.datasource.username=sa", "spring.datasource.password=",
    "spring.jpa.hibernate.ddl-auto=create-drop", "spring.jpa.show-sql=false",
    "security.jwt.secret-key=MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=",
    "security.jwt.expiration-time=3600000",
    "aws.access.key=test", "aws.secret.key=test", "aws.region=us-east-1", "aws.s3.bucket=test",
    "spring.mail.username=test", "spring.mail.password=test"
})
@AutoConfigureMockMvc
class CornellmarketplaceApplicationTests {
    @Autowired MockMvc mvc;
    @Autowired UserRepository users;
    @Autowired JsonMapper json;
    @MockitoBean EmailService mail;
    @MockitoBean S3Service storage;
    @Autowired ListingRepository listings;

    @BeforeEach void resetUsers() { listings.deleteAll(); users.deleteAll(); }

    private void register(String email) throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"Student\",\"email\":\"" + email + "\",\"password\":\"StrongPassword123\"}"))
            .andExpect(status().isOk());
    }

    private String verifyAccount() throws Exception {
        Users user = users.findByEmail("student@cornell.edu").orElseThrow();
        String body = mvc.perform(post("/auth/verify").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"student@cornell.edu\",\"code\":\"" + user.getVerificationCode() + "\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        return json.readTree(body).get("token").asText();
    }

    @Test void registerVerifyLoginAndReadProfile() throws Exception {
        register(" Student@Cornell.edu ");
        verify(mail).sendVerificationCode(eq("student@cornell.edu"), matches("[0-9]{6}"));
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"student@cornell.edu\",\"password\":\"StrongPassword123\"}"))
            .andExpect(status().isForbidden());
        String token = verifyAccount();
        mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\" Student@Cornell.edu \",\"password\":\"StrongPassword123\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.token").isNotEmpty());
        mvc.perform(get("/users/me").header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()).andExpect(jsonPath("$.username").value("Student"));
    }

    @Test void invalidBodiesAreBadRequests() throws Exception {
        for (String path : new String[]{"register", "login", "verify", "resend-verification"}) {
            mvc.perform(post("/auth/" + path).contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isBadRequest());
        }
    }

    @Test void mailFailureDoesNotLeaveUnusableAccount() throws Exception {
        doThrow(new MailSendException("SMTP secret failure")).when(mail).sendVerificationCode(anyString(), anyString());
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"Student\",\"email\":\"student@cornell.edu\",\"password\":\"StrongPassword123\"}"))
            .andExpect(status().isServiceUnavailable());
        assertTrue(users.findByEmail("student@cornell.edu").isEmpty());
    }

    @Test void userSerializationDoesNotExposeVerificationSecrets() throws Exception {
        register("student@cornell.edu");
        JsonNode node = json.valueToTree(users.findByEmail("student@cornell.edu").orElseThrow());
        assertEquals("Student", node.get("username").asText());
        assertFalse(node.has("password"));
        assertFalse(node.has("verificationCode"));
        assertFalse(node.has("verificationCodeExpiry"));
    }

    @Test void invalidTokenReturnsUnauthorizedJson() throws Exception {
        mvc.perform(get("/users/me").header("Authorization", "Bearer invalid"))
            .andExpect(status().isUnauthorized()).andExpect(jsonPath("$.error").isNotEmpty());
    }

    @Test void registrationAndVerificationRejectInvalidInput() throws Exception {
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"Student\",\"email\":\"student@example.com\",\"password\":\"StrongPassword123\"}"))
            .andExpect(status().isBadRequest());
        register("student@cornell.edu");
        mvc.perform(post("/auth/register").contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"Student\",\"email\":\"student@cornell.edu\",\"password\":\"StrongPassword123\"}"))
            .andExpect(status().isConflict());
        Users user = users.findByEmail("student@cornell.edu").orElseThrow();
        user.setVerificationCodeExpiry(new Date(0));
        users.save(user);
        mvc.perform(post("/auth/verify").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"student@cornell.edu\",\"code\":\"" + user.getVerificationCode() + "\"}"))
            .andExpect(status().isBadRequest());
        assertFalse(users.findByEmail("student@cornell.edu").orElseThrow().isVerified());
    }

    @Test void resendFailurePreservesPreviousCode() throws Exception {
        register("student@cornell.edu");
        String oldCode = users.findByEmail("student@cornell.edu").orElseThrow().getVerificationCode();
        doThrow(new MailSendException("Failure")).when(mail).sendVerificationCode(anyString(), anyString());
        mvc.perform(post("/auth/resend-verification").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"student@cornell.edu\"}"))
            .andExpect(status().isServiceUnavailable());
        assertEquals(oldCode, users.findByEmail("student@cornell.edu").orElseThrow().getVerificationCode());
    }

    @Test void publicListingsHideSecretsAndOwnershipIsEnforced() throws Exception {
        register("student@cornell.edu");
        String token = verifyAccount();
        when(storage.upload(any())).thenReturn("https://example.com/image.jpg");
        MockMultipartFile request = new MockMultipartFile("listingRequest", "", "application/json",
            "{\"title\":\"Desk\",\"description\":\"Desk\",\"price\":10}".getBytes());
        MockMultipartFile image = new MockMultipartFile("imageUpload", "image.jpg", "image/jpeg", new byte[]{1});
        String body = mvc.perform(multipart("/listings").file(request).file(image).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long id = json.readTree(body).get("id").asLong();
        mvc.perform(get("/listings/" + id))
            .andExpect(status().isOk()).andExpect(jsonPath("$.seller.username").value("Student"))
            .andExpect(jsonPath("$.seller.verificationCode").doesNotExist())
            .andExpect(jsonPath("$.seller.password").doesNotExist());
        mvc.perform(get("/listings/999999")).andExpect(status().isNotFound());
        register("other@cornell.edu");
        Users other = users.findByEmail("other@cornell.edu").orElseThrow();
        other.setVerified(true); users.save(other);
        String otherBody = mvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON)
            .content("{\"email\":\"other@cornell.edu\",\"password\":\"StrongPassword123\"}"))
            .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        String otherToken = json.readTree(otherBody).get("token").asText();
        mvc.perform(delete("/listings/" + id).header("Authorization", "Bearer " + otherToken))
            .andExpect(status().isForbidden());
        assertTrue(listings.findById(id).isPresent());
        mvc.perform(delete("/listings/" + id).header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }

    @Test void unauthenticatedUsersCannotWriteListings() throws Exception {
        mvc.perform(delete("/listings/1")).andExpect(status().isUnauthorized());
    }
}

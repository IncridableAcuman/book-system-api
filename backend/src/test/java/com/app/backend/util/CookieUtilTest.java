package com.app.backend.util;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class CookieUtilTest {

    private CookieUtil cookieUtil;

    @BeforeEach
    void setUp() {
        cookieUtil = new CookieUtil();
        ReflectionTestUtils.setField(cookieUtil, "refreshTime", 604800000); // 7 days in ms -> 604800s
    }

    @Test
    @DisplayName("Should add refreshToken cookie with correct attributes")
    void testAddCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();
        String sampleToken = "sample-refresh-token";

        cookieUtil.addCookie(sampleToken, response);

        Cookie cookie = response.getCookie("refreshToken");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("refreshToken");
        assertThat(cookie.getValue()).isEqualTo(sampleToken);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getSecure()).isFalse();
        assertThat(cookie.getPath()).isEqualTo("/");
        assertThat(cookie.getMaxAge()).isEqualTo(604800);
    }

    @Test
    @DisplayName("Should clear cookie with zero maxAge and null value")
    void testClearCookie() {
        MockHttpServletResponse response = new MockHttpServletResponse();

        cookieUtil.clearCookie(response);

        Cookie cookie = response.getCookie("refreshToken");
        assertThat(cookie).isNotNull();
        assertThat(cookie.getName()).isEqualTo("refreshToken");
        assertThat(cookie.getValue()).isNull();
        assertThat(cookie.getMaxAge()).isEqualTo(0);
        assertThat(cookie.isHttpOnly()).isTrue();
        assertThat(cookie.getPath()).isEqualTo("/");
    }
}

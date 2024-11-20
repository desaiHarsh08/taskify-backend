package com.taskify.auth.controllers;

import com.taskify.auth.models.RefreshTokenModel;
import com.taskify.auth.services.RefreshTokenServices;
import com.taskify.auth.utils.AuthRequest;
import com.taskify.auth.utils.AuthResponse;
import com.taskify.security.JwtTokenHelper;
import com.taskify.user.dtos.UserDto;
import com.taskify.user.services.UserServices;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtTokenHelper jwtTokenHelper;

    @Autowired
    private RefreshTokenServices refreshTokenServices;

    @Autowired
    private UserServices userServices;

    @PostMapping("")
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        UserDto createdUser = this.userServices.createUser(userDto);
        if (createdUser == null) {
            return new ResponseEntity<>(this.userServices.getUserByEmail(userDto.getEmail()), HttpStatus.OK);
        }

        return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<?> doLogin(@RequestBody AuthRequest authRequest, HttpServletResponse response) {
        if (authRequest.getEmail().isEmpty() || authRequest.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Please provide the valid credentials!");
        }

        System.out.println(authRequest);

        this.authenticateUser(authRequest.getEmail(), authRequest.getPassword());

        UserDetails userDetails = this.userDetailsService.loadUserByUsername(authRequest.getEmail());

        UserDto userDto = this.userServices.getUserByEmail(authRequest.getEmail());

        String accessToken = this.jwtTokenHelper.generateToken(userDetails);
        String refreshToken = this.refreshTokenServices.createRefreshToken(authRequest.getEmail()).getRefreshToken();
        System.out.println("refreshToken: " + refreshToken);


        this.clearCookies(response);

        // Set the `email` and `refreshToken` inside the cookies
        Cookie emailCookie = new Cookie("email", authRequest.getEmail());
        emailCookie.setHttpOnly(true);
        // emailCookie.setSecure(true); // Use true if you're using HTTPS
        emailCookie.setPath("/");
        emailCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days expiration

        Cookie refreshTokenCookie = new Cookie("refreshToken", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        // refreshTokenCookie.setSecure(true); // Use true if you're using HTTPS
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(30 * 24 * 60 * 60); // 30 days expiration

        // Add cookies to the response
        response.addCookie(emailCookie);
        response.addCookie(refreshTokenCookie);

        response.addCookie(emailCookie);
        response.addCookie(refreshTokenCookie);

        // Log the cookies manually since HttpServletResponse does not provide a method
        // to retrieve them
        System.out.println("Cookie added: email = " + emailCookie.getValue());
        System.out.println("Cookie added: refreshToken = " + refreshTokenCookie.getValue());

        AuthResponse authResponse = new AuthResponse(accessToken, userDto);

        return new ResponseEntity<>(authResponse, HttpStatus.OK);
    }

    private void authenticateUser(String username, String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username,
                password);
        this.authenticationManager.authenticate(authenticationToken);
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request, HttpServletResponse response) {
        // Fetch the `email` and `refreshToken` from the cookies
        String email = null;
        String refreshToken = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("email")) {
                    email = cookie.getValue();
                } else if (cookie.getName().equals("refreshToken")) {
                    refreshToken = cookie.getValue();
                }
            }
        }

        if (email == null || refreshToken == null) {
            throw new SecurityException("Security Exception... Please try to login again!");
        }

        // Verify the refresh token
        RefreshTokenModel refreshTokenModel = this.refreshTokenServices.verifyRefreshToken(refreshToken);

        if (!refreshTokenModel.getEmail().equals(email)) {
            throw new SecurityException("Security Exception... Please try to login again!");
        }

        // Generate new access token
        UserDetails userDetails = this.userDetailsService.loadUserByUsername(email);
        String accessToken = this.jwtTokenHelper.generateToken(userDetails);

        return new ResponseEntity<>(new AuthResponse(accessToken, this.userServices.getUserByEmail(email)),
                HttpStatus.OK);

    }

    // Helper method to clear cookies
    private void clearCookies(HttpServletResponse response) {
        setCookie(response, "email", null, 0);
        setCookie(response, "refreshToken", null, 0);
    }

    // Helper method to set cookies
    private void setCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    @GetMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestParam String rawPassword, Long id) {
        return new ResponseEntity<>(this.userServices.resetPassword(id, rawPassword), HttpStatus.OK);
    }

}

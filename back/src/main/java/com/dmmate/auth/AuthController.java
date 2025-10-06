package com.dmmate.auth;

import com.dmmate.auth.dto.*;
import com.dmmate.common.MailService;
import com.dmmate.security.JwtTokenProvider;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final OtpService otpService;
    private final MailService mailService;
    private final AuthService authService;
    private final JwtTokenProvider jwt;

    @PostMapping("/otp")
    public ResponseEntity<?> sendOtp(@Valid @RequestBody OtpSendRequest req) {
        String email = req.email().trim().toLowerCase();
        String code = otpService.generateOtp(email);
        mailService.sendOtp(email, code);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verify(@Valid @RequestBody OtpVerifyRequest req) {
        String email = req.email().trim().toLowerCase();
        String code  = req.code().trim();

        boolean ok = otpService.verifyOtp(email, code);
        if (!ok) return ResponseEntity.status(400).body("Invalid or expired code");

        var userInfo = authService.afterVerifiedLookupOnly(email);
        String signupToken = jwt.createSignupToken(email);

        Map<String, Object> body = new HashMap<>();
        body.put("verified", true);
        body.put("signupToken", signupToken);
        body.put("user", userInfo);
        return ResponseEntity.ok(body);
    }

    @PostMapping("/complete-signup")
    public ResponseEntity<?> completeSignup(@Valid @RequestBody CompleteSignupRequest req) {
        String email = jwt.validateSignupTokenAndGetEmail(req.signupToken());

        var dto = authService.completeSignup(
            email,
            req.name().trim(),
            req.nickname().trim(),
            req.password().trim()
        );

        String accessToken = jwt.createAccessToken(email);

        Map<String, Object> body = new HashMap<>();
        body.put("user", dto);
        body.put("accessToken", accessToken);
        return ResponseEntity.ok(body);
    }
}
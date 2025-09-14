package com.dmmate.common;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {
  private final JavaMailSender mailSender;

  public void sendOtp(String to, String code) {
    log.info("sendOtp called -> to={}, code={}", to, code);
    try {
      SimpleMailMessage msg = new SimpleMailMessage();
      msg.setTo(to);
      msg.setSubject("[Dongguk Mealmate] 이메일 인증 코드");
      msg.setText("인증 코드: " + code + "\n\n10분 안에 입력하세요.");
      mailSender.send(msg);
      log.info("OTP mail sent to {}", to);
    } catch (Exception e) {
      log.error("Failed to send OTP mail to {}: {}", to, e.getMessage(), e);
      throw e;
    }
  }
}
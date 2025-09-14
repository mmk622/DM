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
    // 로그 찍기 (환경변수와 property 확인)
    String userFromEnv = System.getenv("MAIL_USER");
    String userFromProp = System.getProperty("MAIL_USER");
    log.info("sendOtp called -> to={}, code={}", to, code);
    log.info("MAIL_USER (env) = {}", userFromEnv);
    log.info("MAIL_USER (sysprop) = {}", userFromProp);

    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(to);
    msg.setSubject("[Dongguk Mealmate] 이메일 인증 코드");
    msg.setText("인증 코드: " + code + "\n\n10분 안에 입력하세요.");
    mailSender.send(msg);

    log.info("Mail sent successfully to {}", to);
  }
}
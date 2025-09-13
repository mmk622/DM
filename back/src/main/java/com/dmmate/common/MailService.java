package com.dmmate.common;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
  private final JavaMailSender mailSender;

  public void sendOtp(String to, String code) {
    SimpleMailMessage msg = new SimpleMailMessage();
    msg.setTo(to);
    msg.setSubject("[Dongguk Mealmate] 이메일 인증 코드");
    msg.setText("인증 코드: " + code + "\n\n10분 안에 입력하세요.");
    mailSender.send(msg);
  }
}
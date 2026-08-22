package com.example.MailService.model;

public record UserForgotPasswordRequest(Users user, String token) {}

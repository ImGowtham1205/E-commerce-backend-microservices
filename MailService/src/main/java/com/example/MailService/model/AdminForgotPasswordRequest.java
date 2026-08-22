package com.example.MailService.model;

public record AdminForgotPasswordRequest(Admins admin, String token) {

}

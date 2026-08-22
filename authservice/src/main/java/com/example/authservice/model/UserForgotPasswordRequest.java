package com.example.authservice.model;

public record UserForgotPasswordRequest(Users user, String token) {}

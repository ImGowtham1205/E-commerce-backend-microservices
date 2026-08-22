package com.example.authservice.model;

public record AdminForgotPasswordRequest(Admins admin, String token) {

}

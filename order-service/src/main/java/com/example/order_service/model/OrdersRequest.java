package com.example.order_service.model;

public record OrdersRequest(Products product, UserCache user, Orders order) {

}

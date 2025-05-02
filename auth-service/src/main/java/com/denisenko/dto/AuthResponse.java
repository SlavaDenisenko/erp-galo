package com.denisenko.dto;

public record AuthResponse(String accessToken, String tokenType, String expiresIn) {
}

package com.denisenko.orderservice.statemachine;

public enum OrderClosedState {
    STARTED, FISCAL_CONFIRMED, EVENT_PUBLISHED, COMPLETED, FAILED
}

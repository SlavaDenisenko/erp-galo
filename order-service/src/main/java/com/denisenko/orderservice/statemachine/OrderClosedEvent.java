package com.denisenko.orderservice.statemachine;

public enum OrderClosedEvent {
    SEND_TO_FISCAL, PUBLISH_EVENT, COMPLETE, FAIL
}

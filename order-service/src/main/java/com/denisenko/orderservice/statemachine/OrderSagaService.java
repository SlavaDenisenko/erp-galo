package com.denisenko.orderservice.statemachine;

import com.denisenko.orderservice.dto.OrderDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static com.denisenko.orderservice.statemachine.OrderClosedEvent.*;

@Service
@RequiredArgsConstructor
public class OrderSagaService {
    private final StateMachineFactory<OrderClosedState, OrderClosedEvent> factory;

    public void startOrderClosedSaga(OrderDto orderDto, String idempotencyKey) {
        StateMachine<OrderClosedState, OrderClosedEvent> machine = factory.getStateMachine(UUID.randomUUID());
        machine.getExtendedState().getVariables().put("order", orderDto);
        machine.getExtendedState().getVariables().put("idempotencyKey", idempotencyKey);
        Flux.concat(
                machine.startReactively(),
                machine.sendEvent(Mono.just(MessageBuilder.withPayload(SEND_TO_FISCAL).build())),
                machine.sendEvent(Mono.just(MessageBuilder.withPayload(PUBLISH_EVENT).build())),
                machine.sendEvent(Mono.just(MessageBuilder.withPayload(COMPLETE).build())),
                machine.stopReactively()
        ).subscribe();
    }
}

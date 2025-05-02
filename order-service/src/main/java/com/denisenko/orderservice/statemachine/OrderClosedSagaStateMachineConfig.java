package com.denisenko.orderservice.statemachine;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import static com.denisenko.orderservice.statemachine.OrderClosedEvent.*;
import static com.denisenko.orderservice.statemachine.OrderClosedState.*;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
public class OrderClosedSagaStateMachineConfig extends StateMachineConfigurerAdapter<OrderClosedState, OrderClosedEvent> {
    private final OrderSagaActions sagaActions;

    @Override
    public void configure(StateMachineStateConfigurer<OrderClosedState, OrderClosedEvent> states) throws Exception {
        states
                .withStates()
                .initial(STARTED)
                .state(FISCAL_CONFIRMED)
                .state(EVENT_PUBLISHED)
                .end(COMPLETED)
                .end(FAILED);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderClosedState, OrderClosedEvent> transitions) throws Exception {
        transitions
                .withExternal().source(STARTED).target(FISCAL_CONFIRMED).event(SEND_TO_FISCAL).action(sagaActions::sendToFiscal)
                .and()
                .withExternal().source(FISCAL_CONFIRMED).target(EVENT_PUBLISHED).event(PUBLISH_EVENT).action(sagaActions::publishEvent)
                .and()
                .withExternal().source(EVENT_PUBLISHED).target(COMPLETED).event(COMPLETE).action(sagaActions::completeAction)
                .and()
                .withExternal().source(STARTED).target(FAILED).event(FAIL).action(sagaActions::failAction);
    }
}

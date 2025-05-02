package com.denisenko;

import com.denisenko.client.PrinterClient;
import com.denisenko.model.Order;
import com.denisenko.model.OrderItem;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@QuarkusTest
public class PrintResourceTest {

    @InjectMock
    PrinterClient mockPrinterClient;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void printOrderTest() {
        doReturn(Optional.empty()).when(mockPrinterClient).sendToPrinter(any(), anyString());

        given()
                .contentType(ContentType.JSON)
                .body(prepareOrder())
                .post("/print/order")
                .then()
                .statusCode(HttpStatus.SC_OK);
    }

    @Test
    void failPrintOrderTest() {
        doReturn(Optional.of("Failed to print")).when(mockPrinterClient).sendToPrinter(any(), anyString());

        given()
                .contentType(ContentType.JSON)
                .body(prepareOrder())
                .post("/print/order")
                .then()
                .statusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
                .body(equalTo("""
                        Failed to print
                        Failed to print
                        """));
    }

    private Order prepareOrder() {
        OrderItem orderItem1 = new OrderItem();
        orderItem1.setItemId(1);
        orderItem1.setItemName("Pana kota");
        orderItem1.setPrice(BigDecimal.valueOf(370));
        orderItem1.setQuantity(1.0);
        orderItem1.setLocation("kitchen");
        orderItem1.setCourse(1);

        OrderItem orderItem2 = new OrderItem();
        orderItem2.setItemId(2);
        orderItem2.setItemName("Black tea");
        orderItem2.setPrice(BigDecimal.valueOf(230));
        orderItem2.setQuantity(1.5);
        orderItem2.setLocation("bar");
        orderItem2.setCourse(2);

        OrderItem orderItem3 = new OrderItem();
        orderItem3.setItemId(3);
        orderItem3.setItemName("Tiramisu");
        orderItem3.setPrice(BigDecimal.valueOf(450));
        orderItem3.setQuantity(1.0);
        orderItem3.setLocation("kitchen");
        orderItem3.setCourse(2);

        Order order = new Order();
        order.setOfdNumber("OFD-20250331-1000");
        order.setTableNumber(12.1);
        order.setNumberOfGuests(1);
        order.setItems(List.of(orderItem1, orderItem2, orderItem3));
        order.setCreatedAt(LocalDateTime.now());
        order.setTotalPrice(BigDecimal.valueOf(370));
        order.setWaiterName("Egor");
        return order;
    }
}

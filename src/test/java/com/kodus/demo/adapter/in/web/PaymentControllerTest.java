package com.kodus.demo.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kodus.demo.adapter.in.web.dto.PaymentRequestDto;
import com.kodus.demo.application.port.in.ProcessPaymentCommand;
import com.kodus.demo.application.port.in.ProcessPaymentUseCase;
import com.kodus.demo.domain.model.AccountId;
import com.kodus.demo.domain.model.Currency;
import com.kodus.demo.domain.model.Money;
import com.kodus.demo.domain.model.PaymentId;
import com.kodus.demo.domain.model.PaymentOrder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PaymentController.class)
class PaymentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProcessPaymentUseCase processPaymentUseCase;

    @Test
    @DisplayName("POST /api/v1/payments debe responder 201 Created con PaymentResponseDto")
    void shouldReturnCreatedWhenValidRequest() throws Exception {
        PaymentOrder order = new PaymentOrder(
                PaymentId.generate(),
                new AccountId("ACC-001"),
                new AccountId("ACC-002"),
                Money.of(new BigDecimal("150.00"), Currency.USD)
        );
        order.authorize(
                Money.of(new BigDecimal("1000.00"), Currency.USD),
                Money.of(new BigDecimal("0.00"), Currency.USD),
                Money.of(new BigDecimal("2000.00"), Currency.USD)
        );
        order.settle();

        when(processPaymentUseCase.process(any(ProcessPaymentCommand.class))).thenReturn(order);

        PaymentRequestDto request = new PaymentRequestDto(
                "ACC-001",
                "ACC-002",
                new BigDecimal("150.00"),
                "USD"
        );

        mockMvc.perform(post("/api/v1/payments")
                        .header("Idempotency-Key", "TEST-KEY-001")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("SETTLED"))
                .andExpect(jsonPath("$.amount").value(150.00))
                .andExpect(jsonPath("$.currency").value("USD"))
                .andExpect(jsonPath("$.sourceAccountId").value("ACC-001"));
    }

    @Test
    @DisplayName("POST /api/v1/payments debe rechazar con 400 Bad Request si el monto es <= 0")
    void shouldReturnBadRequestWhenAmountIsInvalid() throws Exception {
        PaymentRequestDto invalidRequest = new PaymentRequestDto(
                "ACC-001",
                "ACC-002",
                new BigDecimal("-5.00"),
                "USD"
        );

        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("VALIDATION_ERROR"));
    }
}

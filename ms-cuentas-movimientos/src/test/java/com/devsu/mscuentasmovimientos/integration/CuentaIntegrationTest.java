package com.devsu.mscuentasmovimientos.integration;

import com.devsu.mscuentasmovimientos.domain.model.Cuenta;
import com.devsu.mscuentasmovimientos.domain.port.out.CuentaRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class CuentaIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

    @Container
    @ServiceConnection
    static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CuentaRepositoryPort cuentaRepositoryPort;

    private UUID clienteId;
    private Cuenta cuentaConSaldo;
    private Cuenta cuentaSinSaldo;

    @BeforeEach
    void setUp() {
        clienteId = UUID.randomUUID();

        Cuenta cuenta1 = Cuenta.builder().clienteId(clienteId).numeroCuenta("111111")
                .tipoCuenta(Cuenta.TipoCuenta.AHORROS).saldoInicial(new BigDecimal("100.00"))
                .estado(true).build();

        Cuenta cuenta2 = Cuenta.builder().clienteId(clienteId).numeroCuenta("222222")
                .tipoCuenta(Cuenta.TipoCuenta.CORRIENTE).saldoInicial(new BigDecimal("0.00"))
                .estado(true).build();

        cuentaConSaldo = cuentaRepositoryPort.guardar(cuenta1);
        cuentaSinSaldo = cuentaRepositoryPort.guardar(cuenta2);
    }

    @Test
    void obtenerCuentasPorCliente_DeberiaRetornarCuentas() throws Exception {
        mockMvc.perform(
                get("/cuentas/cliente/" + clienteId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void validarInactivacion_DeberiaRetornarTodasLasCuentasConSaldo() throws Exception {
        mockMvc.perform(get("/cuentas/validacion-inactivacion/" + clienteId)
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())                
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void inactivarCuentasPorCliente_DeberiaInactivarTodas() throws Exception {
        mockMvc.perform(patch("/cuentas/cliente/" + clienteId + "/inactivar")
                .contentType(MediaType.APPLICATION_JSON)).andExpect(status().isNoContent());

        Cuenta c1 = cuentaRepositoryPort.buscarPorId(cuentaConSaldo.getCuentaId()).orElseThrow();
        Cuenta c2 = cuentaRepositoryPort.buscarPorId(cuentaSinSaldo.getCuentaId()).orElseThrow();

        assertFalse(c1.isEstado());
        assertFalse(c2.isEstado());
    }
}

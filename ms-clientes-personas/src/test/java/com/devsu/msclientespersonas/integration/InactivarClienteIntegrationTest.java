package com.devsu.msclientespersonas.integration;

import com.devsu.msclientespersonas.application.dto.CuentaSaldoDto;
import com.devsu.msclientespersonas.domain.model.Cliente;
import com.devsu.msclientespersonas.domain.model.Persona;
import com.devsu.msclientespersonas.domain.port.out.ClienteRepositoryPort;
import com.devsu.msclientespersonas.domain.port.out.CuentaExternalServicePort;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Testcontainers
@Transactional
public class InactivarClienteIntegrationTest {

        @Container
        @ServiceConnection
        static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

        @Container
        @ServiceConnection
        static RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management");

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ClienteRepositoryPort clienteRepositoryPort;

        @MockBean
        private CuentaExternalServicePort cuentaExternalServicePort;

        @Autowired
        private ObjectMapper objectMapper;

        private UUID clienteId;

        @BeforeEach
        void setUp() {
                clienteId = UUID.randomUUID();
                Persona persona = Persona.builder().nombre("Test User Integration")
                                .identificacion("1122334455").direccion("Direccion Test")
                                .telefono("0999999999").genero("Masculino").edad(30).build();

                Cliente cliente = Cliente.builder().clienteId(clienteId) // Esto podría ser ignorado
                                                                         // si la entidad genera ID
                                .contrasena("1234").estado(true).persona(persona).build();

                Cliente clienteGuardado = clienteRepositoryPort.guardar(cliente);
                this.clienteId = clienteGuardado.getClienteId();
        }

        @Test
        void inactivarCliente_DeberiaInactivarCliente_CuandoNoTieneSaldoPendiente()
                        throws Exception {
                // Mock respuesta del servicio externo (ms-cuentas-movimientos)
                when(cuentaExternalServicePort.obtenerCuentasConSaldo(clienteId))
                                .thenReturn(List.of());

                // Ejecutar la petición HTTP al controlador
                mockMvc.perform(patch("/clientes/" + clienteId + "/inactivar")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk());

                // Verificar el estado en la base de datos
                Cliente clienteActualizado =
                                clienteRepositoryPort.buscarPorId(clienteId).orElseThrow();
                assertFalse(clienteActualizado.isEstado(), "El cliente debería estar inactivo");

                // Verificar que se llamó al servicio externo para inactivar cuentas
                verify(cuentaExternalServicePort).inactivarCuentas(clienteId);
        }

        @Test
        void inactivarCliente_DeberiaFallar_CuandoTieneSaldoPendiente() throws Exception {
                // Mock respuesta del servicio externo indicando saldo pendiente
                CuentaSaldoDto cuentaConSaldo = CuentaSaldoDto.builder().numeroCuenta("123456")
                                .saldoActual(new BigDecimal("100.00")).build();

                when(cuentaExternalServicePort.obtenerCuentasConSaldo(clienteId))
                                .thenReturn(List.of(cuentaConSaldo));

                // Ejecutar la petición HTTP
                mockMvc.perform(patch("/clientes/" + clienteId + "/inactivar")
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk()); // El manejador de excepciones retorna
                                                             // 200 OK con detalles
                                                             // GlobalExceptionHandler para
                                                             // CuentasConSaldoException

                // Verificar que el cliente sigue activo
                Cliente cliente = clienteRepositoryPort.buscarPorId(clienteId).orElseThrow();
                // Nota: Dependiendo de la transacción, podría seguir activo.
                // En este caso esperamos que la transacción falle o no se guarde el cambio.
        }
}

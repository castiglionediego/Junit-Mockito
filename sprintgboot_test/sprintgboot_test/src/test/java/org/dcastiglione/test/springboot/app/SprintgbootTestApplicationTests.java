package org.dcastiglione.test.springboot.app;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.dcastiglione.test.springboot.app.Datos.*;

import org.dcastiglione.test.springboot.app.exceptions.DineroInsuficienteException;
import org.dcastiglione.test.springboot.app.models.Banco;
import org.dcastiglione.test.springboot.app.models.Cuenta;
import org.dcastiglione.test.springboot.app.repositories.BancoRepository;
import org.dcastiglione.test.springboot.app.repositories.CuentaRepository;
import org.dcastiglione.test.springboot.app.services.CuentaService;
import org.dcastiglione.test.springboot.app.services.CuentaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@SpringBootTest
class SprintgbootTestApplicationTests {

	@MockBean
	CuentaRepository cuentaRepository;
	@MockBean
	BancoRepository bancoRepository;

	@Autowired
	CuentaService service;

	@BeforeEach
	void setUp() {
		//cuentaRepository = mock(CuentaRepository.class);
		//bancoRepository = mock(BancoRepository.class);
		//service = new CuentaServiceImpl(cuentaRepository, bancoRepository);
	}

	@Test
	void testTransferir() {
		when(cuentaRepository.findById(1L)).thenReturn(getCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(getCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(getBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());


		service.transferir(1L, 2L, new BigDecimal("100"), 1L);

		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("900", saldoOrigen.toPlainString());
		assertEquals("2100", saldoDestino.toPlainString());

		int totalTransferencias = service.revistarTotalTransferencias(1L);

		assertEquals(1, totalTransferencias);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(3)).findById(2L);


		verify(cuentaRepository, times(2)).save(any(Cuenta.class));
		verify(bancoRepository, times(2)).findById(1L);
		verify(bancoRepository).save(any(Banco.class));

		verify(cuentaRepository, times(6)).findById(anyLong());
		verify(cuentaRepository, never()).findAll();

	}

	@Test
	void testDineroInsuficienteException() {
		when(cuentaRepository.findById(1L)).thenReturn(getCuenta001());
		when(cuentaRepository.findById(2L)).thenReturn(getCuenta002());
		when(bancoRepository.findById(1L)).thenReturn(getBanco());

		BigDecimal saldoOrigen = service.revisarSaldo(1L);
		BigDecimal saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		assertThrows(DineroInsuficienteException.class, () -> {
			service.transferir(1L, 2L, new BigDecimal("1200"), 1L);
		});


		saldoOrigen = service.revisarSaldo(1L);
		saldoDestino = service.revisarSaldo(2L);

		assertEquals("1000", saldoOrigen.toPlainString());
		assertEquals("2000", saldoDestino.toPlainString());

		int totalTransferencias = service.revistarTotalTransferencias(1L);

		assertEquals(0, totalTransferencias);

		verify(cuentaRepository, times(3)).findById(1L);
		verify(cuentaRepository, times(2)).findById(2L);

		verify(cuentaRepository, never()).save(any(Cuenta.class));
		verify(bancoRepository).findById(1L);
		verify(bancoRepository, never()).save(any(Banco.class));


	}

	@Test
	void testSameObject() {
		when(cuentaRepository.findById(1L)).thenReturn(getCuenta001());

		Cuenta cuenta1 = service.findById(1L);
		Cuenta cuenta2 = service.findById(1L);

		assertSame(cuenta1, cuenta2);
		verify(cuentaRepository, times(2)).findById(anyLong());

	}

	@Test
	void testFindAll() {
		List<Cuenta> datos = Arrays.asList(Datos.getCuenta001().orElseThrow(), Datos.getCuenta002().orElseThrow());
		when(cuentaRepository.findAll()).thenReturn(datos);

		List<Cuenta> cuentas = service.findAll();

		assertEquals(2, cuentas.size());
		assertFalse(cuentas.isEmpty());
		assertTrue(cuentas.contains(getCuenta001().orElseThrow()));

		verify(cuentaRepository).findAll();

	}

	@Test
	void testGuardar(){
		Cuenta cuentaPepe = new Cuenta(null, "Pepe", new BigDecimal("3000"));
		when(cuentaRepository.save(any())).then(invocation -> {
			Cuenta c = invocation.getArgument(0);
			c.setId(3L);
			return c;
		});

		Cuenta newCuenta = service.save(cuentaPepe);

		assertEquals("Pepe", newCuenta.getPersona());
		assertEquals(3L, newCuenta.getId());

		verify(cuentaRepository).save(any());


	}
}

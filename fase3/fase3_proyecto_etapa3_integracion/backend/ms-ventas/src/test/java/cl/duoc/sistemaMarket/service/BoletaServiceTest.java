package cl.duoc.sistemaMarket.service;

import cl.duoc.sistemaMarket.dto.BoletaDTO;
import cl.duoc.sistemaMarket.model.Boleta;
import cl.duoc.sistemaMarket.model.DetalleVenta;
import cl.duoc.sistemaMarket.model.EstadoVenta;
import cl.duoc.sistemaMarket.model.Venta;
import cl.duoc.sistemaMarket.repository.BoletaRepository;
import cl.duoc.sistemaMarket.repository.DetalleVentaRepository;
import cl.duoc.sistemaMarket.repository.VentaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BoletaServiceTest {

    @Mock
    private BoletaRepository boletaRepository;

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private DetalleVentaRepository detalleVentaRepository;

    @InjectMocks
    private BoletaService boletaService;

    @Test
    void obtenerPorVenta_Existe_RetornaBoletaDTO() {
        Boleta boleta = new Boleta(1L, "B-000001", 10L, LocalDateTime.now(), 5000);
        Venta venta = new Venta(10L, 2L, LocalDateTime.now(), 5000, 5000, EstadoVenta.PAGADA);
        DetalleVenta detalle = new DetalleVenta(1L, 10L, 1L, "Arroz", 2500, 2, 5000);

        when(boletaRepository.findByIdVenta(10L)).thenReturn(Optional.of(boleta));
        when(ventaRepository.findById(10L)).thenReturn(Optional.of(venta));
        when(detalleVentaRepository.findByIdVenta(10L)).thenReturn(List.of(detalle));

        BoletaDTO resultado = boletaService.obtenerPorVenta(10L);

        assertEquals("B-000001", resultado.getNumeroBoleta());
        assertEquals(2L, resultado.getIdUsuario());
        assertEquals(1, resultado.getDetalles().size());
    }

    @Test
    void obtenerPorVenta_NoExisteBoleta_LanzaExcepcion() {
        when(boletaRepository.findByIdVenta(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> boletaService.obtenerPorVenta(99L));
    }

    @Test
    void obtenerPorVenta_NoExisteVenta_LanzaExcepcion() {
        Boleta boleta = new Boleta(1L, "B-000010", 10L, LocalDateTime.now(), 5000);
        when(boletaRepository.findByIdVenta(10L)).thenReturn(Optional.of(boleta));
        when(ventaRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> boletaService.obtenerPorVenta(10L));
    }
}
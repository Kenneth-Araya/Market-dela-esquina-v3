package cl.duoc.sistemaMarket.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import cl.duoc.sistemaMarket.model.ConfiguracionTienda;
import cl.duoc.sistemaMarket.model.Moneda;
import cl.duoc.sistemaMarket.repository.ConfiguracionTiendaRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ConfiguracionService {

    private static final Long ID_UNICO = 1L;

    private final ConfiguracionTiendaRepository configuracionRepository;

    @Transactional
    public ConfiguracionTienda obtener() {
        return configuracionRepository.findById(ID_UNICO)
                .orElseGet(() -> configuracionRepository.save(valoresPorDefecto()));
    }

    @Transactional
    public ConfiguracionTienda actualizar(ConfiguracionTienda datos) {
        validar(datos);
        datos.setId(ID_UNICO);
        if (datos.getNotificarNuevasVentas() == null) datos.setNotificarNuevasVentas(false);
        if (datos.getModoMantenimiento() == null) datos.setModoMantenimiento(false);
        return configuracionRepository.save(datos);
    }

    /** Botón "Restaurar valores por defecto". */
    @Transactional
    public ConfiguracionTienda restaurar() {
        return configuracionRepository.save(valoresPorDefecto());
    }

    private ConfiguracionTienda valoresPorDefecto() {
        ConfiguracionTienda c = new ConfiguracionTienda();
        c.setId(ID_UNICO);
        c.setNombreTienda("Market De La Esquina");
        c.setCorreoContacto("contacto@marketdelaesquina.cl");
        c.setTelefonoContacto("+56 9 1234 5678");
        c.setMoneda(Moneda.CLP);
        c.setDireccionTienda("");
        c.setHorarioAtencion("Lunes a sábado, 9:00 - 20:00");
        c.setNotificarNuevasVentas(true);
        c.setModoMantenimiento(false);
        return c;
    }

    private void validar(ConfiguracionTienda c) {
        if (c.getNombreTienda() == null || c.getNombreTienda().isBlank()) {
            throw new IllegalArgumentException("El nombre de la tienda es obligatorio.");
        }
        limite(c.getNombreTienda(), "El nombre de la tienda", 100);
        limite(c.getCorreoContacto(), "El correo de contacto", 100);
        limite(c.getTelefonoContacto(), "El teléfono de contacto", 20);
        limite(c.getDireccionTienda(), "La dirección", 300);
        limite(c.getHorarioAtencion(), "El horario de atención", 100);
        if (c.getMoneda() == null) {
            c.setMoneda(Moneda.CLP);
        }
    }

    private void limite(String valor, String campo, int maximo) {
        if (valor != null && valor.length() > maximo) {
            throw new IllegalArgumentException(campo + " no puede superar los " + maximo + " caracteres.");
        }
    }
}

package cl.duoc.sistemaMarket.dto;
import org.springframework.stereotype.Component;
import cl.duoc.sistemaMarket.model.Boleta;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class BoletaDTOMapper {

    public BoletaDTO toDTO(Boleta boleta){
        if (boleta == null) {
            return null;
        }

        BoletaDTO boletaDTO = new BoletaDTO();
        boletaDTO.setFolio(boleta.getFolioBoleta());
        boletaDTO.setGlosa(boleta.getGlosaBoleta());
        boletaDTO.setTipo(boleta.getTipoBoleta());
        boletaDTO.setFecha(boleta.getFechaEmisionBoleta());
        boletaDTO.setMontoBruto(boleta.getMontoBrutoBoleta());
        boletaDTO.setMontoNeto(boleta.getMontoNetoBoleta());
        boletaDTO.setEstado(boleta.getEstadoBoleta());
        return boletaDTO;
    }

    public Boleta toModel(BoletaDTO boletaDTO){
        if (boletaDTO == null) {
            return null;
        }

        Boleta boleta = new Boleta();
        boleta.setFolioBoleta(boletaDTO.getFolio());
        boleta.setGlosaBoleta(boletaDTO.getGlosa());
        boleta.setTipoBoleta(boletaDTO.getTipo());
        boleta.setFechaEmisionBoleta(boletaDTO.getFecha());
        boleta.setMontoBrutoBoleta(boletaDTO.getMontoBruto());
        boleta.setMontoNetoBoleta(boletaDTO.getMontoNeto());
        boleta.setEstadoBoleta("PENDIENTE");
        return boleta;
    }
}
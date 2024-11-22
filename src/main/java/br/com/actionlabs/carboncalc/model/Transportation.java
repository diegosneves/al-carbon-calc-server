package br.com.actionlabs.carboncalc.model;

import br.com.actionlabs.carboncalc.dto.TransportationDTO;
import br.com.actionlabs.carboncalc.enums.TransportationType;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Transportation {

    private TransportationType type;
    private Integer monthlyDistance;

    public static Transportation from(TransportationDTO transportationDTO) {
        return new Transportation(transportationDTO.getType(), transportationDTO.getMonthlyDistance());
    }

}

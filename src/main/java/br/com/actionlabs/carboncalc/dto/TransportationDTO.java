package br.com.actionlabs.carboncalc.dto;

import br.com.actionlabs.carboncalc.enums.TransportationType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransportationDTO {
    private TransportationType type;
    private int monthlyDistance;
}

package br.com.actionlabs.carboncalc.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateCalcInfoResponseDTO {
    private boolean success;
}

package br.com.actionlabs.carboncalc.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CarbonCalculationResultDTO {
    private double energy;
    private double transportation;
    private double solidWaste;
    private double total;
}

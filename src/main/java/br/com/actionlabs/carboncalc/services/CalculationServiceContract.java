package br.com.actionlabs.carboncalc.services;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcResponseDTO;

public interface CalculationServiceContract {

    StartCalcResponseDTO startCalculation(StartCalcRequestDTO request);

}

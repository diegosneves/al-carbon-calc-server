package br.com.actionlabs.carboncalc.services;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcResponseDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoRequestDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoResponseDTO;

public interface CalculationServiceContract {

    StartCalcResponseDTO startCalculation(StartCalcRequestDTO request);

    UpdateCalcInfoResponseDTO updateCalculationInfo(UpdateCalcInfoRequestDTO request);

}

package br.com.actionlabs.carboncalc.services;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcResponseDTO;
import br.com.actionlabs.carboncalc.model.CarbonData;
import br.com.actionlabs.carboncalc.model.UserData;
import br.com.actionlabs.carboncalc.repository.CarbonDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CalculationService implements CalculationServiceContract {

    private final CarbonDataRepository repository;

    @Autowired
    public CalculationService(final CarbonDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public StartCalcResponseDTO startCalculation(final StartCalcRequestDTO request) {
        final var carbonData = new CarbonData(this.userDataFrom(request));
        CarbonData storedCarbonData = this.repository.save(carbonData);
        return StartCalcResponseDTO.from(storedCarbonData.getId());
    }


    private UserData userDataFrom(final StartCalcRequestDTO request) {
        return new UserData(request.getName(), request.getEmail(), request.getUf(), request.getPhoneNumber());
    }

}

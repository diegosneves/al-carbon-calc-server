package br.com.actionlabs.carboncalc.services;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcResponseDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoRequestDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoResponseDTO;
import br.com.actionlabs.carboncalc.factory.CarbonEmissionStatsFactory;
import br.com.actionlabs.carboncalc.model.CarbonEmissionStats;
import br.com.actionlabs.carboncalc.model.Transportation;
import br.com.actionlabs.carboncalc.model.UserData;
import br.com.actionlabs.carboncalc.repository.CarbonDataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CalculationService implements CalculationServiceContract {

    private final CarbonDataRepository repository;

    @Autowired
    public CalculationService(final CarbonDataRepository repository) {
        this.repository = repository;
    }

    @Override
    public StartCalcResponseDTO startCalculation(final StartCalcRequestDTO request) {
        final var carbonData = CarbonEmissionStatsFactory.create(this.userDataFrom(request));
        CarbonEmissionStats storedCarbonEmissionStats = this.repository.save(carbonData);
        return StartCalcResponseDTO.from(storedCarbonEmissionStats.getId());
    }


    private UserData userDataFrom(final StartCalcRequestDTO request) {
        return UserData.newUser(request.getName(), request.getEmail(), request.getUf(), request.getPhoneNumber());
    }

    @Override
    public UpdateCalcInfoResponseDTO updateCalculationInfo(final UpdateCalcInfoRequestDTO request) {
        final var updateCalcInfoResponseDTO = UpdateCalcInfoResponseDTO.builder().success(false).build();
        Optional<CarbonEmissionStats> retrievedStats = this.repository.findById(request.getId());
        if (retrievedStats.isPresent()) {
            final var storedCarbonEmissionStats = retrievedStats.get();
            this.repository.save(this.updateCarbonEmissionStats(storedCarbonEmissionStats, request));
            updateCalcInfoResponseDTO.setSuccess(true);
        }
        return updateCalcInfoResponseDTO;
    }

    private CarbonEmissionStats updateCarbonEmissionStats(final CarbonEmissionStats carbonEmissionStats, final UpdateCalcInfoRequestDTO statsRequest) {
        carbonEmissionStats.setEnergyConsumption(statsRequest.getEnergyConsumption());
        carbonEmissionStats.setTransportationList(statsRequest.getTransportation().stream().map(Transportation::from).toList());
        carbonEmissionStats.setSolidWaste(statsRequest.getSolidWasteTotal());
        carbonEmissionStats.setRecyclePercentage(statsRequest.getRecyclePercentage());
        return carbonEmissionStats;
    }

}

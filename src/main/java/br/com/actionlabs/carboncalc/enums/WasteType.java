package br.com.actionlabs.carboncalc.enums;

import br.com.actionlabs.carboncalc.model.CarbonEmissionStats;

public enum WasteType {

    RECYCLABLE {
        @Override
        public Double calculate(CarbonEmissionStats emissionData, Double factor) {
            return emissionData.getSolidWaste() * emissionData.getRecyclePercentage() * factor;
        }
    },
    NON_RECYCLABLE {
        @Override
        public Double calculate(CarbonEmissionStats emissionData, Double factor) {
            return emissionData.getSolidWaste() * (1 - emissionData.getRecyclePercentage()) * factor;
        }
    };

    public abstract Double calculate(CarbonEmissionStats emissionData, Double factor);

}

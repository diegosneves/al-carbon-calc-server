package br.com.actionlabs.carboncalc.model;

import br.com.actionlabs.carboncalc.enums.TransportationType;
import br.com.actionlabs.carboncalc.utils.IdentifierUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "carbon_data")
public class CarbonData {

    @Id
    private String id;
    @NotNull
    @Valid
    private UserData userData;
    private Double energyConsumption;
    private TransportationType transportationType;
    private Double distanceTravelled;
    private Double solidWaste;
    private Double recyclePercentage;

    public CarbonData() {
        this.id = IdentifierUtil.unique();
    }

    public CarbonData(final UserData userData) {
        this.id = IdentifierUtil.unique();
        this.userData = userData;
    }

}

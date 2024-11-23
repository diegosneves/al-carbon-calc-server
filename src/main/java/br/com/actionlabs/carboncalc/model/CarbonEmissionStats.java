package br.com.actionlabs.carboncalc.model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;

@Document(collection = "carbonEmissionStats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarbonEmissionStats {

    @Id
    private String id;
    private UserData userData;
    private Integer energyConsumption;
    private List<Transportation> transportationList;
    private Integer solidWaste;
    private Double recyclePercentage;

    public CarbonEmissionStats(final String anId, final UserData anUser) {
        this.id = anId;
        this.userData = anUser;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CarbonEmissionStats that = (CarbonEmissionStats) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getUserData(), that.getUserData()) && Objects.equals(getEnergyConsumption(), that.getEnergyConsumption()) && Objects.equals(getTransportationList(), that.getTransportationList()) && Objects.equals(getSolidWaste(), that.getSolidWaste()) && Objects.equals(getRecyclePercentage(), that.getRecyclePercentage());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserData(), getEnergyConsumption(), getTransportationList(), getSolidWaste(), getRecyclePercentage());
    }

}

package br.com.actionlabs.carboncalc.dto;

import lombok.Data;

@Data
public class StartCalcResponseDTO {

    private String id;

    public static StartCalcResponseDTO from(final String anId) {
        final var dto = new StartCalcResponseDTO();
        dto.setId(anId);
        return dto;
    }
}

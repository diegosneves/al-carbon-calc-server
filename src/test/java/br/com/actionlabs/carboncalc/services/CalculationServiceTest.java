package br.com.actionlabs.carboncalc.services;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.model.CarbonEmissionStats;
import br.com.actionlabs.carboncalc.model.UserData;
import br.com.actionlabs.carboncalc.repository.CarbonDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
class CalculationServiceTest {

    @InjectMocks
    private CalculationService service;

    @Mock
    private CarbonDataRepository repository;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(this.repository);
    }

    @Test
    void givenAValidRequestWhenCallTheStartCalculationThenShouldPersistDataAndReturnCalculationResultId() {
        final var expectedName = "name";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "AL";
        final var expectedPhoneNumber = "123456789";

        final var request = new StartCalcRequestDTO();
        request.setName(expectedName);
        request.setEmail(expectedEmail);
        request.setUf(expectedUF);
        request.setPhoneNumber(expectedPhoneNumber);

        final var mockCarbonData = new CarbonEmissionStats(new UserData(expectedName, expectedEmail, expectedUF, expectedPhoneNumber));

        when(this.repository.save(any())).thenReturn(mockCarbonData);

        final var actualResult = this.service.startCalculation(request);

        verify(this.repository, times(1)).save(argThat(aCarbonEmissionStats -> Objects.equals(expectedName, aCarbonEmissionStats.getUserData().getName()) &&
                Objects.nonNull(aCarbonEmissionStats.getId()) &&
                Objects.equals(expectedEmail, aCarbonEmissionStats.getUserData().getEmail()) &&
                Objects.equals(expectedUF, aCarbonEmissionStats.getUserData().getUf()) &&
                Objects.equals(expectedPhoneNumber, aCarbonEmissionStats.getUserData().getPhoneNumber())
                ));

        assertNotNull(actualResult);
        assertNotNull(actualResult.getId());
    }

    @Test
    void givenAValidRequestWhenCallTheStartCalculationAndDataBaseConnectionIsFailThenShouldThrowAnException() {
        final var expectedExceptionMessage = "Connection failure";
        final var expectedName = "name";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "AL";
        final var expectedPhoneNumber = "123456789";

        final var request = new StartCalcRequestDTO();
        request.setName(expectedName);
        request.setEmail(expectedEmail);
        request.setUf(expectedUF);
        request.setPhoneNumber(expectedPhoneNumber);

        when(this.repository.save(any())).thenThrow(new IllegalStateException(expectedExceptionMessage));

        final var actualResult = assertThrows(IllegalStateException.class, () -> this.service.startCalculation(request));

        verify(this.repository, times(1)).save(argThat(aCarbonEmissionStats -> Objects.equals(expectedName, aCarbonEmissionStats.getUserData().getName()) &&
                Objects.nonNull(aCarbonEmissionStats.getId()) &&
                Objects.equals(expectedEmail, aCarbonEmissionStats.getUserData().getEmail()) &&
                Objects.equals(expectedUF, aCarbonEmissionStats.getUserData().getUf()) &&
                Objects.equals(expectedPhoneNumber, aCarbonEmissionStats.getUserData().getPhoneNumber())
                ));

        assertNotNull(actualResult);
        assertEquals(expectedExceptionMessage, actualResult.getMessage());
    }

}

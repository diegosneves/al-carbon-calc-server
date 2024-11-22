package br.com.actionlabs.carboncalc.services;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.TransportationDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoRequestDTO;
import br.com.actionlabs.carboncalc.enums.TransportationType;
import br.com.actionlabs.carboncalc.exceptions.UserDataCreateException;
import br.com.actionlabs.carboncalc.factory.CarbonEmissionStatsFactory;
import br.com.actionlabs.carboncalc.model.UserData;
import br.com.actionlabs.carboncalc.repository.CarbonDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.never;
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

        final var mockCarbonData = CarbonEmissionStatsFactory.create(UserData.newUser(expectedName, expectedEmail, expectedUF, expectedPhoneNumber));

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
    void givenAnUsernameNullWhenCallTheStartCalculationThenShouldThrowAnException() {
        final var expectedExceptionMessage = "Username is required";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "AL";
        final var expectedPhoneNumber = "123456789";

        final var request = new StartCalcRequestDTO();
        request.setName(null);
        request.setEmail(expectedEmail);
        request.setUf(expectedUF);
        request.setPhoneNumber(expectedPhoneNumber);

        final var actualResult = assertThrows(UserDataCreateException.class, () -> this.service.startCalculation(request));

        verify(this.repository, never()).save(any());

        assertNotNull(actualResult);
        assertEquals(expectedExceptionMessage, actualResult.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void givenAnInvalidUsernameWhenCallTheStartCalculationThenShouldThrowAnException(String input) {
        final var expectedExceptionMessage = "Username is required";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "AL";
        final var expectedPhoneNumber = "123456789";

        final var request = new StartCalcRequestDTO();
        request.setName(input);
        request.setEmail(expectedEmail);
        request.setUf(expectedUF);
        request.setPhoneNumber(expectedPhoneNumber);

        final var actualResult = assertThrows(UserDataCreateException.class, () -> this.service.startCalculation(request));

        verify(this.repository, never()).save(any());

        assertNotNull(actualResult);
        assertEquals(expectedExceptionMessage, actualResult.getMessage());
    }

    @Test
    void givenAnUserEmailNullWhenCallTheStartCalculationThenShouldThrowAnException() {
        final var expectedExceptionMessage = "Email is required";
        final var expectedName = "Name";
        final var expectedUF = "AL";
        final var expectedPhoneNumber = "123456789";

        final var request = new StartCalcRequestDTO();
        request.setName(expectedName);
        request.setEmail(null);
        request.setUf(expectedUF);
        request.setPhoneNumber(expectedPhoneNumber);

        final var actualResult = assertThrows(UserDataCreateException.class, () -> this.service.startCalculation(request));

        verify(this.repository, never()).save(any());

        assertNotNull(actualResult);
        assertEquals(expectedExceptionMessage, actualResult.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void givenAnInvalidUserEmailWhenCallTheStartCalculationThenShouldThrowAnException(String input) {
        final var expectedExceptionMessage = "Email is required";
        final var expectedName = "Name";
        final var expectedUF = "AL";
        final var expectedPhoneNumber = "123456789";

        final var request = new StartCalcRequestDTO();
        request.setName(expectedName);
        request.setEmail(input);
        request.setUf(expectedUF);
        request.setPhoneNumber(expectedPhoneNumber);

        final var actualResult = assertThrows(UserDataCreateException.class, () -> this.service.startCalculation(request));

        verify(this.repository, never()).save(any());

        assertNotNull(actualResult);
        assertEquals(expectedExceptionMessage, actualResult.getMessage());
    }

    @Test
    void givenAnUfNullWhenCallTheStartCalculationThenShouldThrowAnException() {
        final var expectedExceptionMessage = "Federative unit is required";
        final var expectedName = "Name";
        final var expectedEmail = "email@email.com";
        final var expectedPhoneNumber = "123456789";

        final var request = new StartCalcRequestDTO();
        request.setName(expectedName);
        request.setEmail(expectedEmail);
        request.setUf(null);
        request.setPhoneNumber(expectedPhoneNumber);

        final var actualResult = assertThrows(UserDataCreateException.class, () -> this.service.startCalculation(request));

        verify(this.repository, never()).save(any());

        assertNotNull(actualResult);
        assertEquals(expectedExceptionMessage, actualResult.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void givenAnInvalidUfWhenCallTheStartCalculationThenShouldThrowAnException(String input) {
        final var expectedExceptionMessage = "Federative unit is required";
        final var expectedName = "Name";
        final var expectedEmail = "email@email.com";
        final var expectedPhoneNumber = "123456789";

        final var request = new StartCalcRequestDTO();
        request.setName(expectedName);
        request.setEmail(expectedEmail);
        request.setUf(input);
        request.setPhoneNumber(expectedPhoneNumber);

        final var actualResult = assertThrows(UserDataCreateException.class, () -> this.service.startCalculation(request));

        verify(this.repository, never()).save(any());

        assertNotNull(actualResult);
        assertEquals(expectedExceptionMessage, actualResult.getMessage());
    }

    @Test
    void givenAPhoneNumberNullWhenCallTheStartCalculationThenShouldThrowAnException() {
        final var expectedExceptionMessage = "Phone number is required";
        final var expectedName = "Name";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "AL";

        final var request = new StartCalcRequestDTO();
        request.setName(expectedName);
        request.setEmail(expectedEmail);
        request.setUf(expectedUF);
        request.setPhoneNumber(null);

        final var actualResult = assertThrows(UserDataCreateException.class, () -> this.service.startCalculation(request));

        verify(this.repository, never()).save(any());

        assertNotNull(actualResult);
        assertEquals(expectedExceptionMessage, actualResult.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "    "})
    void givenAnInvalidPhoneNumberWhenCallTheStartCalculationThenShouldThrowAnException(String input) {
        final var expectedExceptionMessage = "Phone number is required";
        final var expectedName = "Name";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "AL";

        final var request = new StartCalcRequestDTO();
        request.setName(expectedName);
        request.setEmail(expectedEmail);
        request.setUf(expectedUF);
        request.setPhoneNumber(input);

        final var actualResult = assertThrows(UserDataCreateException.class, () -> this.service.startCalculation(request));

        verify(this.repository, never()).save(any());

        assertNotNull(actualResult);
        assertEquals(expectedExceptionMessage, actualResult.getMessage());
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

    @Test
    void givenAValidUpdateRequestWhenCallTheUpdateCalculationInfoThenShouldReturnTrue() {
        final var expectedName = "name";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "AL";
        final var expectedPhoneNumber = "123456789";

        final var expectedId = "4dcba6ba34414a348ba6ba34414a347a";
        final var expectedEnergyConsumption = 10;
        final var carTransportationInfo = TransportationDTO.builder().type(TransportationType.CAR).monthlyDistance(100).build();
        final var motorCycleTransportationInfo = TransportationDTO.builder().type(TransportationType.MOTORCYCLE).monthlyDistance(100).build();
        final var expectedTransportationList = List.of(carTransportationInfo, motorCycleTransportationInfo);
        final var expectedTransportationListSize = 2;
        final var expectedSolidWasteTotal = 1000;
        final var expectedRecyclePercentage = 0.5;

        final var mockCarbonData = CarbonEmissionStatsFactory.create(new UserData(expectedName, expectedEmail, expectedUF, expectedPhoneNumber));

        final var updateCalcInfoRequestDTO = UpdateCalcInfoRequestDTO.builder()
                .id(expectedId)
                .energyConsumption(expectedEnergyConsumption)
                .transportation(expectedTransportationList)
                .solidWasteTotal(expectedSolidWasteTotal)
                .recyclePercentage(expectedRecyclePercentage)
                .build();

        when(this.repository.findById(any())).thenReturn(Optional.of(mockCarbonData));
        when(this.repository.save(any())).thenReturn(mockCarbonData);

        final var actualResult = this.service.updateCalculationInfo(updateCalcInfoRequestDTO);

        verify(this.repository, times(1)).findById(argThat(anId -> Objects.equals(expectedId, anId)));
        verify(this.repository, times(1)).save(argThat(aCarbonEmissionStats -> Objects.equals(expectedName, aCarbonEmissionStats.getUserData().getName()) &&
                Objects.nonNull(aCarbonEmissionStats.getId()) &&
                Objects.equals(expectedEmail, aCarbonEmissionStats.getUserData().getEmail()) &&
                Objects.equals(expectedUF, aCarbonEmissionStats.getUserData().getUf()) &&
                Objects.equals(expectedPhoneNumber, aCarbonEmissionStats.getUserData().getPhoneNumber()) &&
                Objects.equals(expectedEnergyConsumption, aCarbonEmissionStats.getEnergyConsumption()) &&
                Objects.equals(expectedTransportationListSize, aCarbonEmissionStats.getTransportationList().size()) &&
                Objects.equals(expectedSolidWasteTotal, aCarbonEmissionStats.getSolidWaste()) &&
                Objects.equals(expectedRecyclePercentage, aCarbonEmissionStats.getRecyclePercentage())
                ));

        assertNotNull(actualResult);
        assertTrue(actualResult.isSuccess());
    }


    @Test
    void givenAnInvalidUpdateRequestIDWhenCallTheUpdateCalculationInfoThenShouldReturnFalse() {

        final var expectedId = "4dcba6ba34414a348ba6ba34414a347a";
        final var expectedEnergyConsumption = 10;
        final var carTransportationInfo = TransportationDTO.builder().type(TransportationType.CAR).monthlyDistance(100).build();
        final var motorCycleTransportationInfo = TransportationDTO.builder().type(TransportationType.MOTORCYCLE).monthlyDistance(100).build();
        final var expectedTransportationList = List.of(carTransportationInfo, motorCycleTransportationInfo);
        final var expectedSolidWasteTotal = 1000;
        final var expectedRecyclePercentage = 0.5;

        final var updateCalcInfoRequestDTO = UpdateCalcInfoRequestDTO.builder()
                .id(expectedId)
                .energyConsumption(expectedEnergyConsumption)
                .transportation(expectedTransportationList)
                .solidWasteTotal(expectedSolidWasteTotal)
                .recyclePercentage(expectedRecyclePercentage)
                .build();

        when(this.repository.findById(any())).thenReturn(Optional.empty());

        final var actualResult = this.service.updateCalculationInfo(updateCalcInfoRequestDTO);

        verify(this.repository, times(1)).findById(argThat(anId -> Objects.equals(expectedId, anId)));
        verify(this.repository, never()).save(any());

        assertNotNull(actualResult);
        assertFalse(actualResult.isSuccess());
    }


    @Test
    void givenAValidRequestWhenCallTheUpdateCalculationInfoAndDataBaseConnectionIsFailThenShouldThrowAnException() {
        final var expectedExceptionMessage = "Connection failure";
        final var expectedId = "4dcba6ba34414a348ba6ba34414a347a";
        final var expectedEnergyConsumption = 10;
        final var carTransportationInfo = TransportationDTO.builder().type(TransportationType.CAR).monthlyDistance(100).build();
        final var motorCycleTransportationInfo = TransportationDTO.builder().type(TransportationType.MOTORCYCLE).monthlyDistance(100).build();
        final var expectedTransportationList = List.of(carTransportationInfo, motorCycleTransportationInfo);
        final var expectedSolidWasteTotal = 1000;
        final var expectedRecyclePercentage = 0.5;

        final var updateCalcInfoRequestDTO = UpdateCalcInfoRequestDTO.builder()
                .id(expectedId)
                .energyConsumption(expectedEnergyConsumption)
                .transportation(expectedTransportationList)
                .solidWasteTotal(expectedSolidWasteTotal)
                .recyclePercentage(expectedRecyclePercentage)
                .build();

        when(this.repository.findById(any())).thenThrow(new IllegalStateException(expectedExceptionMessage));

        final var actualResult = assertThrows(IllegalStateException.class, () -> this.service.updateCalculationInfo(updateCalcInfoRequestDTO));

        verify(this.repository, times(1)).findById(argThat(anId -> Objects.equals(expectedId, anId)));
        verify(this.repository, never()).save(any());

        assertNotNull(actualResult);
        assertEquals(expectedExceptionMessage, actualResult.getMessage());
    }



}

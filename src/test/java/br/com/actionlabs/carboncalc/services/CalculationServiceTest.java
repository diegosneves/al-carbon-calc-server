package br.com.actionlabs.carboncalc.services;

import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.TransportationDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoRequestDTO;
import br.com.actionlabs.carboncalc.enums.TransportationType;
import br.com.actionlabs.carboncalc.exceptions.UserDataCreateException;
import br.com.actionlabs.carboncalc.factory.CarbonEmissionStatsFactory;
import br.com.actionlabs.carboncalc.model.EnergyEmissionFactor;
import br.com.actionlabs.carboncalc.model.SolidWasteEmissionFactor;
import br.com.actionlabs.carboncalc.model.Transportation;
import br.com.actionlabs.carboncalc.model.TransportationEmissionFactor;
import br.com.actionlabs.carboncalc.model.UserData;
import br.com.actionlabs.carboncalc.repository.CarbonEmissionStatsRepository;
import br.com.actionlabs.carboncalc.repository.EnergyEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.SolidWasteEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.TransportationEmissionFactorRepository;
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
    private CarbonEmissionStatsRepository repository;
    @Mock
    private EnergyEmissionFactorRepository energyRepository;
    @Mock
    private TransportationEmissionFactorRepository transportationRepository;
    @Mock
    private SolidWasteEmissionFactorRepository wasteRepository;

    @BeforeEach
    void cleanUp() {
        Mockito.reset(this.repository);
        Mockito.reset(this.energyRepository);
        Mockito.reset(this.transportationRepository);
        Mockito.reset(this.wasteRepository);
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
    void givenAnInvalidRequestWhenCallTheStartCalculationThenShouldThrowException() {
        final var expectedErrorMessage = "User data cannot be null";

        final var actualResult = assertThrows(IllegalArgumentException.class, () -> this.service.startCalculation(null));

        verify(this.repository, never()).findById(any());
        verify(this.energyRepository, never()).findById(any());
        verify(this.transportationRepository, never()).findAll();
        verify(this.wasteRepository, never()).findById(any());

        assertNotNull(actualResult);
        assertEquals(expectedErrorMessage, actualResult.getMessage());
    }

    @Test
    void givenAnInvalidRequestWhenCallTheUpdateCalculationInfoThenShouldThrowException() {
        final var expectedErrorMessage = "Update Data is required";

        final var actualResult = assertThrows(IllegalArgumentException.class, () -> this.service.updateCalculationInfo(null));

        verify(this.repository, never()).findById(any());
        verify(this.energyRepository, never()).findById(any());
        verify(this.transportationRepository, never()).findAll();
        verify(this.wasteRepository, never()).findById(any());

        assertNotNull(actualResult);
        assertEquals(expectedErrorMessage, actualResult.getMessage());
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
    void givenAValidUpdateRequestWithTranportationListNullWhenCallTheUpdateCalculationInfoThenShouldReturnTrue() {
        final var expectedName = "name";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "AL";
        final var expectedPhoneNumber = "123456789";

        final var expectedId = "4dcba6ba34414a348ba6ba34414a347a";
        final var expectedEnergyConsumption = 10;
//        final var carTransportationInfo = TransportationDTO.builder().type(TransportationType.CAR).monthlyDistance(100).build();
//        final var motorCycleTransportationInfo = TransportationDTO.builder().type(TransportationType.MOTORCYCLE).monthlyDistance(100).build();
//        final var expectedTransportationList = List.of(carTransportationInfo, motorCycleTransportationInfo);
        final var expectedTransportationListSize = 2;
        final var expectedSolidWasteTotal = 1000;
        final var expectedRecyclePercentage = 0.5;

        final var mockCarbonData = CarbonEmissionStatsFactory.create(new UserData(expectedName, expectedEmail, expectedUF, expectedPhoneNumber));

        final var updateCalcInfoRequestDTO = UpdateCalcInfoRequestDTO.builder()
                .id(expectedId)
                .energyConsumption(expectedEnergyConsumption)
                .transportation(null)
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
                Objects.isNull(aCarbonEmissionStats.getTransportationList()) &&
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

    @Test
    void givenAValidIdWithCalculationDataWhenCallTheGetResultMethodThenShouldReturnDto() {
        final var expectedId = "4dcba6ba34414a348ba6ba34414a347a";
        final var solidWasteAmount = 690.0;
        final var transportationAmount = 28.0;
        final var energyAmount = 5.0;
        final var expectedTotal = 723.0;

        final var expectedName = "name";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "AL";
        final var expectedPhoneNumber = "123456789";

        final var expectedEnergyConsumption = 10;
        final var carTransportationInfo = TransportationDTO.builder().type(TransportationType.CAR).monthlyDistance(100).build();
        final var motorCycleTransportationInfo = TransportationDTO.builder().type(TransportationType.MOTORCYCLE).monthlyDistance(100).build();
        final var expectedTransportationList = List.of(carTransportationInfo, motorCycleTransportationInfo);
        final var expectedSolidWasteTotal = 1000;
        final var expectedRecyclePercentage = 0.5;


        final var energyEmissionFactor = EnergyEmissionFactor.builder().uf(expectedUF).factor(0.5).build();
        final var transportationEmissionFactorCar = TransportationEmissionFactor.builder().type(TransportationType.CAR).factor(0.19).build();
        final var transportationEmissionFactorMotorcycle = TransportationEmissionFactor.builder().type(TransportationType.MOTORCYCLE).factor(0.09).build();
        final var solidWasteEmissionFactor = SolidWasteEmissionFactor.builder().uf(expectedUF).nonRecyclableFactor(0.95).recyclableFactor(0.43).build();

        final var mockCarbonData = CarbonEmissionStatsFactory.create(UserData.newUser(expectedName, expectedEmail, expectedUF, expectedPhoneNumber));
        mockCarbonData.setEnergyConsumption(expectedEnergyConsumption);
        mockCarbonData.setTransportationList(expectedTransportationList.stream().map(Transportation::from).toList());
        mockCarbonData.setSolidWaste(expectedSolidWasteTotal);
        mockCarbonData.setRecyclePercentage(expectedRecyclePercentage);

        when(this.repository.findById(any())).thenReturn(Optional.of(mockCarbonData));
        when(this.energyRepository.findById(any())).thenReturn(Optional.of(energyEmissionFactor));
        when(this.transportationRepository.findAll()).thenReturn(List.of(transportationEmissionFactorCar, transportationEmissionFactorMotorcycle));
        when(this.wasteRepository.findById(any())).thenReturn(Optional.of(solidWasteEmissionFactor));

        final var actualResul = this.service.getResult(expectedId);

        verify(this.repository, times(1)).findById(argThat(anId -> Objects.equals(expectedId, anId)));
        verify(this.energyRepository, times(1)).findById(any());
        verify(this.transportationRepository, times(1)).findAll();
        verify(this.wasteRepository, times(1)).findById(any());

        assertNotNull(actualResul);
        assertEquals(solidWasteAmount, actualResul.getSolidWaste());
        assertEquals(transportationAmount, actualResul.getTransportation());
        assertEquals(energyAmount, actualResul.getEnergy());
        assertEquals(expectedTotal, actualResul.getTotal());
    }

    @Test
    void givenAValidIdWithoutCalculationDataWhenCallTheGetResultMethodThenShouldReturnDtoWithZeroOnParams() {
        final var expectedId = "4dcba6ba34414a348ba6ba34414a347a";
        final var expectedValue = 0.0;
        final var expectedName = "name";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "AL";
        final var expectedPhoneNumber = "123456789";

        final var mockCarbonData = CarbonEmissionStatsFactory.create(UserData.newUser(expectedName, expectedEmail, expectedUF, expectedPhoneNumber));

        when(this.repository.findById(any())).thenReturn(Optional.of(mockCarbonData));

        final var actualResul = this.service.getResult(expectedId);

        verify(this.repository, times(1)).findById(argThat(anId -> Objects.equals(expectedId, anId)));
        verify(this.energyRepository, times(1)).findById(any());
        verify(this.transportationRepository, times(1)).findAll();
        verify(this.wasteRepository, times(1)).findById(any());

        assertNotNull(actualResul);
        assertEquals(expectedValue, actualResul.getSolidWaste());
        assertEquals(expectedValue, actualResul.getTransportation());
        assertEquals(expectedValue, actualResul.getEnergy());
        assertEquals(expectedValue, actualResul.getTotal());
    }

    @Test
    void givenAnInvalidIdWhenCallTheGetResultMethodThenShouldReturnDtoWithZeroOnParams() {
        final var expectedId = "4dcba6ba34414a348ba6ba34414a347a";
        final var expectedValue = 0.0;

        when(this.repository.findById(any())).thenReturn(Optional.empty());

        final var actualResul = this.service.getResult(expectedId);

        verify(this.repository, times(1)).findById(argThat(anId -> Objects.equals(expectedId, anId)));
        verify(this.energyRepository, never()).findById(any());
        verify(this.transportationRepository, never()).findAll();
        verify(this.wasteRepository, never()).findById(any());

        assertNotNull(actualResul);
        assertEquals(expectedValue, actualResul.getSolidWaste());
        assertEquals(expectedValue, actualResul.getTransportation());
        assertEquals(expectedValue, actualResul.getEnergy());
        assertEquals(expectedValue, actualResul.getTotal());
    }


    @Test
    void givenAnInvalidUfWhenCallTheGetResultMethodThenShouldReturnDtoWithZeroOnParams() {
        final var expectedId = "4dcba6ba34414a348ba6ba34414a347a";
        final var expectedValue = 0.0;

        final var expectedName = "name";
        final var expectedEmail = "email@email.com";
        final var expectedUF = "TT";
        final var expectedPhoneNumber = "123456789";

        final var transportationEmissionFactorCar = TransportationEmissionFactor.builder().type(TransportationType.CAR).factor(0.19).build();
        final var transportationEmissionFactorMotorcycle = TransportationEmissionFactor.builder().type(TransportationType.MOTORCYCLE).factor(0.09).build();


        final var mockCarbonData = CarbonEmissionStatsFactory.create(UserData.newUser(expectedName, expectedEmail, expectedUF, expectedPhoneNumber));

        when(this.repository.findById(any())).thenReturn(Optional.of(mockCarbonData));
        when(this.energyRepository.findById(any())).thenReturn(Optional.empty());
        when(this.transportationRepository.findAll()).thenReturn(List.of(transportationEmissionFactorCar, transportationEmissionFactorMotorcycle));
        when(this.wasteRepository.findById(any())).thenReturn(Optional.empty());

        final var actualResul = this.service.getResult(expectedId);

        verify(this.repository, times(1)).findById(argThat(anId -> Objects.equals(expectedId, anId)));
        verify(this.energyRepository, times(1)).findById(any());
        verify(this.transportationRepository, times(1)).findAll();
        verify(this.wasteRepository, times(1)).findById(any());

        assertNotNull(actualResul);
        assertEquals(expectedValue, actualResul.getSolidWaste());
        assertEquals(expectedValue, actualResul.getTransportation());
        assertEquals(expectedValue, actualResul.getEnergy());
        assertEquals(expectedValue, actualResul.getTotal());
    }

}

package br.com.actionlabs.carboncalc.services;

import br.com.actionlabs.carboncalc.dto.CarbonCalculationResultDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcRequestDTO;
import br.com.actionlabs.carboncalc.dto.StartCalcResponseDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoRequestDTO;
import br.com.actionlabs.carboncalc.dto.UpdateCalcInfoResponseDTO;
import br.com.actionlabs.carboncalc.enums.WasteType;
import br.com.actionlabs.carboncalc.factory.CarbonEmissionStatsFactory;
import br.com.actionlabs.carboncalc.model.CarbonEmissionStats;
import br.com.actionlabs.carboncalc.model.Transportation;
import br.com.actionlabs.carboncalc.model.TransportationEmissionFactor;
import br.com.actionlabs.carboncalc.model.UserData;
import br.com.actionlabs.carboncalc.repository.CarbonEmissionStatsRepository;
import br.com.actionlabs.carboncalc.repository.EnergyEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.SolidWasteEmissionFactorRepository;
import br.com.actionlabs.carboncalc.repository.TransportationEmissionFactorRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Serviço para gerenciamento de cálculos de emissões de carbono.
 *
 * <p>Esta classe fornece métodos para iniciar, atualizar e obter resultados de cálculos
 * de emissões de carbono. Ela faz uso de vários repositórios para armazenar e recuperar
 * informações relacionadas às emissões de carbono.</p>
 *
 * <h2>Dados para Cálculos</h2>
 *
 * <h3>Emissão de Energia</h3>
 *
 * <ul>
 *   <li><strong>Consumo de energia</strong>: 10</li>
 *   <li><strong>Fator de emissão de energia</strong>: 0.5 (para UF "AL")</li>
 * </ul>
 *
 * <p>Cálculo: Emissão de energia = 10 kWh * 0.5 fator de emissão = 5.0</p>
 *
 * <h3>Emissão de Transporte</h3>
 *
 * <ul>
 *   <li><strong>CAR</strong>:
 *   <ul>
 *     <li><strong>Distância mensal</strong>: 100</li>
 *     <li><strong>Fator de emissão</strong>: 0.19</li>
 *   </ul>
 *   <p>Emissão de CAR = 100 km * 0.19 fator de emissão = 19.0</p></li>
 *   <li><strong>MOTORCYCLE</strong>:
 *   <ul>
 *     <li><strong>Distância mensal</strong>: 100</li>
 *     <li><strong>Fator de emissão</strong>: 0.09</li>
 *   </ul>
 *   <p>Emissão de MOTORCYCLE = 100 km * 0.09 fator de emissão = 9.0</p></li>
 * </ul>
 *
 * <p>Soma da emissão de transporte: Emissão total de transporte = 19.0 + 9.0 = 28.0</p>
 *
 * <h3>Emissão de Resíduos Sólidos</h3>
 *
 * <ul>
 *   <li><strong>Produção de resíduos sólidos</strong>: 1000</li>
 *   <li><strong>Percentual reciclado</strong>: 0.5</li>
 *   <li><strong>Percentual não reciclado</strong>: 1 - 0.5 = 0.5</li>
 *   <li><strong>Fator de emissão reciclável</strong>: 0.43</li>
 *   <li><strong>Fator de emissão não reciclável</strong>: 0.95</li>
 * </ul>
 *
 * <p>Emissão de resíduos recicláveis = 1000 kg * 0.5 * 0.43 = 215.0</p>
 * <p>Emissão de resíduos não recicláveis = 1000 kg * 0.5 * 0.95 = 475.0</p>
 *
 * <p>Soma da emissão de resíduos sólidos: Emissão total de resíduos sólidos = 215.0 + 475.0 = 690.0</p>
 *
 * <h2>Emissão Total</h2>
 *
 * <ul>
 *   <li>Emissão total = Emissão de energia + Emissão de transporte + Emissão de resíduos sólidos</li>
 *   <li>Emissão total = 5.0 + 28.0 + 690.0 = 723.0</li>
 * </ul>
 *
 * <h2>Resultados Esperados</h2>
 *
 * <ul>
 *   <li><strong>Solid Waste</strong>: 690.0</li>
 *   <li><strong>Transportation</strong>: 28.0</li>
 *   <li><strong>Energy</strong>: 5.0</li>
 *   <li><strong>Total</strong>: 723.0</li>
 * </ul>
 *
 * @author diegosneves
 * @version 1.0.0
 */
@Service
@Slf4j
public class CalculationService implements CalculationServiceContract {

    private static final double ZERO_VALUE = 0.0;
    private static final String REQUIRED_UPDATE_MESSAGE = "Update Data is required";
    private static final String MESSAGE_USER_DATA_NULL = "User data cannot be null";

    private final CarbonEmissionStatsRepository carbonEmissionRepository;
    private final EnergyEmissionFactorRepository energyRepository;
    private final TransportationEmissionFactorRepository transportationRepository;
    private final SolidWasteEmissionFactorRepository wasteRepository;

    @Autowired
    public CalculationService(final CarbonEmissionStatsRepository carbonEmissionRepository,
                              final EnergyEmissionFactorRepository energyRepository,
                              final TransportationEmissionFactorRepository transportationRepository,
                              final SolidWasteEmissionFactorRepository wasteRepository) {
        this.carbonEmissionRepository = carbonEmissionRepository;
        this.energyRepository = energyRepository;
        this.transportationRepository = transportationRepository;
        this.wasteRepository = wasteRepository;
    }

    /**
     * Adiciona as infos iniciais para o cálculo das emissões de carbono com base nos dados fornecidos na solicitação.
     * <p>
     * Este método cria uma nova instância de {@link CarbonEmissionStats} utilizando os dados do usuário
     * extraídos do {@link StartCalcRequestDTO} fornecido. Em seguida, ele armazena a instância criada no
     * repositório de emissões de carbono {@link CarbonEmissionStatsRepository} e retorna uma resposta
     * contendo o ID da instância armazenada.
     *
     * @param request Um {@link StartCalcRequestDTO} que contém os dados necessários para iniciar o cálculo
     *                das emissões de carbono.
     * @return Um {@link StartCalcResponseDTO} contendo o ID do objeto {@link CarbonEmissionStats}
     * recém-criado e armazenado no repositório.
     * @throws br.com.actionlabs.carboncalc.exceptions.UserDataCreateException se os dados da solicitação estiverem incompletos ou inválidos.
     */
    @Override
    public StartCalcResponseDTO startCalculation(final StartCalcRequestDTO request) {
        this.userDataValidate(request);
        final var carbonData = CarbonEmissionStatsFactory.create(this.userDataFrom(request));
        CarbonEmissionStats storedCarbonEmissionStats = this.carbonEmissionRepository.save(carbonData);
        return StartCalcResponseDTO.from(storedCarbonEmissionStats.getId());
    }

    /**
     * Valida os dados do usuário contidos no objeto {@link StartCalcRequestDTO}.
     *
     * <p>Este método verifica se o objeto de solicitação fornecido é {@code null}.
     * Se for o caso, uma exceção {@link IllegalArgumentException} é lançada com uma mensagem apropriada.
     * </p>
     *
     * @param request O objeto {@link StartCalcRequestDTO} que contém os dados do usuário a serem validados.
     *                Não deve ser {@code null}.
     * @throws IllegalArgumentException Se o objeto {@code request} for {@code null}.
     */
    private void userDataValidate(final StartCalcRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException(MESSAGE_USER_DATA_NULL);
        }
    }


    /**
     * Converte uma solicitação de início de cálculo em um objeto {@link UserData}.
     *
     * <p>Este método recebe um objeto {@link StartCalcRequestDTO} e cria uma nova instância de
     * {@link UserData} a partir dos dados fornecidos na solicitação.</p>
     *
     * @param request o objeto {@link StartCalcRequestDTO} contendo os dados da solicitação.
     *                <ul>
     *                  <li>{@code request.getName()} - O nome do usuário.</li>
     *                  <li>{@code request.getEmail()} - O email do usuário.</li>
     *                  <li>{@code request.getUf()} - A unidade federativa (UF) do usuário.</li>
     *                  <li>{@code request.getPhoneNumber()} - O número de telefone do usuário.</li>
     *                </ul>
     * @return uma nova instância de {@link UserData} contendo os dados fornecidos na solicitação.
     */
    private UserData userDataFrom(final StartCalcRequestDTO request) {
        return UserData.newUser(request.getName(), request.getEmail(), request.getUf(), request.getPhoneNumber());
    }

    /**
     * Atualiza as informações de cálculo com base na solicitação fornecida.
     *
     * <p>Este método recupera as estatísticas atuais de emissão de carbono do repositório utilizando o ID
     * da solicitação. Se as estatísticas forem encontradas, elas são atualizadas e salvas novamente no repositório,
     * e a resposta é configurada para indicar sucesso.
     *
     * @param request o {@link UpdateCalcInfoRequestDTO} contendo o ID das estatísticas de emissão de carbono
     *                para atualizar e os novos valores a serem aplicados
     * @return um {@link UpdateCalcInfoResponseDTO} indicando se a atualização foi bem-sucedida
     */
    @Override
    public UpdateCalcInfoResponseDTO updateCalculationInfo(final UpdateCalcInfoRequestDTO request) {
        this.updateRequestValidate(request);
        final var updateCalcInfoResponseDTO = UpdateCalcInfoResponseDTO.builder().success(false).build();
        Optional<CarbonEmissionStats> retrievedStats = this.carbonEmissionRepository.findById(request.getId());
        if (retrievedStats.isPresent()) {
            final var storedCarbonEmissionStats = retrievedStats.get();
            this.carbonEmissionRepository.save(this.updateCarbonEmissionStats(storedCarbonEmissionStats, request));
            updateCalcInfoResponseDTO.setSuccess(true);
        }
        return updateCalcInfoResponseDTO;
    }

    /**
     * Valida a requisição de atualização para o cálculo de carbono.
     *
     * <p>Este método verifica se o objeto da requisição de atualização não é nulo.
     * Caso o objeto seja nulo, uma exceção {@link IllegalArgumentException} é lançada
     * com uma mensagem de erro específica.</p>
     *
     * @param request o objeto {@link UpdateCalcInfoRequestDTO} que contém as informações
     *                a serem atualizadas no cálculo de carbono. Este parâmetro não pode ser nulo.
     * @throws IllegalArgumentException se o parâmetro {@code request} for nulo.
     */
    private void updateRequestValidate(final UpdateCalcInfoRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException(REQUIRED_UPDATE_MESSAGE);
        }
    }

    /**
     * Atualiza os atributos de um objeto {@link CarbonEmissionStats} com informações provenientes de um objeto {@link UpdateCalcInfoRequestDTO}.
     * <p>
     * Este método modifica as propriedades de consumo de energia, lista de transporte, resíduos sólidos e percentual de reciclagem
     * do objeto {@link CarbonEmissionStats} com base nos valores fornecidos pelo objeto {@link UpdateCalcInfoRequestDTO}.
     *
     * @param carbonEmissionStats o objeto {@link CarbonEmissionStats} que será atualizado.
     * @param statsRequest        o objeto {@link UpdateCalcInfoRequestDTO} que contém as informações para a atualização.
     * @return o objeto {@link CarbonEmissionStats} atualizado.
     */
    private CarbonEmissionStats updateCarbonEmissionStats(final CarbonEmissionStats carbonEmissionStats, final UpdateCalcInfoRequestDTO statsRequest) {
        carbonEmissionStats.setEnergyConsumption(statsRequest.getEnergyConsumption());
        if (statsRequest.getTransportation() != null) {
            carbonEmissionStats.setTransportationList(statsRequest.getTransportation().stream().map(Transportation::from).toList());
        }
        carbonEmissionStats.setSolidWaste(statsRequest.getSolidWasteTotal());
        carbonEmissionStats.setRecyclePercentage(statsRequest.getRecyclePercentage());
        return carbonEmissionStats;
    }

    /**
     * Retorna o resultado do cálculo de emissões de carbono para um dado ID de cálculo.
     *
     * <p>Este método recupera as estatísticas de emissões de carbono correspondentes ao ID de cálculo fornecido
     * a partir do repositório {@link CarbonEmissionStatsRepository}. Se as estatísticas forem encontradas, elas
     * são usadas para construir um objeto {@link CarbonCalculationResultDTO}, que é então retornado.</p>
     *
     * @param calculationId o ID do cálculo das emissões de carbono que se deseja recuperar.
     *                      <ul>
     *                          <li>{@code calculationId} não deve ser {@code null} ou vazio.</li>
     *                      </ul>
     * @return um objeto {@link CarbonCalculationResultDTO} contendo os detalhes das emissões de carbono para o cálculo solicitado.
     * <ul>
     *     <li>Se o cálculo não for encontrado, é retornado um objeto vazio de {@link CarbonCalculationResultDTO}.</li>
     * </ul>
     * @throws IllegalArgumentException se {@code calculationId} for {@code null} ou vazio.
     */
    @Override
    public CarbonCalculationResultDTO getResult(String calculationId) {
        final var carbonStats = this.carbonEmissionRepository.findById(calculationId);
        final var resultDTO = CarbonCalculationResultDTO.builder().build();
        if (carbonStats.isPresent()) {
            final var emissionStats = carbonStats.get();
            this.buildResultDTO(resultDTO, emissionStats);
        }
        return resultDTO;
    }


    /**
     * Constrói o Data Transfer Object (DTO) para o cálculo de carbono, populando-o
     * com os valores calculados para emissões de energia, transporte e resíduos sólidos,
     * bem como o total de emissões.
     *
     * <p>Este método realiza as seguintes ações:
     * <ul>
     *   <li>Calcula as emissões de energia com base nas estatísticas de emissão de carbono fornecidas
     *       e define o valor correspondente no DTO.</li>
     *   <li>Calcula as emissões de transporte com base nas estatísticas de emissão de carbono fornecidas
     *       e define o valor correspondente no DTO.</li>
     *   <li>Calcula as emissões de resíduos sólidos com base nas estatísticas de emissão de carbono fornecidas
     *       e define o valor correspondente no DTO.</li>
     *   <li>Calcula o total das emissões somando os valores individuais das emissões
     *       e define o total no DTO.</li>
     * </ul>
     * </p>
     *
     * @param dto         O {@link CarbonCalculationResultDTO} que será populado com os valores calculados.
     * @param carbonStats O {@link CarbonEmissionStats} contendo os dados necessários para os cálculos das emissões.
     */
    private void buildResultDTO(final CarbonCalculationResultDTO dto, final CarbonEmissionStats carbonStats) {
        dto.setEnergy(this.calculateEnergy(carbonStats));
        dto.setTransportation(this.calculateTransportation(carbonStats));
        dto.setSolidWaste(this.calculateSolidWaste(carbonStats));
        dto.setTotal(this.calculateTotal(dto));
    }

    /**
     * Calcula o total de emissões de carbono combinando os valores das emissões de energia,
     * transporte e resíduos sólidos.
     *
     * <p>Esse método recebe um objeto <code>CarbonCalculationResultDTO</code> que contém os resultados
     * das emissões em três categorias diferentes: energia, transporte e resíduos sólidos. Ele então
     * soma esses valores e retorna o total combinado.</p>
     *
     * @param calculationResult Um objeto <code>CarbonCalculationResultDTO</code> que contém os valores
     *                          das emissões resultantes de energia, transporte e resíduos sólidos.
     * @return O valor total das emissões de carbono como um <code>Double</code>.
     */
    private Double calculateTotal(final CarbonCalculationResultDTO calculationResult) {
        return calculationResult.getEnergy() + calculationResult.getTransportation() + calculationResult.getSolidWaste();
    }

    /**
     * Calcula a emissão de carbono proveniente de resíduos sólidos com base nas
     * estatísticas de emissões fornecidas.
     *
     * @param emissionStats Um objeto {@link CarbonEmissionStats} que contém as
     *                      estatísticas de emissões do usuário, incluindo informações sobre o estado (UF).
     * @return A emissão de carbono calculada proveniente dos resíduos sólidos, como
     * um {@link Double}. Retorna 0.0 se o fator de resíduos sólidos não for encontrado.
     */
    private Double calculateSolidWaste(final CarbonEmissionStats emissionStats) {
        final var solidWasteFactor = this.wasteRepository.findById(emissionStats.getUserData().getUf());
        var result = ZERO_VALUE;
        if (solidWasteFactor.isPresent()) {
            final var factor = solidWasteFactor.get();
            final var recyclableWaste = WasteType.RECYCLABLE.calculate(emissionStats, factor.getRecyclableFactor());
            final var nonRecyclableWaste = WasteType.NON_RECYCLABLE.calculate(emissionStats, factor.getNonRecyclableFactor());
            result = recyclableWaste + nonRecyclableWaste;
        } else {
            ufInvalidLog(emissionStats);
        }
        return result;
    }

    /**
     * Calcula as emissões de carbono resultantes do transporte baseado nas estatísticas de emissões fornecidas.
     *
     * <p>
     * Este método calcula a quantidade total de emissões de carbono derivadas do transporte, tomando como base
     * uma lista de transportes e seus respectivos fatores de emissão. Primeiro, ele garante que a lista de
     * transportes em {@code emissionStats} não é nula. Em seguida, obtém todos os fatores de emissão do
     * {@code transportationRepository} e calcula o total de emissões com base nas distâncias mensais e nos fatores
     * correspondentes.
     * </p>
     *
     * @param emissionStats Um objeto {@link CarbonEmissionStats} que contém os dados estatísticos
     *                      necessários para o cálculo das emissões de transporte. Este objeto não pode ser nulo.
     * @return O valor total das emissões de carbono resultantes do transporte. O valor é retornado como um {@link Double}.
     */
    private Double calculateTransportation(final CarbonEmissionStats emissionStats) {
        var result = ZERO_VALUE;
        if (emissionStats.getTransportationList() == null) {
            emissionStats.setTransportationList(new ArrayList<>());
        }
        final var transportationFactors = this.transportationRepository.findAll();
        for (var transportation : emissionStats.getTransportationList()) {
            var factor = findEmissionFactorByType(transportation, transportationFactors);
            result += transportation.getMonthlyDistance() * factor;
        }
        return result;
    }

    /**
     * Encontra o fator de emissão para um dado tipo de transporte a partir de uma lista de fatores de emissão de transporte.
     *
     * <p>Este método filtra a lista de fatores de emissão de transporte para encontrar o primeiro fator que corresponda
     * ao tipo do objeto de transporte fornecido e retorna o valor desse fator. Se nenhum fator correspondente for encontrado,
     * retorna um valor padrão de zero.
     *
     * @param transportation        O objeto de transporte para o qual o fator de emissão deve ser encontrado. Não deve ser nulo.
     * @param transportationFactors A lista de fatores de emissão de transporte a ser pesquisada. Não deve ser nula.
     * @return O fator de emissão para o tipo de transporte como um Double, ou um valor padrão de zero se nenhuma correspondência for encontrada.
     */
    private static Double findEmissionFactorByType(Transportation transportation, List<TransportationEmissionFactor> transportationFactors) {
        return transportationFactors.stream()
                .filter(transportationEmissionFactor -> transportation.getType().equals(transportationEmissionFactor.getType()))
                .mapToDouble(TransportationEmissionFactor::getFactor)
                .findFirst()
                .orElse(ZERO_VALUE);
    }

    /**
     * Calcula a emissão de carbono baseada no consumo de energia, utilizando fatores de emissão recuperados do repositório.
     *
     * @param emissionStats Uma instância de {@link CarbonEmissionStats} que contém os dados de consumo de energia do usuário.
     *                      Necessário para encontrar o fator de emissão correspondente e realizar o cálculo.
     * @return Um {@link Double} representando a quantidade de emissão de carbono gerada pelo consumo de energia
     * do usuário. Se o fator de emissão não for encontrado, retorna {@link #ZERO_VALUE}.
     */
    private Double calculateEnergy(final CarbonEmissionStats emissionStats) {
        final var energyFactor = this.energyRepository.findById(emissionStats.getUserData().getUf());
        var result = ZERO_VALUE;
        if (energyFactor.isPresent()) {
            final var factor = energyFactor.get();
            result = emissionStats.getEnergyConsumption() * factor.getFactor();
        } else {
            ufInvalidLog(emissionStats);
        }
        return result;
    }

    /**
     * Registra uma mensagem de alerta indicando que a unidade federativa (UF) fornecida não é suportada.
     * <p>
     * Este método extrai a unidade federativa (UF) do objeto {@link CarbonEmissionStats} e registra
     * uma mensagem de alerta usando o logger. Isto normalmente notifica o usuário de que a UF fornecida
     * não é reconhecida ou suportada pelo sistema.
     * </p>
     *
     * @param emissionStats um objeto do tipo {@link CarbonEmissionStats} que contém os dados do usuário,
     *                      incluindo a unidade federativa (UF).
     */
    private static void ufInvalidLog(CarbonEmissionStats emissionStats) {
        log.warn("Federative unit [ {} ] is not supported", emissionStats.getUserData().getUf());
    }

}

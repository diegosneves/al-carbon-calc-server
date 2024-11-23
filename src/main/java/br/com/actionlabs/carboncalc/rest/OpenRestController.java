package br.com.actionlabs.carboncalc.rest;

import br.com.actionlabs.carboncalc.dto.*;
import br.com.actionlabs.carboncalc.services.CalculationServiceContract;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/open")
@RequiredArgsConstructor
@Slf4j
public class OpenRestController {

    private final CalculationServiceContract calculationService;

    @PostMapping("start-calc")
    public ResponseEntity<StartCalcResponseDTO> startCalculation(
            @RequestBody StartCalcRequestDTO request) {
        var responseDTO = this.calculationService.startCalculation(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @PutMapping("info")
    public ResponseEntity<UpdateCalcInfoResponseDTO> updateInfo(
            @RequestBody UpdateCalcInfoRequestDTO request) {
        final var responseDTO = this.calculationService.updateCalculationInfo(request);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("result/{id}")
    public ResponseEntity<CarbonCalculationResultDTO> getResult(@PathVariable String id) {
        final var responseDTO = this.calculationService.getResult(id);
        return ResponseEntity.ok(responseDTO);
    }

}

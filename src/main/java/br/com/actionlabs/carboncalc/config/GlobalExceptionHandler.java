package br.com.actionlabs.carboncalc.config;

import br.com.actionlabs.carboncalc.dto.ExceptionDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    /**
     * Manipula exceções gerais e retorna uma resposta de erro apropriada.
     *
     * @param exception A exceção que ocorreu.
     * @return Uma {@link ResponseEntity} contendo um {@link ExceptionDTO} com a mensagem da exceção e um código de status HTTP
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDTO> handleFailures(Exception exception) {
        ExceptionDTO dto = new ExceptionDTO(exception.getMessage(), HttpStatus.BAD_REQUEST.value());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }

    /**
     * Esta é uma função que lida com exceções do tipo {@link HttpMessageNotReadableException} em todo o controlador.
     * Um objeto {@link HttpMessageNotReadableException} é lançado quando há um erro de sintaxe no corpo HTTP da solicitação.
     * Este método captura essa exceção, registra uma mensagem de erro, cria um objeto {@link ExceptionDTO}
     * contendo essa mensagem e um valor de retorno HTTP de {@code BAD_REQUEST} (400) e, em seguida, retorna essa entidade.
     *
     * @param exception A exceção {@link HttpMessageNotReadableException} que foi lançada quando ocorreu um erro de sintaxe no corpo HTTP de uma solicitação.
     * @return Uma nova ResponseEntity contendo um objeto ExceptionDTO com a mensagem de erro e o status {@code BAD_REQUEST}.
     * O valor de HttpStatus para {@code BAD_REQUEST} é 400, o que indica que a solicitação era inválida ou não pôde ser entendida pelo servidor.
     * @apiNote {@link HttpMessageNotReadableException} Esta exceção é lançada quando ocorre um erro de sintaxe no corpo HTTP da solicitação, o que significa que a solicitação não pode ser lida.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDTO> handleJSONFailures(HttpMessageNotReadableException exception) {
        final var message = "The content of the request could not be processed. Please check that the data was entered correctly.";
        ExceptionDTO dto = new ExceptionDTO(message, HttpStatus.BAD_REQUEST.value());
        log.error(exception.getMessage(), exception);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(dto);
    }


}

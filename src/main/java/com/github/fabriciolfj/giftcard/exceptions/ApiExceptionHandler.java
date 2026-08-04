package com.github.fabriciolfj.giftcard.exceptions;


import com.github.fabriciolfj.giftcard.util.CorrelationUtil;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import tools.jackson.databind.exc.UnrecognizedPropertyException;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY;


@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);
    private static final String BASE = "https://errors.example.com/";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ProblemDetail onInvalidBody(MethodArgumentNotValidException ex) {
        List<Map<String, String>> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", String.valueOf(fe.getDefaultMessage())))
                .toList();

        var problem = problem(UNPROCESSABLE_ENTITY,
                "VALIDATION_ERROR",
                "Requisição inválida",
                "Um ou mais campos não passaram na validação");
        problem.setProperty("errors", errors);
        return problem;
    }


    @ExceptionHandler(MissingRequestHeaderException.class)
    ProblemDetail onMissingHeader(MissingRequestHeaderException ex) {
        return problem(HttpStatus.BAD_REQUEST,
                "MISSING_HEADER",
                "Header obrigatório ausente",
                "Header ausente: " + ex.getHeaderName());
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ProblemDetail onUnreadable(HttpMessageNotReadableException ex) {
        boolean unknownField = ex.getCause() instanceof UnrecognizedPropertyException;

        if (unknownField) {
            var upe = (UnrecognizedPropertyException) ex.getCause();
            var problem = problem(UNPROCESSABLE_ENTITY,
                    "VALIDATION_ERROR",
                    "Campo desconhecido",
                    "Campo não reconhecido: " + upe.getPropertyName());
            problem.setProperty("errors", List.of(Map.of(
                    "field", upe.getPropertyName(),
                    "message", "campo não faz parte do contrato")));
            return problem;
        }

        return problem(HttpStatus.BAD_REQUEST,
                "MALFORMED_REQUEST",
                "Corpo malformado",
                "Não foi possível interpretar o corpo da requisição");
    }


    @ExceptionHandler(ConstraintViolationException.class)
    ProblemDetail onConstraintViolation(ConstraintViolationException ex) {
        boolean headerViolation = ex.getConstraintViolations().stream()
                .anyMatch(v -> v.getPropertyPath().toString().contains("idempotencyKey"));

        var status = headerViolation ? HttpStatus.BAD_REQUEST : UNPROCESSABLE_ENTITY;
        var code = headerViolation ? "INVALID_HEADER" : "VALIDATION_ERROR";

        return problem(status, code, "Parâmetro inválido",
                ex.getConstraintViolations().iterator().next().getMessage());
    }


    @ExceptionHandler(AmountOutOfRangeException.class)
    ProblemDetail onAmountOutOfRange(AmountOutOfRangeException ex) {
        var problem = problem(UNPROCESSABLE_ENTITY,
                "AMOUNT_OUT_OF_RANGE",
                "Valor fora da faixa permitida",
                ex.getMessage());
        problem.setProperty("context", Map.of(
                "minAmountCents", ex.getMinCents(),
                "maxAmountCents", ex.getMaxCents(),
                "requestedAmountCents", ex.getRequestCents()));
        return problem;
    }

    /*
    @ExceptionHandler(AmountNotMultipleException.class)
    ProblemDetail onAmountNotMultiple(AmountNotMultipleException ex) {
        var problem = problem(HttpStatus.UNPROCESSABLE_ENTITY,
                "AMOUNT_NOT_MULTIPLE",
                "Valor não é múltiplo permitido",
                ex.getMessage());
        problem.setProperty("context", Map.of(
                "multipleCents", ex.multipleCents(),
                "requestedAmountCents", ex.requestedCents()));
        return problem;
    }

    // ── Idempotência ──────────────────────────────────────────────────

    @ExceptionHandler(IdempotencyKeyReuseException.class)
    ProblemDetail onKeyReuse(IdempotencyKeyReuseException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY,
                "IDEMPOTENCY_KEY_REUSE",
                "Chave de idempotência reutilizada",
                "A chave já foi usada com conteúdo ou endpoint diferente");
    }

    @ExceptionHandler(IdempotencyInProgressException.class)
    ProblemDetail onInProgress(IdempotencyInProgressException ex) {
        var problem = problem(HttpStatus.CONFLICT,
                "IN_PROGRESS",
                "Requisição em processamento",
                "Outra requisição com a mesma chave está em andamento");
        problem.setProperty("retryAfterSeconds", 1);
        return problem;
    }

    /**
     * Rede de segurança. Loga o stack trace e devolve mensagem genérica —
     * detalhe interno em resposta de erro é vazamento de informação.
     */
    @ExceptionHandler(Exception.class)
    ProblemDetail onUnexpected(Exception ex) {
        log.error("Erro não tratado", ex);
        return problem(HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_ERROR",
                "Erro interno",
                "Ocorreu um erro ao processar a requisição");
    }

    private ProblemDetail problem(HttpStatus status, String code,
                                  String title, String detail) {
        var problem = ProblemDetail.forStatusAndDetail(status, detail);
        problem.setType(URI.create(BASE + code.toLowerCase().replace('_', '-')));
        problem.setTitle(title);
        problem.setProperty("code", code);
        problem.setProperty("correlationId", CorrelationUtil.current());
        return problem;
    }
}
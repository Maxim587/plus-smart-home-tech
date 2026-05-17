package ru.yandex.practicum.validation;

import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.MissingRequestValueException;
import ru.yandex.practicum.exceptions.*;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(MissingRequestValueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleMissingRequestValue(final MissingRequestValueException e) {
        log.debug(e.getDetailMessageCode());
        return new ApiError(e.getDetailMessageCode(), "Отсутствуют необходимые параметры запроса", HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiError handleValidationExceptions(MethodArgumentNotValidException e) {
        List<String> violations = e.getBindingResult().getFieldErrors().stream()
                .map(error -> "field: " + error.getField() + "; message: " + error.getDefaultMessage())
                .toList();
        log.debug(e.getMessage());
        return new ApiError("Ошибка валидации данных", "Некорректные параметры запроса", HttpStatus.BAD_REQUEST, violations);
    }

    @ExceptionHandler(NotAuthorizedUserException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiError handleNotAuthorized(final NotAuthorizedUserException e) {
        log.debug(e.getMessage());
        return new ApiError(e.getMessage(), "Ошибка авторизации", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleConditionsConflict(final ValidationException e) {
        log.debug(e.getMessage());
        return new ApiError(e.getMessage(), "Нарушение условий выполнения запроса", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({
            NotFoundException.class,
            NoDeliveryFoundException.class,
            ProductNotFoundException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiError handleNotFound(final NotFoundException e) {
        log.debug(e.getMessage());
        return new ApiError(e.getMessage(), "Объект не найден", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({
            NoSpecifiedProductInWarehouseException.class,
            SpecifiedProductAlreadyInWarehouseException.class,
            NoOrderFoundException.class,
            NoProductsInShoppingCartException.class,
            NotEnoughInfoInOrderToCalculateException.class,
            ProductInShoppingCartLowQuantityInWarehouseException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleCommonBadRequest(final CommonBadRequestException e) {
        log.debug(e.getMessage());
        return new ApiError(e.getMessage(), "Нарушение условий выполнения запроса", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalServerErrorException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiError handleInternalServerError(final InternalServerErrorException e) {
        log.error(e.getMessage());
        return new ApiError(e.getMessage(), "Ошибка сервера", HttpStatus.INTERNAL_SERVER_ERROR);
    }

}

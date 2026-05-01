package bg.medicalrecord.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

// Глобален обработчик на изключения за Thymeleaf грешки
@ControllerAdvice
public class GlobalExceptionHandler {

    // Обработва ресурс не е намерен (404)
    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/404";
    }

    // Обработва дублиран идентификатор (409)
    @ExceptionHandler(DuplicateIdentifierException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleDuplicate(DuplicateIdentifierException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/409";
    }

    // Обработва забранен достъп (403)
    @ExceptionHandler(AccessForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String handleForbidden(AccessForbiddenException ex, Model model) {
        model.addAttribute("message", ex.getMessage());
        return "error/403";
    }

    // Пропуска Spring Security AccessDeniedException за да се обработи от Security филтрите
    @ExceptionHandler(AccessDeniedException.class)
    public void handleAccessDenied(AccessDeniedException ex) throws AccessDeniedException {
        throw ex;
    }

    // Обработва всички останали грешки (500)
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleGeneral(Exception ex, Model model) {
        model.addAttribute("message", "Възникна неочаквана грешка. Моля, опитайте отново.");
        return "error/500";
    }
}

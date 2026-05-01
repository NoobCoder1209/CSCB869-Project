package bg.medicalrecord.exception;

// Изхвърля се при опит за достъп до чужд ресурс
public class AccessForbiddenException extends RuntimeException {

    public AccessForbiddenException(String message) {
        super(message);
    }
}

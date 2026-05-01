package bg.medicalrecord.exception;

// Изхвърля се при дублиран уникален идентификатор (ЕГН, УИН, потребителско име)
public class DuplicateIdentifierException extends RuntimeException {

    public DuplicateIdentifierException(String message) {
        super(message);
    }
}

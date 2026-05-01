package bg.medicalrecord.exception;

// Изхвърля се когато ресурсът не е намерен в базата данни
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Long id) {
        super(resourceName + " с ID " + id + " не е намерен(а)");
    }

    public ResourceNotFoundException(String message) {
        super(message);
    }
}

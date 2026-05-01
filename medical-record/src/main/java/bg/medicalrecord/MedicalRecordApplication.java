package bg.medicalrecord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Главен клас на приложението за електронен медицински картон
@SpringBootApplication
public class MedicalRecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(MedicalRecordApplication.class, args);
    }
}

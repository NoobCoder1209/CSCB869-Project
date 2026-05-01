package bg.medicalrecord.service;

import bg.medicalrecord.dto.CreateUserDto;
import bg.medicalrecord.model.User;

import java.util.List;

// Интерфейс за управление на потребителски акаунти
public interface UserService {

    // Връща всички потребители
    List<User> findAll();

    // Намира потребител по ID
    User findById(Long id);

    // Намира потребител по потребителско име
    User findByUsername(String username);

    // Връща текущо влезлия потребител
    User getCurrentUser();

    // Създава нов потребител с криптирана парола
    User createUser(CreateUserDto dto);

    // Обновява потребителски данни
    User updateUser(Long id, String username, boolean enabled);

    // Изтрива потребител
    void deleteUser(Long id);
}

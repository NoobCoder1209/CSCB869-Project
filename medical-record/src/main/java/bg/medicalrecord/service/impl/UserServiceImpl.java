package bg.medicalrecord.service.impl;

import bg.medicalrecord.dto.CreateUserDto;
import bg.medicalrecord.exception.DuplicateIdentifierException;
import bg.medicalrecord.exception.ResourceNotFoundException;
import bg.medicalrecord.model.User;
import bg.medicalrecord.repository.UserRepository;
import bg.medicalrecord.service.UserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Сервиз за управление на потребителски акаунти
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Връща всички потребители
    @Override
    public List<User> findAll() {
        return userRepository.findAll();
    }

    // Намира потребител по ID
    @Override
    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Потребител", id));
    }

    // Намира потребител по потребителско име
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Потребител с име '" + username + "' не е намерен"));
    }

    // Връща текущо влезлия потребител
    @Override
    public User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return findByUsername(username);
    }

    // Създава нов потребител с криптирана парола
    @Override
    @Transactional
    public User createUser(CreateUserDto dto) {
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateIdentifierException("Потребител с това име вече съществува");
        }
        User user = new User(dto.getUsername(), passwordEncoder.encode(dto.getPassword()), dto.getRole());
        return userRepository.save(user);
    }

    // Обновява потребителски данни
    @Override
    @Transactional
    public User updateUser(Long id, String username, boolean enabled) {
        User user = findById(id);
        if (!user.getUsername().equals(username) && userRepository.existsByUsername(username)) {
            throw new DuplicateIdentifierException("Потребител с това име вече съществува");
        }
        user.setUsername(username);
        user.setEnabled(enabled);
        return userRepository.save(user);
    }

    // Изтрива потребител
    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = findById(id);
        userRepository.delete(user);
    }
}

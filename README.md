# Електронни медицински досиета (CSCB869)

Уеб приложение на Spring Boot за управление на електронни медицински досиета, разработено като курсов проект по CSCB869 в Нов Български Университет.

## Tech Stack

| Компонент        | Технология                          |
|------------------|-------------------------------------|
| Backend          | Spring Boot 3.2.5                   |
| Език             | Java 17                             |
| Template Engine  | Thymeleaf + Layout Dialect 3.3.0    |
| CSS              | Tailwind CSS (CDN)                  |
| База данни       | H2 (file-based)                     |
| Сигурност        | Spring Security 6 (form login, BCrypt) |
| Build Tool       | Maven (с Maven Wrapper)             |
| ORM              | Spring Data JPA / Hibernate         |

## Бързо стартиране

```bash
cd medical-record
./mvnw spring-boot:run
```

Отворете [http://localhost:8080](http://localhost:8080) в браузъра.

## Демо потребители

| Username     | Password    | Роля    |
|--------------|-------------|---------|
| admin        | admin123    | Admin   |
| dr.petrov    | doctor123   | Doctor  |
| dr.dimitrov  | doctor123   | Doctor  |
| dr.todorov   | doctor123   | Doctor  |
| patient1     | patient123  | Patient |
| patient2     | patient123  | Patient |

## Функционалности

- **Управление на пациенти** — CRUD операции, назначаване на личен лекар, осигурителен статус
- **Управление на лекари** — CRUD със специалност и маркиране като ОПЛ
- **Каталог с диагнози** — диагнози по МКБ-10
- **Прегледи** — пълни записи за посещения с лекар, пациент, диагноза и лечение
- **Болнични листове** — издаване и проследяване на болнични
- **Осигуровки** — месечни плащания с валидация за 6-месечно покритие
- **11 справки** — статистически заявки (пациенти по диагноза, най-честа диагноза, плащания по лекар, история на посещенията, статистика за болничните и др.)
- **Ролеви достъп** — табла за администратор, лекар и пациент с контрол на ниво метод
- **Български интерфейс** — всички етикети и данни на български

## Структура на проекта

```
medical-record/
├── src/main/java/bg/medicalrecord/
│   ├── config/          # SecurityConfig, DataInitializer
│   ├── controller/      # MVC контролери (10 контролера)
│   ├── dto/             # Data transfer objects + report DTOs
│   ├── exception/       # Глобална обработка на грешки
│   ├── model/           # JPA entities + enums
│   ├── repository/      # Spring Data JPA repositories
│   └── service/         # Бизнес логика (8 сервиса)
├── src/main/resources/
│   ├── templates/       # Thymeleaf HTML шаблони
│   └── application.properties
└── pom.xml
```

## Конзола на базата данни

H2 конзолата е достъпна на [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (само за admin).

- JDBC URL: `jdbc:h2:file:./data/medicaldb`
- Username: `sa`
- Password: *(празна)*

## Тестове

```bash
cd medical-record
./mvnw test
```

53 теста (unit + integration) — всички минават успешно.

## Документация

Подробната документация на български се намира в папка `Documentation/`:

1. [Описание на проекта](Documentation/1-ProjectDescription.md)
2. [Ръководство за потребителя](Documentation/2-UserManual.md)
3. [Описание на базата данни](Documentation/3-DatabaseDescription.md)
4. [Външни компоненти](Documentation/4-ExternalComponents.md)

## Лиценз

Курсов проект. Не е предназначен за продуктивна употреба.

# 3. Описание на базата данни

## 3.1. Обща информация

| Параметър          | Стойност                          |
|--------------------|-----------------------------------|
| СУБД               | H2 Database Engine                |
| Режим              | Файлово базиран (file-based)      |
| JDBC URL           | `jdbc:h2:file:./data/medicaldb`   |
| Потребител         | `sa`                              |
| Парола             | *(празна)*                        |
| DDL стратегия      | `update` (автоматична актуализация на схемата) |
| ORM                | Hibernate 6.x (чрез Spring Data JPA) |

Базата данни се създава автоматично при първо стартиране на приложението. Файлът се съхранява в директория `./data/medicaldb.mv.db` спрямо работната директория.

## 3.2. ER модел -- описание

Базата данни съдържа 7 основни таблици:

```
users ─────────┬──── doctors ────────── examinations ──── diagnoses
               │         │                    │
               └──── patients ────────────────┘
                         │                    │
                    insurance_payments    sick_leaves
```

### Връзки

- `User` 1:1 `Doctor` -- един потребител може да бъде свързан с един лекарски профил
- `User` 1:1 `Patient` -- един потребител може да бъде свързан с един пациентски профил
- `Doctor` 1:N `Patient` -- един лекар е личен лекар на много пациенти
- `Doctor` 1:N `Examination` -- един лекар провежда много прегледи
- `Patient` 1:N `Examination` -- един пациент има много прегледи
- `Diagnosis` 1:N `Examination` -- една диагноза се поставя при много прегледи
- `Patient` 1:N `InsurancePayment` -- един пациент има много месечни вноски
- `Examination` 1:1 `SickLeave` -- един преглед може да има най-много един болничен лист

## 3.3. Описание на таблиците

### Таблица `users`

Съответства на клас: `bg.medicalrecord.model.User`

| Колона    | Тип           | Ограничения                | Описание                         |
|-----------|---------------|----------------------------|----------------------------------|
| id        | BIGINT        | PK, AUTO_INCREMENT         | Уникален идентификатор           |
| username  | VARCHAR(100)  | NOT NULL, UNIQUE           | Потребителско име за вход         |
| password  | VARCHAR(255)  | NOT NULL                   | BCrypt хеш на паролата           |
| role      | VARCHAR(20)   | NOT NULL                   | Роля: ROLE_ADMIN, ROLE_DOCTOR, ROLE_PATIENT |
| enabled   | BOOLEAN       | NOT NULL, DEFAULT true     | Флаг за активен акаунт          |

### Таблица `doctors`

Съответства на клас: `bg.medicalrecord.model.Doctor`

| Колона    | Тип           | Ограничения                | Описание                         |
|-----------|---------------|----------------------------|----------------------------------|
| id        | BIGINT        | PK, AUTO_INCREMENT         | Уникален идентификатор           |
| uin       | VARCHAR(20)   | NOT NULL, UNIQUE           | Уникален идентификационен номер  |
| full_name | VARCHAR(150)  | NOT NULL                   | Пълно име на лекаря              |
| specialty | VARCHAR(30)   | NOT NULL                   | Медицинска специалност (enum)    |
| is_gp     | BOOLEAN       | NOT NULL, DEFAULT false    | Общопрактикуващ лекар (ОПЛ)     |
| user_id   | BIGINT        | FK → users(id), UNIQUE     | Свързан потребителски акаунт     |

### Таблица `patients`

Съответства на клас: `bg.medicalrecord.model.Patient`

| Колона             | Тип           | Ограничения                | Описание                         |
|--------------------|---------------|----------------------------|----------------------------------|
| id                 | BIGINT        | PK, AUTO_INCREMENT         | Уникален идентификатор           |
| full_name          | VARCHAR(150)  | NOT NULL                   | Пълно име на пациента            |
| egn                | VARCHAR(10)   | NOT NULL, UNIQUE           | Единен граждански номер          |
| personal_doctor_id | BIGINT        | FK → doctors(id), NULLABLE | Личен лекар (ОПЛ)               |
| user_id            | BIGINT        | FK → users(id), UNIQUE     | Свързан потребителски акаунт     |

### Таблица `diagnoses`

Съответства на клас: `bg.medicalrecord.model.Diagnosis`

| Колона      | Тип           | Ограничения                | Описание                         |
|-------------|---------------|----------------------------|----------------------------------|
| id          | BIGINT        | PK, AUTO_INCREMENT         | Уникален идентификатор           |
| code        | VARCHAR(10)   | NOT NULL, UNIQUE           | МКБ-10 код на диагнозата        |
| description | VARCHAR(255)  | NOT NULL                   | Текстово описание на диагнозата  |

### Таблица `examinations`

Съответства на клас: `bg.medicalrecord.model.Examination`

| Колона           | Тип            | Ограничения                | Описание                         |
|------------------|----------------|----------------------------|----------------------------------|
| id               | BIGINT         | PK, AUTO_INCREMENT         | Уникален идентификатор           |
| examination_date | DATE           | NOT NULL                   | Дата на прегледа                 |
| doctor_id        | BIGINT         | FK → doctors(id), NOT NULL | Лекар, провел прегледа           |
| patient_id       | BIGINT         | FK → patients(id), NOT NULL| Пациент                          |
| diagnosis_id     | BIGINT         | FK → diagnoses(id), NOT NULL| Поставена диагноза              |
| treatment        | VARCHAR(2000)  | NOT NULL                   | Предписано лечение               |
| price            | DECIMAL(10,2)  | NOT NULL                   | Цена на прегледа в лева          |
| paid_by_nhif     | BOOLEAN        | NOT NULL                   | Платено от НЗОК (true/false)     |

### Таблица `sick_leaves`

Съответства на клас: `bg.medicalrecord.model.SickLeave`

| Колона          | Тип     | Ограничения                       | Описание                         |
|-----------------|---------|-----------------------------------|----------------------------------|
| id              | BIGINT  | PK, AUTO_INCREMENT                | Уникален идентификатор           |
| examination_id  | BIGINT  | FK → examinations(id), NOT NULL, UNIQUE | Преглед, при който е издаден |
| start_date      | DATE    | NOT NULL                          | Начална дата на болничния        |
| number_of_days  | INT     | NOT NULL                          | Продължителност в дни            |

### Таблица `insurance_payments`

Съответства на клас: `bg.medicalrecord.model.InsurancePayment`

| Колона        | Тип          | Ограничения                | Описание                         |
|---------------|--------------|----------------------------|----------------------------------|
| id            | BIGINT       | PK, AUTO_INCREMENT         | Уникален идентификатор           |
| patient_id    | BIGINT       | FK → patients(id), NOT NULL| Пациент                          |
| payment_month | VARCHAR(7)   | NOT NULL                   | Месец на вноската (ГГГГ-ММ)     |

Забележка: Полето `payment_month` съхранява `YearMonth` стойност чрез JPA конвертор (`YearMonthConverter`).

## 3.4. Бизнес правило за осигуряване

Пациентът се счита за **здравноосигурен** към дадена дата, ако в таблицата `insurance_payments` има записи за всеки от **6-те последователни месеца** преди месеца на прегледа.

Логиката е имплементирана в метод `InsuranceService.isInsuredAt(Long patientId, LocalDate date)`:

```java
public boolean isInsuredAt(Long patientId, LocalDate date) {
    YearMonth examMonth = YearMonth.from(date);
    List<InsurancePayment> payments = insurancePaymentRepository.findByPatientId(patientId);

    for (int i = 1; i <= 6; i++) {
        YearMonth requiredMonth = examMonth.minusMonths(i);
        boolean found = payments.stream()
                .anyMatch(p -> p.getPaymentMonth().equals(requiredMonth));
        if (!found) {
            return false;
        }
    }
    return true;
}
```

Пример: За преглед на 15.03.2025 г. пациентът трябва да има вноски за месеците от септември 2024 до февруари 2025 (включително).

## 3.5. Диаграма на връзките (текстова)

```
┌─────────────┐       1:1        ┌──────────────┐
│    users    │──────────────────│   doctors    │
│             │                  │              │
│             │       1:1        │   uin        │
│  username   │──────────┐      │   full_name  │
│  password   │          │      │   specialty  │
│  role       │          │      │   is_gp      │
│  enabled    │          │      └──────┬───────┘
└─────────────┘          │             │ 1
                         │             │
                         │             │ N
                    ┌────┴─────────┐   │        ┌──────────────┐
                    │   patients   │───┘        │  diagnoses   │
                    │              │            │              │
                    │  full_name   │            │  code        │
                    │  egn         │            │  description │
                    └──┬───────┬───┘            └──────┬───────┘
                       │       │                       │
                    1  │       │ 1                     │ 1
                       │       │                       │
                    N  │       │ N                     │ N
          ┌────────────┘       └───────────┐           │
          │                                │           │
┌─────────┴──────────┐          ┌──────────┴───────────┴──┐
│ insurance_payments │          │      examinations       │
│                    │          │                          │
│  payment_month     │          │  examination_date       │
└────────────────────┘          │  treatment              │
                                │  price                  │
                                │  paid_by_nhif           │
                                └────────────┬────────────┘
                                             │ 1
                                             │
                                             │ 0..1
                                    ┌────────┴────────┐
                                    │   sick_leaves   │
                                    │                 │
                                    │  start_date     │
                                    │  number_of_days │
                                    └─────────────────┘
```

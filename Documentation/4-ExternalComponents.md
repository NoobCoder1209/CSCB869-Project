# 4. Използвани външни компоненти

## 4.1. Обобщена таблица

| Компонент                    | Версия | Лиценз      | Предназначение в проекта               |
|------------------------------|--------|-------------|----------------------------------------|
| Spring Boot                  | 3.2.5  | Apache 2.0  | Рамка за уеб приложение               |
| Spring Security              | 6.2.x  | Apache 2.0  | Автентикация и оторизация             |
| Spring Data JPA              | 3.2.x  | Apache 2.0  | Достъп до данни                        |
| Hibernate ORM                | 6.4.x  | LGPL 2.1    | JPA имплементация                      |
| Thymeleaf                    | 3.1.x  | Apache 2.0  | Сървърно генериране на HTML           |
| Thymeleaf Layout Dialect     | 3.3.0  | Apache 2.0  | Наследяване на шаблони                |
| Thymeleaf Extras Spring Security 6 | 3.1.x | Apache 2.0 | Интеграция на Security с шаблоните |
| H2 Database                  | 2.2.x  | MPL 2.0     | Вградена релационна БД                |
| Bean Validation (Hibernate Validator) | 8.0.x | Apache 2.0 | Валидация на входни данни     |
| Tailwind CSS                 | 3.x    | MIT         | CSS фреймуърк                          |
| Maven                        | 3.9+   | Apache 2.0  | Билд инструмент и управление на зависимости |

## 4.2. Подробно описание

### Spring Boot 3.2.5

**Какво е:** Фреймуърк за бързо създаване на самостоятелни Spring приложения с минимална конфигурация.

**Как се използва:** Основна рамка на проекта. Осигурява автоматична конфигурация (auto-configuration), вграден Tomcat сървър, управление на жизнения цикъл на приложението и интеграция на всички подмодули.

**Зависимост в `pom.xml`:**
```xml
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>3.2.5</version>
</parent>
```

---

### Spring Security 6.2.x

**Какво е:** Модул за автентикация и оторизация в Spring приложения.

**Как се използва:** Осигурява form-based вход, BCrypt хеширане на пароли, URL-базирано ограничаване на достъпа и ниво-метод сигурност чрез `@PreAuthorize`. Конфигуриран в `SecurityConfig.java`.

**Ключови функции:**
- Трите роли (ADMIN, DOCTOR, PATIENT) с различни права
- Автоматично пренасочване след вход според ролята
- CSRF защита
- Сесиен мениджмънт

**Зависимост:**
```xml
<artifactId>spring-boot-starter-security</artifactId>
```

---

### Spring Data JPA 3.2.x

**Какво е:** Абстракция върху JPA, предоставяща Repository интерфейси за достъп до данни без писане на boilerplate код.

**Как се използва:** Дефинирани са 7 Repository интерфейса (напр. `PatientRepository`, `ExaminationRepository`), наследяващи `JpaRepository`. Използват се JPQL заявки с `@Query` анотации за по-сложни справки.

**Зависимост:**
```xml
<artifactId>spring-boot-starter-data-jpa</artifactId>
```

---

### Hibernate ORM 6.4.x

**Какво е:** Имплементация на JPA спецификацията. Обектно-релационен маперинг (ORM) за Java.

**Как се използва:** Автоматично генериране и актуализация на DB схемата от JPA entity класовете (`ddl-auto=update`). Управлява lazy/eager зареждане, каскадни операции и транзакции.

**Конфигурация:**
```properties
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
```

---

### Thymeleaf 3.1.x

**Какво е:** Сървърен шаблонен двигател (template engine) за Java, генериращ HTML на сървъра.

**Как се използва:** Всички HTML страници (35+ шаблона) са Thymeleaf файлове в `src/main/resources/templates/`. Използват се `th:each`, `th:if`, `th:text`, `th:object` и `th:field` за динамично генериране на съдържание.

**Зависимост:**
```xml
<artifactId>spring-boot-starter-thymeleaf</artifactId>
```

---

### Thymeleaf Layout Dialect 3.3.0

**Какво е:** Разширение на Thymeleaf за наследяване на шаблони (layout inheritance), аналогично на Sitemesh/Tiles.

**Как се използва:** Общият layout е дефиниран в `layout.html`. Всички страници наследяват този layout чрез `layout:decorate="~{layout}"`, което осигурява единна навигация и стил.

**Зависимост:**
```xml
<groupId>nz.net.ultraq.thymeleaf</groupId>
<artifactId>thymeleaf-layout-dialect</artifactId>
<version>3.3.0</version>
```

---

### Thymeleaf Extras Spring Security 6

**Какво е:** Интеграционен модул между Thymeleaf и Spring Security.

**Как се използва:** Позволява в шаблоните да се използват `sec:authorize` атрибути за условно показване на елементи според ролята на потребителя (напр. скриване на административни бутони за пациенти).

**Зависимост:**
```xml
<groupId>org.thymeleaf.extras</groupId>
<artifactId>thymeleaf-extras-springsecurity6</artifactId>
```

---

### H2 Database 2.2.x

**Какво е:** Вградена релационна база данни, написана на Java. Поддържа SQL стандарта и работи в in-memory или file режим.

**Как се използва:** Използва се във file-based режим (`jdbc:h2:file:./data/medicaldb`) за запазване на данните между рестартирания. Включена е уеб конзола на адрес `/h2-console` за директен SQL достъп (само за ADMIN).

**Зависимост:**
```xml
<groupId>com.h2database</groupId>
<artifactId>h2</artifactId>
<scope>runtime</scope>
```

---

### Bean Validation (Hibernate Validator) 8.0.x

**Какво е:** Стандартна Java имплементация за валидация на обекти чрез анотации (`@NotNull`, `@Size`, `@Email` и др.).

**Как се използва:** Валидация на DTO обекти при създаване и редактиране на записи. Грешките се визуализират в Thymeleaf формите.

**Зависимост:**
```xml
<artifactId>spring-boot-starter-validation</artifactId>
```

---

### Tailwind CSS 3.x (CDN)

**Какво е:** Utility-first CSS фреймуърк за бързо стилизиране на HTML елементи чрез класове.

**Как се използва:** Зарежда се чрез CDN линк в `layout.html`. Всички визуални стилове на приложението са дефинирани чрез Tailwind класове директно в HTML шаблоните (напр. `class="bg-white shadow-md rounded-lg p-6"`).

**Включване:**
```html
<script src="https://cdn.tailwindcss.com"></script>
```

---

### Apache Maven 3.9+

**Какво е:** Инструмент за управление на зависимости, билд и жизнен цикъл на Java проекти.

**Как се използва:** Управлява всички библиотечни зависимости (дефинирани в `pom.xml`), компилира проекта и пакетира изпълним JAR файл. Проектът включва Maven Wrapper (`mvnw`), което елиминира нуждата от глобална Maven инсталация.

**Команди:**
```bash
./mvnw spring-boot:run    # Стартиране в dev режим
./mvnw package            # Създаване на JAR файл
./mvnw test               # Изпълнение на тестове
```

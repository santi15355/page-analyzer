### Hexlet tests and linter status:
[![Actions Status](https://github.com/santi15355/java-project-72/workflows/hexlet-check/badge.svg)](https://github.com/santi15355/java-project-72/actions)   [![build](https://github.com/santi15355/java-project-72/actions/workflows/build.yml/badge.svg)](https://github.com/santi15355/java-project-72/actions/workflows/build.yml)    [![Maintainability](https://api.codeclimate.com/v1/badges/9dad2ecdd6a087fab6a3/maintainability)](https://codeclimate.com/github/santi15355/java-project-72/maintainability)    [![Test Coverage](https://api.codeclimate.com/v1/badges/9dad2ecdd6a087fab6a3/test_coverage)](https://codeclimate.com/github/santi15355/java-project-72/test_coverage)


# Приложение "Анализатор страниц"
## Позволяет проверить сайт на SEO пригодность.

### [Деплой на Railway](https://java-project-72-production-25c6.up.railway.app/)


### Для проверки необходимо: 
1. Ввести адрес сайта на главной странице и нажать кнопку "Проверить";
    - сайт будет добавлени в базу данных, либо выйдет предупреждение о
   его нахождении в ней.
2. Выбрать добавленный сайт из списка.
3. На открывшейся странице нажать кнопку "Запустить проверку"

### Сборка проекта:
```bash
make build
```

### Сборка исполняемого файла jar:
```bash
make install
```

### Запуск приложения:
```bash
make start
```

### Тест:
```bash
make test
```

### Чистка:
```bash
make clear
```

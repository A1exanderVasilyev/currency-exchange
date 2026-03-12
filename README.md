# Проект “Обмен валют”
REST API для описания валют и обменных курсов. Позволяет просматривать и редактировать списки валют и обменных курсов, и совершать расчёт конвертации произвольных сумм из одной валюты в другую.
Полное ТЗ - https://zhukovsd.github.io/java-backend-learning-course/projects/currency-exchange

## Цель проекта
Написание REST API на сервлетах для того, чтобы понимать процессы работы приложения на более низком уровне чем при использовании Spring (в связи с его высокой абстракцией)

## База данных

## API

#### GET `/currencies`

Получение списка валют. Пример ответа:

```json
[
  {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  },
  {
    "id": 1,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
  },
  "..."
]
```

#### GET `/currency/USD`

Получение конкретной валюты. Пример ответа:

```json
[
  {
    "id": 0,
    "name": "United States dollar",
    "code": "USD",
    "sign": "$"
  }
]
```

#### POST `/currencies`

Добавление новой валюты в базу. Данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). Поля формы - name, code, sign. Пример ответа - JSON представление вставленной в базу записи, включая её ID:

```json
[
  {
    "id": 2,
    "name": "Euro",
    "code": "EUR",
    "sign": "€"
  }
]
```

### Exchange rates

#### GET `/exchangeRates`

Получение списка всех обменных курсов. Пример ответа:

```json
[
  {
    "id": 0,
    "baseCurrency": {
      "id": 0,
      "name": "United States dollar",
      "code": "USD",
      "sign": "$"
    },
    "targetCurrency": {
      "id": 1,
      "name": "Euro",
      "code": "EUR",
      "sign": "€"
    },
    "rate": 0.93
  },
  "..."
]
```

#### POST `/exchangeRates`

Добавление нового обменного курса в базу. Данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). Поля формы - baseCurrencyCode, targetCurrencyCode, rate. Пример полей формы:

baseCurrencyCode - USD, </br>
targetCurrencyCode - EUR, </br>
rate - 0.99 </br>

```json
[
  {
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Euro",
        "code": "EUR",
        "sign": "€"
    },
    "rate": 0.99
  }
]
```

#### GET `/exchangeRate/USDEUR`

Получение конкретного обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. Пример ответа:

```json
[
  {
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 2,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 80
  }
]
```

#### PATCH `/exchangeRate/USDEUR`

Обновление существующего в базе обменного курса. Валютная пара задаётся идущими подряд кодами валют в адресе запроса. Данные передаются в теле запроса в виде полей формы (x-www-form-urlencoded). Единственное поле формы - rate.
</br>
Пример ответа - JSON представление обновлённой записи в базе данных, включая её ID:

```json
[
  {
    "id": 0,
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 2,
        "name": "Russian Ruble",
        "code": "RUB",
        "sign": "₽"
    },
    "rate": 80
  }
]
```

#### GET `/exchange?from=BASE_CURRENCY_CODE&to=TARGET_CURRENCY_CODE&amount=$AMOUNT`

Расчёт перевода определённого количества средств из одной валюты в другую.
Пример запроса - GET /exchange?from=USD&to=AUD&amount=10.</br>

Пример ответа:

```json
[
  {
    "baseCurrency": {
        "id": 0,
        "name": "United States dollar",
        "code": "USD",
        "sign": "$"
    },
    "targetCurrency": {
        "id": 1,
        "name": "Australian dollar",
        "code": "AUD",
        "sign": "A$"
    },
    "rate": 1.45,
    "amount": 10.00,
    "convertedAmount": 14.50
  }
]
```
Получение курса для обмена может пройти по одному из трёх сценариев. Допустим, совершаем перевод из валюты A в валюту B:</br>

В таблице ExchangeRates существует валютная пара AB - берём её курс</br>
В таблице ExchangeRates существует валютная пара BA - берем её курс, и считаем обратный, чтобы получить AB</br>
В таблице ExchangeRates существуют валютные пары USD-A и USD-B - вычисляем из этих курсов курс AB</br>

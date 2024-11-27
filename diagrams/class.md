# Диаграмма классов  

![Диаграмма классов](https://github.com/polinaKarotkaya/pmsLabs/diagrams/img/classDiagram.png)

# Глоссарий

| Термин                   | Определение                                                                                       |
|:------------------------|:--------------------------------------------------------------------------------------------------|
| MainActivity            | Основной класс активности, который инициализирует и управляет основными компонентами приложения. Содержит методы для валидации цен и сохранения состояния. |
| AppDatabase             | Класс, представляющий базу данных приложения. Содержит DAO (Data Access Object) для работы с данными о машинах и пользователях. |
| CarDao                  | DAO для работы с данными о машинах. Содержит методы для вставки, получения, удаления и обновления записей о машинах. |
| Car                     | Класс, представляющий машину с идентификатором, маркой, моделью и ценой. |
| CarItemDao              | DAO для работы с данными о товарах в корзине. Содержит методы для вставки, получения, удаления и обновления записей о товарах в корзине. |
| CarItem                 | Класс, представляющий товар в корзине с идентификатором, идентификатором пользователя, идентификатором машины и статусом. |
| MockCarDao              | Мокированный DAO для работы с данными о машинах. Используется для тестирования и содержит те же методы, что и CarDao. |
| MockCarItemDao          | Мокированный DAO для работы с данными о товарах в корзине. Используется для тестирования и содержит те же методы, что и CarItemDao. |
| UserDao                 | DAO для работы с данными о пользователях. Содержит методы для вставки, получения пользователей по роли и никнейму. |
| User                    | Класс, представляющий пользователя с идентификатором, никнеймом, паролем и ролью. |
| MockUserDao             | Мокированный DAO для работы с данными о пользователях. Используется для тестирования и содержит те же методы, что и UserDao. |
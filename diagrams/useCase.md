# Диаграмма вариантов использования

![Диаграмма вариантов использования](https://github.com/polinaKarotkaya/pmsLabs/diagrams/img/useCase.png)

# Глоссарий

| Термин | Определение |
|:--|:--|
| Авторизованный пользователь | Пользователь, который вошел в свою учетную запись |
| Корзина | Список товаров, которые пользователь планирует приобрести |
| Товар | Единица продукта, которую можно добавить в корзину или список желаемого |


# Поток событий 

# Содержание
1 [Актёры](#actors)  
2 [Варианты использования](#use_case)  
2.1 [Войти в свою учетную запись](#sign_in_to_your_account)  
2.2 [Зарегистрироваться](#sign_up)  
2.3 [Просмотреть товар](#view_product)  
2.4 [Добавить товар в корзину](#add_to_cart)  
2.5 [Удалить товар из корзины](#remove_from_cart)  
2.6 [Поиск товаров](#search_products)  
2.7 [Выйти из учетной записи](#sign_out_of_your_account)  

<a name="actors"/>

# 1 Актёры

| Актёр | Описание |
|:--|:--|
| Авторизованный пользователь | Пользователь, который вошел в приложение и может взаимодействовать с товарами |

<a name="use_case"/>

# 2 Варианты использования

<a name="sign_in_to_your_account"/>

## 2.1 Войти в свою учетную запись

**Описание.** Вариант использования "Войти в свою учетную запись" позволяет пользователю войти в систему для управления корзиной и списком желаемого.  
**Предусловия.** Пользователь выбрал способ "Sign in".  
**Основной поток.**
1. Приложение отображает окно входа в учетную запись;
2. Пользователь вводит свои данные (логин и пароль);
3. Приложение проверяет введенные данные;
4. Приложение присваивает пользователю статус "авторизован";
5. Приложение отображает главное окно с товарами;
6. Вариант использования завершается.

**Альтернативный поток А1.**
1. Приложение выводит сообщение о неверных данных;
2. Вариант использования завершается досрочно.

**Постусловия.** Пользователь может добавлять или удалять товары из корзины и списка желаемого.

<a name="sign_up"/>

## 2.2 Зарегистрироваться

**Описание.** Вариант использования "Зарегистрироваться" позволяет пользователю создать новую учетную запись.  
**Предусловия.** Пользователь выбрал "Sign up".  
**Основной поток.**
1. Приложение отображает форму регистрации;
2. Пользователь вводит необходимые данные для регистрации;
3. Приложение проверяет данные на уникальность;
4. Приложение создает учетную запись;
5. Приложение присваивает пользователю статус "авторизован";
6. Вариант использования завершается.

**Альтернативный поток А2.**
1. Приложение уведомляет о существующей учетной записи;
2. Возврат к основному потоку с выбором другого ввода.

<a name="view_product"/>

## 2.3 Просмотреть товар

**Описание.** Позволяет пользователю просматривать информацию о товарах.  
**Предусловия.** Пользователь находится в приложении.  
**Основной поток.**
1. Приложение отображает список товаров;
2. Пользователь выбирает товар для просмотра;
3. Приложение отображает информацию о товаре;
4. Вариант использования завершается.

<a name="add_to_cart"/>

## 2.4 Добавить товар в корзину

**Описание.** Позволяет авторизованному пользователю добавить товар в корзину.  
**Предусловия.** Пользователь выбрал товар и нажал "Add to cart".  
**Основной поток.**
1. Приложение добавляет выбранный товар в корзину пользователя;
2. Приложение обновляет информацию о корзине;
3. Вариант использования завершается.

**Альтернативный поток А3.**
1. Приложение уведомляет пользователя о проблеме с добавлением товара;
2. Вариант использования завершается.

<a name="remove_from_cart"/>

## 2.5 Удалить товар из корзины

**Описание.** Позволяет авторизованному пользователю удалить товар из корзины.  
**Предусловия.** Пользователь выбрал товар в корзине и нажал "Remove".  
**Основной поток.**
1. Приложение удаляет выбранный товар из корзины;
2. Приложение обновляет информацию о корзине;
3. Вариант использования завершается.

<a name="search_products"/>

## 2.6 Поиск товаров 

**Описание.** Позволяет пользователю найти товары для более удобного просмотра.  
**Предусловия.** Пользователь выбрал фильтр.  
**Основной поток.**
1. Приложение отображает поле поиска;
2. Пользователь вводит нужное ему название;
3. Приложение отображает товары, соответствующие выбранной категории;
4. Вариант использования завершается.

<a name="sign_out_of_your_account"/>

## 2.7 Выйти из учетной записи

**Описание.** Позволяет авторизованному пользователю выйти из учетной записи.  
**Предусловия.** Пользователь нажал "Sign out".  
**Основной поток.**
1. Приложение завершает сеанс пользователя;
2. Приложение возвращается на главный экран или экран входа;
3. Вариант использования завершается.
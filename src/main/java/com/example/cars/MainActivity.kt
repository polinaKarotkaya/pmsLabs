package com.example.cars

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import com.google.accompanist.pager.HorizontalPager
import com.google.accompanist.pager.rememberPagerState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.example.carapp.ui.theme.CarAppTheme
import androidx.room.Room
import com.example.cars.adapters.CarAdapter

import com.example.cars.entity.CarItem
import com.example.cars.entity.Car
import com.example.cars.entity.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


@ExperimentalPagerApi
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            CarAppTheme {
                MyApp(
                    validatePrice = this@MainActivity::validatePrice // Передача функции
                )
            }
        }
    }

    private external fun validatePriceNative(price: String): Boolean
    private fun validatePrice(price: String): Boolean {
        return validatePriceNative(price) // Вызов JNI функции
    }
}

@Composable
@ExperimentalFoundationApi
@ExperimentalPagerApi
fun MyApp(
    validatePrice: (String) -> Boolean
) {
    val pagerState = rememberPagerState(initialPage = 0)

    val tabs = listOf("Поиск авто", "Мои объявления", "Корзина", "Профиль")
    val coroutineScope = rememberCoroutineScope()
    var nickname by remember { mutableStateOf("") }
    var userRole by remember { mutableStateOf("") }
    var cart by remember { mutableStateOf<List<Car>>(emptyList()) }
    var isAuthenticated by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(pagerState.currentPage) { newIndex ->
                coroutineScope.launch {
                    pagerState.scrollToPage(newIndex)
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            HorizontalPager(
                state = pagerState,
                count = tabs.size
            ) { page ->
                when (page) {
                    0 -> if (isAuthenticated) {
                        CarSearchScreen(
                            userRole = userRole,
                            onNavigateToLogin = {
                                coroutineScope.launch {
                                    pagerState.scrollToPage(3)
                                }
                            },
                            nickname = nickname,
                            validatePrice = validatePrice,
                            onAddToCart = { car ->
                                cart = cart + car
                            }
                        )
                    } else {
                        ProfileScreen(nickname = nickname, onNicknameChange = { newNickname ->
                            nickname = newNickname
                            isAuthenticated = newNickname.isNotEmpty()
                        })
                    }
                    1 -> if (isAuthenticated) {
                        MyAdsScreen(nickname = nickname)
                    } else {
                        ProfileScreen(nickname = nickname, onNicknameChange = { newNickname ->
                            nickname = newNickname
                            isAuthenticated = newNickname.isNotEmpty()
                        })
                    }
                    2 -> if (isAuthenticated) {
                        CartScreen(
                            cart = cart,
                            onRemoveFromCart = { car ->
                                cart = cart - car
                            }
                        )
                    } else {
                        ProfileScreen(nickname = nickname, onNicknameChange = { newNickname ->
                            nickname = newNickname
                            isAuthenticated = newNickname.isNotEmpty()
                        })
                    }
                    3 -> ProfileScreen(nickname = nickname, onNicknameChange = { newNickname ->
                        nickname = newNickname
                        isAuthenticated = newNickname.isNotEmpty()
                    })
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar(selectedTab: Int, onTabSelected: (Int) -> Unit) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Поиск авто") },
            label = { Text("Авто") },
            selected = selectedTab == 0,
            onClick = { onTabSelected(0) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Мои объявления") },
            label = { Text("Мои объявления") },
            selected = selectedTab == 1,
            onClick = { onTabSelected(1) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.ShoppingCart, contentDescription = "Корзина") },
            label = { Text("Корзина") },
            selected = selectedTab == 2,
            onClick = { onTabSelected(2) }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Person, contentDescription = "Профиль") },
            label = { Text("Профиль") },
            selected = selectedTab == 3,
            onClick = { onTabSelected(3) }
        )
    }
}

@Composable
fun CarSearchScreen(
    userRole: String,
    onNavigateToLogin: () -> Unit,
    nickname: String,
    validatePrice: (String) -> Boolean,
    onAddToCart: (Car) -> Unit
) {
    var showAddCarDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var allCars by remember { mutableStateOf<List<Car>>(emptyList()) }
    var availableCars by remember { mutableStateOf<List<Car>>(emptyList()) }
    var showConfirmationDialog by remember { mutableStateOf(false) }
    var selectedCar by remember { mutableStateOf<Car?>(null) }
    var showCarDetailsDialog by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    // Simulate a list of all available cars
    allCars = listOf(
        Car(make = "Toyota", model = "Camry", price = 25000.0, imageResId = R.drawable.car_image1, owner = "user1"),
        Car(make = "Honda", model = "Civic", price = 20000.0, imageResId = R.drawable.car_image2, owner = "user1"),
        Car(make = "Ford", model = "Mustang", price = 35000.0, imageResId = R.drawable.car_image3, owner = "user3"),
        Car(make = "Chevrolet", model = "Malibu", price = 22000.0, imageResId = R.drawable.car_image4, owner = "user4")
    )

    // Filter the list of available cars based on the search query
    availableCars = allCars.filter { car ->
        car.make.contains(searchQuery, ignoreCase = true) || car.model.contains(searchQuery, ignoreCase = true)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Поиск автомобилей", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        // Поле для ввода запроса поиска
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text("Введите марку или модель") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Отображение автомобилей
        if (availableCars.isNotEmpty()) {
            Text(
                text = "Доступные автомобили",
                style = MaterialTheme.typography.titleMedium
            )
            availableCars.forEach { car ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            selectedCar = car
                            showCarDetailsDialog = true
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(horizontalAlignment = Alignment.Start) {
                            Text(text = "${car.make} ${car.model}", style = MaterialTheme.typography.bodyMedium)
                            Text(text = "Цена: ${car.price} руб.", style = MaterialTheme.typography.bodySmall)
                        }
                    }
                    IconButton(
                        onClick = {
                            if (car.owner != nickname) {
                                onAddToCart(car)
                            } else {
                                errorMessage = "Вы не можете добавить свое объявление в корзину"
                            }
                        }
                    ) {
                        Icon(Icons.Filled.ShoppingCart, contentDescription = "Добавить в корзину")
                    }
                }
            }
        } else if (searchQuery.isNotEmpty()) {
            Text("Нет автомобилей по вашему запросу", style = MaterialTheme.typography.bodySmall)
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (userRole == "admin") {
            Button(onClick = { showAddCarDialog = true }) {
                Text("Добавить автомобиль")
            }
        }

        if (showConfirmationDialog) {
            ConfirmationDialog(
                onConfirm = {
                    showConfirmationDialog = false
                },
                onDismiss = { showConfirmationDialog = false }
            )
        }

        if (showAddCarDialog) {
            AddCarDialog(onDismiss = { showAddCarDialog = false }, validatePrice = validatePrice)
        }

        if (showCarDetailsDialog && selectedCar != null) {
            CarDetailsDialog(car = selectedCar!!, onDismiss = { showCarDetailsDialog = false })
        }
    }
}


@Composable
fun MyAdsScreen(nickname: String) {
    var showAddCarDialog by remember { mutableStateOf(false) }
    var showCarDetailsDialog by remember { mutableStateOf(false) }
    var selectedCar by remember { mutableStateOf<Car?>(null) }

    val hardcodedCars = listOf(
        Car(make = "Toyota", model = "Camry", price = 25000.0, imageResId = R.drawable.car_image1, owner = "user1"),
        Car(make = "Honda", model = "Civic", price = 20000.0, imageResId = R.drawable.car_image2, owner = "user2")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Мои объявления", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (nickname.isEmpty()) {
            Text("Вы не вошли в аккаунт.", style = MaterialTheme.typography.bodySmall)
        } else {
            if (hardcodedCars.isNotEmpty()) {
                Text("Ваши объявления:", style = MaterialTheme.typography.titleMedium)
                hardcodedCars.forEach { car ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                selectedCar = car
                                showCarDetailsDialog = true
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(horizontalAlignment = Alignment.Start) {
                                Text(text = "${car.make} ${car.model}", style = MaterialTheme.typography.bodyMedium)
                                Text(text = "Цена: ${car.price} руб.", style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            } else {
                Text("У вас пока нет объявлений.", style = MaterialTheme.typography.bodySmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = { showAddCarDialog = true }) {
            Text("Добавить объявление")
        }

        if (showCarDetailsDialog) {
            selectedCar?.let { car ->
                CarDetailsDialog(car = car, onDismiss = { showCarDetailsDialog = false })
            }
        }

        if (showAddCarDialog) {
            AddCarDialog(onDismiss = { showAddCarDialog = false }, validatePrice = { price -> price.toDoubleOrNull() != null })
        }
    }
}


@Composable
fun CartScreen(
    cart: List<Car>,
    onRemoveFromCart: (Car) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Корзина", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        if (cart.isNotEmpty()) {
            cart.forEach { car ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.Start) {
                        Text(text = "${car.make} ${car.model}", style = MaterialTheme.typography.bodyMedium)
                        Text(text = "Цена: ${car.price} руб.", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(onClick = { onRemoveFromCart(car) }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Удалить из корзины")
                    }
                }
            }
        } else {
            Text("Ваша корзина пуста.", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun CarDetailsDialog(car: Car, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Детали автомобиля") },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Image(
                    painter = painterResource(id = car.imageResId),
                    contentDescription = null,
                    modifier = Modifier.size(200.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Марка: ${car.make}")
                Text("Модель: ${car.model}")
                Text("Цена: ${car.price} руб.")
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Закрыть")
            }
        }
    )
}

@Composable
fun ProfileScreen(nickname: String, onNicknameChange: (String) -> Unit) {
    var isLoggedIn by remember { mutableStateOf(nickname.isNotEmpty()) }
    var showLogin by remember { mutableStateOf(true) }

    if (isLoggedIn) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Профиль", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))
            Text("Имя пользователя: $nickname")

            Button(onClick = {
                isLoggedIn = false
                onNicknameChange("")
            }) {
                Text("Выйти")
            }
        }
    } else {
        if (showLogin) {
            LoginScreen(onLoginSuccess = { loginNickname ->
                isLoggedIn = true
                onNicknameChange(loginNickname)
                showLogin = false
            })
        } else {
            RegistrationScreen(onRegisterSuccess = { registerNickname ->
                isLoggedIn = true
                onNicknameChange(registerNickname)
                showLogin = true
            })
        }

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(onClick = { showLogin = !showLogin }) {
            Text(if (showLogin) "Нет аккаунта? Зарегистрируйтесь" else "Уже есть аккаунт? Войти")
        }
    }
}

@Composable
fun ConfirmationDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Подтвердить бронирование") },
        text = { Text("Вы уверены, что хотите подтвердить бронирование?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Да")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Нет")
            }
        }
    )
}

@Composable
fun AddCarDialog(
    onDismiss: () -> Unit,
    validatePrice: (String) -> Boolean
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.background
        ) {
            AddCarScreen(
                onCarAdded = onDismiss,
                validatePrice = validatePrice
            )
        }
    }
}

@Composable
fun AddCarScreen(
    onCarAdded: () -> Unit,
    validatePrice: (String) -> Boolean
) {
    var make by remember { mutableStateOf("") }
    var model by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Добавить автомобиль", style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = make,
            onValueChange = { make = it },
            label = { Text("Марка") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = model,
            onValueChange = { model = it },
            label = { Text("Модель") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = price,
            onValueChange = { price = it },
            label = { Text("Цена") },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            if (make.isNotEmpty() && model.isNotEmpty() && validatePrice(price)) {
                // Simulate adding a car
                onCarAdded()
            } else {
                errorMessage = "Пожалуйста, заполните все поля корректно"
            }
        }) {
            Text("Добавить")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun LoginScreen(onLoginSuccess: (String) -> Unit) {
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Вход", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Никнейм") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            if (nickname.isNotEmpty() && password.isNotEmpty()) {
                // Simulate login
                onLoginSuccess(nickname)
            } else {
                errorMessage = "Пожалуйста, заполните все поля"
            }
        }) {
            Text("Войти")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun RegistrationScreen(onRegisterSuccess: (String) -> Unit) {
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Регистрация", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = nickname,
            onValueChange = { nickname = it },
            label = { Text("Никнейм") }
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Пароль") },
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = {
            if (nickname.isNotEmpty() && password.isNotEmpty()) {
                // Simulate registration
                onRegisterSuccess(nickname)
            } else {
                errorMessage = "Пожалуйста, заполните все поля"
            }
        }) {
            Text("Зарегистрироваться")
        }

        if (errorMessage.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
        }
    }
}

@Composable
fun ConfirmationDeleteDialog(onConfirm: () -> Unit, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Подтвердить удаление") },
        text = { Text("Вы уверены, что хотите удалить это бронирование?") },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Да")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Нет")
            }
        }
    )
}

@Composable
@Preview
fun MyAdsScreenPreview() {
    MyAdsScreen(nickname = "exampleNickname")
}

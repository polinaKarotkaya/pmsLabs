package com.example.cars

import CarDatabaseManager
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
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
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Delete
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.view.GestureDetectorCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.accompanist.pager.ExperimentalPagerApi
import com.example.carapp.ui.theme.CarAppTheme
import com.example.cars.adapters.AvailableCarsAdapter
import com.example.cars.adapters.CartAdapter
import com.example.cars.entity.Car
import com.example.cars.entity.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration

@ExperimentalPagerApi
@ExperimentalFoundationApi
class MainActivity : ComponentActivity() {
    private lateinit var databaseManager: CarDatabaseManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        databaseManager = CarDatabaseManager(applicationContext)

        setContent {
            CarAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MyApp(
                        validatePrice = this@MainActivity::validatePrice,
                        databaseManager = databaseManager
                    )
                }
            }
        }
    }

    private fun validatePrice(price: String): Boolean {
        return try {
            price.toDouble()
            true
        } catch (e: NumberFormatException) {
            false
        }
    }

    @Composable
    fun AdaptiveLayout(
        phoneContent: @Composable () -> Unit,
        tabletContent: @Composable () -> Unit
    ) {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp

        if (screenWidth > 600) {
            tabletContent()
        } else {
            phoneContent()
        }
    }

    @ExperimentalFoundationApi
    @ExperimentalPagerApi
    @Composable
    fun MyApp(
        validatePrice: (String) -> Boolean,
        databaseManager: CarDatabaseManager
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
                    AdaptiveLayout(
                        phoneContent = {
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
                                        },
                                        databaseManager = databaseManager,
                                        cart = cart // Передаем текущую корзину
                                    )
                                } else {
                                    ProfileScreen(
                                        nickname = nickname,
                                        onNicknameChange = { newNickname ->
                                            nickname = newNickname
                                            isAuthenticated = newNickname.isNotEmpty()
                                        },
                                        databaseManager = databaseManager
                                    )
                                }

                                1 -> if (isAuthenticated) {
                                    MyAdsScreen(
                                        nickname = nickname,
                                        userRole = userRole,
                                        databaseManager = databaseManager,
                                        cart = cart
                                    )
                                } else {
                                    ProfileScreen(
                                        nickname = nickname,
                                        onNicknameChange = { newNickname ->
                                            nickname = newNickname
                                            isAuthenticated = newNickname.isNotEmpty()
                                        },
                                        databaseManager = databaseManager
                                    )
                                }

                                2 -> if (isAuthenticated) {
                                    CartScreen(
                                        cart = cart,
                                        onRemoveFromCart = { car ->
                                            cart = cart - car
                                        },
                                    )
                                } else {
                                    ProfileScreen(
                                        nickname = nickname,
                                        onNicknameChange = { newNickname ->
                                            nickname = newNickname
                                            isAuthenticated = newNickname.isNotEmpty()
                                        },
                                        databaseManager = databaseManager
                                    )
                                }

                                3 -> ProfileScreen(
                                    nickname = nickname,
                                    onNicknameChange = { newNickname ->
                                        nickname = newNickname
                                        isAuthenticated = newNickname.isNotEmpty()
                                    },
                                    databaseManager = databaseManager
                                )
                            }
                        },
                        tabletContent = {
                            when (page) {
                                0 -> if (isAuthenticated) {
                                    CarSearchScreenTablet(
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
                                        },
                                        databaseManager = databaseManager,
                                        cart = cart // Передаем текущую корзину
                                    )
                                } else {
                                    ProfileScreenTablet(
                                        nickname = nickname,
                                        onNicknameChange = { newNickname ->
                                            nickname = newNickname
                                            isAuthenticated = newNickname.isNotEmpty()
                                        },
                                        databaseManager = databaseManager
                                    )
                                }

                                1 -> if (isAuthenticated) {
                                    MyAdsScreenTablet(
                                        nickname = nickname,
                                        userRole = userRole,
                                        databaseManager = databaseManager,
                                        cart = cart
                                    )
                                } else {
                                    ProfileScreenTablet(
                                        nickname = nickname,
                                        onNicknameChange = { newNickname ->
                                            nickname = newNickname
                                            isAuthenticated = newNickname.isNotEmpty()
                                        },
                                        databaseManager = databaseManager
                                    )
                                }

                                2 -> if (isAuthenticated) {
                                    CartScreenTablet(
                                        cart = cart,
                                        onRemoveFromCart = { car ->
                                            cart = cart - car
                                        }
                                    )
                                } else {
                                    ProfileScreenTablet(
                                        nickname = nickname,
                                        onNicknameChange = { newNickname ->
                                            nickname = newNickname
                                            isAuthenticated = newNickname.isNotEmpty()
                                        },
                                        databaseManager = databaseManager
                                    )
                                }

                                3 -> ProfileScreenTablet(
                                    nickname = nickname,
                                    onNicknameChange = { newNickname ->
                                        nickname = newNickname
                                        isAuthenticated = newNickname.isNotEmpty()
                                    },
                                    databaseManager = databaseManager
                                )
                            }
                        }
                    )
                }
            }
        }
    }


    @Composable
    fun CarSearchScreenTablet(
        userRole: String,
        onNavigateToLogin: () -> Unit,
        nickname: String,
        validatePrice: (String) -> Boolean,
        onAddToCart: (Car) -> Unit,
        databaseManager: CarDatabaseManager,
        cart: List<Car>
    ) {
        CarSearchScreen(
            userRole = userRole,
            onNavigateToLogin = onNavigateToLogin,
            nickname = nickname,
            validatePrice = validatePrice,
            onAddToCart = onAddToCart,
            databaseManager = databaseManager,
            cart = cart
        )
    }

    @Composable
    fun MyAdsScreenTablet(
        nickname: String,
        userRole: String,
        databaseManager: CarDatabaseManager,
        cart: List<Car>
    ) {
        MyAdsScreen(
            nickname = nickname,
            userRole = userRole,
            databaseManager = databaseManager,
            cart = cart
        )
    }

    @Composable
    fun CartScreenTablet(
        cart: List<Car>,
        onRemoveFromCart: (Car) -> Unit,

        ) {
        CartScreen(
            cart = cart,
            onRemoveFromCart = onRemoveFromCart,
        )
    }

    @Composable
    fun ProfileScreenTablet(
        nickname: String,
        onNicknameChange: (String) -> Unit,
        databaseManager: CarDatabaseManager
    ) {
        ProfileScreen(
            nickname = nickname,
            onNicknameChange = onNicknameChange,
            databaseManager = databaseManager
        )
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
        onAddToCart: (Car) -> Unit,
        databaseManager: CarDatabaseManager,
        cart: List<Car>
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

        LaunchedEffect(Unit) {
            allCars = withContext(Dispatchers.IO) {
                databaseManager.getAllCars()
            }
        }


        availableCars = allCars.filter { car ->
            (car.make.contains(searchQuery, ignoreCase = true) || car.model.contains(
                searchQuery,
                ignoreCase = true
            )) && car.owner != nickname
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

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Введите марку или модель") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (availableCars.isNotEmpty()) {
                Text(
                    text = "Доступные автомобили",
                    style = MaterialTheme.typography.titleMedium
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(availableCars) { car ->
                        CarItem(
                            car = car,
                            onAddToCart = {
                                if (cart.any { it.id == car.id }) {
                                    errorMessage = "Это объявление уже в корзине"
                                } else {
                                    onAddToCart(car)
                                }
                            },
                            onShowDetails = { selectedCar = car; showCarDetailsDialog = true }
                        )
                    }
                }
            } else if (searchQuery.isNotEmpty()) {
                Text(
                    "Нет автомобилей по вашему запросу",
                    style = MaterialTheme.typography.bodySmall
                )
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
                AddCarDialog(
                    onDismiss = { showAddCarDialog = false },
                    validatePrice = validatePrice,
                    databaseManager = databaseManager,
                    nickname = nickname // Передаем nickname
                )
            }

            if (showCarDetailsDialog && selectedCar != null) {
                CarDetailsDialog(car = selectedCar!!, onDismiss = { showCarDetailsDialog = false })
            }
        }
    }


    @Composable
    fun CarItem(
        car: Car,
        onAddToCart: (Car) -> Unit,
        onShowDetails: () -> Unit
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { onShowDetails() }
                    )
                },
            elevation = CardDefaults.cardElevation(4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "${car.make} ${car.model}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Цена: ${car.price} руб.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Button(onClick = { onAddToCart(car) }) {
                    Text("Добавить в корзину")
                }
            }
        }
    }

    @Composable
    fun AddCarDialog(
        onDismiss: () -> Unit,
        validatePrice: (String) -> Boolean,
        databaseManager: CarDatabaseManager,
        nickname: String,
        carToEdit: Car? = null
    ) {
        var make by remember { mutableStateOf(carToEdit?.make ?: "") }
        var model by remember { mutableStateOf(carToEdit?.model ?: "") }
        var price by remember { mutableStateOf(carToEdit?.price?.toString() ?: "") }
        var errorMessage by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
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
                            coroutineScope.launch {
                                val car = Car(
                                    id = carToEdit?.id ?: 0,
                                    make = make,
                                    model = model,
                                    price = price.toDouble(),
                                    imageResId = R.drawable.car_image1,
                                    owner = nickname
                                )
                                val result = withContext(Dispatchers.IO) {
                                    if (carToEdit != null) {
                                        databaseManager.updateCar(car)
                                    } else {
                                        databaseManager.insertCar(car)
                                    }
                                }
                                if (result != -1L) {
                                    onDismiss()
                                } else {
                                    errorMessage = "Ошибка добавления автомобиля"
                                }
                            }
                        } else {
                            errorMessage = "Пожалуйста, заполните все поля корректно"
                        }
                    }) {
                        Text(if (carToEdit != null) "Сохранить" else "Добавить")
                    }

                    if (errorMessage.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }

    @Composable
    fun LoginScreen(onLoginSuccess: (String) -> Unit, databaseManager: CarDatabaseManager) {
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
                    coroutineScope.launch {
                        val user = withContext(Dispatchers.IO) {
                            databaseManager.getUserByNickname(nickname)
                        }
                        if (user != null && user.password == password) {
                            onLoginSuccess(nickname)
                        } else {
                            errorMessage = "Неверный никнейм или пароль"
                        }
                    }
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
    fun RegistrationScreen(
        onRegisterSuccess: (String) -> Unit,
        databaseManager: CarDatabaseManager
    ) {
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
                    coroutineScope.launch {
                        val user = User(nickname = nickname, password = password)
                        val result = withContext(Dispatchers.IO) {
                            databaseManager.insertUser(user)
                        }
                        if (result != -1L) {
                            onRegisterSuccess(nickname)
                        } else {
                            errorMessage = "Ошибка регистрации"
                        }
                    }
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
    fun MyAdsScreen(
        nickname: String,
        userRole: String,
        databaseManager: CarDatabaseManager,
        cart: List<Car>
    ) {
        var myCars by remember { mutableStateOf<List<Car>>(emptyList()) }
        var showEditDialog by remember { mutableStateOf(false) }
        var carToEdit by remember { mutableStateOf<Car?>(null) }
        var showCarInfo by remember { mutableStateOf(false) }
        var selectedCar by remember { mutableStateOf<Car?>(null) }
        var showAddCarDialog by remember { mutableStateOf(false) }

        LaunchedEffect(Unit) {
            myCars = withContext(Dispatchers.IO) {
                databaseManager.getCarsByOwner(nickname)
            }
        }

        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            Text("Мои объявления", style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = Modifier.height(16.dp))

            if (myCars.isNotEmpty()) {
                LazyColumn {
                    items(myCars) { car ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .pointerInput(Unit) {
                                    detectTapGestures(
                                        onLongPress = {
                                            carToEdit = car
                                            showEditDialog = true
                                        },
                                        onDoubleTap = {
                                            selectedCar = car
                                            showCarInfo = true
                                        }
                                    )
                                },
                            elevation = CardDefaults.cardElevation(4.dp),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface // Белый фон
                            )
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = "${car.make} ${car.model}",
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.onSurface // Черный текст
                                )
                                Text(
                                    text = "Цена: ${car.price} руб.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface // Черный текст
                                )
                            }
                        }
                    }
                }
            } else {
                Text("У вас пока нет объявлений.", style = MaterialTheme.typography.bodySmall)
            }

            Spacer(modifier = Modifier.height(32.dp))

            if (userRole == "admin" || nickname.isNotEmpty()) {
                Button(onClick = { showAddCarDialog = true }) {
                    Text("Добавить автомобиль")
                }
            }
        }

        if (showEditDialog && carToEdit != null) {
            AddCarDialog(
                onDismiss = { showEditDialog = false },
                validatePrice = { price -> price.toDoubleOrNull() != null },
                databaseManager = databaseManager,
                carToEdit = carToEdit,
                nickname = nickname
            )
        }

        if (showCarInfo && selectedCar != null) {
            CarInfoDialog(
                onDismiss = { showCarInfo = false },
                car = selectedCar!!
            )
        }

        if (showAddCarDialog) {
            AddCarDialog(
                onDismiss = { showAddCarDialog = false },
                validatePrice = { price -> price.toDoubleOrNull() != null },
                databaseManager = databaseManager,
                nickname = nickname
            )
        }
    }

    @Composable
    fun CarInfoDialog(onDismiss: () -> Unit, car: Car) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Информация об автомобиле",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Марка: ${car.make}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Модель: ${car.model}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Text(
                        text = "Цена: ${car.price} руб.",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onDismiss) {
                        Text("Закрыть")
                    }
                }
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
                AndroidView(
                    factory = { context ->
                        RecyclerView(context).apply {
                            layoutManager = LinearLayoutManager(context)
                            adapter = CartAdapter(cart, onRemoveFromCart)
                        }
                    }
                )
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
    fun ProfileScreen(
        nickname: String,
        onNicknameChange: (String) -> Unit,
        databaseManager: CarDatabaseManager
    ) {
        var isLoggedIn by remember { mutableStateOf(nickname.isNotEmpty()) }
        var showLogin by remember { mutableStateOf(true) }
        var errorMessage by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

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
                    coroutineScope.launch {
                        val user = withContext(Dispatchers.IO) {
                            databaseManager.getUserByNickname(loginNickname)
                        }
                        if (user != null) {
                            isLoggedIn = true
                            onNicknameChange(loginNickname)
                            showLogin = false
                        } else {
                            errorMessage = "Неверный никнейм или пароль"
                        }
                    }
                }, databaseManager = databaseManager)
            } else {
                RegistrationScreen(onRegisterSuccess = { registerNickname ->
                    coroutineScope.launch {
                        val user = withContext(Dispatchers.IO) {
                            databaseManager.getUserByNickname(registerNickname)
                        }
                        if (user != null) {
                            isLoggedIn = true
                            onNicknameChange(registerNickname)
                            showLogin = true
                        } else {
                            errorMessage = "Неверный никнейм или пароль"
                        }
                    }
                }, databaseManager = databaseManager)
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(onClick = { showLogin = !showLogin }) {
                Text(if (showLogin) "Нет аккаунта? Зарегистрируйтесь" else "Уже есть аккаунт? Войти")
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
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
    fun AddCarScreen(
        onCarAdded: () -> Unit,
        validatePrice: (String) -> Boolean,
        databaseManager: CarDatabaseManager,
        nickname: String,
        carToEdit: Car? = null
    ) {
        var make by remember { mutableStateOf(carToEdit?.make ?: "") }
        var model by remember { mutableStateOf(carToEdit?.model ?: "") }
        var price by remember { mutableStateOf(carToEdit?.price?.toString() ?: "") }
        var errorMessage by remember { mutableStateOf("") }
        val coroutineScope = rememberCoroutineScope()

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
                    coroutineScope.launch {
                        val car = Car(
                            id = carToEdit?.id ?: 0,
                            make = make,
                            model = model,
                            price = price.toDouble(),
                            imageResId = R.drawable.car_image1,
                            owner = nickname // Записываем объявление на конкретного пользователя
                        )
                        val result = withContext(Dispatchers.IO) {
                            if (carToEdit != null) {
                                databaseManager.updateCar(car)
                            } else {
                                databaseManager.insertCar(car)
                            }
                        }
                        if (result != -1L) {
                            onCarAdded()
                        } else {
                            errorMessage = "Ошибка добавления автомобиля"
                        }
                    }
                } else {
                    errorMessage = "Пожалуйста, заполните все поля корректно"
                }
            }) {
                Text(if (carToEdit != null) "Сохранить" else "Добавить")
            }

            if (errorMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

package com.usoftware.challenge.presentation.rate_tracker

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ShowChart
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usoftware.challenge.R
import com.usoftware.challenge.data.database.database_models.RateChangeNameDBModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Currency
import java.util.Locale

const val INTERVAL = 5000L

@Composable
fun RateTrackerScreen(
    viewModel: RateTrackerViewModel = hiltViewModel(),
    onAddAssetClick: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    var lastUpdateText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    val lifecycleOwner = LocalLifecycleOwner.current
    val lifecycleState by lifecycleOwner.lifecycle.currentStateFlow.collectAsState()

    LaunchedEffect(lifecycleState) {

        withContext(Dispatchers.IO){
            while (lifecycleState.isAtLeast(Lifecycle.State.STARTED)) {
                viewModel.state.handleEvent(RateTrackerEvent.RequestRates)

                val interval =
                    System.currentTimeMillis() - (viewState.model.firstOrNull()?.timestamp ?: 0L)

                lastUpdateText = if (interval < 5000) {
                    "Just now"
                } else if (interval > 1000 * 60 * 60 * 24) {
                    ""
                } else {
                    val hours = interval / 1000 / 60 / 24
                    val minutes = interval / 1000 / 60 % 24
                    val seconds = interval / 1000 % 60
                    String.format(Locale.getDefault(), "%02d:%02d:%02d ago", hours, minutes, seconds)
                }
                delay(INTERVAL)
            }
        }
    }

    val onDismissClick: (String) -> Unit = { code ->
        viewModel.state.handleEvent(RateTrackerEvent.DismissCurrency(code))
        coroutineScope.launch {
            val result = snackbarHostState.showSnackbar(
                message = "${code} removed",
                actionLabel = "Undo",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.state.handleEvent(RateTrackerEvent.RestoreCurrency(code))
            }
        }
    }

    val onDismissDialogClick: () -> Unit = {
        viewModel.state.handleEvent(RateTrackerEvent.HideError)
    }

    RateTrackerScreenUI(viewState = viewState,
        lastUpdateText = lastUpdateText,
        onAddAssetClick = onAddAssetClick,
        onDismissClick = onDismissClick,
        onDismissDialogClick = onDismissDialogClick,
        snackbarHostState = snackbarHostState)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class,
    ExperimentalFoundationApi::class
)
@Composable
fun RateTrackerScreenUI(
    viewState: RateTrackerState,
    lastUpdateText: String,
    onAddAssetClick: () -> Unit,
    onDismissClick: (String) -> Unit,
    onDismissDialogClick: () -> Unit,
    snackbarHostState: SnackbarHostState
) {

    val listScrollState = rememberLazyListState()

    if (viewState.errorMsg != null) {
        AlertDialog(
            title = { Text(text = "Error") },
            text = { Text(text = viewState.errorMsg) },
            onDismissRequest = onDismissDialogClick,
            confirmButton = {
                TextButton(
                    onClick = onDismissDialogClick
                ) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = onDismissDialogClick

                ) {
                    Text(stringResource(R.string.dismiss))
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.exchange_rates)) },
                actions = {
                    IconButton(onClick = onAddAssetClick) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                }
            )
        },
        bottomBar = { BottomNavigationBar() },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF6F6FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.last_updated, lastUpdateText),
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp),
                fontSize = 14.sp,
                color = Color.Gray
            )

            LazyColumn(
                state = listScrollState,
                modifier = Modifier.clipToBounds()
            ) {
                items(
                    items = viewState.model,
                    key = { item -> item.code }
                ) { item ->
                    val dismissState = rememberDismissState(
                        confirmStateChange = { dismissValue ->
                            if (dismissValue == DismissValue.DismissedToEnd || dismissValue == DismissValue.DismissedToStart) {
                                onDismissClick(item.code)
                                true
                            } else {
                                false
                            }
                        }
                    )

                    SwipeToDismiss(
                        modifier = Modifier
                            .clipToBounds()
                            .animateItemPlacement(),
                        state = dismissState,
                        background = {
                            val color = when (dismissState.dismissDirection) {
                                DismissDirection.StartToEnd, DismissDirection.EndToStart -> Color.Red
                                null -> Color.Transparent
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(color)
                                    .padding(horizontal = 20.dp),
                                contentAlignment = Alignment.CenterEnd
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    tint = Color.White
                                )
                            }
                        },
                        directions = setOf(DismissDirection.EndToStart),
                        dismissContent = {
                            Box(
                                modifier = Modifier
                                    .animateItem()
                                    .fillMaxWidth()
                            ) {
                                ExchangeRateItem(
                                    code = item.code,
                                    name = item.name,
                                    value = String.format(Locale.US, "%.6f", item.endRate),
                                    change = String.format(Locale.US, "%.4f%%", item.changePct)
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ExchangeRateItem(code: String, name: String, value: String, change: String) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF2C2C2E), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = Currency.getInstance(code).getSymbol(Locale.US),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(text = code, fontWeight = FontWeight.Bold)
                Text(text = name, fontSize = 12.sp, color = Color.Gray)
            }

            Column(horizontalAlignment = Alignment.End) {
                AnimatedValueChange(value = value) { bgColor ->
                    Text(
                        text = value,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .background(bgColor)
                            .padding(horizontal = 4.dp)
                    )
                }

                AnimatedValueChange(value = change) { bgColor ->
                    Text(
                        text = change,
                        fontSize = 12.sp,
                        color = if (change.startsWith("-")) Color.Red else Color(0xFF2ECC71),
                        modifier = Modifier
                            .background(bgColor)
                            .padding(horizontal = 4.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun BottomNavigationBar() {
    NavigationBar {
        NavigationBarItem(
            selected = true,
            onClick = { },
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            label = { Text(stringResource(R.string.home)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Star, contentDescription = "Favorites") },
            label = { Text(stringResource(R.string.favorites)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.AutoMirrored.Filled.ShowChart, contentDescription = "Markets") },
            label = { Text(stringResource(R.string.markets)) }
        )
        NavigationBarItem(
            selected = false,
            onClick = { },
            icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
            label = { Text(stringResource(R.string.settings)) }
        )
    }
}

@Composable
fun AnimatedValueChange(
    value: String,
    content: @Composable (Color) -> Unit
) {
    val animatedColor = remember { Animatable(Color.Transparent) }
    val previousValue = remember { mutableStateOf(value) }

    LaunchedEffect(value) {
        if (value != previousValue.value) {
            animatedColor.animateTo(
                targetValue = Color.Yellow.copy(alpha = 0.5f),
                animationSpec = tween(durationMillis = 400)
            )
            animatedColor.animateTo(
                targetValue = Color.Transparent,
                animationSpec = tween(durationMillis = 800)
            )
            previousValue.value = value
        }
    }

    content(animatedColor.value)
}

@Preview(showBackground = true)
@Composable
fun RateTrackerScreenUIPreview() {
    val state = RateTrackerState(
        model = listOf(
            RateChangeNameDBModel(
                "USD",
                "US Dollar",
                1643723900,
                1.2345,
                0.0123,
                1.1111,
                1.1321
            ),
            RateChangeNameDBModel("EUR", "Euro", 1643724000, 0.8934, -0.014, 0.8935, 0.9898),
            RateChangeNameDBModel(
                "BTC",
                "Bitcoin",
                1643724100,
                42368.99,
                0.012,
                42369.01,
                54654.0
            )
        )
    )
    val snackbarHostState = remember { SnackbarHostState() }
    RateTrackerScreenUI(state, "lastUpdateText", {}, {}, {}, snackbarHostState )
}

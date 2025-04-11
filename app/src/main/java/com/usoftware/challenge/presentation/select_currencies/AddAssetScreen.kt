package com.usoftware.challenge.presentation.select_currencies

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.usoftware.challenge.R
import com.usoftware.challenge.data.database.database_models.CurrencyDBModel
import java.util.Currency
import java.util.Locale

@Composable
fun AddAssetScreen(
    navigateBack: () -> Unit,
    viewModel: AddAssetViewModel = hiltViewModel()
    )
{
    val viewState by viewModel.state.collectAsStateWithLifecycle()
    val onFavoriteClick : (String) -> Unit = {
        code -> viewModel.state.handleEvent(AddAssetEvent.SetCurrencyFavorite(code))
    }

    val onUnFavoriteClick : (String) -> Unit = {
            code -> viewModel.state.handleEvent(AddAssetEvent.UnSetCurrencyFavorite(code))
    }

    val onDismissDialogClick: () -> Unit = {
        viewModel.state.handleEvent(AddAssetEvent.HideError)
    }

    AddAssetScreenUI(navigateBack, viewState, onFavoriteClick, onUnFavoriteClick, onDismissDialogClick)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAssetScreenUI(
    navigateBack: () -> Unit,
    viewState: AddAssetState,
    onFavoriteClick: (String) -> Unit,
    onUnFavoriteClick: (String) -> Unit,
    onDismissDialogClick: () -> Unit
) {

    val searchQuery = rememberSaveable { mutableStateOf("") }
    val listScrollState = rememberLazyListState()

    val filteredList = remember(searchQuery.value, viewState.model) {
        viewState.model.filter {
            it.code.contains(searchQuery.value, ignoreCase = true) ||
                    it.name.contains(searchQuery.value, ignoreCase = true)
        }
    }

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
                title = { Text(stringResource(R.string.add_asset)) },
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(onClick = navigateBack) {
                        Text(stringResource(R.string.done))
                    }
                }
            )
        },
        containerColor = Color(0xFFF6F6FA)
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            OutlinedTextField(
                value = searchQuery.value,
                onValueChange = { searchQuery.value = it },
                placeholder = { Text(stringResource(R.string.search_assets)) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = Color.Black
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                singleLine = true
            )


            LazyColumn(
                state = listScrollState,
                modifier = Modifier
                    .fillMaxSize()
            ) {
                items(
                    items = filteredList,
                    key = { it.code }
                ) { item ->
                    Box(
                        modifier = Modifier.animateItem()
                            .fillMaxWidth()
                    ) {
                        AssetItem(
                            code = item.code,
                            name = item.name,
                            favorite = item.favorite,
                            onFavoriteClick = onFavoriteClick,
                            onUnFavoriteClick = onUnFavoriteClick
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AssetItem(code: String, name: String, favorite: Boolean,
              onFavoriteClick: (String) -> Unit,
              onUnFavoriteClick: (String) -> Unit ) {
    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            ,
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(0xFF2C2C2E), shape = CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = Currency.getInstance(code).getSymbol(Locale.US), color = Color.White, fontWeight = FontWeight.Bold)
            }

            Column(
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            ) {
                Text(text = code, fontWeight = FontWeight.Bold)
                Text(text = name, fontSize = 12.sp, color = Color.Gray)
            }

            RadioButton(selected = favorite, onClick = {
                    if (favorite) onUnFavoriteClick(code) else onFavoriteClick(code)

            })
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AddAssetScreenPreview() {

    val model = listOf<CurrencyDBModel>(
        CurrencyDBModel("UAH", "Ukrainian Hryvnia", true),
        CurrencyDBModel("USD", "United States Dollar", false),
        CurrencyDBModel("TWD", "New Taiwan Dollar", true),
        CurrencyDBModel("LYD", "Libyan Dinar", false),
        CurrencyDBModel("BIF", "Burundian Franc", true)
    )

    AddAssetScreenUI({}, AddAssetState(model), { }, { }, {})
}

package com.example.weathercompose.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.weathercompose.R
import com.example.weathercompose.domain.model.city.CityDomainModel
import com.example.weathercompose.ui.UIState
import com.example.weathercompose.ui.navigation.NavigationRoutes
import com.example.weathercompose.ui.viewmodel.CitySearchViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private const val TAG = "CitiesManagerCompose"

@Composable
fun CitiesManagerContent(
    viewModel: CitySearchViewModel,
    navController: NavController,
) {
    var query by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val citySearchResult by viewModel.citySearchResult.collectAsState(initial = UIState.Loading())
    val onCityItemClick = { city: CityDomainModel ->
        coroutineScope.launch {
            viewModel.saveCity(city)

            val savedStateHandle =
                navController.getBackStackEntry<NavigationRoutes.Forecast>().savedStateHandle
            savedStateHandle[SAVED_CITY_ID_KEY] = city.id
            navController.popBackStack<NavigationRoutes.Forecast>(inclusive = false)
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SearchBox(
            query = query,
            onQueryChanged = { newQuery ->
                query = newQuery
                coroutineScope.launch {
                    viewModel.searchCity(name = newQuery)
                }
            }
        )

        when (citySearchResult) {
            is UIState.Content -> {
                CityList(
                    cities = (citySearchResult as UIState.Content<List<CityDomainModel>>).data,
                    onCityItemClick = onCityItemClick,
                )
            }

            is UIState.Empty -> {}

            is UIState.Error -> {}

            is UIState.Loading -> {
                LoadingProgressBar()
            }
        }
    }
}

@Composable
fun SearchBox(
    query: String,
    onQueryChanged: (String) -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.padding(top = 30.dp, bottom = 15.dp)
    ) {
        BasicTextField(
            modifier = Modifier
                .size(width = 225.dp, height = 40.dp)
                .background(
                    color = colorResource(R.color.liberty),
                    shape = RoundedCornerShape(15.dp)
                )
                .padding(top = 10.dp, start = 15.dp, end = 15.dp),
            value = query,
            onValueChange = onQueryChanged,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = Color.White,
                fontSize = 18.sp
            ),
            singleLine = true,
        )
    }
}

@Composable
fun CityList(
    cities: List<CityDomainModel>,
    modifier: Modifier = Modifier,
    onCityItemClick: (CityDomainModel) -> Job,
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp, bottom = 10.dp, start = 15.dp, end = 15.dp)
    ) {
        itemsIndexed(cities) { index, city ->
            CityListItem(
                city = city,
                onCityItemClick = onCityItemClick,
            )
            if (index < cities.size - 1) {
                HorizontalDivider(
                    thickness = 1.dp,
                    color = colorResource(R.color.castle_moat)
                )
            }
        }
    }
}

@Composable
fun CityListItem(
    city: CityDomainModel,
    onCityItemClick: (CityDomainModel) -> Job,
) {
    Row(
        modifier = Modifier
            .padding(15.dp)
            .clickable {
                onCityItemClick(city)
            }
    ) {
        Text(
            color = Color.White,
            text = city.getFullLocation(),
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
fun LoadingProgressBar() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = Color.White,
            modifier = Modifier.size(50.dp)
        )
    }
}

// search bar with material design
/*
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CitySearchBar(
    query: String,
    onQueryChange: suspend (String) -> Unit,
) {

    var active by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    SearchBar(
        query = query,
        onQueryChange = { newQuery ->
            coroutineScope.launch {
                onQueryChange(newQuery)
            }
        },
        onSearch = { active = false },
        active = active,
        onActiveChange = { active = it },
        placeholder = {
            CityList()
        }
    )
}
 */
package com.example.weathercompose.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.navigation.NavController
import com.example.weathercompose.R
import com.example.weathercompose.ui.UIState
import com.example.weathercompose.ui.model.CityUIModel
import com.example.weathercompose.ui.navigation.NavigationRoutes
import com.example.weathercompose.ui.viewmodel.CityManagerViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun CityListContent(
    viewModel: CityManagerViewModel,
    navController: NavController,
) {
    val citiesLoadResult by viewModel.citiesUIState.collectAsState(initial = UIState.Loading())
    val onButtonClick = { navController.navigate(NavigationRoutes.CitySearch) }
    val coroutineScope = rememberCoroutineScope()
    val onCityItemClick = { city: CityUIModel ->
        coroutineScope.launch {
            val previousBackStackEntry = navController.previousBackStackEntry
            val savedStateHandle = previousBackStackEntry?.savedStateHandle
            savedStateHandle?.set(SAVED_CITY_ID_KEY, city.id)
            navController.popBackStack()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 10.dp)
            .padding(top = 10.dp, bottom = 20.dp)
    ) {
        when (citiesLoadResult) {
            is UIState.Content -> {
                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxSize()
                ) {
                    val (cityList, addCityButton) = createRefs()
                    val cityListModifier = Modifier.constrainAs(cityList) {
                        top.linkTo(parent.top)
                        bottom.linkTo(addCityButton.top, margin = 20.dp)
                        height = Dimension.fillToConstraints
                    }
                    val addCityButtonModifier = Modifier.constrainAs(addCityButton) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }

                    CityList(
                        cities = (citiesLoadResult as UIState.Content<List<CityUIModel>>).data,
                        modifier = cityListModifier,
                        onCityItemClick = onCityItemClick,
                    )

                    AddCityButton(
                        onButtonClick = onButtonClick,
                        modifier = addCityButtonModifier,
                    )
                }
            }

            is UIState.Empty -> {}
            is UIState.Error -> {}
            is UIState.Loading -> {}
        }
    }
}

@Composable
private fun CityList(
    cities: List<CityUIModel>,
    modifier: Modifier = Modifier,
    onCityItemClick: (CityUIModel) -> Job,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth()
    ) {
        itemsIndexed(cities) { index, item ->
            CityListItem(
                city = item,
                onCityItemClick = onCityItemClick,
            )
            if (index != cities.size - 1) {
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    thickness = 5.dp,
                    color = Color.Transparent
                )
            }
        }
    }
}

@Composable
private fun CityListItem(
    city: CityUIModel,
    onCityItemClick: (CityUIModel) -> Job,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(
                color = colorResource(R.color.liberty),
                shape = RoundedCornerShape(15.dp)
            )
            .clickable {
                onCityItemClick(city)
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = city.getFullLocation(),
            modifier = Modifier.padding(horizontal = 10.dp),
            color = Color.White,
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
        )
    }
}

@Composable
private fun AddCityButton(
    onButtonClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onButtonClick,
        modifier = modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        colors = ButtonColors(
            containerColor = colorResource(R.color.liberty),
            contentColor = Color.White,
            disabledContentColor = Color.Gray,
            disabledContainerColor = Color.Gray,
        )
    ) {
        Text(
            text = "Add city",
            modifier = Modifier
                .padding(
                    vertical = 10.dp,
                    horizontal = 25.dp
                ),
            color = Color.White,
            fontSize = 18.sp,
        )
    }
}
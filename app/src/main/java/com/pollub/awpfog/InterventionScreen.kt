package com.pollub.awpfog

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.rememberMarkerState

@Composable
fun InterventionScreen() {

    var interventionStarted = remember { mutableStateOf(false) }
    var supportAlongTheWay = remember { mutableStateOf(false) }

    val mapPosition = LatLng(51.2299, 22.5562)

    var uiSettings = remember { mutableStateOf(MapUiSettings()) }

    var properties = remember {
        mutableStateOf(MapProperties(
            mapType = MapType.NORMAL,
            mapStyleOptions = MapStyleOptions(MapStyle.json)
        ))
    }

    val mapMarkerState = rememberMarkerState(position = mapPosition)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.Builder()
            .target(mapPosition)
            .zoom(14f)
            .build()
    }
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            InterventionTopAppBar("Interwencja nr 2137",{})
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier.padding(innerPadding)
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = properties.value,
                    uiSettings = uiSettings.value

                ){
                    Marker(
                        state = mapMarkerState,
                        title = "MARKER"
                    )
                }
                Button(
                    onClick = { /*TODO: Call action*/ },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(5.dp),
                    modifier = Modifier
                        .padding(5.dp)
                        .size(60.dp)
                        .align(Alignment.TopEnd),
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.baseline_navigation_24),
                        contentDescription = "Call",
                        modifier = Modifier.size(40.dp).rotate(45f),
                    )
                }

            }


            // Intervention details
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .padding(top = 10.dp)
            ) {
                Text(
                    text = "Interwencja nr 2137",
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 20.sp
                )
                Text(
                    text = "Krańcowa 1, Lublin, 20-001",
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }

            InterventionButtons(interventionStarted, supportAlongTheWay)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InterventionTopAppBar(
    title: String,
    onMenuClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = title,
                color = Color.White
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Menu",
                    tint = Color.White
                )
            }
        },
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.primary
        )
    )
}

@Composable
fun InterventionButtons(interventionStarted: MutableState<Boolean>, supportAlongTheWay:  MutableState<Boolean>){
    if(!interventionStarted.value)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { interventionStarted.value=true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(80.dp)
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = "Potwierdź przybycie", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
            Button(
                onClick = {
                    interventionStarted.value=false
                    supportAlongTheWay.value=false
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(80.dp)
                    .weight(1f)
                    .padding(start = 8.dp)
            ) {
                Text(text = "Przerwij interwencję", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }
    else
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 0.dp),
        ) {
            Button(
                onClick = {
                    interventionStarted.value=false
                    supportAlongTheWay.value=false
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()

            ) {
                Text(text = "Zakończ interwencję", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
            Button(
                onClick = {
                    supportAlongTheWay.value=true
                },
                colors = ButtonDefaults.buttonColors(containerColor = if (supportAlongTheWay.value) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .height(100.dp)
                    .padding(bottom = 16.dp)
                    .fillMaxWidth()
            ) {
                if(supportAlongTheWay.value)
                    Text(text = "Wsparcie w drodze", color = Color.DarkGray, fontSize = 18.sp, textAlign = TextAlign.Center)
                else
                    Text(text = "Wezwij wsparcie", color = Color.White, fontSize = 18.sp, textAlign = TextAlign.Center)
            }
        }

}

@Preview(showBackground = true)
@Composable
fun InterventionScreenPreview() {
    InterventionScreen()
}

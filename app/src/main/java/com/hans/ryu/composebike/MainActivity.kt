package com.hans.ryu.composebike

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.hans.ryu.composebike.ui.theme.ComposeBikeTheme
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ComposeBikeTheme {
                Screen()
            }
        }
    }

    @Composable
    fun Screen(lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current) {
        Log.d(TAG, "Screen()")

        var currentWindowState by remember { mutableStateOf(WindowState.Open) }

        Surface(color = MaterialTheme.colors.primary, modifier = Modifier.fillMaxSize()) {
            when (currentWindowState) {
                WindowState.Open -> Window(
                    onMinimize = {
                        currentWindowState = WindowState.Minimized
                    },
                    onClose = {
                        //FIXME rememberCoroutineScope
                        //FIXME window??? ?????? ??? unloadingResourcesForALongTime() ??? ???????????? ?????????
                        //FIXME suspend function ?????? ??? ??? ??????. ?????????????
                        currentWindowState = WindowState.Closed
                    }
                )
                WindowState.Minimized -> MinimizedWindow(onClick = {
                    currentWindowState = WindowState.Open
                })
                WindowState.Closed -> {
                    // show nothing as a window
                }
            }
        }

        DisposableEffect(lifecycleOwner) {
            Log.d(TAG, "Screen() enters composition or key changed: lifecycleOwner=$lifecycleOwner")

            val observer = LifecycleEventObserver { _, event ->
                Log.d(TAG, "Screen() got an lifecycle changing event: $event")
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                Log.d(
                    TAG,
                    "Screen() leaves composition or key changed: lifecycleOwner=$lifecycleOwner"
                )
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }
    }

    @Composable
    fun Window(onMinimize: () -> Unit, onClose: () -> Unit) {
        Log.d(TAG, "Window()")

        val currentOnMinimize by rememberUpdatedState(onMinimize)
        val currentOnClose by rememberUpdatedState(onClose)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Surface(
                modifier = Modifier
                    .size(SIZE_WINDOW.dp),
                shape = RoundedCornerShape(corner = CornerSize(SIZE_WINDOW_ROUNDED_CORNER.dp)),
                color = Color.Gray,
                elevation = SIZE_WINDOW_ELEVATION.dp
            ) {
                Column {
                    WindowStatusBar(currentOnMinimize, currentOnClose)

                    WindowContent()
                }
            }
        }

    }

    @Composable
    fun MinimizedWindow(onClick: () -> Unit) {
        Log.d(TAG, "MinimizedWindow()")

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Bottom,
        ) {
            Button(
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color.Gray,
                ),
                onClick = { onClick.invoke() },
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bike),
                    contentDescription = "",
                    colorFilter = ColorFilter.tint(Color.White),
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "BIKE APP",
                    fontSize = 16.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    @Composable
    fun WindowStatusBar(onMinimize: () -> Unit, onClose: () -> Unit) {
        Log.d(TAG, "WindowStatusBar()")

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(HEIGHT_WINDOW_STATUS_BAR.dp)
                .background(color = Color.DarkGray)
                .padding(start = 4.dp, end = 4.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WindowButton(color = Color.Yellow) {
                onMinimize.invoke()
            }
            WindowButton(
                color = Color.Red,
                sizePaddingStart = PADDING_WINDOW_STATUS_BAR_BUTTON.dp
            ) {
                onClose.invoke()
            }
        }

        SideEffect {
            Log.d(TAG, "SideEffect from WindowStatusBar()")
        }
    }

    @Composable
    fun WindowButton(
        color: Color,
        sizePaddingStart: Dp = 0.dp,
        onClicked: () -> Unit
    ) {
        Log.d(TAG, "WindowButton()")

        val currentOnClicked by rememberUpdatedState(onClicked)
        Button(
            modifier = Modifier
                .padding(start = sizePaddingStart)
                .size(SIZE_WINDOW_STATUS_BAR_BUTTON.dp),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = color
            ),
            onClick = { currentOnClicked.invoke() },
            shape = CircleShape
        ) {

        }
    }

    @Composable
    fun WindowContent() {
        Log.d(TAG, "WindowContent()")

        var destinationContent by remember { mutableStateOf(WindowContent.Splash) }
        when (destinationContent) {
            WindowContent.Splash -> SplashWindowContent(onReady = {
                destinationContent = WindowContent.Bike
            })
            WindowContent.Bike -> BikeWindowContent()
            WindowContent.Error -> ErrorWindowContent()
        }

        //FIXME produceState
        //FIXME BikeContent()??? ???????????? ?????? ????????? ????????? State ????????? ?????????
        //FIXME SplashWindowContent??? ????????? ?????? ???????????? ????????????
        //FIXME (????????? ????????? ?????? ?????? ????????? ???)
    }

    @Composable
    fun SplashWindowContent(onReady: (() -> Unit)? = null) {
        Log.d(TAG, "SplashWindowContent()")

        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.secondary) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(text = "Let's bike!", fontSize = 40.sp, fontWeight = FontWeight.Bold)
                Text(text = "now loading...", fontSize = 24.sp, fontStyle = FontStyle.Italic)
            }
        }

        //FIXME LaunchedEffect
        //FIXME ???????????? suspend fun loadingResourcesForALongTime()??? ????????????
        //FIXME ????????? ???????????? onReady()??? ?????? ?????? ???????????? ???????????? ??????.
    }

    @Composable
    fun BikeWindowContent() {
        Log.d(TAG, "BikeWindowContent()")

        var bikeState by remember { mutableStateOf(BikePosition.Start) }
        val bikeOffsetState = animateIntAsState(
            targetValue = if (bikeState == BikePosition.Start) OFFSET_BIKE_START else OFFSET_BIKE_FINISH,
            animationSpec = tween(DURATION_MILLIS_ANIMATED_BIKE)
        )

        //FIXME derivedStateOf()
        //FIXME showCrowd??? bikeOffset??? 100dp ????????? ???????????? true??? ????????? ????????????.
        val showCrowd = false

        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {

            Column(modifier = Modifier.fillMaxSize()) {

                Box {
                    if (showCrowd) {
                        Crowd()
                    }

                    Bike(offsetState = bikeOffsetState)
                }

                RidingDistanceLabel(distance = bikeOffsetState.value)

                Button(
                    onClick = {
                        bikeState = when (bikeState) {
                            BikePosition.Start -> BikePosition.Finish
                            BikePosition.Finish -> BikePosition.Start
                        }
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(align = Alignment.Center)
                ) {
                    Text(text = "Ride")
                }
            }
        }
    }

    @Composable
    fun Bike(offsetState: State<Int>) {
        Log.d(TAG, "Bike(${offsetState.value})")

        Image(
            painter = painterResource(id = R.drawable.bike),
            modifier = Modifier
                .padding(top = 40.dp)
                .size(SIZE_BIKE.dp)
                .absoluteOffset(x = offsetState.value.dp),
            contentDescription = null,
        )

        //FIXME snapshotFlow
        //FIXME offset ??? 200??? ????????? ?????? ?????? ????????? ???????????? ??????
        //FIXME ????????? ????????? ???????????? reportBikeReachedEnd() ????????? suspend ????????????.
    }

    @Composable
    fun Crowd() {
        Log.d(TAG, "Crowd()")

        Image(
            painter = painterResource(id = R.drawable.crowd),
            modifier = Modifier
                .padding(top = 40.dp, start = 8.dp)
                .size(SIZE_CROWD.dp),
            contentDescription = null,
        )
    }

    @Composable
    fun RidingDistanceLabel(distance: Int) {
        Log.d(TAG, "RidingDistanceLabel()")

        //FIXME remember, rememberUpdatedState ????????? ?????? ????????? ?????? ????????? ??????
//        val rememberedDistance by remember { mutableStateOf(distance) }
//        val currentDistance by rememberUpdatedState(newValue = distance)
//        Log.d(TAG, "BikeOffsetLabel() distance=$distance, rememberedDistance=$rememberedDistance, currentDistance=$currentDistance")

        Text(
            text = "Distance = $distance km",
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(align = Alignment.CenterHorizontally)
        )
    }


    @Composable
    fun ErrorWindowContent() {
        Log.d(TAG, "ErrorWindowContent()")

        Surface(modifier = Modifier.fillMaxSize(), color = Color.White) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_error),
                    colorFilter = ColorFilter.tint(color = Color.Red),
                    contentDescription = "error image",
                    modifier = Modifier.size(SIZE_ERROR_IMAGE.dp)
                )
                Text(
                    text = "Error!",
                    color = Color.Red,
                    fontWeight = FontWeight.Bold,
                    fontSize = 50.sp
                )
            }
        }
    }

    @Preview
    @Composable
    fun PreviewScreen() {
        ComposeBikeTheme {
            Screen()
        }
    }

    private suspend fun loadingResourcesForALongTime(): Boolean {
        delay(3000L)
        return true
    }

    private suspend fun unloadingResourcesForALongTime() {
        delay(3000L)
    }

    private suspend fun reportBikeReachedEnd() {
        Log.d(TAG, "reportBikeReachedEnd()")
    }

    companion object {
        private const val TAG = "ComposeBikeApp"

        private const val SIZE_WINDOW = 320
        private const val SIZE_WINDOW_ROUNDED_CORNER = 10
        private const val SIZE_WINDOW_ELEVATION = 30
        private const val HEIGHT_WINDOW_STATUS_BAR = 30
        private const val SIZE_WINDOW_STATUS_BAR_BUTTON = 20
        private const val PADDING_WINDOW_STATUS_BAR_BUTTON = 8
        private const val SIZE_BIKE = 120
        private const val SIZE_CROWD = SIZE_BIKE
        private const val SIZE_ERROR_IMAGE = 120
        private const val OFFSET_BIKE_START = 0
        private const val OFFSET_BIKE_FINISH = SIZE_WINDOW - SIZE_BIKE
        private const val DURATION_MILLIS_ANIMATED_BIKE = 2100
    }
}

enum class WindowState {
    Open, Minimized, Closed
}

enum class WindowContent {
    Splash, Bike, Error
}

enum class BikePosition {
    Start, Finish
}


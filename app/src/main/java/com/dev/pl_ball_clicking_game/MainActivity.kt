package com.dev.pl_ball_clicking_game

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.pl_ball_clicking_game.ui.theme.PL_Ball_Clicking_GameTheme
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PL_Ball_Clicking_GameTheme {
                MainScreen()
            }
        }
    }
}

@Composable
fun MainScreen() {
    var points by remember {
        mutableStateOf(0)
    }
    var isTimerRunning by remember {
        mutableStateOf(false)
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Points: $points",
                style = MaterialTheme.typography.subtitle1.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )

            Button(
                onClick = {
                    isTimerRunning = !isTimerRunning
                    points = 0
                }
            ) {
                Text(text = if(isTimerRunning) "Reset" else "Start")
            }

            CountDownTimer(
                isTimerRunning = isTimerRunning
            ) {
                isTimerRunning = false
            }
        }

        BallClicker(
            enabled = isTimerRunning
        ) {
            points++
        }
    }
}

// simple countDownTimer which is different with CountDownTimer
@Composable
fun CountDownTimer(
    time: Int = 30000,
    isTimerRunning: Boolean = false,
    onTimerEnd: () -> Unit = {}
) {
    var currentTime by remember {
        mutableStateOf(time)
    }

    LaunchedEffect(key1 = currentTime, key2 = isTimerRunning) {
        if(!isTimerRunning) {
            currentTime = time
            return@LaunchedEffect
        }
        if(currentTime > 0) {
            delay(1000L)
            currentTime -= 1000
        } else {
            onTimerEnd()
        }
    }

    Text(
        text = (currentTime/1000).toString(),
        style = MaterialTheme.typography.subtitle1.copy(
            fontWeight = FontWeight.SemiBold
        )
    )
}

@Composable
fun BallClicker(
    modifier: Modifier = Modifier,
    radius: Float = 100f,
    enabled: Boolean = false,
    ballColor: Color = Color.Red,
    onBallClick: () -> Unit = {}
) {
    // use box with constraint to gain the size of canvas if it is needed
    // outside drawScope
    BoxWithConstraints(
        modifier = modifier.fillMaxSize()
    ) {
        var ballPosition by remember {
            mutableStateOf(
                randomOffset(
                    radius,
                    constraints.maxWidth,
                    constraints.maxWidth
                )
            )
        }
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(enabled) { // coroutine
                    // to accept user pointer input
                    if (!enabled) {
                        return@pointerInput
                    }

                    // to give click listener to canvas
                    // and to make an action when the right item clicked,
                    // we must get the distance of the click and make condition
                    // if the click offset is in the scope of the item offset
                    detectTapGestures {
                        // sqrt() akar
                        // pow(n) pangkat
                        // get distance between two points => akar(a^2 + b^2)
                        // get a -> xA - xB
                        // get b -> yA - yB

                        /**Case with circle**/
                        val distance = sqrt(
                            (it.x - ballPosition.x).pow(2) +
                                    (it.y - ballPosition.y).pow(2)
                        )

                        if (distance <= radius) {
                            ballPosition = randomOffset(
                                radius,
                                constraints.maxWidth,
                                constraints.maxWidth
                            )
                            onBallClick()
                        }
                    }
                }
        ) {
            drawCircle(
                radius = radius,
                color = ballColor,
                center = ballPosition
            )
        }
    }
}

private fun randomOffset(
    radius: Float,
    width: Int,
    height: Int
): Offset =
    Offset(
        x = Random.nextInt(radius.roundToInt(), width-radius.roundToInt()).toFloat(),
        y = Random.nextInt(radius.roundToInt(), height-radius.roundToInt()).toFloat()
    )
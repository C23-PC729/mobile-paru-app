package com.faatikhriziq.paru.ui.screens.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.faatikhriziq.paru.R
import com.faatikhriziq.paru.ui.components.ImageCard
import com.faatikhriziq.paru.ui.theme.primaryColor

@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    Column(modifier.padding(all = 20.dp).verticalScroll(rememberScrollState())) {
        Box(
            modifier
                .background(color = primaryColor, shape = RoundedCornerShape(12.dp))
                .fillMaxWidth()
                .padding(all = 20.dp)
        ) {
            Column() {
                Row() {
                    Image(
                        painter = painterResource(id = R.drawable.weather_app),
                        contentDescription = "",
                        modifier
                            .height(80.dp)

                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Column() {
                        Text(
                            text = "Tarub, Tegal",
                            style = TextStyle(fontSize = 27.sp, color = Color.White),
                        )


                        Text(
                            text = "30°C",
                            style = TextStyle(
                                fontSize = 40.sp,
                                color = Color.White,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row() {
                    Image(
                        painter = painterResource(id = R.drawable.air_quality),
                        contentDescription = "",
                        modifier
                            .height(80.dp)

                    )
                    Spacer(modifier = Modifier.width(15.dp))
                    Box(
                        modifier
                            .height(80.dp)
                            .padding(5.dp)
                            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxHeight()
                                .padding(horizontal = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "AQI",
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    color = primaryColor,
                                    fontWeight = FontWeight.SemiBold
                                ),
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "120",
                                style = TextStyle(
                                    fontSize = 30.sp,
                                    color = primaryColor,
                                    fontWeight = FontWeight.SemiBold
                                ),
                            )
                        }
                    }

                }
            }

        }
        Spacer(modifier = Modifier.padding(top = 20.dp))
        Text(
            text = "Highlights Blog",
            style = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Medium, color = Color.Gray)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(modifier=Modifier.fillMaxWidth(0.5f) ) {

        }
        ImageCard(
            painter = painterResource(id = R.drawable.jakarta),
            contentDescription = "Blog News",
            title = "Bagaimana Cara Mengatasi Polusi Udara di Jakarta?",
        )
        Spacer(modifier = Modifier.height(10.dp))
        ImageCard(
            painter = painterResource(id = R.drawable.berasap),
            contentDescription = "Blog News",
            title = "Apa penyebab polusi udara di dunia?",
        )
        Spacer(modifier = Modifier.height(10.dp))
        ImageCard(
            painter = painterResource(id = R.drawable.sky),
            contentDescription = "Blog News",
            title = "Apakah langit biru itu tanda udara bersih?",
        )
    }
}


@Preview(showBackground = true)
@Composable
fun HomePreview() {
    HomeScreen()
}




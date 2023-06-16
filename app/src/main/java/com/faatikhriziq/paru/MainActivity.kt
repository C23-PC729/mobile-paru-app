package com.faatikhriziq.paru

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.IconButton
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.faatikhriziq.paru.ui.navigation.NavigationItem
import com.faatikhriziq.paru.ui.navigation.Screen
import com.faatikhriziq.paru.ui.screens.auth.LoginScreen
import com.faatikhriziq.paru.ui.screens.blog.BlogScreen
import com.faatikhriziq.paru.ui.screens.capture.CaptureScreen
import com.faatikhriziq.paru.ui.screens.explore.ExploreScreen
import com.faatikhriziq.paru.ui.screens.history.HistoryScreen
import com.faatikhriziq.paru.ui.screens.home.HomeScreen
import com.faatikhriziq.paru.ui.theme.primaryColor

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            // A surface container using the 'background' color from the theme
            Surface(
                modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
            ) {
               ParuApp()
            }

        }
    }
}

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun ParuApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Scaffold(topBar = {
        TopAppBar(backgroundColor = primaryColor, title = {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = modifier.fillMaxSize()
            ) {
                if (currentRoute != Screen.Capture.route){

                    Image(
                        painter = painterResource(id = R.drawable.appbar_logo),
                        contentDescription = "Appbar Logo",
                        modifier = modifier
                            .size(100.dp)
                            .padding(end = 13.dp),
                    )
                }else if (currentRoute == Screen.Capture.route){
                    Text(
                        text = Screen.Capture.route,
                        color = Color.White,
                        fontSize = 20.sp,
                        modifier = modifier
                            .padding(end = 13.dp)
                            .align(CenterVertically),
                    )
                }
            }
        }, navigationIcon = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_menu_24),
                    contentDescription = "Notification",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(start = 20.dp),
                )
            }
        }, actions = {
            IconButton(onClick = {}) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.baseline_info_24),
                    contentDescription = "Notification",
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.padding(end = 20.dp),
                )
            }
        })
    },
        bottomBar = {
            if (currentRoute != Screen.Capture.route) {
                BottomBar(navController)
            }
        },
        floatingActionButton = {
            if (currentRoute == Screen.Home.route) {
                FloatingActionButton(onClick = {navController.navigate(Screen.Capture.route)}, containerColor = primaryColor, content = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.baseline_camera_24),
                        contentDescription = "Add",
                        tint = MaterialTheme.colorScheme.onPrimary,
                    )
                }, shape = RoundedCornerShape(50)
                )
            }
        }

    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen()
            }
            composable(Screen.Blog.route) {
                BlogScreen()
            }
            composable(Screen.History.route) {
                HistoryScreen()
            }
            composable(Screen.Explore.route) {
                ExploreScreen()
            }
            composable(Screen.Capture.route) {
                CaptureScreen()
            }
        }

    }
}


@Composable
fun BottomBar(navController: NavHostController, modifier: Modifier = Modifier) {
    BottomNavigation {
        val navigationItems = listOf(
            NavigationItem(
                title = "Home",
                icon = ImageVector.vectorResource(id = R.drawable.baseline_home_24),
                screen = Screen.Home
            ),
            NavigationItem(
                title = "Blog",
                icon = ImageVector.vectorResource(id = R.drawable.baseline_newspaper_24),
                screen = Screen.Blog
            ),
            NavigationItem(
                title = "History",
                icon = ImageVector.vectorResource(id = R.drawable.baseline_history_edu_24),
                screen = Screen.History
            ),
            NavigationItem(
                title = "Explore",
                icon = ImageVector.vectorResource(id = R.drawable.baseline_explore_24),
                screen = Screen.Explore
            ),
        )
        BottomNavigation(modifier = modifier, backgroundColor = Color.White, elevation = 2.dp) {
            navigationItems.map { item ->
                BottomNavigationItem(

                    selected = true,
                    onClick = {
                        navController.navigate(item.screen.route) {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = true
                            }
                            restoreState = true
                            launchSingleTop = true
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.title,
                            tint = primaryColor,
                        )
                    }, label = {
                        Text(
                            text = item.title,
                            color = Color.Gray,
                            fontSize = 12.sp,
                        )
                    }
                )
            }
        }
    }

}


@Preview
@Composable
fun ParuAppPreview() {
    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        ParuApp()
    }
}



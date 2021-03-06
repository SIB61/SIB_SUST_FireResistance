package com.sibdev.firefighter.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sibdev.firefighter.screens.*

@Composable
fun ProfileNavigation(){
    val profileNavController= rememberNavController()
    NavHost(navController = profileNavController, startDestination = "profile_screen"){
        composable("profile_screen"){
            ProfileScreen(profileNavController)
        }
        composable("add_community_screen"){
            AddCommunityScreen(profileNavController)
        }
        composable("emergency_number_screen"){
            EmergencyNumberScreen(profileNavController)
        }
        composable("community_screen"){
            CommunityScreen()
        }
    }
}
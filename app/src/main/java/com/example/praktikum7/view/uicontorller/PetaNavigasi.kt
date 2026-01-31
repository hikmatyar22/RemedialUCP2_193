package com.example.praktikum7.view.uicontorller

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.praktikum7.view.DetailSiswaScreen
import com.example.praktikum7.view.EditSiswaScreen
import com.example.praktikum7.view.EntrySiswaScreen
import com.example.praktikum7.view.HomeScreen
import com.example.praktikum7.view.route.DestinasiDetailSiswa
import com.example.praktikum7.view.route.DestinasiDetailSiswa.itemIdArg
import com.example.praktikum7.view.route.DestinasiEditSiswa
import com.example.praktikum7.view.route.DestinasiEntry
import com.example.praktikum7.view.route.DestinasiEntryKategori
import com.example.praktikum7.view.route.DestinasiHome
import com.example.praktikum7.view.EntryKategoriScreen

@Composable
fun SiswaApp(navController: NavHostController = rememberNavController(), modifier: Modifier){
    HostNavigasi(navController = navController)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HostNavigasi(
    navController: NavHostController,
    modifier: Modifier = Modifier
){
    NavHost(navController=navController, startDestination = DestinasiHome.route, modifier = Modifier)
    {
        composable(DestinasiHome.route){
            HomeScreen(
                navigateToItemEntry = {navController.navigate(DestinasiEntry.route)},
                //edit 1 : tambahkan parameter navigateToItemUpdate
                navigateToItemUpdate = {
                    navController.navigate("${DestinasiDetailSiswa.route}/${it}")
                }
            )
        }
        composable(DestinasiEntry.route){
            EntrySiswaScreen(
                navigateBack = { navController.popBackStack()},
                onNavigateToKategori = { navController.navigate(DestinasiEntryKategori.route) }
            )
        }
        composable(DestinasiEntryKategori.route){
            EntryKategoriScreen(navigateBack = { navController.popBackStack() })
        }
        //edit 2 : tambahkan 2 composable route
        composable(route = DestinasiDetailSiswa.routeWithArgs,
            arguments = listOf(navArgument(itemIdArg) {
                type = NavType.IntType
            })
        ){
            DetailSiswaScreen(
                navigateToEditItem = {navController.navigate("${DestinasiEditSiswa.route}/${it}")},
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(route=DestinasiEditSiswa.routeWithArgs,
            arguments = listOf(navArgument(DestinasiEditSiswa.itemIdArg){
                type= NavType.IntType}))
        {
            EditSiswaScreen(
                navigateBack = {navController.popBackStack()},
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
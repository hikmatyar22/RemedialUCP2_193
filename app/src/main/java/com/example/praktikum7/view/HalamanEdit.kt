package com.example.praktikum7.view

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.praktikum7.view.route.DestinasiEditSiswa
import com.example.praktikum7.viewmodel.EditViewModel
import com.example.praktikum7.viewmodel.provider.PenyediaViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditSiswaScreen(
    navigateBack: () -> Unit,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: EditViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val coroutineScope = rememberCoroutineScope()
    
    // Collect Categories
    val pilihanKategori by viewModel.kategoriList.collectAsState()

    Scaffold(
        topBar = {
            SiswaTopAppBar(
                title = stringResource(DestinasiEditSiswa.titleRes),
                canNavigateBack = true,
                navigateUp = onNavigateUp
            )
        },
        modifier = modifier
    ) { innerPadding ->
        EntryBukuBody(
            uiStateBuku = viewModel.uiStateBuku,
            kategoriList = pilihanKategori, // Pass list here
            onBukuValueChange = viewModel::updateUIState,
            onSaveClick = {
                coroutineScope.launch {
                    viewModel.updateBuku()
                    navigateBack()
                }
            },
            modifier = Modifier.padding(innerPadding)
        )
    }
}
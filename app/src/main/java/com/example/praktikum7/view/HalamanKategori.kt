package com.example.praktikum7.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Category
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.praktikum7.R
import com.example.praktikum7.room.Buku
import com.example.praktikum7.room.KategoriWithLevel
import com.example.praktikum7.viewmodel.KategoriViewModel
import com.example.praktikum7.viewmodel.provider.PenyediaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KategoriScreen(
    onNavigateBack: () -> Unit,
    onNavigateToBookDetail: (Int) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: KategoriViewModel = viewModel(factory = PenyediaViewModel.Factory)
) {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
    val selectedCategoryId by viewModel.selectedCategoryId
    val categoryPath by viewModel.categoryPath
    val subCategories by viewModel.getSubCategories(selectedCategoryId).collectAsState(initial = emptyList())
    val booksInCategory by if (selectedCategoryId != null) {
        viewModel.getBooksByCategoryRecursive(selectedCategoryId!!).collectAsState(initial = emptyList())
    } else {
        viewModel.getBooksByCategoryRecursive(0).collectAsState(initial = emptyList())
    }

    Scaffold(
        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = { Text("Kategori Buku") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                scrollBehavior = scrollBehavior
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // Breadcrumb navigation
            if (categoryPath.isNotEmpty()) {
                CategoryBreadcrumb(
                    path = categoryPath,
                    onCategoryClick = { categoryId ->
                        viewModel.selectCategory(categoryId)
                    },
                    onRootClick = {
                        viewModel.selectCategory(null)
                    }
                )
            }

            // Categories and Books
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(dimensionResource(id = R.dimen.padding_small))
            ) {
                // Categories section
                if (subCategories.isNotEmpty()) {
                    item {
                        Text(
                            text = "Kategori",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                        )
                    }
                    items(subCategories) { category ->
                        CategoryItem(
                            category = category,
                            onCategoryClick = { viewModel.selectCategory(category.id) },
                            viewModel = viewModel
                        )
                    }
                }

                // Books section
                if (booksInCategory.isNotEmpty()) {
                    item {
                        Text(
                            text = "Buku dalam Kategori",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
                        )
                    }
                    items(booksInCategory) { book ->
                        BookItem(
                            book = book,
                            onBookClick = { onNavigateToBookDetail(book.id) }
                        )
                    }
                }

                // Empty state
                if (subCategories.isEmpty() && booksInCategory.isEmpty()) {
                    item {
                        Text(
                            text = if (selectedCategoryId == null) "Tidak ada kategori tersedia" else "Tidak ada sub-kategori atau buku dalam kategori ini",
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(dimensionResource(id = R.dimen.padding_large)),
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryBreadcrumb(
    path: List<KategoriWithLevel>,
    onCategoryClick: (Int) -> Unit,
    onRootClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Root/Home
        Text(
            text = "Beranda",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onRootClick() }
        )

        // Path items
        path.reversed().forEachIndexed { index, category ->
            Icon(
                Icons.Default.ArrowRight,
                contentDescription = "Arrow",
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Text(
                text = category.nama,
                style = MaterialTheme.typography.bodyMedium,
                color = if (index == path.size - 1) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                fontWeight = if (index == path.size - 1) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.clickable { 
                    if (index < path.size - 1) onCategoryClick(category.id)
                }
            )
        }
    }
}

@Composable
fun CategoryItem(
    category: KategoriWithLevel,
    onCategoryClick: () -> Unit,
    viewModel: KategoriViewModel
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            .clickable { onCategoryClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Category,
                contentDescription = "Kategori",
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = category.nama,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                if (category.deskripsi.isNotBlank()) {
                    Text(
                        text = category.deskripsi,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Show sub-category count if available
            // Note: This would require async call, simplified for now
            Icon(
                Icons.Default.ArrowRight,
                contentDescription = "Sub-kategori"
            )
        }
    }
}

@Composable
fun BookItem(
    book: Buku,
    onBookClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(id = R.dimen.padding_medium))
            .clickable { onBookClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(dimensionResource(id = R.dimen.padding_medium)),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.Default.Book,
                contentDescription = "Buku",
                modifier = Modifier.padding(end = 12.dp)
            )
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = book.judul,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "Stok: ${book.stok}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

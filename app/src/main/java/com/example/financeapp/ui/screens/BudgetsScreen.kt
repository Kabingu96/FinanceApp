package com.example.financeapp.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.ui.viewmodel.TransactionViewModel
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BudgetsScreen(viewModel: TransactionViewModel = viewModel()) {
    val transactions by viewModel.expenseTransactions.collectAsState()

    // Group expenses by category
    val expensesByCategory = transactions.groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }

    // Mock Budget Limits (In a real app, these would be user-defined)
    val budgetLimits = mapOf(
        "Food" to 500.0,
        "Rent" to 1500.0,
        "Transport" to 300.0,
        "Entertainment" to 200.0,
        "Other" to 100.0
    )

    Scaffold(
        topBar = { TopAppBar(title = { Text("Budget Utilization") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Navigate to Add Budget screen */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Budget")
            }
        }
    ) { paddingValues ->
        if (budgetLimits.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No budgets set up yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(budgetLimits.toList()) { (category, limit) ->
                    val spent = expensesByCategory[category] ?: 0.0
                    BudgetCard(category = category, spent = spent, limit = limit)
                }
            }
        }
    }
}

@Composable
fun BudgetCard(category: String, spent: Double, limit: Double) {
    val currencyFormat = NumberFormat.getCurrencyInstance()
    val overspent = spent > limit
    val progress = if (limit > 0) (spent / limit).toFloat() else 0f

    val animatedProgress by animateFloatAsState(targetValue = progress, label = "budget_progress")
    val progressColor = if (overspent) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary

    val icon = when (category) {
        "Food" -> Icons.Default.ShoppingCart
        "Rent" -> Icons.Default.Home
        "Transport" -> Icons.Default.Build // Placeholder
        "Entertainment" -> Icons.Default.ArrowUpward // Placeholder
        else -> Icons.Default.ArrowDownward // Placeholder
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = "$category icon", tint = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.padding(start = 12.dp))
                Text(category, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp),
                color = progressColor,
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                strokeCap = StrokeCap.Round
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Spent: ${currencyFormat.format(spent)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Limit: ${currencyFormat.format(limit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (overspent) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Over budget by ${currencyFormat.format(spent - limit)}",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

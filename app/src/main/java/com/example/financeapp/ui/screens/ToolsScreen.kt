package com.example.financeapp.ui.screens

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
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CreditCard
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.data.model.DebtEntity
import com.example.financeapp.data.model.SavingsGoalEntity
import com.example.financeapp.ui.viewmodel.FinancialToolsViewModel
import java.text.NumberFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ToolsScreen(viewModel: FinancialToolsViewModel = viewModel()) {
    val debts by viewModel.debts.collectAsState()
    val savingsGoals by viewModel.savingsGoals.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Financial Tools") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = { /* TODO: Navigate to Add Debt/Goal screen */ }) {
                Icon(Icons.Default.Add, contentDescription = "Add Tool")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Debt Tracker Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader("Debt Tracker")
                        Spacer(modifier = Modifier.height(12.dp))
                        if (debts.isEmpty()) {
                            EmptyStateMessage("No debts recorded.")
                        } else {
                            debts.forEach { debt ->
                                DebtItem(debt)
                            }
                        }
                    }
                }
            }

            // Savings Goals Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SectionHeader("Savings Goals")
                        Spacer(modifier = Modifier.height(12.dp))
                        if (savingsGoals.isEmpty()) {
                            EmptyStateMessage("No savings goals set.")
                        } else {
                            savingsGoals.forEach { goal ->
                                SavingsGoalItem(goal)
                            }
                        }
                    }
                }
            }

            // Other Tools Section
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(4.dp),
                    shape = MaterialTheme.shapes.large
                ) {
                    Column(modifier = Modifier.padding(vertical = 8.dp)) {
                        SectionHeader(title = "More Tools", modifier = Modifier.padding(start = 16.dp, top = 8.dp))
                        ComingSoonToolItem("Currency Converter", "Real-time currency conversion", Icons.Outlined.AccountBalanceWallet)
                        ComingSoonToolItem("Bill Reminders", "Get notified for upcoming bills", Icons.Outlined.Receipt)
                        ComingSoonToolItem("Smart Suggestions", "ML-based spending insights", Icons.Outlined.Lightbulb)
                    }
                }
            }
        }
    }
}

@Composable
private fun DebtItem(debt: DebtEntity) {
    val currencyFormat = NumberFormat.getCurrencyInstance()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(debt.name, fontWeight = FontWeight.Medium)
            Text(
                "Due: ${java.text.SimpleDateFormat("dd/MM/yyyy").format(java.util.Date(debt.dueDate))}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            currencyFormat.format(debt.amount),
            fontWeight = FontWeight.Bold,
            color = if (debt.type == "OWE") MaterialTheme.colorScheme.error else Color(0xFF4CAF50)
        )
    }
}

@Composable
private fun SavingsGoalItem(goal: SavingsGoalEntity) {
    val currencyFormat = NumberFormat.getCurrencyInstance()
    val progress = (goal.currentAmount / goal.targetAmount).toFloat().coerceIn(0f, 1f)
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(goal.name, fontWeight = FontWeight.Medium)
            Text(
                "${currencyFormat.format(goal.currentAmount)} / ${currencyFormat.format(goal.targetAmount)}",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ComingSoonToolItem(title: String, subtitle: String, icon: ImageVector) {
    ListItem(
        modifier = Modifier.alpha(0.6f),
        headlineContent = { Text(title) },
        supportingContent = { Text(subtitle) },
        leadingContent = { Icon(icon, contentDescription = null) },
        colors = ListItemDefaults.colors(disabledColor = MaterialTheme.colorScheme.onSurfaceVariant)
    )
}


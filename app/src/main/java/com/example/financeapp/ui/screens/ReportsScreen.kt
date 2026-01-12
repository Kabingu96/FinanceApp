package com.example.financeapp.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.ui.viewmodel.TransactionViewModel
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(viewModel: TransactionViewModel = viewModel()) {
    val expenseTransactions by viewModel.expenseTransactions.collectAsState()
    val incomeTransactions by viewModel.incomeTransactions.collectAsState()

    // Data Calculation
    val expensesByCategory = expenseTransactions.groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
    val totalExpenses = expensesByCategory.values.sum()
    val totalIncome = incomeTransactions.sumOf { it.amount }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Reports & Analytics") }) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                SummaryCard(
                    title = "Income",
                    amount = totalIncome,
                    color = Color(0xFF4CAF50), // Green
                    modifier = Modifier.weight(1f)
                )
                SummaryCard(
                    title = "Expenses",
                    amount = totalExpenses,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Spending Breakdown
            SectionHeader("Spending Breakdown")
            if (totalExpenses > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DonutChart(data = expensesByCategory, total = totalExpenses)
                    }
                }
            } else {
                EmptyStateMessage("No expense data available")
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Income vs Expense
            SectionHeader("Income vs Expense")
             if (totalIncome > 0 || totalExpenses > 0) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                         LinearComparisonChart(income = totalIncome, expense = totalExpenses)
                    }
                }
            } else {
                 EmptyStateMessage("No transaction data available")
            }

            Spacer(modifier = Modifier.height(24.dp))
            
            // Trends Placeholder
            SectionHeader("Monthly Trends")
            Card(
                modifier = Modifier.fillMaxWidth().height(150.dp),
                 colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
                 shape = RoundedCornerShape(16.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Trend analysis coming soon", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}

@Composable
fun SummaryCard(title: String, amount: Double, color: Color, modifier: Modifier = Modifier) {
    val currencyFormat = NumberFormat.getCurrencyInstance(Locale.getDefault())
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(text = title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = currencyFormat.format(amount),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }
    }
}

@Composable
fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun EmptyStateMessage(message: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
        contentAlignment = Alignment.Center
    ) {
        Text(message, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
fun DonutChart(data: Map<String, Double>, total: Double) {
    val colors = listOf(
        Color(0xFFEF5350), Color(0xFF42A5F5), Color(0xFF66BB6A), 
        Color(0xFFFFEE58), Color(0xFFAB47BC), Color(0xFFFFA726)
    )
    
    Row(verticalAlignment = Alignment.CenterVertically) {
        // Chart
        Box(
            modifier = Modifier.size(140.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(120.dp)) {
                var startAngle = -90f
                val strokeWidth = 30f
                
                data.entries.forEachIndexed { index, entry ->
                    val sweepAngle = (entry.value / total * 360).toFloat()
                    drawArc(
                        color = colors[index % colors.size],
                        startAngle = startAngle,
                        sweepAngle = sweepAngle,
                        useCenter = false,
                        style = Stroke(width = strokeWidth)
                    )
                    startAngle += sweepAngle
                }
            }
            // Center Text
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Total", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    NumberFormat.getCurrencyInstance().format(total), // Simplified
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        
        Spacer(modifier = Modifier.width(24.dp))
        
        // Legend
        Column {
            data.keys.forEachIndexed { index, category ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                    Box(modifier = Modifier.size(10.dp).background(colors[index % colors.size], RoundedCornerShape(2.dp)))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(category, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        Text(
                            "${"%.1f".format(data[category]!! / total * 100)}%", 
                            style = MaterialTheme.typography.labelSmall, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun LinearComparisonChart(income: Double, expense: Double) {
    val maxVal = maxOf(income, expense)
    if (maxVal == 0.0) return

    val incomeRatio = (income / maxVal).toFloat()
    val expenseRatio = (expense / maxVal).toFloat()
    
    Column {
        ComparisonBar(label = "Income", amount = income, ratio = incomeRatio, color = Color(0xFF4CAF50))
        Spacer(modifier = Modifier.height(12.dp))
        ComparisonBar(label = "Expense", amount = expense, ratio = expenseRatio, color = MaterialTheme.colorScheme.error)
    }
}

@Composable
fun ComparisonBar(label: String, amount: Double, ratio: Float, color: Color) {
    val currencyFormat = NumberFormat.getCurrencyInstance()
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.width(60.dp), style = MaterialTheme.typography.bodySmall)
        
        Box(modifier = Modifier.weight(1f).height(24.dp), contentAlignment = Alignment.CenterStart) {
            // Background track
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
            )
            // Filled bar
            Box(
                modifier = Modifier
                    .fillMaxWidth(ratio)
                    .fillMaxHeight()
                    .background(color, RoundedCornerShape(4.dp))
            )
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            currencyFormat.format(amount), 
            style = MaterialTheme.typography.bodySmall, 
            fontWeight = FontWeight.Bold,
            modifier = Modifier.width(70.dp) // Fixed width for alignment
        )
    }
}

package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MintPrimary
import com.example.ui.viewmodel.CurrencyViewModel
import com.example.ui.util.Localization
import com.example.ui.util.MathEvaluator
import java.util.Locale

@Composable
fun CalculatorScreen(
    viewModel: CurrencyViewModel,
    modifier: Modifier = Modifier
) {
    val languageCode by viewModel.languageCode.collectAsState()
    val customAppName by viewModel.customAppName.collectAsState()

    var expression by remember { mutableStateOf("") }
    var resultText by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    // Memory storage state
    var memoryValue by remember { mutableStateOf(0.0) }
    // Calculation history
    var historyList by remember { mutableStateOf(listOf<Pair<String, String>>()) }
    var showHistorySection by remember { mutableStateOf(false) }

    val isFa = languageCode == "fa"

    fun evaluateRealTime(exp: String) {
        if (exp.isEmpty()) {
            resultText = ""
            errorMsg = ""
            return
        }
        val lastChar = exp.last()
        if (lastChar in listOf('+', '-', '×', '÷', '.')) {
            return
        }
        try {
            val eval = MathEvaluator.evaluate(exp)
            val formatStr = if (eval % 1.0 == 0.0) {
                String.format(Locale.US, "%,.0f", eval)
            } else {
                String.format(Locale.US, "%,.4f", eval).trimEnd('0').trimEnd('.')
            }
            resultText = formatStr
            errorMsg = ""
        } catch (e: Exception) {
            // Keep silent during fast interactive typing
        }
    }

    fun handleKeyPress(key: String) {
        when (key) {
            "C" -> {
                expression = ""
                resultText = ""
                errorMsg = ""
            }
            "DEL" -> {
                if (expression.isNotEmpty()) {
                    expression = expression.dropLast(1)
                    evaluateRealTime(expression)
                } else {
                    resultText = ""
                    errorMsg = ""
                }
            }
            "=" -> {
                if (expression.isEmpty()) return
                try {
                    val eval = MathEvaluator.evaluate(expression)
                    val formatStr = if (eval % 1.0 == 0.0) {
                        String.format(Locale.US, "%.0f", eval)
                    } else {
                        String.format(Locale.US, "%.4f", eval).trimEnd('0').trimEnd('.')
                    }
                    val formattedResult = formatStr
                    
                    // prepend to local session tape
                    historyList = listOf(expression to formattedResult) + historyList
                    if (historyList.size > 15) {
                        historyList = historyList.take(15)
                    }

                    expression = formattedResult
                    resultText = ""
                    errorMsg = ""
                } catch (e: Exception) {
                    errorMsg = if (isFa) "فرمول ریاضی نامعتبر" else "Invalid Formula"
                }
            }
            "M+" -> {
                val currentVal = resultText.replace(",", "").toDoubleOrNull()
                    ?: expression.replace(",", "").toDoubleOrNull()
                    ?: 0.0
                memoryValue += currentVal
            }
            "M-" -> {
                val currentVal = resultText.replace(",", "").toDoubleOrNull()
                    ?: expression.replace(",", "").toDoubleOrNull()
                    ?: 0.0
                memoryValue -= currentVal
            }
            "MR" -> {
                val formattedMem = if (memoryValue % 1.0 == 0.0) {
                    String.format(Locale.US, "%.0f", memoryValue)
                } else {
                    String.format(Locale.US, "%.4f", memoryValue).trimEnd('0').trimEnd('.')
                }
                expression += formattedMem
                evaluateRealTime(expression)
            }
            "MC" -> {
                memoryValue = 0.0
            }
            "+", "-", "×", "÷" -> {
                if (expression.isNotEmpty()) {
                    val lastChar = expression.last()
                    if (lastChar in listOf('+', '-', '×', '÷')) {
                        expression = expression.dropLast(1) + key
                    } else {
                        expression += key
                    }
                } else if (key == "-") {
                    expression += key
                }
            }
            else -> {
                expression += key
                evaluateRealTime(expression)
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MintPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Calculate,
                                contentDescription = null,
                                tint = MintPrimary,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Column {
                            Text(
                                text = if (isFa) "ماشین حساب هوشمند" else "Smart Calculator",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = if (isFa) "محاسبات ریاضی آفلاین" else "Offline Math Calculations",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (memoryValue != 0.0) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MintPrimary.copy(alpha = 0.15f),
                                modifier = Modifier.padding(end = 4.dp)
                            ) {
                                Text(
                                    text = "M: " + if (memoryValue % 1.0 == 0.0) String.format(Locale.US, "%.0f", memoryValue) else String.format(Locale.US, "%.2f", memoryValue),
                                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                    color = MintPrimary,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = { showHistorySection = !showHistorySection },
                            enabled = historyList.isNotEmpty()
                        ) {
                            Icon(
                                imageVector = Icons.Default.History,
                                contentDescription = "Calculation History",
                                tint = if (showHistorySection) MintPrimary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                // Top Content Area: Display panel OR History panel. Takes remainder space and pushes everything down.
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(bottom = 8.dp)
                ) {
                    // History Overlay List
                    androidx.compose.animation.AnimatedVisibility(
                        visible = showHistorySection && historyList.isNotEmpty(),
                        enter = fadeIn() + expandVertically(),
                        exit = fadeOut() + shrinkVertically(),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (isFa) "تاریخچه محاسبات اخیر" else "Recent Calculations",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.titleSmall,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    TextButton(
                                        onClick = { historyList = emptyList(); showHistorySection = false },
                                        contentPadding = PaddingValues(0.dp)
                                    ) {
                                        Text(
                                            text = if (isFa) "پاک کردن همه" else "Clear All",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))

                                LazyColumn(
                                    modifier = Modifier.fillMaxSize(),
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    items(historyList) { item ->
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(8.dp))
                                                .clickable {
                                                    expression = item.second
                                                    evaluateRealTime(expression)
                                                    showHistorySection = false
                                                }
                                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
                                                .padding(8.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = item.first,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Text(
                                                    text = "= ",
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MintPrimary
                                                )
                                                Text(
                                                    text = item.second,
                                                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Display card taking up the full height of the weight area when history is hidden
                    if (!showHistorySection || historyList.isEmpty()) {
                        Card(
                            modifier = Modifier.fillMaxSize(),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                            ),
                            shape = RoundedCornerShape(24.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(20.dp),
                                verticalArrangement = Arrangement.Bottom,
                                horizontalAlignment = Alignment.End
                            ) {
                                // Mathematical Expression Display
                                Text(
                                    text = expression.ifEmpty { "0" },
                                    style = MaterialTheme.typography.headlineMedium.copy(
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 32.sp,
                                        textAlign = TextAlign.End
                                    ),
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.75f),
                                    maxLines = 3,
                                    overflow = TextOverflow.Ellipsis,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                // Real-Time Result / Error Display
                                when {
                                    errorMsg.isNotEmpty() -> {
                                        Text(
                                            text = errorMsg,
                                            style = MaterialTheme.typography.titleMedium,
                                            color = MaterialTheme.colorScheme.error,
                                            fontWeight = FontWeight.Bold,
                                            textAlign = TextAlign.End,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    resultText.isNotEmpty() -> {
                                        Text(
                                            text = "= $resultText",
                                            style = MaterialTheme.typography.headlineLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 42.sp,
                                                textAlign = TextAlign.End
                                            ),
                                            color = MintPrimary,
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Backspace and Copy utilities
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val valueToCopy = if (resultText.isNotEmpty()) resultText else expression
                            if (valueToCopy.isNotEmpty()) {
                                viewModel.updateAmountInput(valueToCopy.replace(",", ""))
                                // trigger converting offline values immediately
                                viewModel.saveCurrentConversionToHistory()
                            }
                        },
                        enabled = expression.isNotEmpty() || resultText.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Input,
                            contentDescription = "Paste to converter",
                            tint = if (expression.isNotEmpty() || resultText.isNotEmpty()) MintPrimary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                    }

                    IconButton(
                        onClick = { handleKeyPress("DEL") },
                        enabled = expression.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Backspace,
                            contentDescription = "Backspace",
                            tint = if (expression.isNotEmpty()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                        )
                    }
                }

                // Keyboard Pad Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Memory functions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("MC", "MR", "M+", "M-").forEach { memKey ->
                            CalculatorButton(
                                text = memKey,
                                modifier = Modifier.weight(1f),
                                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                                isBold = false,
                                onClick = { handleKeyPress(memKey) }
                            )
                        }
                    }

                    // Pad Row 1
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton(
                            text = "C",
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                            isBold = true,
                            onClick = { handleKeyPress("C") }
                        )
                        CalculatorButton(
                            text = "(",
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = { handleKeyPress("(") }
                        )
                        CalculatorButton(
                            text = ")",
                            modifier = Modifier.weight(1f),
                            containerColor = MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            onClick = { handleKeyPress(")") }
                        )
                        CalculatorButton(
                            text = "÷",
                            modifier = Modifier.weight(1f),
                            containerColor = MintPrimary.copy(alpha = 0.85f),
                            contentColor = Color.White,
                            isBold = true,
                            onClick = { handleKeyPress("÷") }
                        )
                    }

                    // Pad Row 2
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("7", "8", "9").forEach { num ->
                            CalculatorButton(
                                text = num,
                                modifier = Modifier.weight(1f),
                                onClick = { handleKeyPress(num) }
                            )
                        }
                        CalculatorButton(
                            text = "×",
                            modifier = Modifier.weight(1f),
                            containerColor = MintPrimary.copy(alpha = 0.85f),
                            contentColor = Color.White,
                            isBold = true,
                            onClick = { handleKeyPress("×") }
                        )
                    }

                    // Pad Row 3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("4", "5", "6").forEach { num ->
                            CalculatorButton(
                                text = num,
                                modifier = Modifier.weight(1f),
                                onClick = { handleKeyPress(num) }
                            )
                        }
                        CalculatorButton(
                            text = "-",
                            modifier = Modifier.weight(1f),
                            containerColor = MintPrimary.copy(alpha = 0.85f),
                            contentColor = Color.White,
                            isBold = true,
                            onClick = { handleKeyPress("-") }
                        )
                    }

                    // Pad Row 4
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("1", "2", "3").forEach { num ->
                            CalculatorButton(
                                text = num,
                                modifier = Modifier.weight(1f),
                                onClick = { handleKeyPress(num) }
                            )
                        }
                        CalculatorButton(
                            text = "+",
                            modifier = Modifier.weight(1f),
                            containerColor = MintPrimary.copy(alpha = 0.85f),
                            contentColor = Color.White,
                            isBold = true,
                            onClick = { handleKeyPress("+") }
                        )
                    }

                    // Pad Row 5
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CalculatorButton(
                            text = "0",
                            modifier = Modifier.weight(2f), // wider 0 key
                            onClick = { handleKeyPress("0") }
                        )
                        CalculatorButton(
                            text = ".",
                            modifier = Modifier.weight(1f),
                            onClick = { handleKeyPress(".") }
                        )
                        CalculatorButton(
                            text = "=",
                            modifier = Modifier.weight(1f),
                            containerColor = MintPrimary,
                            contentColor = Color.White,
                            isBold = true,
                            onClick = { handleKeyPress("=") }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CalculatorButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    isBold: Boolean = false,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(54.dp)
            .testTag("calc_btn_$text"),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
            maxLines = 1
        )
    }
}

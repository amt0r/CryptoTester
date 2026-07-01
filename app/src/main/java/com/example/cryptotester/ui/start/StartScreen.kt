package com.example.cryptotester.ui.start

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.cryptotester.ui.theme.AccentBlue
import com.example.cryptotester.ui.theme.CryptoGreen
import com.example.cryptotester.ui.theme.DarkBackground
import com.example.cryptotester.ui.theme.DarkCard
import com.example.cryptotester.ui.theme.DarkSurfaceVariant
import com.example.cryptotester.ui.theme.TextPrimary
import com.example.cryptotester.ui.theme.TextSecondary

@Composable
fun StartScreen(
    onNavigateToSimulator: () -> Unit,
    onNavigateToStats: () -> Unit,
    viewModel: StartViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.navigateToSimulator) {
        if (uiState.navigateToSimulator) {
            viewModel.onNavigationHandled()
            onNavigateToSimulator()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Title
            Text(
                text = "CRYPTO",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )
            Text(
                text = "TRADING SIMULATOR",
                style = MaterialTheme.typography.titleMedium,
                color = AccentBlue,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 4.sp,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Balance input card
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkCard)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Starting Balance",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextSecondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = uiState.balanceInput,
                    onValueChange = viewModel::onBalanceChanged,
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.headlineMedium.copy(
                        textAlign = TextAlign.Center,
                        color = TextPrimary
                    ),
                    suffix = {
                        Text(
                            text = "USDT",
                            style = MaterialTheme.typography.titleSmall,
                            color = TextSecondary
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AccentBlue,
                        unfocusedBorderColor = DarkSurfaceVariant,
                        cursorColor = AccentBlue
                    )
                )

                // Error message
                AnimatedVisibility(
                    visible = uiState.error != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    Text(
                        text = uiState.error ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Start button
            Button(
                onClick = viewModel::onStartSimulation,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CryptoGreen,
                    contentColor = DarkBackground
                ),
                enabled = !uiState.isLoading
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = DarkBackground,
                        strokeWidth = 2.dp
                    )
                } else {
                    Text(
                        text = "Start Simulation",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Stats button
            OutlinedButton(
                onClick = onNavigateToStats,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = AccentBlue
                )
            ) {
                Text(
                    text = "My Stats",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

package mads.group3.stagecheck.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import mads.group3.stagecheck.R
import mads.group3.stagecheck.viewmodels.AuthViewModel

/* TODO
*   Make this actually look like the figma (this means logo too)
*/

@Composable
fun AuthScreen(viewModel: AuthViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.checkSignedIn()
    }

    if (uiState.errorMessage != null) {
        LaunchedEffect(uiState.errorMessage) {
            delay(3000)
            viewModel.clearError()
        }
    }

    val ibmPlexCondensedBold = FontFamily(
        Font(R.font.ibm_plex_sans_condensed_bold)
    )

    val logoTextStyle = TextStyle(
        fontFamily = ibmPlexCondensedBold,
        fontSize = 32.sp,
        color = Color(0xFFF2F3F4),
        letterSpacing = (-0.05).em,
        shadow = Shadow(
            color = Color(0xBF6F00FF),
            offset = Offset(2f, 2f),
            blurRadius = 6f
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(0.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 32.dp, start = 32.dp, end = 32.dp)
                .height(60.dp)
                .background(
                    color = Color(0xFF242124),
                    shape = RoundedCornerShape(30.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "StageCheck",
                style = logoTextStyle
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var email by remember { mutableStateOf("") }
            var password by remember { mutableStateOf("") }

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Password") },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { viewModel.signIn(email, password) },
                    modifier = Modifier
                        .weight(.7f)
                        .height(48.dp),
                    enabled = !uiState.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text("Sign In")
                }

                OutlinedButton(
                    onClick = { viewModel.register(email, password) },
                    modifier = Modifier
                        .weight(.7f)
                        .height(48.dp),
                    enabled = !uiState.isLoading,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text("Register")
                }
            }
        }

        if (uiState.isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }

        if (uiState.errorMessage != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                Text(
                    text = uiState.errorMessage!!,
                    modifier = Modifier.padding(12.dp),
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(0.dp))
    }
}

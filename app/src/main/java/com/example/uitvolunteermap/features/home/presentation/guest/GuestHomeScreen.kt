package com.example.uitvolunteermap.features.home.presentation.guest

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.uitvolunteermap.R

// 🔹 Main GuestHomeScreen
@Composable
fun GuestHomeScreen(uiState: GuestHomeUiState) {

    when {
        uiState.isLoading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }

        uiState.error != null -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Error: ${uiState.error}")
            }
        }

        else -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
            ) {

                // 🔹 Banner Hero
                item {
                    HeroBanner()
                }

                // 🔹 Campaign list
                items(uiState.campaigns) { campaign ->
                    CampaignCard(campaign)
                }

                // 🔹 Footer
                item {
                    FooterSection()
                }
            }
        }
    }
}

// 🔹 Hero Banner
@Composable
fun HeroBanner() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        // Background image (replace with your drawable)
        Image(
            painter = painterResource(id = R.drawable.banner_placeholder),
            contentDescription = "Banner",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Overlay title
        Text(
            text = "Explore Our Campaigns",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
        )

        // CTA button
        Button(
            onClick = { /* TODO: handle click */ },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 16.dp)
        ) {
            Text("Join Now")
        }
    }
}

// 🔹 Campaign Card
@Composable
fun CampaignCard(campaign: CampaignUiModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { /* navigate */ },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Logo / Thumbnail
            Image(
                painter = painterResource(id = R.drawable.logo_placeholder),
                contentDescription = "Campaign Logo",
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(8.dp))
            )

            Spacer(modifier = Modifier.width(12.dp))

            // Text info
            Column {
                Text(
                    text = campaign.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = campaign.description,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "${campaign.startDate} - ${campaign.endDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
            }
        }
    }
}

// 🔹 Footer Section
@Composable
fun FooterSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("UIT Volunteer Map", fontWeight = FontWeight.Bold)
        Text("Connecting volunteers with meaningful campaigns", color = Color.Gray, fontSize = 12.sp)
    }
}
package com.example.uitvolunteermap.app

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.uitvolunteermap.R
import com.example.uitvolunteermap.databinding.ActivityMainBinding
import com.example.uitvolunteermap.core.common.ui.resolve
import com.example.uitvolunteermap.features.campaign.presentation.campaigns.CampaignsAction
import com.example.uitvolunteermap.features.campaign.presentation.campaigns.CampaignsUiEffect
import com.example.uitvolunteermap.features.campaign.presentation.campaigns.CampaignsUiState
import com.example.uitvolunteermap.features.campaign.presentation.campaigns.CampaignsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CampaignsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupActions()
        observeViewModel()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)
    }

    private fun setupActions() {
        binding.buttonLoadCampaigns.setOnClickListener {
            viewModel.onAction(CampaignsAction.LoadCampaigns(forceRefresh = true))
        }
    }

    private fun observeViewModel() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.uiState.collect(::render)
                }
                launch {
                    viewModel.uiEffect.collect(::handleEffect)
                }
            }
        }
    }

    private fun render(state: CampaignsUiState) {
        binding.progressBar.isVisible = state.isLoading
        binding.textStatus.text = state.message?.resolve(this) ?: getString(R.string.main_ready_message)
        binding.textCampaignCount.text = getString(R.string.main_campaign_count, state.campaigns.size)
        binding.textCampaignList.text = if (state.campaigns.isEmpty()) {
            getString(R.string.main_empty_campaigns)
        } else {
            state.campaigns.joinToString(separator = "\n\n") {
                getString(
                    R.string.main_campaign_item,
                    it.name,
                    it.province,
                    it.status,
                )
            }
        }
    }

    private fun handleEffect(effect: CampaignsUiEffect) {
        when (effect) {
            is CampaignsUiEffect.ShowToast -> {
                Toast.makeText(this, effect.message.resolve(this), Toast.LENGTH_SHORT).show()
            }
        }
    }
}

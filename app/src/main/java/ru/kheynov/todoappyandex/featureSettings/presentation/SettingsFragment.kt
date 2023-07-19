package ru.kheynov.todoappyandex.featureSettings.presentation

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.Navigation
import kotlinx.coroutines.launch
import ru.kheynov.todoappyandex.R
import ru.kheynov.todoappyandex.appComponent
import ru.kheynov.todoappyandex.core.ui.AppTheme
import ru.kheynov.todoappyandex.databinding.FragmentSettingsBinding
import ru.kheynov.todoappyandex.featureSettings.presentation.stateHolders.SettingsAction
import javax.inject.Inject


class SettingsFragment : Fragment() {
    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private val viewModel: SettingsViewModel by viewModels { viewModelFactory }

    private lateinit var navController: NavController

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        context.appComponent.settingsComponent().create().inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.composeSettingsView.apply {
            findViewById<ComposeView>(R.id.composeSettingsView).setContent {
                val state = viewModel.state.collectAsStateWithLifecycle()
                AppTheme {
                    SettingsScreen(
                        state = state, onEvent = viewModel::handleEvent
                    )
                }
            }
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.action.collect { event ->
                    when (event) {
                        SettingsAction.NavigateBack -> navController.popBackStack()
                    }
                }
            }
        }
    }

}

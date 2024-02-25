package com.solodilov.exchanger.presentation.converter

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.solodilov.exchanger.App
import com.solodilov.exchanger.R
import com.solodilov.exchanger.databinding.FragmentConverterBinding
import com.solodilov.exchanger.presentation.common.UiState
import com.solodilov.exchanger.presentation.common.ViewModelFactory
import com.solodilov.exchanger.presentation.common.collectFlow
import com.solodilov.exchanger.presentation.common.onError
import com.solodilov.exchanger.presentation.common.onSuccess
import com.solodilov.exchanger.presentation.common.showToast
import com.solodilov.exchanger.presentation.common.viewBinding
import javax.inject.Inject

class ConverterFragment : Fragment(R.layout.fragment_converter) {
    private val binding by viewBinding(FragmentConverterBinding::bind)

    @Inject
    lateinit var viewModelFactory: ViewModelFactory
    private val viewModel: ConverterViewModel by viewModels { viewModelFactory }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        (requireActivity().application as App).appComponent.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        collectFlow(viewModel.uiState, ::handleState)
        collectFlow(viewModel.exchangedAmount, ::updateExchangedAmount)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initViews() {
        binding.topCard.root.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                viewModel.moveCardLeft(isTop = true)
            }

            override fun onSwipeRight() {
                viewModel.moveCardRight(isTop = true)
            }
        })
        binding.bottomCard.root.setOnTouchListener(object : OnSwipeTouchListener(requireContext()) {
            override fun onSwipeLeft() {
                viewModel.moveCardLeft(isTop = false)
            }

            override fun onSwipeRight() {
                viewModel.moveCardRight(isTop = false)
            }
        })
        binding.topCard.exchangedAmount.setOnFocusChangeListener { _, b ->
            if (b) viewModel.enableTopCard(true)
        }
        binding.bottomCard.exchangedAmount.setOnFocusChangeListener { _, b ->
            if (b) viewModel.enableTopCard(false)
        }
        binding.topCard.exchangedAmount.addTextChangedListener { text ->
            viewModel.onTextChanged(text?.toString() ?: "", true)
        }
        binding.bottomCard.exchangedAmount.addTextChangedListener { text ->
            viewModel.onTextChanged(text?.toString() ?: "", false)
        }
        binding.exchangeButton.setOnClickListener { viewModel.onExchangeClicked() }
        binding.errorLayout.tryButton.setOnClickListener { viewModel.getData(forceRefresh = true) }
    }

    private fun handleState(state: UiState<Pair<CurrencyUi, CurrencyUi>>) = with(binding) {
        progressBar.isVisible = state is UiState.Loading
        errorLayout.root.isVisible = state is UiState.Error
        topCard.root.isVisible = state is UiState.Success
        arrow.isVisible = state is UiState.Success
        bottomCard.root.isVisible = state is UiState.Success

        state
            .onSuccess { data -> updateCards(data) }
            .onError { error -> showToast(error.message.toString()) }
    }

    private fun updateCards(data: Pair<CurrencyUi, CurrencyUi>) = with(binding) {
        binding.topCard.name.text = data.first.name
        binding.topCard.amount.text = getString(
            R.string.amount,
            data.first.amount,
            getCurrencySymbol(data.first.name)
        )
        binding.topCard.rate.text = getString(
            R.string.exchange_format,
            getCurrencySymbol(data.first.name),
            data.first.exchangedRate,
            getCurrencySymbol(data.first.exchangedName)
        )
        binding.bottomCard.name.text = data.second.name
        binding.bottomCard.amount.text = getString(
            R.string.amount,
            data.second.amount,
            getCurrencySymbol(data.second.name)
        )
        binding.bottomCard.rate.text = getString(
            R.string.exchange_format,
            getCurrencySymbol(data.second.name),
            data.second.exchangedRate,
            getCurrencySymbol(data.second.exchangedName)
        )
        toolbarTitle.text = getString(
            R.string.exchange_format,
            data.first.name,
            data.first.exchangedRate,
            data.first.exchangedName,
        )
    }

    private fun updateExchangedAmount(data: Pair<String, String>) = with(binding) {
        topCard.exchangedAmount.setText(data.first)
        topCard.exchangedAmount.setSelection(data.first.length)
        bottomCard.exchangedAmount.setText(data.second)
        bottomCard.exchangedAmount.setSelection(data.second.length)
    }

    private fun getCurrencySymbol(currencyCode: String): String =
        try {
            java.util.Currency.getInstance(currencyCode).symbol
        } catch (e: Exception) {
            ""
        }
}
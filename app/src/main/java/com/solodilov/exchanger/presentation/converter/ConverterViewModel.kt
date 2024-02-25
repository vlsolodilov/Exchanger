package com.solodilov.exchanger.presentation.converter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solodilov.exchanger.domain.entity.Currency
import com.solodilov.exchanger.domain.usecase.GetCurrenciesUseCase
import com.solodilov.exchanger.domain.usecase.UpdateCurrencyAmountUseCase
import com.solodilov.exchanger.presentation.common.Result
import com.solodilov.exchanger.presentation.common.UiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.roundToInt

class ConverterViewModel @Inject constructor(
    private val getCurrenciesUseCase: GetCurrenciesUseCase,
    private val updateCurrencyAmountUseCase: UpdateCurrencyAmountUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<UiState<Pair<CurrencyUi,CurrencyUi>>>(UiState.Loading)
    val uiState: StateFlow<UiState<Pair<CurrencyUi,CurrencyUi>>> = _uiState

    private val _exchangedAmount = MutableStateFlow<Pair<String,String>>(Pair("",""))
    val exchangedAmount: StateFlow<Pair<String,String>> = _exchangedAmount

    private var topIndex = 0
    private var bottomIndex = 0
    private var currencyList: List<Currency> = emptyList()
    private var topEnable: Boolean? = null

    init {
        getData()
    }

    fun getData(forceRefresh: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            flow {
                emit(Result.Loading)
                try {
                    emit(Result.Success(getCurrencyUiPair(forceRefresh)))
                } catch (e: Exception) {
                    emit(Result.Error(e))
                }
            }.collect { result ->
                _uiState.value = when (result) {
                    is Result.Loading -> UiState.Loading
                    is Result.Success -> UiState.Success(result.data)
                    is Result.Error -> UiState.Error(result.exception)
                }
            }
        }
    }

    fun moveCardLeft(isTop: Boolean) {
        if (isTop) {
            topIndex = (topIndex + 1).coerceIn(0, currencyList.lastIndex)
        } else {
            bottomIndex = (bottomIndex + 1).coerceIn(0, currencyList.lastIndex)
        }
        updateCard()
    }

    fun moveCardRight(isTop: Boolean) {
        if (isTop) {
            topIndex = (topIndex - 1).coerceIn(0, currencyList.lastIndex)
        } else {
            bottomIndex = (bottomIndex - 1).coerceIn(0, currencyList.lastIndex)
        }
        updateCard()
    }

    private fun updateCard() {
        clearExchangedAmount()
        _uiState.value = try {
            val topCurrency = currencyList[topIndex].toCurrencyUi(currencyList[bottomIndex])
            val bottomCurrency = currencyList[bottomIndex].toCurrencyUi(currencyList[topIndex])
            UiState.Success(Pair(topCurrency, bottomCurrency))
        } catch (e: Exception) {
            UiState.Error(e)
        }
    }

    private suspend fun getCurrencyUiPair(forceRefresh: Boolean): Pair<CurrencyUi, CurrencyUi> {
        currencyList = getCurrenciesUseCase(forceRefresh)
        val topCurrency = currencyList[topIndex]
        val bottomCurrency = currencyList[bottomIndex]
        return Pair(topCurrency.toCurrencyUi(bottomCurrency), bottomCurrency.toCurrencyUi(topCurrency))
    }

    fun enableTopCard(enable: Boolean) {
        topEnable = enable
    }

    fun onTextChanged(text: String, isTop: Boolean) {
        if (topEnable != isTop) return
        val topCurrency = currencyList.getOrNull(topIndex) ?: return
        val bottomCurrency = currencyList.getOrNull(bottomIndex) ?: return
        val amount = text.replace("-","").replace(',', '.').toFloatOrNull()
        if (isTop) {
            _exchangedAmount.value = Pair(
                amount?.let { "%+.0f".format(-it) } ?: "",
                amount?.let { "%+.2f".format(((((it * 100) / (topCurrency.rate / bottomCurrency.rate)).roundToInt()).toFloat())/100) } ?: "",
            )
        } else {
            _exchangedAmount.value = Pair(
                amount?.let { "%+.2f".format(((((-it * 100) / (bottomCurrency.rate / topCurrency.rate)).roundToInt())).toFloat()/100) } ?: "",
                amount?.let { "%+.0f".format(it) } ?: "",
            )
        }
    }

    private fun clearExchangedAmount() {
        _exchangedAmount.value = Pair("", "")
    }

    fun onExchangeClicked() {
        if (_uiState.value !is UiState.Success) return
        val (topCurrency, bottomCurrency) = (_uiState.value as UiState.Success).data
        val (topText,bottomText) = _exchangedAmount.value
        val topAmount = topText.replace("-","").replace(',', '.').toFloatOrNull() ?: return
        val bottomAmount = bottomText.replace("-","").replace(',', '.').toFloatOrNull() ?: return
        viewModelScope.launch(Dispatchers.IO) {
            updateCurrencyAmountUseCase(
                from = topCurrency.name,
                fromAmount = topCurrency.amount - topAmount,
                to = bottomCurrency.name,
                toAmount = bottomCurrency.amount + bottomAmount,
            )
        }
        clearExchangedAmount()
        getData()
    }
}

fun Currency.toCurrencyUi(otherCurrency: Currency): CurrencyUi = CurrencyUi(
    name = name,
    amount = amount,
    rate = rate,
    exchangedName = otherCurrency.name,
    exchangedRate = otherCurrency.rate / rate,
)
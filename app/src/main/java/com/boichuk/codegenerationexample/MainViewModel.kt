package com.boichuk.codegenerationexample

import androidx.lifecycle.ViewModel
import com.boichuk.annotation.GenerateUpdateFunctions
import com.generated.functions.updateCounterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

@GenerateUpdateFunctions("ViewState")
class MainViewModel: ViewModel() {
    val _viewState = MutableStateFlow (ViewState())
    val viewState get() = _viewState.asStateFlow()

    init {
        updateCounterState(2)
    }

    data class ViewState(val counter: Int = 0)
}
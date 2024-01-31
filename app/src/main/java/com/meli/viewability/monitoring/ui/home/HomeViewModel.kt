package com.meli.viewability.monitoring.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meli.viewability.monitoring.domain.models.Component
import com.meli.viewability.monitoring.domain.repository.response.ResultWrapper
import com.meli.viewability.monitoring.domain.uses_case.GetComponentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getComponentsUseCase: GetComponentsUseCase,
): ViewModel() {

    private var state = MutableLiveData<ViewState>()
    val stateLiveData: LiveData<ViewState> = state

    sealed class ViewState {
        data object LoadingComponents: ViewState()
        data object ErrorLoadComponents: ViewState()
        data object EmptyComponents: ViewState()
        class LoadedComponents(val items: List<Component>): ViewState()
    }

    fun getComponents(){
        state.value = ViewState.LoadingComponents

        viewModelScope.launch {
            getComponentsUseCase.execute()
                .catch {
                    state.postValue(ViewState.ErrorLoadComponents)
                }
                .collect {
                    state.value = if(it is ResultWrapper.Success) {
                        handleItemsQuick(it.value)
                    } else {
                        ViewState.ErrorLoadComponents
                    }
                }
        }
    }

    private fun handleItemsQuick(items: List<Component>): ViewState {
        return if(items.isEmpty()) {
            ViewState.EmptyComponents
        } else {
            ViewState.LoadedComponents(items)
        }
    }
}
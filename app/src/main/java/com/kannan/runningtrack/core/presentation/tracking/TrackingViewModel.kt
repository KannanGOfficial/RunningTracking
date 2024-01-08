package com.kannan.runningtrack.core.presentation.tracking

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import com.kannan.runningtrack.R
import com.kannan.runningtrack.core.domain.repository.RunRepository
import com.kannan.runningtrack.utils.uitext.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(private val runRepository: RunRepository) :
    ViewModel() {

    private val timberTag = TrackingViewModel::class.java.simpleName

    private val _uiState: MutableStateFlow<TrackingUiState> = MutableStateFlow(TrackingUiState())
    val uiState = _uiState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000L),
        initialValue = TrackingUiState(),
    )


    private val _uiEvent: MutableSharedFlow<TrackingUiEvent> = MutableSharedFlow()
    val uiEvent = _uiEvent.asSharedFlow()

    val accept = ::onUiAction
    private fun onUiAction(event: TrackingUiAction) {
        when (event) {
            TrackingUiAction.ToggleRunButtonClicked -> toggleRun()
        }
    }

    val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            val trackingServiceBinder = binder as TrackingService.TrackingServiceBinder
            trackingServiceBinder.getBoundService()
                .polyLineFlow.onEach {
                    Timber.tag(timberTag).d("!!!Locations are : $it")
                    updateCameraPositionLatLng(findTheLastLatLng(it))
                    updateLastAndPreLastLng(findLastAndPreLastLatLng(it))
                }.launchIn(viewModelScope)

        }

        override fun onServiceDisconnected(p0: ComponentName?) {}
    }

    private fun findTheLastLatLng(polyLines: PolyLines) =
        if (polyLines.isNotEmpty() && polyLines.last().isNotEmpty())
            polyLines.last().last()
        else
            null

    private fun updateCameraPositionLatLng(latLng: LatLng?) = _uiState.update {
        it.copy(
            cameraPosition = latLng
        )
    }

    private fun findLastAndPreLastLatLng(polyLines: PolyLines) =
        if (polyLines.isNotEmpty() && polyLines.last().size > 1) {
            val preLastLng = polyLines.last()[polyLines.last().size - 2]
            val lastLng = polyLines.last().last()
            LastAndPreLastLatLng(preLastLng, lastLng)
        } else
           null


    private fun updateLastAndPreLastLng(lastAndPreLastLng: LastAndPreLastLatLng?) = _uiState.update {
        it.copy(
            lastAndPreLastLatLng = lastAndPreLastLng
        )
    }

    private fun sendUiEvent(event: TrackingUiEvent) = viewModelScope.launch {
        _uiEvent.emit(event)
    }

    private fun updateTrackingUiState(trackingState: TrackingState) = _uiState.update {
        it.copy(
            trackingState = trackingState
        )
    }

    private fun updateToggleRunButtonText(text: UiText) = _uiState.update {
        it.copy(
            toggleRunButtonText = text
        )
    }

    private fun updateTrackingServiceAction(trackingServiceAction: TrackingServiceAction) =
        _uiState.update {
            it.copy(
                trackingServiceAction = trackingServiceAction
            )
        }

    private fun toggleRun() {
        when (_uiState.value.trackingState) {
            TrackingState.TRACKING -> {
                updateToggleRunButtonText(UiText.StringResource(R.string.start))
                updateTrackingUiState(TrackingState.NOT_TRACKING)
                updateTrackingServiceAction(TrackingServiceAction.PAUSE_SERVICE)
            }

            TrackingState.NOT_TRACKING -> {
                updateToggleRunButtonText(UiText.StringResource(R.string.stop))
                updateTrackingUiState(TrackingState.TRACKING)
                updateTrackingServiceAction(TrackingServiceAction.START_OR_RESUME_SERVICE)

            }
        }
    }

}
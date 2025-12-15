// Package
package dk.project.app

// Imports
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service

// Service
@Service
@State(name = "dk.project.app.UserSoundSettings", storages = [Storage("userSoundSettings.xml")])

class SettingsService : PersistentStateComponent<SettingsService.State> {

    // Inner state class
    data class State(
        var soundEnabled: Boolean = true,
        var volumePercent: Int = 50                             // Initial Volume (50%)
    )

    private var state = State()

    override fun getState(): State {
        return this.state
    }

    override fun loadState(state: State) {
        this.state = state
    }

    fun isSoundEnabled(): Boolean {
        return state.soundEnabled
    }

    fun setSoundEnabled(enabled: Boolean) {
        this.state.soundEnabled = enabled
    }

    fun getVolume(): Int {
        return this.state.volumePercent.coerceIn(0, 100)
    }

    fun setVolume(volume: Int) {
        this.state.volumePercent = volume.coerceIn(0, 100)
    }

    companion object {
        fun getInstance(): SettingsService = service()
    }

}
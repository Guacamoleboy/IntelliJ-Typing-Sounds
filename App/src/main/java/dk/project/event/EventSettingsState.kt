// Package
package dk.project.event

// Imports
import com.intellij.openapi.components.*

data class SettingsState(
    var enableOnRunStart: Boolean = true,
    var enableOnRunNotStarted: Boolean = true,
    var customRunStartPath: String = "",
    var customRunNotStartedPath: String = ""
)

@State(name = "EventSettingsState", storages = [Storage("EventSettingsState.xml")])
@Service(Service.Level.APP)
class EventSettingsState : PersistentStateComponent<SettingsState> {

    // Attributes
    private var state = SettingsState()

    // ____________________________________________________________

    override fun getState(): SettingsState = state

    // ____________________________________________________________

    override fun loadState(state: SettingsState) {
        this.state = state
    }

    // ____________________________________________________________

    companion object {
        @JvmStatic
        fun getInstance(): EventSettingsState = service()
    }

}
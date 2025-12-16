// Package
package dk.project.event

// Imports
import com.intellij.execution.ExecutionListener
import com.intellij.execution.ExecutionManager
import com.intellij.execution.process.ProcessHandler
import com.intellij.execution.runners.ExecutionEnvironment
import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import com.intellij.util.messages.MessageBusConnection
import dk.project.app.SettingsService

class EventSoundComponent(project: Project) : ProjectComponent {

    // Attributes
    private val bus: MessageBusConnection = project.messageBus.connect()
    val success = Sound("complete")
    val error = Sound("aborted")

    // ____________________________________________________________

    init {

        bus.subscribe(ExecutionManager.EXECUTION_TOPIC, object : ExecutionListener {
            override fun processStarted(executorId: String, env: ExecutionEnvironment, handler: ProcessHandler) {
                super.processStarted(executorId, env, handler)
                if (!SettingsService.getInstance().isEventSoundsEnabled()) return
                val settings = EventSettingsState.getInstance().state
                if (settings.enableOnRunStart) {
                    stopAll()
                    success.play(settings.customRunStartPath.ifBlank { null })
                }
            }

            override fun processNotStarted(executorId: String, env: ExecutionEnvironment) {
                super.processNotStarted(executorId, env)
                if (!SettingsService.getInstance().isEventSoundsEnabled()) return
                val settings = EventSettingsState.getInstance().state
                if (settings.enableOnRunNotStarted) {
                    stopAll()
                    error.play(settings.customRunNotStartedPath.ifBlank { null })
                }
            }

        })
    }

    // ____________________________________________________________

    fun stopAll() {
        success.stop()
        error.stop()
    }

    // ____________________________________________________________

    @Deprecated("Deprecated in Java")
    override fun disposeComponent() {
        bus.disconnect()
    }

    // ____________________________________________________________

    override fun projectClosed() {
        stopAll()
    }

    // ____________________________________________________________

    @Deprecated("Deprecated in Java")
    override fun initComponent() {}

    // ____________________________________________________________

    override fun projectOpened() {}

    // ____________________________________________________________

    override fun getComponentName(): String = javaClass.simpleName

}
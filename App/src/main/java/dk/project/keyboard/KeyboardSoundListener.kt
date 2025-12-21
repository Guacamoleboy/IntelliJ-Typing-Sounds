// Package
package dk.project.keyboard

// Imports
import com.intellij.openapi.editor.event.*
import dk.project.app.SoundPlayer
import kotlinx.coroutines.*
import java.awt.event.KeyEvent
import java.awt.event.KeyListener
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.ex.AnActionListener
import java.util.concurrent.atomic.AtomicBoolean
import com.intellij.openapi.application.ApplicationManager

class KeyboardSoundListener : EditorFactoryListener {

    // Attributes
    private val soundPlayer = SoundPlayer()
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO) // V. 1.3.0 FIX
    private val pressedKeys = mutableSetOf<Int>()
    private val actionListenerRegistered = AtomicBoolean(false) // V 1.6.0 FIX

    // __________________________________________________________

    override fun editorCreated(event: EditorFactoryEvent) {

        registerGlobalActionListener() // V 1.6.0 FIX
        val editor = event.editor
        val component = editor.contentComponent

        component.addKeyListener(object : KeyListener {

            // Typed
            override fun keyTyped(e: java.awt.event.KeyEvent) {}

            // Released
            override fun keyReleased(e: java.awt.event.KeyEvent) {
                pressedKeys.remove(e.keyCode)
                soundPlayer.keyReleased(e.keyCode)
            }

            // Pressed
            override fun keyPressed(e: java.awt.event.KeyEvent) {

                val keyCode = e.keyCode

                // Fixes the ctrl+s sound issue
                if (e.isControlDown || e.isAltDown || e.isMetaDown) return

                if (keyCode == KeyEvent.VK_ENTER || keyCode == KeyEvent.VK_BACK_SPACE || keyCode == KeyEvent.VK_DELETE) { // V 1.6.0 FIX
                    return
                }

                // Don't play if key already is pressed
                if (!pressedKeys.contains(keyCode)) {
                    pressedKeys.add(keyCode)
                    scope.launch { soundPlayer.playSound(keyCode) }
                }
            }

        })

    }

    // __________________________________________________________
    // V 1.6.0 FIX

    private fun registerGlobalActionListener() {
        if (actionListenerRegistered.compareAndSet(false, true)) {
            ApplicationManager.getApplication().messageBus.connect()
                .subscribe(AnActionListener.TOPIC, object : AnActionListener {
                    override fun beforeActionPerformed(action: AnAction, event: AnActionEvent) {
                        val actionId = event.actionManager.getId(action) ?: return
                        when (actionId) {
                            "EditorEnter" -> scope.launch { soundPlayer.playSound(SoundPlayer.ENTER_MARKER) }
                            "EditorBackSpace" -> scope.launch { soundPlayer.playSound(KeyEvent.VK_BACK_SPACE) }
                            "EditorDelete" -> scope.launch { soundPlayer.playSound(KeyEvent.VK_DELETE) }
                        }
                    }
                })
        }
    }

    // __________________________________________________________

    override fun editorReleased(event: EditorFactoryEvent) {
        // scope.cancel() V. 1.3.0 FIX
    }

} // KeyboardSoundListener end

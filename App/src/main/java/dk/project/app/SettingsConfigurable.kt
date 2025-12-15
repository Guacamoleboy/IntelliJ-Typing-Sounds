// Package
package dk.project.app

// Imports
import com.intellij.openapi.options.Configurable
import com.intellij.util.ui.JBUI
import java.awt.BorderLayout
import java.awt.Component
import javax.swing.*

class SettingsConfigurable : Configurable {

    // Attributes
    private var panel: JPanel? = null                               // "?" needed as it's initialized as null
    private var enabledCheck: JCheckBox? = null
    private var volumeSlider: JSlider? = null

    // ____________________________________________________

    override fun createComponent(): JComponent? {

        if (panel == null) {

            panel = JPanel(BorderLayout())

            val inner = JPanel()

            inner.layout = BoxLayout(inner, BoxLayout.Y_AXIS)
            inner.border = JBUI.Borders.empty(10)

            // Enabled checkbox
            enabledCheck = JCheckBox("Enable Typing Sounds")
            enabledCheck!!.alignmentX = Component.LEFT_ALIGNMENT
            inner.add(enabledCheck)

            inner.add(Box.createVerticalStrut(8))

            // Volume label and slider
            val label = JLabel("Volume")
            label.alignmentX = Component.LEFT_ALIGNMENT
            inner.add(label)

            volumeSlider = JSlider(0, 100, SettingsService.getInstance().getVolume())
            volumeSlider!!.alignmentX = Component.LEFT_ALIGNMENT
            inner.add(volumeSlider)

            panel!!.add(inner, BorderLayout.NORTH)

        }

        return panel

    }

    // ____________________________________________________

    override fun isModified(): Boolean {
        val settings = SettingsService.getInstance()
        val enabledModified = enabledCheck?.isSelected != settings.isSoundEnabled()
        val volumeModified = volumeSlider?.value != settings.getVolume()
        return enabledModified || volumeModified
    }

    // ____________________________________________________

    override fun apply() {
        val settings = SettingsService.getInstance()
        enabledCheck?.isSelected?.let { settings.setSoundEnabled(it) }
        volumeSlider?.value?.let { settings.setVolume(it) }
    }

    // ____________________________________________________

    override fun reset() {
        val settings = SettingsService.getInstance()
        enabledCheck?.isSelected = settings.isSoundEnabled()
        volumeSlider?.value = settings.getVolume()
    }

    // ____________________________________________________

    override fun disposeUIResources() {
        panel = null
        enabledCheck = null
        volumeSlider = null
    }

    // ____________________________________________________

    override fun getDisplayName(): String {
        return "Typing Sounds"
    }

}
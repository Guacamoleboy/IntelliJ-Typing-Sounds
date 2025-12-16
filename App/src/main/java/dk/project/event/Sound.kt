// Package
package dk.project.event

// Imports
import com.intellij.openapi.diagnostic.Logger
import java.io.BufferedInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.Clip
import dk.project.app.SettingsService

// Global Attribute
private val log = Logger.getInstance(Sound::class.java)

class Sound(private val name: String) {

    // Attributes
    private var clip: Clip? = null

    // __________________________________________________________________
    // Made to follow the global volume slider settings

    fun play(customPath: String? = null, volumePercent: Int = SettingsService.getInstance().getVolume()) {

        // Pathing
        val resourceFilename = "/sounds/$name.wav"

        try {
            stop()

            val input = if (!customPath.isNullOrBlank()) {
                val file = java.io.File(customPath)
                if (file.exists() && file.isFile) {
                    BufferedInputStream(file.inputStream())
                } else {
                    BufferedInputStream(javaClass.getResourceAsStream(resourceFilename))
                }
            } else {
                BufferedInputStream(javaClass.getResourceAsStream(resourceFilename))
            }

            if (input == null) {
                return
            }

            val audioStream = AudioSystem.getAudioInputStream(input)
            clip = AudioSystem.getClip().apply {
                open(audioStream)

                // Volume
                val volumeControl = getControl(javax.sound.sampled.FloatControl.Type.MASTER_GAIN) as javax.sound.sampled.FloatControl
                volumeControl.value = percentToDb(volumePercent)

                start()
            }

        } catch (e: Exception) {
            log.error("Problem playing sound ${customPath ?: resourceFilename}", e)
        }
    }

    // __________________________________________________________________

    private fun percentToDb(volumePercent: Int): Float {
        val p = volumePercent.coerceIn(0, 100)
        return if (p == 0) -80f else 20 * kotlin.math.log10(p / 100.0).toFloat()
    }

    // __________________________________________________________________

    fun stop() {
        clip?.stop()
        clip?.close()
    }

}
package net.thechance.mena.core_chat.data.utils.audio

import dev.theolm.record.Record
import dev.theolm.record.config.AudioEncoder
import dev.theolm.record.config.OutputFormat
import dev.theolm.record.config.OutputLocation
import dev.theolm.record.config.RecordConfig

class AudioRecorderImpl : AudioRecorder {

    init {
        Record.setConfig(
            RecordConfig(
                outputLocation = OutputLocation.Cache,
                outputFormat = OutputFormat.WAV,
                audioEncoder = AudioEncoder.PCM_16BIT,
                sampleRate = 48000
            )
        )
    }

    override fun startRecording() {
        Record.startRecording()
    }

    override fun stopRecording(): String {
        return Record.stopRecording()
    }

    override fun isRecording(): Boolean {
        return Record.isRecording()
    }
}


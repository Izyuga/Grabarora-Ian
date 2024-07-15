package com.goldenfenix.grabadoradevoz

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.IOException

class MainActivity : AppCompatActivity() {

    private lateinit var recordButton: Button
    private lateinit var stopButton: Button
    private lateinit var playButton: Button
    private var mediaRecorder: MediaRecorder? = null
    private var mediaPlayer: MediaPlayer? = null
    private var audioFilePath: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recordButton = findViewById(R.id.recordButton)
        stopButton = findViewById(R.id.stopButton)
        playButton = findViewById(R.id.playButton)

        if (!hasMicrophone()) {
            recordButton.isEnabled = false
            stopButton.isEnabled = false
            playButton.isEnabled = false
        } else {
            recordButton.setOnClickListener { startRecording() }
            stopButton.setOnClickListener { stopRecording() }
            playButton.setOnClickListener { playRecording() }
        }

        requestPermissions()
    }

    private fun hasMicrophone(): Boolean {
        val pmanager = this.packageManager
        return pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE)
    }

    private fun requestPermissions() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0)
        }
    }

    private fun startRecording() {
        audioFilePath = "${externalCacheDir?.absolutePath}/audiorecordtest.3gp"

        mediaRecorder = MediaRecorder().apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            setOutputFile(audioFilePath)
            setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            try {
                prepare()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            start()
        }

        recordButton.isEnabled = false
        stopButton.isEnabled = true
        stopButton.visibility = Button.VISIBLE  // Hacer visible el botón de detener
        Toast.makeText(this, "Grabando...", Toast.LENGTH_SHORT).show()
    }

    private fun stopRecording() {
        mediaRecorder?.apply {
            stop()
            release()
        }
        mediaRecorder = null

        recordButton.isEnabled = true
        stopButton.isEnabled = false
        stopButton.visibility = Button.GONE  // Ocultar el botón de detener
        playButton.isEnabled = true

        Toast.makeText(this, "Grabación guardada en: $audioFilePath", Toast.LENGTH_SHORT).show()
    }

    private fun playRecording() {
        mediaPlayer = MediaPlayer().apply {
            try {
                setDataSource(audioFilePath)
                prepare()
                start()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }

        Toast.makeText(this, "Reproduciendo grabación", Toast.LENGTH_SHORT).show()
    }
}

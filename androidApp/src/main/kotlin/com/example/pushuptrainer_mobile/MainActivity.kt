package com.example.pushuptrainer_mobile

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import app.pushups.contract.Hint
import app.pushups.contract.TechniqueError
import app.pushups.logic.PushupCounter

class MainActivity : ComponentActivity() {
    private lateinit var preview: PreviewView
    private lateinit var count: TextView
    private lateinit var hint: TextView
    private lateinit var errors: TextView
    private var source: AndroidPoseSource? = null
    private val permission = registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (granted) startCamera() else hint.text = "Разрешите доступ к камере"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preview = PreviewView(this)
        count = TextView(this).apply { textSize = 32f; gravity = Gravity.CENTER; text = "Отжимания: 0" }
        hint = TextView(this).apply { textSize = 18f; gravity = Gravity.CENTER; text = "Ожидание камеры" }
        errors = TextView(this).apply { textSize = 18f; gravity = Gravity.CENTER }
        setContentView(LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(preview, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, 1f))
            addView(count, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            addView(hint, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            addView(errors, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT))
        })
    }

    override fun onStart() {
        super.onStart()
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            permission.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        if (source != null) return
        val counter = PushupCounter()
        val poseSource = AndroidPoseSource(this, preview) { message -> hint.text = message }
        source = poseSource
        poseSource.start { frame ->
            val state = counter.onFrame(frame)
            count.text = "Отжимания: ${state.reps}"
            hint.text = when (state.hint) {
                Hint.NO_PERSON -> "Встаньте в кадр"
                Hint.ARMS_HIDDEN -> "Не видно плеч или рук"
                Hint.TOO_FAR -> "Слишком далеко от камеры"
                Hint.TOO_CLOSE -> "Слишком близко к камере"
                null -> ""
            }
            errors.text = state.errors.joinToString("\n") {
                when (it) {
                    TechniqueError.HANDS_TOO_WIDE -> "Руки слишком широко"
                    TechniqueError.HANDS_TOO_NARROW -> "Руки слишком узко"
                    TechniqueError.SHALLOW_REP -> "Неглубокий повтор"
                }
            }
        }
    }

    override fun onStop() {
        source?.stop()
        source = null
        super.onStop()
    }
}

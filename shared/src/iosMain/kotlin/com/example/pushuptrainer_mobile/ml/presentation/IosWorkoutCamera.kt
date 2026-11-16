package com.example.pushuptrainer_mobile.ml.presentation

import platform.UIKit.UIView

interface IosWorkoutCamera {
    val view: UIView
    fun start(listener: WorkoutCameraListener)
    fun stop()
}

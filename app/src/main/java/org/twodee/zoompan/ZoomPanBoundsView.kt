package org.twodee.zoompan

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

class ZoomPanBoundsView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, styleAttributes: Int = 0) : View(context, attributes, styleAttributes) {
  private val image: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.clip)
  private var bounds = RectF()

  // Task
  private val panListener = object : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
      return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
      bounds.offset(-distanceX, -distanceY)
      invalidate()
      return true
    }
  }

  // Task
  private val zoomListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
    override fun onScale(detector: ScaleGestureDetector): Boolean {
      bounds.left = ((bounds.left - detector.focusX) * detector.scaleFactor) + detector.focusX
      bounds.right = ((bounds.right - detector.focusX) * detector.scaleFactor) + detector.focusX
      bounds.top = ((bounds.top - detector.focusY) * detector.scaleFactor) + detector.focusY
      bounds.bottom = ((bounds.bottom - detector.focusY) * detector.scaleFactor) + detector.focusY

      invalidate()

      return true
    }
  }

  // Task
  private val panDetector = GestureDetector(context, panListener)
  private val zoomDetector = ScaleGestureDetector(context, zoomListener)

  // Task
  override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
    super.onSizeChanged(w, h, oldw, oldh)

    val imageAspect = image.width / image.height.toFloat()
    val frameAspect = w / h.toFloat()

    if (imageAspect < frameAspect) {
      fillHeight()
    } else {
      fillWidth()
    }
  }

  private fun fillWidth() {
    val imageAspect = image.width / image.height.toFloat()

    val fitWidth = width.toFloat()
    val fitHeight = width / imageAspect

    val centerX = width * 0.5f
    val centerY = height * 0.5f

    bounds.left = centerX - fitWidth * 0.5f
    bounds.right = centerX + fitWidth * 0.5f
    bounds.top = centerY - fitHeight * 0.5f
    bounds.bottom = centerY + fitHeight * 0.5f
  }

  private fun fillHeight() {
    val imageAspect = image.width / image.height.toFloat()

    val fitHeight = height.toFloat()
    val fitWidth = height * imageAspect

    val centerX = width * 0.5f
    val centerY = height * 0.5f

    bounds.left = centerX - fitWidth * 0.5f
    bounds.right = centerX + fitWidth * 0.5f
    bounds.top = centerY - fitHeight * 0.5f
    bounds.bottom = centerY + fitHeight * 0.5f
  }

  // Task
  override fun onDraw(canvas: Canvas) {
    canvas.drawBitmap(image, null, bounds, null)
  }

  // Task
  override fun onTouchEvent(event: MotionEvent): Boolean {
    zoomDetector.onTouchEvent(event)
    panDetector.onTouchEvent(event)
    return true
  }
}
package org.twodee.zoompan

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

class ZoomPanView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, styleAttributes: Int = 0) : View(context, attributes, styleAttributes) {
  private val image: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.clip)
  private var bounds = RectF()
  private var scale = 1f

  // Task
  private val panListener = object : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
      return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
      bounds.offset(-distanceX, -distanceY)
      invalidate()
      return super.onScroll(e1, e2, distanceX, distanceY)
    }
  }

  // Task
  private val zoomListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
    override fun onScale(detector: ScaleGestureDetector): Boolean {
      scale *= detector.scaleFactor

      if (scale > 0.1f) {
        val xx = detector.focusX
        val yy = detector.focusY

        bounds.left = ((bounds.left - xx) * detector.scaleFactor) + xx
        bounds.right = ((bounds.right - xx) * detector.scaleFactor) + xx
        bounds.top = ((bounds.top - yy) * detector.scaleFactor) + yy
        bounds.bottom = ((bounds.bottom - yy) * detector.scaleFactor) + yy

        invalidate()
      }

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
    val fitHeight: Float
    val fitWidth: Float

    if (imageAspect < frameAspect) {
      fitWidth = h * imageAspect
      fitHeight = h.toFloat()
    } else {
      fitWidth = w.toFloat()
      fitHeight = w / imageAspect
    }

    val centerX = w * 0.5f
    val centerY = h * 0.5f

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
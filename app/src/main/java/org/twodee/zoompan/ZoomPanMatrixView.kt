package org.twodee.zoompan

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

class ZoomPanMatrixView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, styleAttributes: Int = 0) : View(context, attributes, styleAttributes) {
  private val image: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.portrait)
  private val xform: Matrix = Matrix()

  // Task
  private val panListener = object : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
      return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
      xform.postTranslate(-distanceX, -distanceY)
      invalidate()
      return true
    }
  }

  // Task
  private val zoomListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
    override fun onScale(detector: ScaleGestureDetector): Boolean {
      xform.postTranslate(-detector.focusX, -detector.focusY)
      xform.postScale(detector.scaleFactor, detector.scaleFactor)
      xform.postTranslate(detector.focusX, detector.focusY)

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

    xform.reset()

    val imageAspect = image.width / image.height.toFloat()
    val frameAspect = w / h.toFloat()

    val scale = if (imageAspect < frameAspect) {
      h / image.height.toFloat()
    } else {
      w / image.width.toFloat()
    }

    xform.postTranslate(-0.5f * image.width, -0.5f * image.height)
    xform.postScale(scale, scale)
    xform.postTranslate(0.5f * w, 0.5f * h)
  }

  // Task
  override fun onDraw(canvas: Canvas) {
    canvas.drawBitmap(image, xform, null)
  }

  // Task
  override fun onTouchEvent(event: MotionEvent): Boolean {
    zoomDetector.onTouchEvent(event)
    panDetector.onTouchEvent(event)
    return true
  }
}
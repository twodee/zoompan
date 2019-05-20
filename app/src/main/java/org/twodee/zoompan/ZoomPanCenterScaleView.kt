package org.twodee.zoompan

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View

class ZoomPanCenterScaleView @JvmOverloads constructor(context: Context, attributes: AttributeSet? = null, styleAttributes: Int = 0) : View(context, attributes, styleAttributes) {
  private val image: Bitmap = BitmapFactory.decodeResource(resources, R.drawable.clip)

  private val bounds = RectF()
  private var center = PointF()
  private var scale = 1f

  // Task
  private val panListener = object : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent): Boolean {
      return true
    }

    override fun onScroll(e1: MotionEvent, e2: MotionEvent, distanceX: Float, distanceY: Float): Boolean {
      center.x -= distanceX
      center.y -= distanceY
      syncBounds()
      invalidate()
      return true
    }
  }

  // Task
  private val zoomListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
    override fun onScale(detector: ScaleGestureDetector): Boolean {
      center.x = (center.x - detector.focusX) * detector.scaleFactor + detector.focusX
      center.y = (center.y - detector.focusY) * detector.scaleFactor + detector.focusY
      syncBounds()
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

    scale = if (imageAspect < frameAspect) {
      h / image.height.toFloat()
    } else {
      w / image.width.toFloat()
    }

    center.x = w * 0.5f
    center.y = h * 0.5f

    syncBounds()
  }

  private fun syncBounds() {
    bounds.apply {
      left = center.x - scale * image.width * 0.5f
      top = center.y - scale * image.height * 0.5f
      right = center.x + scale * image.width * 0.5f
      bottom = center.y + scale * image.height * 0.5f
    }
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
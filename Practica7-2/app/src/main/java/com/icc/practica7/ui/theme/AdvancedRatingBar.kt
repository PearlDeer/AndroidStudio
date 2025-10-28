package com.icc.practica7.ui  // ← CORREGIDO: ui en lugar de ui.theme

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.os.Bundle
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.view.animation.DecelerateInterpolator
import com.icc.practica7.R
import kotlin.math.min

/**
 * Control personalizado de Rating Bar con características avanzadas:
 * - Ratings parciales (medias estrellas)
 * - Animaciones suaves
 * - Manejo completo de estado
 * - Accesibilidad integrada
 * - Atributos personalizables
 */
class AdvancedRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.advancedRatingBarStyle
) : View(context, attrs, defStyleAttr) {

    // Propiedades configurables
    var numStars: Int = 5
        set(value) {
            field = value.coerceIn(1, 10)
            rating = rating.coerceIn(0f, field.toFloat())
            requestLayout()
        }

    var rating: Float = 0f
        set(value) {
            val clampedValue = value.coerceIn(0f, numStars.toFloat())
            if (field != clampedValue) {
                field = clampedValue
                animateToRating(clampedValue)
                onRatingChangeListener?.invoke(clampedValue)
                announceRatingChange()
            }
        }

    var starSize: Float = 48f
        set(value) {
            field = value
            requestLayout()
        }

    var starPadding: Float = 8f
        set(value) {
            field = value
            requestLayout()
        }

    var filledStarColor: Int = 0xFFFFD700.toInt()
        set(value) {
            field = value
            filledPaint.color = value
            invalidate()
        }

    var emptyStarColor: Int = 0xFFE0E0E0.toInt()
        set(value) {
            field = value
            emptyPaint.color = value
            invalidate()
        }

    var allowHalfStars: Boolean = true
    var isEditable: Boolean = true
    var animationDuration: Long = 200
    var enableHapticFeedback: Boolean = true

    // Listener para cambios de rating
    var onRatingChangeListener: ((Float) -> Unit)? = null

    // Paints para dibujar
    private val filledPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = filledStarColor
    }

    private val emptyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = emptyStarColor
    }

    // Path para la estrella (se reutiliza)
    private val starPath = Path()

    // Animación
    private var currentAnimatedRating = 0f
    private var animator: ValueAnimator? = null

    init {
        // Leer atributos del XML
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.AdvancedRatingBar,
            defStyleAttr,
            R.style.Widget_App_AdvancedRatingBar
        ).apply {
            try {
                numStars = getInt(R.styleable.AdvancedRatingBar_numStars, 5)
                rating = getFloat(R.styleable.AdvancedRatingBar_rating, 0f)
                starSize = getDimension(R.styleable.AdvancedRatingBar_starSize, 48f)
                starPadding = getDimension(R.styleable.AdvancedRatingBar_starPadding, 8f)
                filledStarColor = getColor(R.styleable.AdvancedRatingBar_filledStarColor, 0xFFFFD700.toInt())
                emptyStarColor = getColor(R.styleable.AdvancedRatingBar_emptyStarColor, 0xFFE0E0E0.toInt())
                allowHalfStars = getBoolean(R.styleable.AdvancedRatingBar_allowHalfStars, true)
                isEditable = getBoolean(R.styleable.AdvancedRatingBar_isEditable, true)
                animationDuration = getInt(R.styleable.AdvancedRatingBar_animationDuration, 200).toLong()
                enableHapticFeedback = getBoolean(R.styleable.AdvancedRatingBar_enableHapticFeedback, true)
            } finally {
                recycle()
            }
        }

        currentAnimatedRating = rating
        setupAccessibility()
    }

    private fun setupAccessibility() {
        importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
        contentDescription = context.getString(
            R.string.rating_bar_current_rating,
            rating,
            numStars
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val totalWidth = (numStars * starSize + (numStars - 1) * starPadding).toInt()
        val totalHeight = starSize.toInt()

        val width = resolveSize(totalWidth + paddingLeft + paddingRight, widthMeasureSpec)
        val height = resolveSize(totalHeight + paddingTop + paddingBottom, heightMeasureSpec)

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val actualStarSize = min(starSize, availableHeight.toFloat())

        for (i in 0 until numStars) {
            val left = paddingLeft + i * (actualStarSize + starPadding)
            val top = paddingTop + (availableHeight - actualStarSize) / 2

            // Calcular qué tanto de esta estrella debe estar llena
            val fillAmount = (currentAnimatedRating - i).coerceIn(0f, 1f)

            drawStar(canvas, left, top, actualStarSize, fillAmount)
        }
    }

    private fun drawStar(canvas: Canvas, left: Float, top: Float, size: Float, fillAmount: Float) {
        val centerX = left + size / 2
        val centerY = top + size / 2
        val outerRadius = size / 2
        val innerRadius = outerRadius * 0.4f

        starPath.reset()

        // Crear path de estrella de 5 puntas
        for (i in 0 until 10) {
            val angle = Math.PI / 2 - Math.PI / 5 * i
            val radius = if (i % 2 == 0) outerRadius else innerRadius
            val x = centerX + (radius * Math.cos(angle)).toFloat()
            val y = centerY - (radius * Math.sin(angle)).toFloat()

            if (i == 0) {
                starPath.moveTo(x, y)
            } else {
                starPath.lineTo(x, y)
            }
        }
        starPath.close()

        // Dibujar estrella vacía completa
        canvas.drawPath(starPath, emptyPaint)

        // Dibujar parte llena si aplica
        if (fillAmount > 0) {
            canvas.save()

            // Clip para mostrar solo la parte llena
            if (fillAmount < 1f) {
                canvas.clipRect(
                    left,
                    top,
                    left + size * fillAmount,
                    top + size
                )
            }

            canvas.drawPath(starPath, filledPaint)
            canvas.restore()
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEditable) return false

        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val x = event.x - paddingLeft
                val starWidth = starSize + starPadding
                var newRating = (x / starWidth).coerceIn(0f, numStars.toFloat())

                // Ajustar a medias estrellas si está habilitado
                if (allowHalfStars) {
                    val fractional = newRating % 1
                    newRating = newRating.toInt() + if (fractional < 0.25f) 0f
                    else if (fractional < 0.75f) 0.5f
                    else 1f
                } else {
                    newRating = kotlin.math.ceil(newRating)
                }

                if (newRating != rating) {
                    rating = newRating
                    if (enableHapticFeedback) {
                        performHapticFeedback(android.view.HapticFeedbackConstants.CLOCK_TICK)
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun animateToRating(targetRating: Float) {
        animator?.cancel()

        animator = ValueAnimator.ofFloat(currentAnimatedRating, targetRating).apply {
            duration = animationDuration
            interpolator = DecelerateInterpolator()
            addUpdateListener { animation ->
                currentAnimatedRating = animation.animatedValue as Float
                invalidate()
            }
            start()
        }
    }

    private fun announceRatingChange() {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        if (accessibilityManager?.isEnabled == true) {
            val announcement = context.getString(
                R.string.rating_bar_rating_changed,
                rating
            )
            announceForAccessibility(announcement)
            sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED)
        }
    }

    // ===== MANEJO DE ESTADO =====

    override fun onSaveInstanceState(): Parcelable {
        val superState = super.onSaveInstanceState()
        val savedState = SavedState(superState)
        savedState.rating = rating
        savedState.numStars = numStars
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state.superState)
            numStars = state.numStars
            rating = state.rating
        } else {
            super.onRestoreInstanceState(state)
        }
    }

    private class SavedState : BaseSavedState {
        var rating: Float = 0f
        var numStars: Int = 5

        constructor(superState: Parcelable?) : super(superState)

        constructor(source: android.os.Parcel) : super(source) {
            rating = source.readFloat()
            numStars = source.readInt()
        }

        override fun writeToParcel(out: android.os.Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeFloat(rating)
            out.writeInt(numStars)
        }

        companion object {
            @JvmField
            val CREATOR = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(source: android.os.Parcel): SavedState {
                    return SavedState(source)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        animator?.cancel()
    }
}
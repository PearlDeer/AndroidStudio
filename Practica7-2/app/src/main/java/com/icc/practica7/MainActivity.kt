package com.icc.practica7


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.icc.practica7.ui.AdvancedRatingBar

class MainActivity : AppCompatActivity() {

    private lateinit var ratingBasic: AdvancedRatingBar
    private lateinit var ratingSmall: AdvancedRatingBar
    private lateinit var ratingLarge: AdvancedRatingBar
    private lateinit var ratingReadOnly: AdvancedRatingBar
    private lateinit var ratingCustom: AdvancedRatingBar

    private lateinit var tvRatingBasic: TextView
    private lateinit var tvRatingSmall: TextView
    private lateinit var tvRatingLarge: TextView
    private lateinit var tvRatingCustom: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initializeViews()
        setupListeners()
        updateRatingTexts()
    }

    private fun initializeViews() {
        ratingBasic = findViewById(R.id.ratingBasic)
        ratingSmall = findViewById(R.id.ratingSmall)
        ratingLarge = findViewById(R.id.ratingLarge)
        ratingReadOnly = findViewById(R.id.ratingReadOnly)
        ratingCustom = findViewById(R.id.ratingCustom)

        tvRatingBasic = findViewById(R.id.tvRatingBasic)
        tvRatingSmall = findViewById(R.id.tvRatingSmall)
        tvRatingLarge = findViewById(R.id.tvRatingLarge)
        tvRatingCustom = findViewById(R.id.tvRatingCustom)
    }

    private fun setupListeners() {
        // Listener para rating básico
        ratingBasic.onRatingChangeListener = { rating ->
            tvRatingBasic.text = getString(
                R.string.current_rating,
                rating,
                ratingBasic.numStars
            )
        }

        // Listener para rating pequeño
        ratingSmall.onRatingChangeListener = { rating ->
            tvRatingSmall.text = getString(
                R.string.current_rating,
                rating,
                ratingSmall.numStars
            )
        }

        // Listener para rating grande
        ratingLarge.onRatingChangeListener = { rating ->
            tvRatingLarge.text = getString(
                R.string.current_rating,
                rating,
                ratingLarge.numStars
            )
        }

        // Listener para rating con colores personalizados
        ratingCustom.onRatingChangeListener = { rating ->
            tvRatingCustom.text = getString(
                R.string.current_rating,
                rating,
                ratingCustom.numStars
            )
        }
    }

    private fun updateRatingTexts() {
        tvRatingBasic.text = getString(
            R.string.current_rating,
            ratingBasic.rating,
            ratingBasic.numStars
        )

        tvRatingSmall.text = getString(
            R.string.current_rating,
            ratingSmall.rating,
            ratingSmall.numStars
        )

        tvRatingLarge.text = getString(
            R.string.current_rating,
            ratingLarge.rating,
            ratingLarge.numStars
        )

        tvRatingCustom.text = getString(
            R.string.current_rating,
            ratingCustom.rating,
            ratingCustom.numStars
        )
    }
}
package com.briostrategies.adfone

import android.os.Bundle
import android.view.inputmethod.EditorInfo.IME_ACTION_DONE
import android.widget.SeekBar
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModel = ViewModelProvider(this, AndroidViewModelFactory(application))
            .get(MainActivityViewModel::class.java)
            .apply {
                placesData.observe(this@MainActivity, Observer { processPlaces(it) })
                radiusData.observe(this@MainActivity, Observer { updateMiles(it) })
            }

        button.setOnClickListener {
            viewModel.search()
        }

        radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.radius = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        keyword.apply {
            doAfterTextChanged { viewModel.keyword = it }
            setOnEditorActionListener { _, action, _ ->
                if (action == IME_ACTION_DONE) {
                    viewModel.search()
                }
                false
            }
        }
    }

    private fun updateMiles(radiusMiles: Int) {
        Logger.d(TAG, "Radius $radiusMiles miles")
        miles.text = getString(R.string.miles_template, radiusMiles)
    }

    private fun processPlaces(places: Places?) {
        Logger.d(TAG, "Places $places")
        val uri =
            PlacesApi.buildPhotoUri(places?.places?.getOrNull(0)?.photos?.getOrNull(0) ?: return)
        Logger.d(TAG, "Photo Uri = $uri")

        Picasso.get()
            .load(uri)
            .into(image)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

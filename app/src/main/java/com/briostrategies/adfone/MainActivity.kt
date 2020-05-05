package com.briostrategies.adfone

import android.os.Bundle
import android.util.Log
import android.widget.SeekBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val viewModel = ViewModelProvider(this, AndroidViewModelFactory(application))
            .get(MainActivityViewModel::class.java)
            .apply {
                placesData.observe(this@MainActivity, Observer { processPlaces(it) })
            }

        button.setOnClickListener {
            viewModel.update()
        }

        radius.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                viewModel.radius = progress
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

//        Picasso.get()
//            .load(url)
//            .placeholder(R.drawable.user_placeholder)
//            .error(R.drawable.user_placeholder_error)
//            .into(imageView);
    }

    private fun processPlaces(places: Places) {
        Log.d(TAG, "$places")
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}

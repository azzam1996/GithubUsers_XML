package com.azzam.githubusers_xml.presentation.customView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.TextView
import com.azzam.githubusers_xml.R

class LabelValueView(context: Context, attrs: AttributeSet?) : FrameLayout(context, attrs) {
    private var tvLabel: TextView
    private var tvValue: TextView

    init {
        LayoutInflater.from(context).inflate(R.layout.layout_label_value, this)
        tvLabel = findViewById(R.id.tvLabel)
        tvValue = findViewById(R.id.tvValue)
    }

    fun setValues(label: String, value: String) {
        tvLabel.text = "$label : "
        tvValue.text = value
    }
}
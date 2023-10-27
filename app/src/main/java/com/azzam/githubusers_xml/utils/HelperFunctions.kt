package com.azzam.githubusers_xml.utils

import android.content.res.Resources
import com.azzam.githubusers_xml.R


fun getValueOrNoData(value: String?, resources: Resources): String{
    return value ?: resources.getString(R.string.no_data)
}
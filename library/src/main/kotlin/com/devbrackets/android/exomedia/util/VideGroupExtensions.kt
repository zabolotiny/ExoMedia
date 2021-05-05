package com.devbrackets.android.exomedia.util

import android.view.View
import android.view.ViewGroup

fun ViewGroup.children(recursive: Boolean = true): List<View> {
    val views = mutableListOf<View>()
    for (i in 0 until childCount) {
        val view = getChildAt(i)
        views.add(view)
        if (recursive && view is ViewGroup) {
            views.addAll(view.children(true))
        }
    }
    return views
}

fun View.children(): List<View> {
    val views = mutableListOf<View>()
    views.add(this)
    if (this is ViewGroup)
        views.addAll(this.children(true))
    return views
}
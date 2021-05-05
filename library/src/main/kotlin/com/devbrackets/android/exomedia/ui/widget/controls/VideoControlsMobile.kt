/*
 * Copyright (C) 2015 - 2019 ExoMedia Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.devbrackets.android.exomedia.ui.widget.controls

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.accessibility.AccessibilityEvent
import android.widget.LinearLayout
import android.widget.SeekBar
import androidx.annotation.IntRange
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat
import com.devbrackets.android.exomedia.R
import com.devbrackets.android.exomedia.ui.animation.BottomViewHideShowAnimation
import com.devbrackets.android.exomedia.ui.animation.TopViewHideShowAnimation
import com.devbrackets.android.exomedia.util.children
import com.devbrackets.android.exomedia.util.millisToFormattedTimeContentDescription
import com.devbrackets.android.exomedia.util.millisToFormattedTimeString
import java.util.*

/**
 * Provides playback controls for the [VideoView] on Mobile
 * (Phone, Tablet, etc.) devices.
 */
class VideoControlsMobile : DefaultVideoControls {
    protected lateinit var seekBar: SeekBar
    protected lateinit var extraViewsContainer: LinearLayout

    protected var userInteracting = false

    override val layoutResource: Int
        get() = R.layout.exomedia_default_controls_mobile

    override val extraViews: List<View>
        get() {
            val childCount = extraViewsContainer.childCount
            if (childCount <= 0) {
                return super.extraViews
            }
            val children = LinkedList<View>()
            for (i in 0 until childCount) {
                children.add(extraViewsContainer.getChildAt(i))
            }

            return children
        }

    private lateinit var delegateCompat: AccessibilityDelegateCompat

    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun setPosition(@IntRange(from = 0) position: Long) {
        currentTimeTextView.text = position.millisToFormattedTimeString()
        currentTimeTextView.contentDescription =
            position.millisToFormattedTimeContentDescription(context)
        seekBar.progress = position.toInt()
    }

    override fun setDuration(@IntRange(from = 0) duration: Long) {
        if (duration != seekBar.max.toLong()) {
            endTimeTextView.text = duration.millisToFormattedTimeString()
            endTimeTextView.contentDescription =
                duration.millisToFormattedTimeContentDescription(context)
            seekBar.max = duration.toInt()
        }
    }

    override fun updateProgress(
        @IntRange(from = 0) position: Long,
        @IntRange(from = 0) duration: Long,
        @IntRange(from = 0, to = 100) bufferPercent: Int
    ) {
        if (!userInteracting) {
            seekBar.secondaryProgress = (seekBar.max * (bufferPercent.toFloat() / 100)).toInt()
            seekBar.progress = position.toInt()

            updateCurrentTime(position)
        }
    }

    override fun registerListeners() {
        super.registerListeners()
        seekBar.setOnSeekBarChangeListener(SeekBarChanged())
    }

    override fun retrieveViews() {
        super.retrieveViews()
        seekBar = findViewById(R.id.exomedia_controls_video_seek)
        extraViewsContainer = findViewById(R.id.exomedia_controls_extra_container)
    }

    override fun addExtraView(view: View) {
        extraViewsContainer.addView(view)
    }

    override fun removeExtraView(view: View) {
        extraViewsContainer.removeView(view)
    }

    override fun hideDelayed(delay: Long) {
        hideDelay = delay

        if (delay < 0 || !canViewHide || isLoading) {
            return
        }

        //If the user is interacting with controls we don't want to start the delayed hide yet
        if (!userInteracting) {
            visibilityHandler.postDelayed({ animateVisibility(false) }, delay)
        }
    }

    override fun animateVisibility(toVisible: Boolean) {
        if (isVisible == toVisible) {
            return
        }

        if (!hideEmptyTextContainer || !isTextContainerEmpty) {
            textContainer.startAnimation(
                TopViewHideShowAnimation(
                    textContainer,
                    toVisible,
                    CONTROL_VISIBILITY_ANIMATION_LENGTH
                )
            )
        }

        if (!isLoading) {
            controlsContainer.startAnimation(
                BottomViewHideShowAnimation(
                    controlsContainer,
                    toVisible,
                    CONTROL_VISIBILITY_ANIMATION_LENGTH
                )
            )
        }

        isVisible = toVisible
        onVisibilityChanged()
    }

    override fun updateTextContainerVisibility() {
        if (!isVisible) {
            return
        }

        val emptyText = isTextContainerEmpty
        if (hideEmptyTextContainer && emptyText && textContainer.visibility == View.VISIBLE) {
            textContainer.clearAnimation()
            textContainer.startAnimation(
                TopViewHideShowAnimation(
                    textContainer,
                    false,
                    CONTROL_VISIBILITY_ANIMATION_LENGTH
                )
            )
        } else if ((!hideEmptyTextContainer || !emptyText) && textContainer.visibility != View.VISIBLE) {
            textContainer.clearAnimation()
            textContainer.startAnimation(
                TopViewHideShowAnimation(
                    textContainer,
                    true,
                    CONTROL_VISIBILITY_ANIMATION_LENGTH
                )
            )
        }
    }

    override fun showLoading(initialLoad: Boolean) {
        if (isLoading) {
            return
        }

        isLoading = true
        loadingProgressBar.visibility = View.VISIBLE

        if (initialLoad) {
            controlsContainer.visibility = View.GONE
        } else {
            playPauseButton.isEnabled = false
            previousButton.isEnabled = false
            nextButton.isEnabled = false
        }

        show()
    }

    override fun finishLoading() {
        if (!isLoading) {
            return
        }

        isLoading = false
        loadingProgressBar.visibility = View.GONE
        controlsContainer.visibility = View.VISIBLE

        playPauseButton.isEnabled = true
        previousButton.isEnabled = enabledViews.get(R.id.exomedia_controls_previous_btn, true)
        nextButton.isEnabled = enabledViews.get(R.id.exomedia_controls_next_btn, true)

        updatePlaybackState(videoView?.isPlaying == true)
    }

    /**
     * Listens to the seek bar change events and correctly handles the changes
     */
    protected inner class SeekBarChanged : SeekBar.OnSeekBarChangeListener {
        private var seekToTime: Long = 0

        override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
            if (!fromUser) {
                return
            }

            seekToTime = progress.toLong()
            currentTimeTextView.text = seekToTime.millisToFormattedTimeString()
            currentTimeTextView.contentDescription =
                seekToTime.millisToFormattedTimeContentDescription(context)
        }

        override fun onStartTrackingTouch(seekBar: SeekBar) {
            userInteracting = true

            if (seekListener?.onSeekStarted() != true) {
                internalListener.onSeekStarted()
            }
        }

        override fun onStopTrackingTouch(seekBar: SeekBar) {
            userInteracting = false
            if (seekListener?.onSeekEnded(seekToTime) != true) {
                internalListener.onSeekEnded(seekToTime)
            }
        }
    }

    override fun setup(context: Context) {
        super.setup(context)
        updateDelegates()
    }

  override fun addView(child: View?, index: Int, params: ViewGroup.LayoutParams?) {
    super.addView(child, index, params)
    child?.children()?.forEach { view ->
      ViewCompat.setAccessibilityDelegate(view, getDelegateCompat())
    }
  }

  private fun updateDelegates() {
    children(true).forEach { view ->
      ViewCompat.setAccessibilityDelegate(view, getDelegateCompat())
    }
  }

  private fun getDelegateCompat(): AccessibilityDelegateCompat {
    if (!this::delegateCompat.isInitialized)
      delegateCompat = object : AccessibilityDelegateCompat() {
        override fun onInitializeAccessibilityEvent(host: View?, event: AccessibilityEvent?) {
          super.onInitializeAccessibilityEvent(host, event)
          if (event?.eventType == AccessibilityEvent.TYPE_VIEW_ACCESSIBILITY_FOCUSED) {
            show()
            hideDelayed()
          }
        }
      }

    return delegateCompat
  }
}
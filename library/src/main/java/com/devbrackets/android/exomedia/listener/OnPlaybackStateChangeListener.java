/*
 * Copyright (C) 2016 Brian Wernick
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

package com.devbrackets.android.exomedia.listener;

import com.devbrackets.android.exomedia.annotation.PlaybackStateType;

/**
 * Interface definition for a callback to be invoked when the playback
 * state of the {@link com.devbrackets.android.exomedia.ui.widget.EMVideoView} or
 * {@link com.devbrackets.android.exomedia.EMAudioPlayer} changes
 */
public interface OnPlaybackStateChangeListener {
    /**
     * Called when the playback state has changed with one of the value
     * from {@link com.devbrackets.android.exomedia.type.PlaybackState}
     * @param state The value representing the new playback state
     */
    void onPlaybackStateChange(@PlaybackStateType int state);
}

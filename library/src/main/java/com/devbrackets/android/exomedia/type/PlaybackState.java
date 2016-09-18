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

package com.devbrackets.android.exomedia.type;

import com.google.android.exoplayer2.ExoPlayer;

/**
 * States associated with the current media playback
 * state for the {@link com.devbrackets.android.exomedia.AudioPlayer}
 * and {@link com.devbrackets.android.exomedia.ui.widget.VideoView}
 *
 * TODO: layout a diagram of states and cleanup/finish documentation
 */
public interface PlaybackState {
    /**
     * The player has not been initialized with a media uri or previously prepared.
     */
    int IDLE = 1;

    /**
     * The media uri specified is currently being prepared for playback
     */
    int PREPARING = 2;

    /**
     * The media needs to buffer before playback can resume
     */
    int BUFFERING = 3;

    /**
     * The media is currently seeking to the requested position
     */
    int SEEKING = 4;

    /**
     * The media is ready for playback. This can occur after {@link #BUFFERING}
     * if playback wasn't requested
     */
    int READY = 5;

    /**
     * The media is currently in playing
     */
    int PLAYING = 6;

    /**
     * The media playback was paused
     */
    int PAUSED = 7;

    /**
     * The playback completed normally (i.e. played through completion)
     */
    int COMPLETED = 8;

    /**
     * The playback was stopped
     */
    int STOPPED = 9;

    /**
     * The player has been released and can no longer be reused (todo is this true)
     */
    int RELEASED = 10;

    /**
     * There was an error during playback
     */
    int ERROR = 11;
}
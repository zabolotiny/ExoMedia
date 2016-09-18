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

package com.devbrackets.android.exomedia.annotation;

import android.support.annotation.IntDef;

import com.google.android.exoplayer2.ExoPlayer;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@IntDef({
        ExoPlayer.STATE_IDLE,
        ExoPlayer.STATE_BUFFERING,
        ExoPlayer.STATE_ENDED,
        ExoPlayer.STATE_READY
})
@Retention(RetentionPolicy.SOURCE)
public @interface ExoPlayerState {
    //Purposefully left blank
}

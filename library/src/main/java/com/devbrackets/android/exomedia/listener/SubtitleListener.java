package com.devbrackets.android.exomedia.listener;

import android.support.annotation.NonNull;

import com.google.android.exoplayer.text.Cue;

import java.util.List;

/**
 * Interface definition of a callback to be invoked indicating
 * a new subtitle cue for the content being played
 */
public interface SubtitleListener {
    //TODO: this should be using SubtitleCue instead
    void onSubtitleCues(@NonNull List<Cue> cues);
}

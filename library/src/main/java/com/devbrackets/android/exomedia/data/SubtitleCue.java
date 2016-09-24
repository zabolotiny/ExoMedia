package com.devbrackets.android.exomedia.data;

/**
 * TODO: document and actually create custom fields
 */
public class SubtitleCue {
    //TODO: I don't think we will have enough information to distinguish between these :(
    public enum Type {
        /**
         * Closed captions represent text in the primary or requested language for
         * the media in playback. This should be used for those that are hard of hearing or
         * otherwise have difficulty understanding the spoken language (or accents)
         */
        CLOSED_CAPTION,

        /**
         * Subtitles are similar to closed captions however they represent text in a language
         * other than the primary or requested language for the media in playback. This should
         * be used when viewers may not understand all spoken languages in the media.
         * <p>
         * e.g.<br/>
         * A video has a primary language in English with French guest speakers.
         * The sections with French would include subtitles in English (the primary language)
         */
        SUBTITLE
    }
}

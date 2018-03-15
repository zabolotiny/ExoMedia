package com.devbrackets.android.exomedia.core.source.builder;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.rtsp.RtspMediaSource;
import com.google.android.exoplayer2.source.rtsp.core.Header;
import com.google.android.exoplayer2.source.rtsp.core.MediaType;
import com.google.android.exoplayer2.source.rtsp.core.Method;
import com.google.android.exoplayer2.source.rtsp.core.Range;
import com.google.android.exoplayer2.source.rtsp.core.Request;
import com.google.android.exoplayer2.source.rtsp.core.Response;
import com.google.android.exoplayer2.source.rtsp.core.Status;
import com.google.android.exoplayer2.source.rtsp.core.Transport;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.TransferListener;

import java.util.HashSet;
import java.util.Set;

public class RtspMediaSourceBuilder extends MediaSourceBuilder {

    /**
     * A {@link LoadControl} that has minimum amounts of buffering so that the RTSP playback is
     * as close to live as possible
     */
    public static LoadControl rtspLoadControl = new DefaultLoadControl.Builder()
            .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
            .setBufferDurationsMs(0, 100, 0, 0)
            .createDefaultLoadControl();

    @NonNull
    @Override
    public MediaSource build(@NonNull Context context, @NonNull Uri uri, @NonNull String userAgent, @NonNull Handler handler, @Nullable TransferListener<? super DataSource> transferListener) {
        return new RtspMediaSource.Factory(Client.factory(userAgent))
                .createMediaSource(uri, handler, null);
    }

    protected static class Client extends com.google.android.exoplayer2.source.rtsp.api.Client {
        public static com.google.android.exoplayer2.source.rtsp.api.Client.Factory<Client> factory(@NonNull final String userAgent) {
            return new com.google.android.exoplayer2.source.rtsp.api.Client.Factory<Client>() {
                public Client create(com.google.android.exoplayer2.source.rtsp.api.Client.Builder builder) {
                    return new Client(builder, userAgent);
                }
            };
        }

        @NonNull
        protected String userAgent;

        @NonNull
        protected Set<Method> disallowedMethods = new HashSet<>();

        protected Client(@NonNull Builder builder, @NonNull String userAgent) {
            super(builder);
            this.userAgent = userAgent;
            disallowedMethods.add(Method.OPTIONS); //todo
        }

        @Override
        protected String userAgent() {
            return userAgent;
        }

        @Override
        protected void sendOptionsRequest() {
            if (disallowedMethods.contains(Method.OPTIONS)) {
                Response.Builder responseBuilder = new Response.Builder().status(Status.NotImplemented).header(Header.CSeq, session().getCSeq() + 1);
                onUnSuccess(makeBaseBuilder(Method.OPTIONS).build(), responseBuilder.build());
                return;
            }

            dispatch(makeBaseBuilder(Method.OPTIONS).build());
        }

        @Override
        protected void sendDescribeRequest() {
            Request.Builder builder = makeBaseBuilder(Method.DESCRIBE)
                    .header(Header.Accept, MediaType.APPLICATION_SDP);

            dispatch(builder.build());
        }

        @Override
        public void sendSetupRequest(String trackId, Transport transport, int localPort) {
            Request.Builder builder = makeBaseBuilder(Method.SETUP)
                    .url(trackId)
                    .header(Header.Transport, transport + ";client_port=" + localPort + "-" + (localPort + 1));

            dispatch(builder.build());
        }

        @Override
        public void sendPlayRequest(Range range) {
            Request.Builder builder = makeBaseBuilder(Method.PLAY)
                    .header(Header.Range, range);

            dispatch(builder.build());
        }

        @Override
        public void sendPlayRequest(Range range, float scale) {
            Request.Builder builder = makeBaseBuilder(Method.PLAY)
                    .header(Header.Range, range)
                    .header(Header.Scale, scale);

            dispatch(builder.build());
        }

        @Override
        public void sendPauseRequest() {
            dispatch(makeBaseBuilder(Method.PAUSE).build());
        }

        @Override
        protected void sendRecordRequest() {
            // Not Implemented
        }

        @Override
        protected void sendGetParameterRequest() {
            dispatch(makeBaseBuilder(Method.GET_PARAMETER).build());
        }

        @Override
        protected void sendSetParameterRequest(String name, String value) {
            // Not Implemented
        }

        @Override
        public void sendTeardownRequest() {
            dispatch(makeBaseBuilder(Method.TEARDOWN).build());
        }

        private Request.Builder makeBaseBuilder(@NonNull Method method) {
            Request.Builder builder = new Request.Builder()
                    .method(method)
                    .url(session().uri().toString())
                    .header(Header.CSeq, Integer.toString(session().nexCSeq()))
                    .header(Header.UserAgent, userAgent);

            String sessionId = session().getId();
            if (sessionId != null && sessionId.trim().length() > 0) {
                builder.header(Header.Session, sessionId);
            }

            return builder;
        }
    }
}

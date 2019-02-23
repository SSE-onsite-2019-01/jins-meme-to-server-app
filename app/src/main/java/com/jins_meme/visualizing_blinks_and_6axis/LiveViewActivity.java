package com.jins_meme.visualizing_blinks_and_6axis;

import android.content.Intent;
import android.graphics.Matrix;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.framework.CastButtonFactory;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.google.android.gms.cast.framework.SessionManagerListener;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.jins_jp.meme.MemeConnectListener;
import com.jins_jp.meme.MemeLib;
import com.jins_jp.meme.MemeRealtimeData;
import com.jins_jp.meme.MemeRealtimeListener;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import sse.JinsMemePublisher;
import sse.JinsMemeSubscriber;
import sse.MemeDoubleData;


public class LiveViewActivity extends AppCompatActivity {

    // TODO : Replace APP_ID and APP_SECRET
    private static final String APP_ID = "362082240657536";
    private static final String APP_SECRET = "f52vjkqstc45zyr8uhxty7yp7j7koog7";
    private long startTime = 0;
    private MemeDoubleData doubleData = null;
    private FrameLayout blinkLayout;
    private ImageView blinkImage;
    private VideoView blinkView;
    private FrameLayout bodyLayout;
    private ImageView bodyImage;
    private final MemeRealtimeListener memeRealtimeListener = new MemeRealtimeListener() {
        @Override
        public void memeRealtimeCallback(final MemeRealtimeData memeRealtimeData) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    updateMemeData(memeRealtimeData);
                }
            });
        }
    };
    private TextView statusLabel;
    private Button connectButton;
    final private MemeConnectListener memeConnectListener = new MemeConnectListener() {
        @Override
        public void memeConnectCallback(boolean b) {
            //describe actions after connection with JINS MEME
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeViewStatus(true);
                }
            });
        }

        @Override
        public void memeDisconnectCallback() {
            //describe actions after disconnection from JINS MEME
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    changeViewStatus(false);
                    Toast.makeText(LiveViewActivity.this, "DISCONNECTED", Toast.LENGTH_LONG).show();
                }
            });
        }
    };
    private MemeLib memeLib;
    private MqttClient subscriberClient;
    private JinsMemeSubscriber subscriber;

    private CastContext m_castContext;
    private SessionManager m_sessionManager;
    private final SessionManagerListener mSessionManagerListener = new SessionManagerListenerImpl();

    private final Handler castHiHandler = new Handler();
    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            try {
                SessionManager manager = m_castContext.getSessionManager();
                CastSession session = manager.getCurrentCastSession();
                double score = subscriber.getLastScore();

                MediaMetadata soundMetadata = new MediaMetadata(MediaMetadata.MEDIA_TYPE_MUSIC_TRACK);
                soundMetadata.putString(MediaMetadata.KEY_TITLE, "jins-meme");
                soundMetadata.putString(MediaMetadata.KEY_SUBTITLE, "your stress is " + score);

                MediaInfo mediaInfo;

                if (score > 3.0) {
                    mediaInfo = new MediaInfo.Builder("https://soundoftext.nyc3.digitaloceanspaces.com/a4d27100-3425-11e9-8130-0582ccfcede9.mp3")
                            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                            .setContentType("musics/mp3")
                            .setMetadata(soundMetadata)
                            .build();
                } else {
                    mediaInfo = new MediaInfo.Builder("https://soundoftext.nyc3.digitaloceanspaces.com/d61a1a60-3425-11e9-8130-0582ccfcede9.mp3")
                            .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                            .setContentType("musics/mp3")
                            .setMetadata(soundMetadata)
                            .build();
                }

                final RemoteMediaClient remoteMediaClient = session.getRemoteMediaClient();
                remoteMediaClient.load(mediaInfo, true, 0);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            castHiHandler.postDelayed(this, 1000 * 30);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_view);

        try {
            m_castContext = CastContext.getSharedInstance(this);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        init();
        initMqttSubscriber();
        castHiHandler.post(runnable);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_main, menu);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), menu, R.id.media_route_menu_item);

        return true;
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);

        //Sets MemeConnectListener to get connection result.
        memeLib.setMemeConnectListener(memeConnectListener);

        changeViewStatus(memeLib.isConnected());

        //Starts receiving realtime data if MEME is connected
        if (memeLib.isConnected()) {
            memeLib.startDataReport(memeRealtimeListener);
        }
    }

    @Override
    protected void onResume() {
        m_castContext.getSessionManager().addSessionManagerListener(mSessionManagerListener, CastSession.class);
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        m_castContext.getSessionManager().removeSessionManagerListener(mSessionManagerListener, CastSession.class);
    }

    private void init() {
        //Authentication and authorization of App and SDK
        MemeLib.setAppClientID(getApplicationContext(), APP_ID, APP_SECRET);
        memeLib = MemeLib.getInstance();

        blinkLayout = (FrameLayout) findViewById(R.id.blink_layout);

        blinkImage = (ImageView) findViewById(R.id.blink_image);

        blinkView = (VideoView) findViewById(R.id.blink_view);
        blinkView.setZOrderOnTop(true);
        blinkView.setVideoPath("android.resource://" + this.getPackageName() + "/" + R.raw.blink);
        blinkView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mediaPlayer.seekTo(0);
            }
        });

        bodyLayout = (FrameLayout) findViewById(R.id.body_layout);

        bodyImage = (ImageView) findViewById(R.id.body_image);

        statusLabel = (TextView) findViewById(R.id.status_label);

        connectButton = (Button) findViewById(R.id.connect_button);
        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (memeLib.isConnected()) {
                    memeLib.disconnect();
                } else {
                    Intent intent = new Intent(LiveViewActivity.this, ConnectActivity.class);
                    startActivity(intent);
                }
            }
        });

        changeViewStatus(memeLib.isConnected());
    }

    private void initMqttSubscriber()
    {
        m_sessionManager = m_castContext.getSessionManager();
        this.subscriber = new JinsMemeSubscriber(m_sessionManager);

        try {
            subscriberClient = new MqttClient(getString(R.string.broker), getString(R.string.clientId)+"-sub", new MemoryPersistence());
            subscriberClient.setCallback(this.subscriber);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(false);

            final String topic = "jins-meme-stress";
            final int qos = 0;

            subscriberClient.connect(connOpts);
            subscriberClient.subscribe(topic, qos);
        }
        catch (MqttException e)
        {
            e.printStackTrace();
        }
    }

    private void changeViewStatus(boolean connected) {
        if (connected) {
            statusLabel.setText(R.string.connected);
            statusLabel.setBackgroundColor(ContextCompat.getColor(this, R.color.black));

            connectButton.setBackground(ContextCompat.getDrawable(this, R.drawable.disconnect_button));

            blinkLayout.setAlpha(1.0f);
            blinkView.setVisibility(View.VISIBLE);
            bodyLayout.setAlpha(1.0f);
        } else {
            statusLabel.setText(R.string.not_connected);
            statusLabel.setBackgroundColor(ContextCompat.getColor(this, R.color.red));

            connectButton.setBackground(ContextCompat.getDrawable(this, R.drawable.connect_button));

            blinkImage.setVisibility(View.VISIBLE);
            blinkLayout.setAlpha(0.2f);
            blinkView.setVisibility(View.INVISIBLE);
            bodyLayout.setAlpha(0.2f);
        }
    }

    private void updateMemeData(MemeRealtimeData d) {
        long currentTime = System.currentTimeMillis();

        if (startTime == 0) {
            doubleData = new MemeDoubleData();
            JinsMemePublisher publisher = new JinsMemePublisher(getString(R.string.broker), getString(R.string.clientId));
            publisher.execute(doubleData.copy());

            doubleData = new MemeDoubleData();
            startTime = currentTime;
            doubleData.addMemeDoubleData(d);
        } else if ((currentTime - startTime) > 1000 * 60) {

            JinsMemePublisher publisher = new JinsMemePublisher(getString(R.string.broker), getString(R.string.clientId));
            publisher.execute(doubleData.copy());

            doubleData = new MemeDoubleData();
            startTime = currentTime;
            doubleData.addMemeDoubleData(d);
        } else {
            doubleData.addMemeDoubleData(d);
        }


        // for blink
        if (d.getBlinkSpeed() > 0) {
            Log.d("LiveViewActivity", "Blink Speed:" + d.getBlinkSpeed());
            blinkImage.setVisibility(View.INVISIBLE);
            blink();
        }

        // for body (Y axis rotation)
        double radian = Math.atan2(d.getAccX(), d.getAccZ());
        rotate(Math.toDegrees(-radian)); // for mirroring display(radian x -1)
    }

    private void blink() {
        blinkView.seekTo(0);
        blinkView.start();
    }

    private void rotate(double degree) {
        int width = bodyImage.getDrawable().getBounds().width();
        int height = bodyImage.getDrawable().getBounds().height();

        Matrix matrix = new Matrix();
        bodyImage.setScaleType(ImageView.ScaleType.MATRIX);
        matrix.postRotate((float) degree, width / 2, height / 2);
        matrix.postScale(0.5f, 0.5f);
        bodyImage.setImageMatrix(matrix);
    }

    private class SessionManagerListenerImpl implements SessionManagerListener<CastSession> {
        @Override
        public void onSessionStarting(CastSession castSession) {
        }

        @Override
        public void onSessionStarted(CastSession castSession, String s) {
        }

        @Override
        public void onSessionStartFailed(CastSession castSession, int i) {
        }

        @Override
        public void onSessionEnding(CastSession castSession) {
        }

        @Override
        public void onSessionEnded(CastSession castSession, int i) {
        }

        @Override
        public void onSessionResuming(CastSession castSession, String s) {
        }

        @Override
        public void onSessionResumed(CastSession castSession, boolean b) {
        }

        @Override
        public void onSessionResumeFailed(CastSession castSession, int i) {
        }

        @Override
        public void onSessionSuspended(CastSession castSession, int i) {
        }
    }
}

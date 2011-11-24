package org.andengine.ui.activity;

import org.andengine.audio.music.MusicManager;
import org.andengine.audio.sound.SoundManager;
import org.andengine.engine.Engine;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.WakeLockOptions;
import org.andengine.entity.scene.Scene;
import org.andengine.opengl.view.RenderSurfaceView;
import org.andengine.opengl.view.RenderSurfaceView.IRendererListener;
import org.andengine.sensor.accelerometer.AccelerometerSensorOptions;
import org.andengine.sensor.accelerometer.IAccelerometerListener;
import org.andengine.sensor.location.ILocationListener;
import org.andengine.sensor.location.LocationSensorOptions;
import org.andengine.sensor.orientation.IOrientationListener;
import org.andengine.sensor.orientation.OrientationSensorOptions;
import org.andengine.ui.IGameInterface;
import org.andengine.util.ActivityUtils;
import org.andengine.util.debug.Debug;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.view.Gravity;
import android.widget.FrameLayout.LayoutParams;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 * 
 * @author Nicolas Gramlich
 * @since 11:27:06 - 08.03.2010
 */
public abstract class BaseGameActivity extends BaseActivity implements IGameInterface, IRendererListener {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected Engine mEngine;
	private WakeLock mWakeLock;
	protected RenderSurfaceView mRenderSurfaceView;
	protected boolean mWindowFocused;
	private boolean mPaused;
	private boolean mGameLoaded;

	// ===========================================================
	// Constructors
	// ===========================================================

	@Override
	protected void onCreate(final Bundle pSavedInstanceState) {
		super.onCreate(pSavedInstanceState);

		this.mPaused = true;

		this.mEngine = this.onLoadEngine();

		this.applyEngineOptions(this.mEngine.getEngineOptions());

		this.onSetContentView();
	}

	@Override
	protected void onResume() {
		super.onResume();

		if(this.mPaused && this.mWindowFocused) {
			this.doResume();
		}
	}

	@Override
	public void onWindowFocusChanged(final boolean pHasWindowFocus) {
		super.onWindowFocusChanged(pHasWindowFocus);

		this.mWindowFocused = pHasWindowFocus;

		if(pHasWindowFocus && this.mPaused) {
			this.doResume();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

		if(!this.mPaused) {
			this.doPause();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		this.mEngine.onDestroy();

		this.onUnloadResources();
	}

	@Override
	public void onUnloadResources() {
		if(this.mEngine.getEngineOptions().getAudioOptions().needsMusic()) {
			this.getMusicManager().releaseAll();
		}
		if(this.mEngine.getEngineOptions().getAudioOptions().needsSound()) {
			this.getSoundManager().releaseAll();
		}
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	public Engine getEngine() {
		return this.mEngine;
	}

	public boolean isPaused() {
		return this.mPaused;
	}

	public boolean isRunning() {
		return !this.mPaused;
	}

	public boolean isWindowFocused() {
		return this.mWindowFocused;
	}

	public boolean isGameLoaded() {
		return this.mGameLoaded;
	}

	public SoundManager getSoundManager() {
		return this.mEngine.getSoundManager();
	}

	public MusicManager getMusicManager() {
		return this.mEngine.getMusicManager();
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onResumeGame() {

	}

	@Override
	public void onPauseGame() {

	}

	@Override
	public void onSurfaceCreated() {
		Debug.d("onSurfaceCreated");
	}

	@Override
	public void onSurfaceChanged(final int pWidth, final int pHeight) {
		Debug.d("onSurfaceChanged: pWidth=" + pWidth + "  pHeight=" + pHeight);
	}

	// ===========================================================
	// Methods
	// ===========================================================

	private void doResume() {
		if(!this.mGameLoaded) {
			this.onLoadResources();
			final Scene scene = this.onLoadScene();
			this.mEngine.onLoadComplete(scene);
			this.onLoadComplete();
			this.mGameLoaded = true;
		}

		this.mPaused = false;
		this.acquireWakeLock(this.mEngine.getEngineOptions().getWakeLockOptions());
		this.mEngine.onResume();

		this.mRenderSurfaceView.onResume();
		this.mEngine.start();
		this.onResumeGame();
	}

	private void doPause() {
		this.mPaused = true;
		this.releaseWakeLock();

		this.mEngine.onPause();
		this.mEngine.stop();
		this.mRenderSurfaceView.onPause();
		this.onPauseGame();
	}

	public void runOnUpdateThread(final Runnable pRunnable) {
		this.mEngine.runOnUpdateThread(pRunnable);
	}

	protected void onSetContentView() {
		this.mRenderSurfaceView = new RenderSurfaceView(this);
		this.mRenderSurfaceView.setRenderer(this.mEngine, this);

		this.setContentView(this.mRenderSurfaceView, BaseGameActivity.createSurfaceViewLayoutParams());
	}

	private void acquireWakeLock(final WakeLockOptions pWakeLockOptions) {
		if(pWakeLockOptions == WakeLockOptions.SCREEN_ON) {
			ActivityUtils.keepScreenOn(this);
		} else {
			final PowerManager pm = (PowerManager) this.getSystemService(Context.POWER_SERVICE);
			this.mWakeLock = pm.newWakeLock(pWakeLockOptions.getFlag() | PowerManager.ON_AFTER_RELEASE, "AndEngine");
			try {
				this.mWakeLock.acquire();
			} catch (final SecurityException e) {
				Debug.e("You have to add\n\t<uses-permission android:name=\"android.permission.WAKE_LOCK\"/>\nto your AndroidManifest.xml !", e);
			}
		}
	}

	private void releaseWakeLock() {
		if(this.mWakeLock != null && this.mWakeLock.isHeld()) {
			this.mWakeLock.release();
		}
	}

	private void applyEngineOptions(final EngineOptions pEngineOptions) {
		if(pEngineOptions.isFullscreen()) {
			ActivityUtils.requestFullscreen(this);
		}

		if(pEngineOptions.getAudioOptions().needsMusic() || pEngineOptions.getAudioOptions().needsSound()) {
			this.setVolumeControlStream(AudioManager.STREAM_MUSIC);
		}

		switch(pEngineOptions.getScreenOrientation()) {
			case LANDSCAPE:
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
				break;
			case PORTRAIT:
				this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				break;
		}
	}

	protected static LayoutParams createSurfaceViewLayoutParams() {
		final LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		layoutParams.gravity = Gravity.CENTER;
		return layoutParams;
	}

	protected void enableVibrator() {
		this.mEngine.enableVibrator(this);
	}

	/**
	 * @see {@link Engine#enableLocationSensor(Context, ILocationListener, LocationSensorOptions)}
	 */
	protected void enableLocationSensor(final ILocationListener pLocationListener, final LocationSensorOptions pLocationSensorOptions) {
		this.mEngine.enableLocationSensor(this, pLocationListener, pLocationSensorOptions);
	}

	/**
	 * @see {@link Engine#disableLocationSensor(Context)}
	 */
	protected void disableLocationSensor() {
		this.mEngine.disableLocationSensor(this);
	}

	/**
	 * @see {@link Engine#enableAccelerometerSensor(Context, IAccelerometerListener)}
	 */
	protected boolean enableAccelerometerSensor(final IAccelerometerListener pAccelerometerListener) {
		return this.mEngine.enableAccelerometerSensor(this, pAccelerometerListener);
	}

	/**
	 * @see {@link Engine#enableAccelerometerSensor(Context, IAccelerometerListener, AccelerometerSensorOptions)}
	 */
	protected boolean enableAccelerometerSensor(final IAccelerometerListener pAccelerometerListener, final AccelerometerSensorOptions pAccelerometerSensorOptions) {
		return this.mEngine.enableAccelerometerSensor(this, pAccelerometerListener, pAccelerometerSensorOptions);
	}

	/**
	 * @see {@link Engine#disableAccelerometerSensor(Context)}
	 */
	protected boolean disableAccelerometerSensor() {
		return this.mEngine.disableAccelerometerSensor(this);
	}

	/**
	 * @see {@link Engine#enableOrientationSensor(Context, IOrientationListener)}
	 */
	protected boolean enableOrientationSensor(final IOrientationListener pOrientationListener) {
		return this.mEngine.enableOrientationSensor(this, pOrientationListener);
	}

	/**
	 * @see {@link Engine#enableOrientationSensor(Context, IOrientationListener, OrientationSensorOptions)}
	 */
	protected boolean enableOrientationSensor(final IOrientationListener pOrientationListener, final OrientationSensorOptions pLocationSensorOptions) {
		return this.mEngine.enableOrientationSensor(this, pOrientationListener, pLocationSensorOptions);
	}

	/**
	 * @see {@link Engine#disableOrientationSensor(Context)}
	 */
	protected boolean disableOrientationSensor() {
		return this.mEngine.disableOrientationSensor(this);
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
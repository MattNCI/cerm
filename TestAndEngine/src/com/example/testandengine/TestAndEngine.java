package com.example.testandengine;


import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.FillResolutionPolicy;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.engine.camera.Camera;

import android.graphics.Typeface;

public class TestAndEngine extends SimpleBaseGameActivity 
{
	
	private Camera camera;
	private static final int CAMERA_WIDTH = 720;
	private static final int CAMERA_HEIGHT = 480;
	private Font mFont;
	
	
	
	@Override
	public EngineOptions onCreateEngineOptions()
	{
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED,
		new FillResolutionPolicy(), camera);
		return engineOptions;
	}
	
	@Override
	protected void onCreateResources()
	{
		this.mFont = FontFactory.create(this.getFontManager(), this.getTextureManager(), 256, 256, Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 32);
		this.mFont.load();
		
	}
	
	@Override
	protected Scene onCreateScene()
	{
		
		Scene scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		final VertexBufferObjectManager vertexBufferObjectManager = this.getVertexBufferObjectManager();
		final Text centerText = new Text(100, 40, this.mFont, "Hello guys!\nYou can even have multilined text!\nThis text is centred.", new TextOptions(HorizontalAlign.CENTER), vertexBufferObjectManager);
		final Text leftText = new Text(100, 170, this.mFont, "Also left aligned!\nLorem ipsum dolor sit amat...", new TextOptions(HorizontalAlign.LEFT), vertexBufferObjectManager);
		final Text rightText = new Text(100, 300, this.mFont, "And right aligned!\nLorem ipsum dolor sit amat...", new TextOptions(HorizontalAlign.RIGHT), vertexBufferObjectManager);
		
		scene.attachChild(centerText);
		scene.attachChild(leftText);
		scene.attachChild(rightText);
		
		return scene;
	}
}

package ie.nci.bshbise3.prj.andengine.pinball;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;

public class ManagedSceneSplash extends ManagedSceneBase {

	private BitmapTextureAtlas splashTA;
	private ITextureRegion splashTR;

	public ManagedSceneSplash(BaseGameActivity pActivity) {
		super(pActivity);
		this.setActivity(pActivity);
	}
	

	@Override
	protected void onCreateScene(Scene s) {
		s.setBackground(new Background(0, 0, 0));

		Sprite icon = new Sprite(0, 0, splashTR,
				this.getActivity().getVertexBufferObjectManager());
		
		// TODO: get camera info here!?!!
		/*
		icon.setPosition((camera.getWidth() - icon.getWidth()) / 2,
				(camera.getHeight() - icon.getHeight()) / 2);
		*/
		s.attachChild(icon);
	}

	@Override
	protected void onLoadResources() {
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		splashTA = new BitmapTextureAtlas(this.getActivity().getTextureManager(),480,800); //256, 256);
		splashTR = BitmapTextureAtlasTextureRegionFactory.createFromAsset(
				splashTA, this.getActivity(), "splash.png", 0, 0);

		splashTA.load();
	}
	
	// not implem.
	@Override protected void onCreateMenuScene(MenuScene aMenuScene) {}


}

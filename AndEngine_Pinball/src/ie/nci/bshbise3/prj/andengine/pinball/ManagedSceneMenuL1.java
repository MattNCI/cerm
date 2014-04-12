package ie.nci.bshbise3.prj.andengine.pinball;

import java.util.ArrayList;

import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.scene.menu.MenuScene;
import org.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.andengine.entity.scene.menu.item.IMenuItem;
import org.andengine.entity.scene.menu.item.SpriteMenuItem;
import org.andengine.entity.scene.menu.item.TextMenuItem;
import org.andengine.entity.scene.menu.item.decorator.ScaleMenuItemDecorator;
import org.andengine.entity.text.TextOptions;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.StrokeFont;
import org.andengine.opengl.texture.ITexture;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.ui.activity.BaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.debug.Debug;

import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.Toast;

public class ManagedSceneMenuL1 extends ManagedSceneBase implements IOnMenuItemClickListener {

	// menu switch scene observable
	//private String menuActivityRequestString = null;
	
	// menu and font variables
	private int FONT_SIZE = 48;
	private Boolean useTextMenu = true;
	private int menuWidth = 256;
	private int menuHeight = 512;
	private int menuItemHeight = 64;
	private int menuItemPadding = 5;

	// resources
	private Font mFont;
	private Background mMenuBg;
	private BitmapTextureAtlas mMenuTA;

	// the menu
	ArrayList<MenuItemTextureReference> iMenuItemTextures = new ArrayList<MenuItemTextureReference>();
	
	// ### constructor ### //
	public ManagedSceneMenuL1(BaseGameActivity pActivity) {
		this.setActivity(pActivity);
	}
	
	// ### methods ### //
	@Override
	public void onLoadResources() {

		// what's my image base folder
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/menu/");
		
		// menu scene texture atlas
		this.mMenuTA = new BitmapTextureAtlas(
				this.getActivity().getTextureManager(), 
				menuWidth, 
				menuHeight,
				TextureOptions.BILINEAR
		);
		
		// menu background
		mMenuBg = new Background(0, 0, 1);
		
		// create and load font textures
		final ITexture strokeFontTexture = new BitmapTextureAtlas(
				this.getActivity().getTextureManager(), 
				256, 256, 
				TextureOptions.BILINEAR);

		this.mFont =new StrokeFont(
				this.getActivity().getFontManager(), 
				strokeFontTexture, 
				Typeface.create(Typeface.DEFAULT, Typeface.BOLD), 
				FONT_SIZE, 
				true, 
				Color.BLACK, 
				2, Color.WHITE,
				false); 

		this.mFont.load();

		// populate iMenuItemTextures from GameMenuEnum
		loadMenuItemTextures(this.mMenuTA, menuItemHeight, menuItemPadding);

		// do load
		this.mMenuTA.load();
		
	}
	private void loadMenuItemTextures(BitmapTextureAtlas aBitmapTextureAtlas, int itemHeight, int itemPadding) {
		// used in the for-each loop
		MenuItemTextureReference mitr;
		
		// load images (if not forcing text menu)
		for (GameMenuEnum m : GameMenuEnum.values()){
			mitr = new MenuItemTextureReference();
			mitr.enumId = m.ordinal();
			mitr.enumItem = m;
			mitr.textureRegion = (useTextMenu || m.imageFileName() == null)
					? null
					: BitmapTextureAtlasTextureRegionFactory
							.createFromAsset(
									aBitmapTextureAtlas, 
									this.getActivity(),
									m.imageFileName(),
									0, (m.ordinal() * (itemHeight + itemPadding))
					);
			
			// push texture to array at enum ordinal pos
			this.iMenuItemTextures.add(mitr);
		}
	}

	@Override
	public void onCreateMenuScene(MenuScene s) {
		
		//background
		s.setBackground(mMenuBg);
		
		// iterate cached iMenuItemTextures
		for(MenuItemTextureReference tr : iMenuItemTextures){
			s.addMenuItem(new ScaleMenuItemDecorator(
					(tr.textureRegion == null) 
							? new TextMenuItem(
									tr.enumItem.id(),
									this.mFont,
									tr.enumItem.text(getActivity()), 
									new TextOptions(HorizontalAlign.CENTER),
									getActivity().getVertexBufferObjectManager()
							)
							: new SpriteMenuItem(
									tr.enumItem.id(),
									200, 70, 
									tr.textureRegion,
									getActivity().getVertexBufferObjectManager()
							),
					0.9f, 1
			));
		}
		s.setOnMenuItemClickListener(this);
		s.buildAnimations();
	}

	private void menuToast(String txt) {
		try {
			this.getActivity().toastOnUIThread(txt, Toast.LENGTH_SHORT);
		} catch (final Exception e) {
			Debug.e(e);
		}
	}

	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {
		// in scene MENU? or move to scene Manager?

		final int menuItemId = pMenuItem.getID();
		
		/*
		// when click play
		if (menuItemId ==  GameMenuEnum.MENU_PLAY.id()) {
			menuActivityRequestString = GameMenuEnum.MENU_PLAY.text(getActivity());
		}
		// when click options
		else if (menuItemId == GameMenuEnum.MENU_OPTIONS.id()) {
			menuActivityRequestString = GameMenuEnum.MENU_OPTIONS.text(getActivity());
		}
		// when click about
		else if (menuItemId == GameMenuEnum.MENU_ABOUT.id()) {
			menuActivityRequestString = GameMenuEnum.MENU_ABOUT.text(getActivity());
		}
		// when click quit
		else if (menuItemId == GameMenuEnum.MENU_QUIT.id()) {
			menuActivityRequestString = GameMenuEnum.MENU_OPTIONS.text(getActivity());
			//this.getActivity().finish();
		} else {
			menuToast("!?!?");			
		}
		
		menuToast(menuActivityRequestString);
		*/
		
		this.setChanged();
		this.notifyObservers(menuItemId);

		return false;
	}

	protected class MenuItemTextureReference {
		int enumId;
		GameMenuEnum enumItem;
		ITextureRegion textureRegion;
	}
	
	// not implem.
	@Override protected void onCreateScene(Scene aScene) {
		menuToast("### onCreateScene ###");
	}


}

package de.zoneofcontrol.quadtree.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.zoneofcontrol.quadtree.Constants;
import de.zoneofcontrol.quadtree.Core;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height=Constants.SCREEN_HEIGHT;
		config.width= Constants.SCREEN_WIDTH;
		config.fullscreen=true;
		new LwjglApplication(new Core(), config);
	}
}

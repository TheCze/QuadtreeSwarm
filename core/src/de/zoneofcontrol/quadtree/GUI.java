package de.zoneofcontrol.quadtree;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class GUI {
    Rectangle area;
    String name;

    public abstract boolean interact(Vector2 pos, String info);

    public abstract void drawShape(ShapeRenderer shaper);
    public abstract void drawText(SpriteBatch batch, BitmapFont font);

    public String getName(){
        return name;
    }
}

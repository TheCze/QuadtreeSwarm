package de.zoneofcontrol.quadtree;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class Slider extends GUI {
    private float currentpercentage=0.5f;
    private float minvalue=0f;
    private float maxvalue=1f;
    private final float width=100;
    private final float height=20;
    private final float sliderwidth=2;

    public Slider(String name,float x, float y){
        area=new Rectangle(x,y,width,height);
        this.name=name;
    }

    public void drawShape(ShapeRenderer shaper){
        shaper.setColor(Color.GRAY);
        shaper.rect(area.x,area.y,area.getWidth(),area.getHeight());
        shaper.setColor(Color.WHITE);
        shaper.rect(area.x,area.y,width*currentpercentage,area.getHeight());
    }

    @Override
    public void drawText(SpriteBatch batch, BitmapFont font) {
        float percentage=currentpercentage*100;
        String text=name+" :"+(int)percentage+"%";
        font.draw(batch,text,area.x,area.y-5);
    }


    private float getSliderPosition() {
        return (area.getWidth()*currentpercentage)-sliderwidth/2;
    }


    @Override
    public boolean interact(Vector2 pos, String info) {
        if(area.contains(pos)){
            float pixel=pos.x-area.x;
            currentpercentage=pixel/area.width;
            return true;
        }
        else
            return false;
    }

    public float getCurrentpercentage() {
        return currentpercentage;
    }
}

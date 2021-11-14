package de.zoneofcontrol.quadtree;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;


import java.util.ArrayList;
import java.util.Random;

public class Core extends ApplicationAdapter {
	private float timepernewunit=1110.1f;
	private float unittimer=0f;


	private OrthographicCamera gamecam;
	private OrthographicCamera menucam;
	private SpriteBatch menubatch;
	private SpriteBatch gamebatch;
	private ShapeRenderer gameshaper;
	private ShapeRenderer menushaper;
	private static Sprite sprunit;
	private Quadtree quadtree;
	private BitmapFont font;
	private Timer timer;
	private ArrayList<GUI> gui;
	
	@Override
	public void create () {

		timer=new Timer();
		gamecam=new OrthographicCamera();
		gamecam.setToOrtho(false,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
		menucam=new OrthographicCamera();
		menucam.setToOrtho(false,Constants.SCREEN_WIDTH,Constants.SCREEN_HEIGHT);
		gamebatch = new SpriteBatch();
		menubatch = new SpriteBatch();
		gamebatch.setProjectionMatrix(gamecam.combined);
		font= new BitmapFont();
		gameshaper =new ShapeRenderer();
		menushaper=new ShapeRenderer();
		sprunit = new Sprite(new Texture("unit.png"));
		sprunit.setScale(0.25f);
		quadtree=new Quadtree(new Rectangle(Constants.WORLD_START_X,Constants.WORLD_START_Y,Constants.WORLD_WIDTH,Constants.WORLD_HEIGHT));
		addRandomUnits(2000,quadtree);
		createGUI();
		//quadtree.addUnit(new Unit(new Vector2(Constants.WORLD_START_X+100,Constants.WORLD_START_Y+100),0,sprunit));
	}

	private void createGUI() {
		gui=new ArrayList<GUI>();
		gui.add(new Slider("wanderfactor",50,100));
		gui.add(new Slider("distancefactor",50,150));
		gui.add(new Slider("vectorfactor",50,200));
	}

	public void addRandomUnits(int amount, Quadtree quad){
		Random rdm=new Random();
		for(int i=0; i<amount;i++){
			Vector2 pos=new Vector2(quad.getArea().x+quad.getArea().width*rdm.nextFloat(),quad.getArea().y+quad.getArea().height*rdm.nextFloat());
			Unit u=new Unit(pos,1,sprunit);
			quad.addUnit(u);
		}
	}

	@Override
	public void render () {
		checkInput();
		updateGame(Gdx.graphics.getDeltaTime());
		gamecam.update();
		gamebatch.setProjectionMatrix(gamecam.combined);
		gameshaper.setProjectionMatrix(gamecam.combined);
		Gdx.gl.glClearColor(0.05f, 0.05f, 0.05f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		gameshaper.begin(ShapeRenderer.ShapeType.Line);
		quadtree.drawQuad(gameshaper);
		gameshaper.end();
		gamebatch.begin();
		quadtree.drawUnits(gamebatch);
		gamebatch.end();
		menushaper.begin(ShapeRenderer.ShapeType.Filled);
		for(GUI g: gui){
			g.drawShape(menushaper);
		}
		menushaper.end();
		menubatch.begin();
		for(GUI g: gui){
			g.drawText(menubatch,font);
		}
		writeText();
		menubatch.end();
	}

	private void writeText() {
		font.draw(menubatch,"Unit update MMS: "+timer.getAverageMStime(),5,30);
		font.draw(menubatch,"Unitcount: "+quadtree.getNumberOfUnitsInQuad(),5,15);
	}

	private void updateGame(float delta) {
		timer.start();
		quadtree.update(delta);
		timer.stop();
		unittimer+=delta;
		int added=0;
		while(unittimer>timepernewunit){
			addRandomUnits(1,quadtree);
			unittimer-=timepernewunit;
			added++;
		}
	}

	private void checkInput(){
		Vector2 pos=new Vector2(Gdx.input.getX(),Gdx.input.getY());
		Vector3 pos3=menucam.unproject(new Vector3(pos.x,pos.y,0));
		pos.x=pos3.x;
		pos.y=pos3.y;


		if(Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
			if(isInGUI(pos)){
				updateSettings();
			}
			else {
				pos=new Vector2(Gdx.input.getX(),Gdx.input.getY());
				pos3=gamecam.unproject(new Vector3(pos.x,pos.y,0));
				pos.x=pos3.x;
				pos.y=pos3.y;
				quadtree.sendSignal(pos);
			}
		}
		if(Gdx.input.isButtonPressed(Input.Buttons.RIGHT)){
			pos=new Vector2(Gdx.input.getX(),Gdx.input.getY());
			pos3=gamecam.unproject(new Vector3(pos.x,pos.y,0));
			pos.x=pos3.x;
			pos.y=pos3.y;
			Unit u=new Unit(pos,1,sprunit);
			quadtree.addUnit(u);
		}
		if(Gdx.input.isKeyJustPressed(Input.Keys.B)){
			if(Constants.SHOW_VECTORS)
				Constants.SHOW_VECTORS=false;
			else
				Constants.SHOW_VECTORS=true;
		}

		if(Gdx.input.isKeyJustPressed(Input.Keys.X)){
			Constants.DEBUG_QUADS=!Constants.DEBUG_QUADS;
		}

		if(Gdx.input.isKeyPressed(Input.Keys.W)){
			gamecam.translate(0,Constants.CAMERA_SPEED);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.A)){
			gamecam.translate(-Constants.CAMERA_SPEED,0);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.S)){
			gamecam.translate(0,-Constants.CAMERA_SPEED);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.D)){
			gamecam.translate(Constants.CAMERA_SPEED,-1);
		}
		if(Gdx.input.isKeyPressed(Input.Keys.C)){
			gamecam.zoom+=0.02f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.V)){
			gamecam.zoom-=0.02f;
		}
		if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE)){
			Gdx.app.exit();
		}
	}

	private void updateSettings() {
		for(GUI g: gui){
			if(g instanceof Slider){
				float perc=((Slider) g).getCurrentpercentage();
				switch (g.name){
					case "wanderfactor":
						Constants.wanderfactor=perc;
						break;
					case "distancefactor":
						Constants.distancefactor=perc;
						break;
					case "vectorfactor":
						Constants.vectorfactor=perc;
						break;
				}
			}
		}
	}

	private boolean isInGUI(Vector2 pos) {
		for(GUI g:gui){
			if(g.interact(pos,"")){
				return true;
			}
		}
		return false;
	}

	@Override
	public void dispose () {
		gamebatch.dispose();
	}
}

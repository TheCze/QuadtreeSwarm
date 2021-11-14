package de.zoneofcontrol.quadtree;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

public class Unit {

    private Circle rangecircle;
    private Vector2 position;
    private Vector2 movegoal;
    private Vector2 distancegoal;
    private Vector2 totalvector;
    private Vector2 swarmvector;
    private float health;
    private int team;
    private float speed=1f;
    private float range=45f;
    private float minrange=10f;
    private float perfectrange=10f;
    private float maxrange=50f;
    private Sprite sprite;
    private Color mycolor;
    private Quadtree myquad;


    public Unit(Vector2 position, int team, Sprite sprite){
        this.position=position;
        this.movegoal=position.cpy();
        this.distancegoal =new Vector2();
        this.totalvector=new Vector2();
        this.team=team;
        this.sprite=sprite;
        rangecircle=new Circle();
        this.swarmvector=new Vector2();
        updateMyCircle();
        health=100;
        if(team==1){
            mycolor=Color.WHITE;
        }
        else
        {
            mycolor=Color.RED;
        }
    }

    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void update(float delta){
        updateMyCircle();
        searchForUnits();
        updateTotalVector();
        move();
        checkForNewMovegoal();
    }

    private void checkForNewMovegoal() {
        if(targetInRange(movegoal)){
            getNewGoal();
        }
    }

    private void updateTotalVector() {
        totalvector.setZero();

        swarmvector.nor();
        swarmvector.scl(Constants.vectorfactor);
        totalvector.add(swarmvector);

        Vector2 wandervector = getWandervector();
        totalvector.add(wandervector);

        distancegoal.nor();
        distancegoal.scl(Constants.distancefactor);
        totalvector.add(distancegoal);


        totalvector.nor();
        totalvector.scl(speed);


    }

    public Vector2 getWandervector() {
        Vector2 wandervector = movegoal.cpy().sub(position);
        wandervector.nor();
        wandervector.scl(Constants.wanderfactor);
        return wandervector;
    }

    private void updateMyCircle() {
        rangecircle.setPosition(position);
        rangecircle.setRadius(range);
    }

    private void move() {
            position.add(totalvector);

    }

    private boolean targetInRange(Vector2 goal) {
        float dst=position.dst(goal);
        if(dst<range)
            return true;
        else
            return false;
    }

    private void searchForUnits() {
            ArrayList<Unit> units=myquad.getUnitsInCircle(rangecircle,true);
            swarmvector.setZero();
            distancegoal.setZero();
            for(Unit u: units){
                    if(u.getPosition().dst(this.position)<range&&!u.equals(this)){
                        adjustDistanceGoal(u.getPosition());
                        swarmvector.add(u.getTotalvector());
                }
            }
    }

    private void checkIfGoalIsLegal(Vector2 vector) {
        if(vector.x<Constants.WORLD_START_X)
            vector.x=Constants.WORLD_START_X;
        if(vector.x>Constants.WORLD_START_X+Constants.WORLD_WIDTH)
            vector.x=Constants.WORLD_START_X+Constants.WORLD_WIDTH;

        if(vector.y<Constants.WORLD_START_Y)
            vector.y=Constants.WORLD_START_Y;
        if(vector.y>Constants.WORLD_START_Y+Constants.WORLD_HEIGHT)
            vector.y=Constants.WORLD_START_Y+Constants.WORLD_HEIGHT;
    }

    private void adjustDistanceGoal(Vector2 targetposition) {
        /*Vector2 difference=targetposition.cpy().sub(position);
        float diflength=difference.len()-perfectrange;
        difference.scl(diflength);
        distancegoal.add(difference);*/

            Vector2 mindis=targetposition.cpy().sub(position);
            float diflength=perfectrange/mindis.len();
            mindis.nor();
            mindis.scl(perfectrange);
            mindis.scl(diflength);
            distancegoal.sub(mindis);

    }

    private void getNewGoal() {
        Random rdm=new Random();
        movegoal =new Vector2(Constants.WORLD_START_X+rdm.nextInt(Constants.WORLD_WIDTH),Constants.WORLD_START_Y+rdm.nextInt(Constants.WORLD_HEIGHT));
        checkIfGoalIsLegal(movegoal);
    }

    public void draw(SpriteBatch batch){
        sprite.setPosition(position.x-sprite.getWidth()/2,position.y-sprite.getHeight()/2);
        sprite.setColor(mycolor);
        sprite.draw(batch);

    }

    public void setColor(Color c){
        this.mycolor=c;
    }

    public void setMovegoal(Vector2 pos){
        if(isInBounds(pos)) {
            this.movegoal = pos;
        }
    }

    private boolean isInBounds(Vector2 pos){
        Rectangle rect=new Rectangle(Constants.WORLD_START_X,Constants.WORLD_START_Y,Constants.WORLD_WIDTH, Constants.WORLD_HEIGHT);
        return rect.contains(pos);
    }

    public void setMyquad(Quadtree myquad) {
        this.myquad = myquad;
    }

    public int getTeam() {
        return team;
    }

    public Circle getRangecircle(){
        return rangecircle;
    }

    public Vector2 getTotalvector(){
        return totalvector;
    }

    public Vector2 getMovegoal(){
        return movegoal;
    }

    public Vector2 getDistancegoal() {
        return distancegoal;
    }

    public Vector2 getSwarmvector() {
        return swarmvector;
    }
}

package de.zoneofcontrol.quadtree;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.*;

import java.util.*;

public class Quadtree {
    private ArrayList<Unit> units;
    private ArrayList<Quadtree> subquads;
    private Quadtree parentquad;
    Rectangle area;
    Color myColor;

    public Quadtree(Rectangle area) {
        units = new ArrayList<Unit>();
        this.units = units;
        this.area = area;
        Random rdm = new Random();
        myColor = new Color(rdm.nextFloat(), rdm.nextFloat(), rdm.nextFloat(), 1);
    }

    private Quadtree(Quadtree parent, Rectangle area) {
        this(area);
        this.parentquad = parent;
    }

    public Rectangle getArea() {
        return area;
    }

    public void addUnits(ArrayList<Unit> units) {
        for (Unit u : units) {
            addUnit(u);
        }
    }

    public void addUnit(Unit u) {
        if (contains(u)) {
            if (subquads == null) {
                units.add(u);
                u.setMyquad(this);
                contains(u);
                checkForDivision();
            } else {
                for (Quadtree q : subquads) {
                    if (q.contains(u)) {
                        q.addUnit(u);
                        break;
                    }
                }
            }
        } else {
            if (parentquad != null) {
                parentquad.addUnit(u);
            } else {
                System.out.println("ERROR UNIT QUAD NOT FOUND");
            }
        }
    }

    public void drawUnits(SpriteBatch batch) {
        if (subquads == null) {
            for (Unit u : units) {
                if(Constants.DEBUG_QUADS) {
                    //u.setColor(myColor);
                }
                u.draw(batch);
            }
        } else
            for (Quadtree q : subquads) {
                q.drawUnits(batch);
            }
    }

    private void transferUnitsUp(ArrayList<Unit> leaving) {
        if(leaving.size()>0) {
            units.removeAll(leaving);
            if(parentquad!=null)
                parentquad.addUnits(leaving);
        }
    }

    private void checkForDivision() {
        if (isCapacityFilled(units.size())) {
            createSubquads();
            divideUnits();
        }
    }

    private boolean isCapacityFilled(int size) {
        return size > Constants.MAX_UNITS_PER_QUADRANT;
    }

    private void divideUnits() {
        for (Unit u : units) {
            for (Quadtree q : subquads) {
                if (q.contains(u)) {
                    q.addUnit(u);
                    break;
                }
            }
        }
        units.clear();
    }

    private void createSubquads() {
        Rectangle r1 = new Rectangle(
                area.x,
                area.y,
                area.getWidth() / 2,
                area.getHeight() / 2);
        Rectangle r2 = new Rectangle(
                area.x + area.getWidth() / 2,
                area.y,
                area.getWidth() / 2,
                area.getHeight() / 2);
        Rectangle r3 = new Rectangle(
                area.x,
                area.y + area.getHeight() / 2,
                area.getWidth() / 2,
                area.getHeight() / 2);
        Rectangle r4 = new Rectangle(
                area.x + area.getWidth() / 2,
                area.y + area.getHeight() / 2,
                area.getWidth() / 2,
                area.getHeight() / 2);
        Quadtree q1 = new Quadtree(this, r1);
        Quadtree q2 = new Quadtree(this, r2);
        Quadtree q3 = new Quadtree(this, r3);
        Quadtree q4 = new Quadtree(this, r4);
        subquads = new ArrayList<Quadtree>();
        subquads.add(q1);
        subquads.add(q2);
        subquads.add(q3);
        subquads.add(q4);

    }

    public boolean contains(Unit u) {
        if (this.area.contains(u.getPosition())) {
            return true;
        } else
            return false;
    }

    public void update(float deltaTime) {
        if (subquads == null) {
            updateMyUnits(deltaTime);
        } else {

            for (Quadtree q : subquads) {
                q.update(deltaTime);
            }
            checkForCollapse();
        }
    }

    private boolean checkForCollapse() {
        int unittotal = getNumberOfUnitsInQuad();
        if (!isCapacityFilled(unittotal)) {
            ArrayList<Unit> newUnits = new ArrayList<Unit>();
            for (Quadtree q : subquads) {
                for (Unit u : q.getUnits()) {
                    newUnits.add(u);

                }
            }
            this.units = newUnits;
            subquads = null;
            return true;
        }
        return false;
    }

    private void updateMyUnits(float deltaTime) {
        for (Unit u : units) {
            u.update(deltaTime);
        }
        checkForLeavingUnits();
    }

    private void checkForLeavingUnits(){
        ArrayList<Unit> leaving = new ArrayList<Unit>();
        for (Unit u : units) {
            if (!contains(u)) {
                leaving.add(u);
            }
        }
        transferUnitsUp(leaving);
    }

    public void drawQuad(ShapeRenderer shaperender) {
       if(Constants.DEBUG_QUADS) {
           shaperender.setColor(myColor);
           shaperender.rect(area.x + 2, area.y + 2, area.getWidth() - 2, area.getHeight() - 2);
           shaperender.rect(area.x + 1, area.y + 1, area.getWidth() - 1, area.getHeight() - 1);
           if (subquads != null) {
               for (Quadtree q : subquads) {
                   q.drawQuad(shaperender);
               }
           }
           else{
               for (Unit u : units) {
                   u.setColor(myColor);
               }
           }

       }
       else{
           if(parentquad==null) {
               shaperender.setColor(Color.WHITE);
               shaperender.rect(area.x + 2, area.y + 2, area.getWidth() - 2, area.getHeight() - 2);
               shaperender.rect(area.x + 1, area.y + 1, area.getWidth() - 1, area.getHeight() - 1);
           }
           if (subquads != null) {
               for (Quadtree q : subquads) {
                   q.drawQuad(shaperender);
               }
           }
           else{
            if(Constants.SHOW_VECTORS)
               drawVectors(shaperender);
           }
       }


    }

    private void drawVectors(ShapeRenderer shaperender) {
        for (Unit u : units) {
            shaperender.setColor(Color.GREEN);
            Vector2 distanceline=u.getDistancegoal().cpy();
            distanceline.nor();
            distanceline.scl(25f*Constants.distancefactor);
            distanceline.add(u.getPosition());
            shaperender.line(u.getPosition(),distanceline);

            shaperender.setColor(Color.BLUE);
            Vector2 swarmvector=u.getSwarmvector().cpy();
            swarmvector.nor();
            swarmvector.scl(25f*Constants.vectorfactor);
            swarmvector.add(u.getPosition());
            shaperender.line(u.getPosition(),swarmvector);

            shaperender.setColor(Color.RED);
            Vector2 totalline=u.getTotalvector().cpy();
            totalline.nor();
            totalline.scl(25f);
            totalline.add(u.getPosition());
            shaperender.line(u.getPosition(),totalline);

            shaperender.setColor(Color.YELLOW);
            Vector2 wanderline=u.getWandervector();
            wanderline.nor();
            wanderline.scl(25f*Constants.wanderfactor);
            wanderline.add(u.getPosition());
            shaperender.line(u.getPosition(),wanderline);
        }
    }

    public ArrayList<Unit> getUnits() {
        return units;
    }

    public int getNumberOfUnitsInQuad() {
        int size = 0;
        if (subquads == null) {
            size = units.size();
        } else {
            for (Quadtree q : subquads) {
                size += q.getNumberOfUnitsInQuad();
            }
        }
        return size;
    }

    public void sendSignal(Vector2 pos) {
        if (subquads == null) {
            signalMyUnits(pos);
        } else {

            for (Quadtree q : subquads) {
                q.sendSignal(pos);
            }
            checkForCollapse();
        }
    }

    private void signalMyUnits(Vector2 pos) {
        for(Unit u: units){
            u.setMovegoal(pos);
        }
    }



    public ArrayList<Unit> getUnitsInCircle(Circle c,boolean isTopmostQuad){
        ArrayList<Unit> allunits=new ArrayList<Unit>();
        if(isFullCircleInArea(c)||!isTopmostQuad){
            if(this.subquads==null){
               // myColor=Color.PINK;
                allunits=this.units;
                        }
            else{
                for(Quadtree q: subquads){
                    if(doesCircleOverlapArea(q.getArea(),c)){
                   //     q.setColor(Color.YELLOW);
                        allunits.addAll(q.getUnitsInCircle(c,false));
                    }
                }
            }
        }
        else{
            if(parentquad!=null) {
               // myColor=Color.DARK_GRAY;
                return parentquad.getUnitsInCircle(c,true);
            }
            else {
              //  myColor=Color.BLUE;
                return units;
            }
        }
        return allunits;
    }

    private boolean doesCircleOverlapArea(Rectangle area, Circle c) {
        Rectangle r=new Rectangle(c.x-c.radius,c.y-c.radius,c.radius*2,c.radius*2);
        return area.overlaps(r);
    }

    private void setColor(Color color) {
        this.myColor=color;
    }

    private boolean isFullCircleInArea(Circle c){
        if(c.x+c.radius<area.x+area.width)
            if(c.x-c.radius>area.x)
                if(c.y+c.radius<area.y+area.height)
                    if(c.y-c.radius>area.y) {
                        return true;
                    }


     return false;
    }
}

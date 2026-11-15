import javax.swing.*;
import java.awt.Rectangle;
import java.util.Random;
import java.awt.Container;

public class node{

    private int settingId;
    private enemies[] eList;

    public void init(int settingId, int enemyCount, Rectangle area, Random rng) {
        this.settingId = settingId;
        eList = new enemies[enemyCount];
        for(int i = 0; i < eList.length; i++) {
            eList[i] = new enemies(area, rng); // random type at a random spot
        }
    }

    public void addAndShow(Container screen) {
        for(enemies e : eList) {
            screen.add(e.getLabel(), 1);
            e.getLabel().setVisible(true);
        }
    }

    public void update(int userX, int userY){
        for(int i = 0; i < eList.length; i++){
            eList[i].increment(userX, userY); 
        }
    }

    public enemies[] getEnemies(){ return eList; }

    public JLabel getLabelE(int i){
        return eList[i].getLabel();
    }
}
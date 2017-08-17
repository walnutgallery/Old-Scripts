import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.awt.event.*;
import javax.imageio.ImageIO;
import com.rarebot.event.events.MessageEvent;
import com.rarebot.event.listeners.MessageListener;
import com.rarebot.event.listeners.PaintListener;
import com.rarebot.script.Script;
import com.rarebot.script.ScriptManifest;
import com.rarebot.script.util.Filter;
import com.rarebot.script.methods.*;
import com.rarebot.script.wrappers.*;


public class MHunter extends Script implements PaintListener, MouseListener, MessageListener {

    String status, addNull1, addNull2, lastLog;
    final static int butterflyJar = 10012;
    final static int bankingItem = 995; //When hunting swamp lizards and you have this item, you will only powertrain - release them
    final static int buryingItem = 995; //When hunting birds and HAVE this item, you will DROP bones
    final static int[] dropIDs = {9978, 10115, 10125, 10127},
            buryIDs = {526},
            releaseIDs = {10149, 10146, 10147, 10148, 10092},
            mouseX = {577, 622, 663, 705, 577, 622, 663, 705, 577, 622, 663, 705, 577, 622, 663, 705, 577, 622, 663, 705, 577, 622, 663, 705, 577, 622, 663, 705},
            mouseY = {227, 227, 227, 227, 265, 265, 265, 265, 302, 302, 302, 302, 340, 340, 340, 340, 375, 375, 375, 375, 410, 410, 410, 410, 447, 447, 447, 447},
            trapIDs = {10006, 10008, 303, 954, 19965},
            layedTraps = {19187, 19175, 19678, 19650, 19662},
            failedTraps = {19192, 19174, 56813},
            layTrapHere = {19679, 19652, 19663},
            collapsedTraps = {19190, 19180, 19178, 19182, 19189, 19675, 19186, 19654, 19659, 19184, 56819, 19191, 56821},
            editY = {1, 1, 1, 0, 0, 0, -1, -1, -1},
            editX = {-1, 0, 1, -1, 0, 1, -1, 0, 1},
            catchedButterflies = {10014, 10016, 10018, 10020},
            falconTargets = {5098, 5099, 5100},
            falconSuccessful = {5094, 5095, 5096};
    final static int[][] pathes = {
        {0, 1, 2, 3, 7, 6, 5, 4, 8, 9, 10, 11, 15, 14, 13, 12, 16, 17, 18, 19, 23, 22, 21, 20, 24, 25, 26, 27},
        {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27},
        {0, 1, 2, 3, 7, 11, 15, 19, 23, 27, 26, 25, 24, 20, 16, 12, 8, 4, 5, 6, 10, 14, 18, 22, 21, 17, 13, 9},
        {0, 4, 8, 12, 16, 20, 24, 25, 26, 27, 23, 19, 15, 11, 7, 3, 2, 1, 5, 9, 13, 17, 21, 22, 18, 14, 10, 6},
        {0, 4, 8, 12, 16, 20, 24, 1, 5, 9, 13, 17, 21, 25, 2, 6, 10, 14, 18, 22, 26, 3, 7, 11, 15, 19, 23, 27},
        {3, 7, 11, 15, 19, 23, 27, 2, 6, 10, 14, 18, 22, 26, 1, 5, 9, 13, 17, 21, 25, 0, 4, 8, 12, 16, 20, 24},
        {3, 2, 1, 0, 4, 5, 6, 7, 11, 10, 9, 8, 12, 13, 14, 15, 19, 18, 17, 16, 20, 21, 22, 23, 27, 26, 25, 24},
        {0, 4, 8, 12, 16, 20, 24, 25, 21, 17, 13, 9, 5, 1, 2, 3, 7, 6, 10, 11, 15, 14, 18, 19, 23, 22, 26, 27},
        {0, 4, 8, 12, 16, 20, 24, 25, 26, 27, 23, 19, 15, 11, 7, 3, 2, 1, 5, 6, 10, 9, 13, 14, 18, 17, 21, 22}
    };
    RSTile startLoc, clickedFalcon;
    int antibans = 0, Mode = 2, startExp, startLvl, caught = 0, huntingType = 1, maxTraps, actTraps, layedCount = 0, layAtPos = 0, actArea, invCount, startingTraps = 0, nextRun = random(20, 40);
    long startedAt, lastScreen = 0;
    boolean hidden = false, turnOff = false, started = false, goingToBank = false, goingFromBank = false, doNotLay = false, catchingButterfly = true,
            onTheGround = false, haveFalcon = true, buryBones = true, huntEverywhere = true, jadinkos = false, draconic = false, isDraconic = false;
    Image Background, Background2, Show, turnOffImage, startImg;
    boolean[] huntTrap = {false, false, false, false, true, false, false, false, false};
    RSTile[] traps = {null, null, null, null, null},
            notMyFalcons = {null, null, null, null, null};
    int[] objAt = {0, 0, 0, 0, 0},
            trapsX = {0, 0, 0, 0, 0},
            trapsY = {0, 0, 0, 0, 0},
            butterflyIDs = {5082, 5084, 5085},
            butterflyIDs2 = {5082, 5083, 5084, 5085};
    int[] jujuPot = {20026, 20025, 20024, 20023};
    /* Defining areas */
    final int[] bankArea = {3507, 3475, 3513, 3483},
            swampLizardArea = {3533, 3432, 3581, 3454},
            orangeSalamanderArea = {3398, 3072, 3422, 3151},
            redSalamanderArea = {2440, 3210, 2482, 3261},
            falconryArea = {2364, 3573, 2392, 3619};
    final int[][] swampLizard = {
        {3546, 3437, 3555, 3443},
        {3547, 3448, 3558, 3454},
        {3535, 3450, 3539, 3452}
    },
            orangeSalamander = {
        {3410, 3072, 3417, 3079},
        {3408, 3080, 3413, 3083},
        {3402, 3086, 3412, 3094},
        {3400, 3099, 3406, 3102},
        {3401, 3131, 3410, 3136}
    },
            redSalamander = {
        {2466, 3234, 2480, 3246},
        {2445, 3220, 2452, 3228},
        {2450, 3216, 2456, 3222}
    },
            butterflyAreas = {
        {2537, 2898, 2570, 2917},
        {2306, 3506, 2361, 3621},
        {2690, 3758, 2747, 3802},
        {2702, 3803, 2740, 3838}
    },
            butterflyStartingLocations = {
        {2558, 2910},
        {2322, 3600},
        {2714, 3792},
        {2711, 3822}
    };
    int[][] areas;

    public boolean onStart() {
        startedAt = System.currentTimeMillis();
        startExp = skills.getCurrentExp(skills.HUNTER);
        startLvl = skills.getRealLevel(skills.HUNTER);
        startLoc = getMyPlayer().getLocation();
        maxTraps = getMaxTraps();
        actTraps = getActTraps();
        invCount = inventory.getCount(false);
        if (areaContains(falconryArea, getMyPlayer().getLocation())) {
            huntingType = 5;
            log("You are in the falconry area - I hope you have your falcon!");
        }
        if (inventory.getCount(19965) > 0) {
            jadinkos = true;
            log("You have Marasamaw trap in inventory!");
            log("If you want to hunt jadinkos, check Box trapping.");
            if (inventory.getCount(jujuPot) > 0) {
                final int[] draconicJadinkos = {13121, 13124, 13128, 13129};
                if (npcs.getNearest(draconicJadinkos) != null) {
                    draconic = true;
                    isDraconic = true;
                }
            }
        }
        Arrays.sort(layedTraps);
        Arrays.sort(failedTraps);
        Arrays.sort(collapsedTraps);
        Arrays.sort(dropIDs);
        Arrays.sort(buryIDs);
        Arrays.sort(releaseIDs);
        Arrays.sort(layTrapHere);
        Arrays.sort(butterflyIDs);
        Arrays.sort(catchedButterflies);
        return true;
    }

    private void lg(String str) {
        if (lastLog != str) {
            lastLog = str;
            log(str);
        }
    }

    private int getMaxTraps() {
        int lvl = skills.getRealLevel(skills.HUNTER);
        int max = 1;
        if (lvl >= 80) {
            max = 5;
        } else if (lvl >= 60) {
            max = 4;
        } else if (lvl >= 40) {
            max = 3;
        } else if (lvl >= 20) {
            max = 2;
        }
        return max;
    }

    private int getActTraps() {
        int act = 0;
        for (int i = 0; i <= 8; i++) {
            if (huntTrap[i]) {
                traps[act] = new RSTile(startLoc.getX() + editX[i], startLoc.getY() + editY[i]);
                act++;
            }
        }
        if (act < 5) {
            for (int i = act; i < 5; i++) {
                traps[i] = traps[0];
            }
        }
        return act;
    }

    public void antiban() {
        boolean antibanUsed = false;
        int j = random(1, 100);
        if (j == 1) {
            mouse.moveRandomly(random(50, 500));
            sleep(random(200, 2000));
            status = "Antiban - mouse move";
            antibanUsed = true;
        } else if (j == 2 || j == 3) {
            status = "Antiban - mouse off screen";
            mouse.moveOffScreen();
            sleep(random(1000, 3000));
            antibanUsed = true;
        } else if (j == 4) {
            status = "Antiban - mouse move";
            mouse.moveSlightly();
            sleep(random(200, 2000));
            antibanUsed = true;
        } else if (j == 5) {
            status = "Antiban - checking exp";
            skills.doHover(skills.INTERFACE_HUNTER);
            sleep(1000, 2000);
            antibanUsed = true;
        } else if (j > 5 && j < 9) {
            camera.setAngle(random(j * 5, 68));
            sleep(random(200, 2000));
            status = "Antiban - camera angle";
            antibanUsed = true;
        }
        if (antibanUsed) {
            antibans++;
        }
    }

    public boolean bankWithdraw(int itemID, int itemCount, int withdrawCount, String Status, int timeout) {
        while (inventory.getCount(true, itemID) < itemCount && bank.isOpen() && !inventory.isFull()) {
            status = Status;
            if (bank.getCount(itemID) > 0) {
                bank.withdraw(itemID, withdrawCount);
                long t = System.currentTimeMillis();
                while (((inventory.getCount(true, itemID) < itemCount && itemCount < 12345) || (itemCount == 12345 && !inventory.isFull()))
                        && System.currentTimeMillis() - t < timeout && !inventory.isFull()) {
                    sleep(random(90, 110));
                }
            }
        }
        if (inventory.getCount(itemID) < itemCount) {
            return false;
        }
        return true;
    }

    private void wTT(RSTile tile) {
        final RSWeb walkWeb = web.getWeb(getMyPlayer().getLocation(), tile);
        if (walkWeb != null && !walkWeb.finished()) {
            if (!getMyPlayer().isMoving() || calc.distanceTo(walking.getDestination()) < 4) {
                try {
                    walkWeb.step();
                } catch (Exception ignored) {
                }
            }
        }
    }

    public boolean areaContains(int[] area, RSTile tile) {
        final int X = tile.getX();
        final int Y = tile.getY();
        return X >= area[0] && X <= area[2] && Y >= area[1] && Y <= area[3];
    }

    private int getObjectAtTile(RSTile tile) {
        RSObject[] objs = objects.getAllAt(tile);
        if (objs.length > 0) {
            RSObject obj = objs[0];
            if (obj != null) {
                return obj.getID();
            }
        }
        return 0;
    }

    private int getObjects() {
        layedCount = 0;
        for (int i = 0; i <= 4; i++) {
            RSTile tile = traps[i];
            if (tile != null) {
                objAt[i] = getObjectAtTile(tile);
                if (objAt[i] > 0) {
                    layedCount++;
                }
            }
        }
        return layedCount;
    }

    private int getActArea() {
        if (huntingType == 3) {
            RSTile myLoc = getMyPlayer().getLocation();
            if (areaContains(swampLizardArea, myLoc)) {
                areas = swampLizard;
            } else if (areaContains(orangeSalamanderArea, myLoc)) {
                areas = orangeSalamander;
            } else if (areaContains(redSalamanderArea, myLoc)) {
                areas = redSalamander;
            }
            for (int i = 0; i <= areas.length; i++) {
                if (areaContains(areas[i], myLoc)) {
                    actArea = i;
                    return i;
                }
            }
        } else if (huntingType == 4) {
            areas = butterflyAreas;
            RSTile myLoc = getMyPlayer().getLocation();
            for (int i = 0; i <= areas.length; i++) {
                if (areaContains(areas[i], myLoc)) {
                    actArea = i;
                    startLoc = new RSTile(butterflyStartingLocations[i][0], butterflyStartingLocations[i][1]);
                    return i;
                }
            }
            log("I can not find myself in any butterfly area, please move to other place");
        }
        return -1;
    }

    private int containsObj(int[] objArray) {
        for (int i = 0; i <= 4; i++) {
            for (int j = 0; j < objArray.length; j++) {
                if (objAt[i] == objArray[j]) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void objInteract(RSObject obj, String action, String changeStatus) {
        status = changeStatus;
        if (obj != null) {
            if (obj.isOnScreen()) {
                if (obj.interact(action) && waitTillChangedID(obj.getLocation(), obj.getID())) {
                    //if () {
                    if (huntingType == 3) {
                        if (action == "Set-trap Young tree") {
                            addLayedTrap(obj.getLocation());
                        } else if (action == "Check Net trap") {
                            removeLayedTrap(obj.getLocation());
                        }
                    }
                    //}
                }
            } else {
                camera.turnTo(obj.getLocation());
                camera.setPitch(30);
                if (obj.isOnScreen()) {
                    if (obj.interact(action) && waitTillChangedID(obj.getLocation(), obj.getID())) {
                        //if () {
                        if (huntingType == 3) {
                            if (action == "Set-trap Young tree") {
                                addLayedTrap(obj.getLocation());
                            } else if (action == "Check Net trap") {
                                removeLayedTrap(obj.getLocation());
                            }
                            if (random(1, 5) == 1) {
                                camera.setPitch(random(2, 6) * 10);
                            }
                        }
                        //}
                    }
                } else {
                    walking.walkTileMM(obj.getLocation());
                    sleep(random(750, 1250));
                }
            }
        }
    }

    private void moveItem(int from, int to) {
        status = "Moving item in inventory";
        mouse.move(mouseX[from] + random(0, 14) - 7, mouseY[from] + random(0, 14) - 7);
        sleep(random(100, 200));
        mouse.drag(mouseX[to] + random(0, 14) - 7, mouseY[to] + random(0, 14) - 7);
    }

    private void mouseToItem(int invID) {
        if (invID > -1) {
            mouse.move(mouseX[invID] + random(0, 14) - 7, mouseY[invID] + random(0, 14) - 7);
        }
        return;
    }

    private void useJujuHunterPotion(RSObject flower) {
        if (flower != null) {
            if (inventory.getCount(jujuPot) > 0) {
                int Index = getIndex(jujuPot);
                int generatedX = mouseX[Index] + random(0, 14) - 7;
                int generatedY = mouseY[Index] + random(0, 14) - 7;
                int generatedX2 = generatedX - 25 + random(0, 50);
                int generatedY2 = generatedY + random(40, 50);
                mouse.move(generatedX, generatedY);
                sleep(random(250, 500));
                if (menu.contains("Juju")) {
                    mouse.click(false);
                    sleep(random(250, 500));
                    mouse.move(generatedX2, generatedY2);
                    sleep(random(250, 500));
                    mouse.click(true);
                    sleep(random(250, 500));
                    for (int i = 0; i < 10; i++) {
                        mouse.move(flower.getModel().getPoint());
                        sleep(random(250, 500));
                        if (menu.contains("Red vine") && menu.contains("Juju")) {
                            sleep(random(400, 600));
                            if (menu.contains("Red vine") && menu.contains("Juju")) {
                                break;
                            }
                        }
                    }
                    if (menu.contains("Red vine") && menu.contains("Juju")) {
                        mouse.click(true);
                        sleep(random(3500, 4000));
                        isDraconic = true;
                    } else {
                        mouse.move(535, random(237, 445));
                        sleep(random(250, 500));
                        mouse.click(true);
                    }
                }
            }
        }
    }

    public int getIndex(int itemID) {
        RSItem[] items = inventory.getItems();
        for (int i = 0; i <= 27; i++) {
            if (i + 1 <= items.length) {
                RSItem item = items[i];
                if (item != null) {
                    if (item.getID() == itemID) {
                        return i;
                    }
                }
            }
        }
        return -1;
    }

    public int getIndex(int[] itemID) {
        RSItem[] items = inventory.getItems();
        for (int i = 0; i <= 27; i++) {
            if (i + 1 <= items.length) {
                RSItem item = items[i];
                if (item != null) {
                    for (int j = 0; j <= itemID.length - 1; j++) {
                        if (item.getID() == itemID[j]) {
                            return i;
                        }
                    }
                }
            }
        }
        return -1;
    }

    private RSObject objInArea(int[] IDs) {
        final int[] objIDs = IDs;
        return objects.getNearest(new Filter<RSObject>() {

            public boolean accept(RSObject o) {
                int iId = o.getID();
                RSTile loc = o.getLocation();
                if (Arrays.binarySearch(objIDs, iId) > -1) {
                    if (huntingType == 3) {
                        if (objIDs != collapsedTraps) {
                            return areaContains(areas[actArea], loc);
                        } else {
                            if (areaContains(areas[actArea], loc)) {
                                if (isTrapLayed(o.getLocation())) {
                                    return true;
                                } else {
                                    return false;
                                }
                            }
                        }
                    } else if (huntingType < 3) {
                        boolean goodPos = false;
                        for (int i = 0; i < 5; i++) {
                            if (loc.getX() == traps[i].getX() && loc.getY() == traps[i].getY()) {
                                goodPos = true;
                            }
                        }
                        return goodPos;
                    }
                }
                return false;
            }
        });
    }

    private RSNPC npcInArea(int[] IDs) {
        final int[] objIDs = IDs;
        return npcs.getNearest(new Filter<RSNPC>() {

            public boolean accept(RSNPC o) {
                int iId = o.getID();
                RSTile loc = o.getLocation();
                if (Arrays.binarySearch(objIDs, iId) > -1) {
                    if (huntingType == 4) {
                        if (areaContains(areas[actArea], loc)) {
                            return true;
                        } else if (calc.distanceTo(loc) < 7) {
                            return true;
                        }
                    }
                }
                return false;
            }
        });
    }

    private void deleteNotMyFalcons() {
        for (int i = 0; i < 5; i++) {
            if (notMyFalcons[i] == null) {
                break;
            }
            notMyFalcons[i] = null;
        }
        return;
    }

    private void addNotMyFalcons(RSTile addTile) {
        for (int i = 0; i < 5; i++) {
            if (notMyFalcons[i] == null) {
                notMyFalcons[i] = addTile;
                break;
            }
        }
        return;
    }

    private RSNPC findFalcon(boolean alv) {
        int[] IDs;
        final boolean alive = alv;
        if (alive) {
            IDs = falconTargets;
        } else {
            IDs = falconSuccessful;
        }
        final int[] objIDs = IDs;
        return npcs.getNearest(new Filter<RSNPC>() {

            public boolean accept(RSNPC o) {
                int iId = o.getID();
                RSTile loc = o.getLocation();
                if (Arrays.binarySearch(objIDs, iId) > -1) {
                    if (alive) {
                        if (calc.distanceBetween(startLoc, loc) < 10 && !huntEverywhere) {
                            return true;
                        }
                    } else {
                        for (int i = 0; i < 5; i++) {
                            if (notMyFalcons[i] == null) {
                                break;
                            }
                            RSTile nTile = notMyFalcons[i];
                            if (nTile.getX() == loc.getX() && nTile.getY() == loc.getY()) {
                                return false;
                            }
                        }
                        return true;
                    }
                }
                return false;
            }
        });
    }

    private int getFreeTile() {
        RSObject[] objs;
        getActTraps();
        getObjects();
        int nearest = 9999;
        int currentNearest = -1;
        int dist = 0;
        for (int i = 0; i < 5; i++) {
            objs = objects.getAllAt(traps[i]);
            if (objs.length == 0) {
                dist = calc.distanceTo(traps[i]);
                if (dist < nearest) {
                    nearest = dist;
                    currentNearest = i;
                }
            }
        }
        return currentNearest;
    }

    private void addLayedTrap(RSTile tile) {
        boolean isLayed = false;
        int firstUnlayed = -1;
        for (int i = 0; i <= 4; i++) {
            if (firstUnlayed == -1 && trapsX[i] == 0) {
                firstUnlayed = i;
            }
            if (trapsX[i] == tile.getX() && trapsY[i] == tile.getY()) {
                isLayed = true;
            }
        }
        if (!isLayed) {
            trapsX[firstUnlayed] = tile.getX();
            trapsY[firstUnlayed] = tile.getY();
            //log("Trap added to list");
            //log(trapsX[0] + ", " + trapsY[0] + "; " + trapsX[1] + ", " + trapsY[1] + "; " + trapsX[2] + ", " + trapsY[2] + "; " + trapsX[3] + ", " + trapsY[3] + "; " + trapsX[4] + ", " + trapsY[4] + "; ");
        }
    }

    private void removeLayedTrap(RSTile tile) {
        for (int i = 0; i <= 4; i++) {
            if (Math.abs(trapsX[i] - tile.getX()) <= 1 && Math.abs(trapsY[i] - tile.getY()) <= 1) {
                trapsX[i] = 0;
                trapsY[i] = 0;
                //log("Trap removed from list");
            }
        }
    }

    private void areYourTrapsLayed() {
        if (calc.distanceTo(startLoc) < 10) {
            for (int i = 0; i < 5; i++) {
                if (trapsX[i] > 0 && trapsY[i] > 0) {
                    RSObject[] objs = objects.getAllAt(new RSTile(trapsX[i], trapsY[i]));
                    if (objs.length > 0) {
                        RSObject obj = objs[0];
                        int iId = obj.getID();
                        if (Arrays.binarySearch(layTrapHere, iId) > -1) {
                            trapsX[i] = 0;
                            trapsY[i] = 0;
                            //log("Trap has been removed");
                        }
                    }
                }
            }
        }
    }

    private boolean isTrapLayed(RSTile tile) {
        boolean isLayed = false;
        //log(trapsX[0] + ", " + trapsY[0]+"; "+trapsX[1] + ", " + trapsY[1]+"; "+trapsX[2] + ", " + trapsY[2]+"; "+trapsX[3] + ", " + trapsY[3]+"; "+trapsX[4] + ", " + trapsY[4]+"; ===> "+tile.getX() + ", " + tile.getY());
        for (int i = 0; i <= 4; i++) {
            if (Math.abs(trapsX[i] - tile.getX()) <= 1 && Math.abs(trapsY[i] - tile.getY()) <= 1) {
                isLayed = true;
            }
        }

        return isLayed;
    }

    public boolean waitTillChangedID(RSTile tile, int objID) {
        int anim = getMyPlayer().getAnimation();
        RSObject[] objs = objects.getAllAt(tile);
        RSObject obj = objs[0];
        long t = System.currentTimeMillis();
        while (obj != null && System.currentTimeMillis() - t <= 5000 && anim == -1) {
            if (obj.getID() != objID) {
                for (int i = 0; i <= 200; i++) {
                    sleep(5);
                    anim = getMyPlayer().getAnimation();
                    if (anim > -1) {
                        break;
                    }
                }
                break;
            } else {
                anim = getMyPlayer().getAnimation();
                if (anim > -1) {
                    break;
                }
                sleep(5);
                objs = objects.getAllAt(tile);
                obj = objs[0];
            }
        }
        //log("Changed ID: " + (anim > -1));
        return anim > -1;
    }

    public boolean interactIt(RSTile tile, int objID, RSObject obj, String action, String changeStatus) {
        if (obj != null) {
            status = changeStatus;
            int anim = getMyPlayer().getAnimation();
            RSObject[] objs = objects.getAllAt(tile);
            obj = objs[0];
            long t = System.currentTimeMillis();
            boolean clicked = false;
            while (System.currentTimeMillis() - t <= 7500 && anim == -1) {
                if (System.currentTimeMillis() - t > 350 && !clicked) {
                    if (obj.isOnScreen()) {
                        if (obj.interact(action)) {
                            clicked = true;
                        }
                    } else {
                        camera.turnTo(obj.getLocation());
                        camera.setPitch(30);
                        if (obj.isOnScreen()) {
                            if (obj.interact(action)) {
                                clicked = true;
                            }
                        } else {
                            walking.walkTileMM(obj.getLocation());
                            sleep(random(750, 1250));
                        }
                    }
                    //objInteract(obj, action, changeStatus);
                    //clicked = true
                }
                /*if (obj.getID() != objID) {
                for (int i = 0; i <= 200; i++) {
                sleep(5);
                anim = getMyPlayer().getAnimation();
                if (anim > -1) {
                break;
                }
                }
                break;
                } else {*/
                for (int i = 0; i <= 20; i++) {
                    sleep(5);
                    anim = getMyPlayer().getAnimation();
                    if (anim > -1) {
                        break;
                    }
                }
                objs = objects.getAllAt(tile);
                if (objs.length > 0) {
                    obj = objs[0];
                    if (obj.getID() != objID) {
                        for (int i = 0; i <= 200; i++) {
                            sleep(5);
                            anim = getMyPlayer().getAnimation();
                            if (anim > -1) {
                                break;
                            }
                        }

                    }
                }
                //}
            }
            //log("Changed ID: " + (anim > -1));
            return anim > -1;
        }
        return false;
    }

    public boolean anyLayedTrap() {
        for (int i = 0; i < 5; i++) {
            if (trapsX[i] > 0) {
                //log("checking layed traps");
                return true;
            }
        }
        return false;
    }

    private void releaseUntil(int until) {
        status = "Releasing";
        if (huntingType == 4) {
            while (inventory.getCount(catchedButterflies) > 0) {
                RSItem catched = inventory.getItem(catchedButterflies);
                if (catched != null) {
                    if (catched.interact("Release ")) {
                        sleep(random(950, 1350));
                    }
                }
            }
            return;
        }
        while (28 - inventory.getCount(false) < until && inventory.getCount(releaseIDs) > 0) {
            RSItem release = inventory.getItem(releaseIDs);
            if (release != null) {
                if (release.interact("Release")) {
                    sleep(random(500, 1000));
                }
            }
        }
    }

    public int whatToDo() {
        if (interfaces.canContinue()) {
            interfaces.clickContinue();
            sleep(random(250, 500));
            return 0;
        }
        if (!walking.isRunEnabled() && walking.getEnergy() > nextRun) {
            walking.setRun(true);
            nextRun = random(20, 40);
            sleep(random(1000, 1400));
            return 0;
        }
        if (!turnOff) {
            if (huntingType < 3) {
                if (draconic && (!isDraconic || getMyPlayer().getLocation().getY() < 2916)) {
                    return 10;
                }
                if (calc.distanceTo(startLoc) > 4) {
                    return 7;
                }
                getActTraps();
                int getObjects = getObjects();
                if (getMyPlayer().getAnimation() == -1) {
                    RSGroundItem tr = groundItems.getNearest(trapIDs);
                    if (inventory.getCount(buryIDs) > 0) {
                        return 2;
                    } else if (inventory.getCount(dropIDs) > 0) {
                        return 3;
                    } else if (28 - inventory.getCount(false) <= 1 && inventory.getCount(releaseIDs) > 0) {
                        return 9;
                    }
                    if (tr != null) {
                        if (calc.distanceTo(tr.getLocation()) < 5) {
                            return 6;
                        }
                    }
                    layAtPos = getFreeTile();
                    if (layAtPos > -1 && inventory.getCount(trapIDs) > 0) {
                        /*for (int i = 0; i < getMaxTraps(); i++) {
                        if (objAt[i] == 0 && traps[i] != null) {
                        layAtPos = i;
                        return 8;
                        }
                        }*/
                        return 8;
                    }
                    if (getObjects > 0) {
                        /*
                        if (containsObj(collapsedTraps) > -1) {
                        return 4;
                        } else if (containsObj(failedTraps) > -1) {
                        return 5;
                        }*/
                        if (objInArea(collapsedTraps) != null) {
                            return 4;
                        } else if (objInArea(failedTraps) != null) {
                            return 5;
                        }
                    }
                } else {
                    return 0;
                }
            } else if (huntingType == 3) {
                areYourTrapsLayed();
                if (getMyPlayer().getAnimation() == -1) {
                    if (!inventory.isFull()) {
                        if (goingToBank) {
                            goingToBank = false;
                            goingFromBank = true;
                        }
                        if (goingFromBank) {
                            if (calc.distanceTo(startLoc) > 4) {
                                return 8;
                            } else {
                                goingFromBank = false;
                            }
                        }
                        RSGroundItem tr = groundItems.getNearest(trapIDs);
                        if (tr != null) {
                            if (areaContains(areas[actArea], tr.getLocation())) {
                                releaseUntil(1);
                                return 4;
                            }
                        }
                        if (inventory.getCount(trapIDs) > 1) {
                            if (!doNotLay) {
                                if (objInArea(layTrapHere) != null) {
                                    return 2;
                                }
                            } else {
                                return 9;
                            }
                        }
                        if (objInArea(collapsedTraps) != null) {
                            if (28 - inventory.getCount(false) < 3) {
                                releaseUntil(3);
                            }
                            return 3;
                        }
                        if ((inventory.contains(bankingItem) && areas == swampLizard) || areas != swampLizard) {
                            if (inventory.getCount(releaseIDs) > 0) {
                                return 1;
                            }
                        }
                        if (doNotLay) {
                            if (objInArea(layedTraps) != null) {
                                if (28 - inventory.getCount(false) < 2) {
                                    releaseUntil(2);
                                }
                                return 9;
                            }
                        }
                    } else {
                        if (inventory.contains(bankingItem) || areas != swampLizard) {
                            if (inventory.getCount(releaseIDs) > 0) {
                                return 1;
                            } else {
                                log("Your inventory does not contain items to drop/bury/release, and your inventory is full!");
                                stopScript();
                            }
                        } else {
                            if (areaContains(bankArea, getMyPlayer().getLocation())) {
                                return 7;
                            }
                            doNotLay = true;
                            if (!anyLayedTrap()) {
                                return 6;
                            } else {
                                return 9;
                            }
                        }
                    }
                    return 5;
                } else {
                    return 0;
                }
            } else if (huntingType == 4) {
                if (skills.getRealLevel(skills.HUNTER) >= 35) {
                    butterflyIDs = butterflyIDs2;
                }
                if (getMyPlayer().getAnimation() == -1) {
                    if (!getMyPlayer().isMoving()) {
                        RSGroundItem tr = groundItems.getNearest(butterflyJar);
                        if (tr != null) {
                            if (areaContains(areas[actArea], tr.getLocation())) {
                                return 5;
                            }
                        } else if (inventory.contains(butterflyJar)) {
                            return 2;
                        } else if (inventory.getCount(catchedButterflies) == 0 && !inventory.contains(butterflyJar)) {
                            return 2;
                        } else if (inventory.getCount(catchedButterflies) > 0) {
                            return 3;
                        } else if (!areaContains(areas[actArea], getMyPlayer().getLocation())) {
                            return 4;
                        }
                    } else {
                        if (!catchingButterfly) {
                            if ((inventory.contains(butterflyJar)) || (inventory.getCount(catchedButterflies) == 0 && !inventory.contains(butterflyJar))) {
                                return 2;
                            }
                        }
                        if (random(1, 250) == 1) {
                            return 1;
                        }
                        return 0;
                    }
                }
            } else if (huntingType == 5) {
                if (walking.getDestination() == null) {
                    if (haveFalcon) {
                        if (!onTheGround) {
                            if (28 - inventory.getCount(false) < 4) {
                                return 2; //burying bones and dropping furs
                            } else if (getMyPlayer().getAnimation() == -1) {
                                if (random(1, 30) == 1) {
                                    return 1; // antiban
                                } else {
                                    return 3; //catching kebbit
                                }
                            }
                        } else {
                            return 4; //taking from the ground
                        }
                    } else {
                        return 5; // retrieveing falcon back from Matthias
                    }
                }
            }
        }
        return 1;
    }

    public int loop() {
        int sleep = 50;
        try {
            int objPos;
            if (!turnOff) {
                mouse.setSpeed(random(4, 6));
                long t;
                if (started) {
                    if (huntingType == 1 || huntingType == 2) {
                        switch (whatToDo()) {
                            case 1:
                                if (random(1, 3) == 1) {
                                    antiban();
                                }
                                sleep = (random(750, 1250));
                                break;
                            case 2:
                                status = "Burying bones";
                                RSItem bury = inventory.getItem(buryIDs);
                                if (bury != null) {
                                    if (buryBones) {
                                        bury.interact("Bury");
                                    } else {
                                        bury.interact("Drop");
                                    }
                                    sleep = (random(750, 1250));
                                }
                                break;
                            case 3:
                                status = "Dropping items";
                                RSItem drop = inventory.getItem(dropIDs);
                                if (drop != null) {
                                    drop.interact("Drop");
                                    sleep = (random(750, 1250));
                                }
                                break;
                            case 4:
                                status = "Checking trap";
                                RSObject obj = objInArea(collapsedTraps);
                                if (obj != null) {
                                    if (obj.interact("Check")) {
                                        sleep = (random(1000, 2000));
                                    }
                                }
                                break;
                            case 5:
                                status = "Dismantling failed trap";
                                obj = objInArea(failedTraps);
                                if (obj != null) {
                                    String action = "Dismantle";
                                    if (jadinkos) {
                                        action = "Pick";
                                    }
                                    if (obj.interact(action)) {
                                        sleep = (random(1000, 2000));
                                    }
                                }
                                break;
                            case 6:
                                status = "Taking trap from the ground";
                                RSGroundItem tr = groundItems.getNearest(trapIDs);
                                if (tr != null) {
                                    if (calc.distanceTo(tr.getLocation()) < 5) {
                                        boolean goodPos = false;
                                        for (int i = 0; i <= 4; i++) {
                                            if (traps[i].getX() == tr.getLocation().getX() && traps[i].getY() == tr.getLocation().getY()) {
                                                goodPos = true;
                                            }
                                        }
                                        if (goodPos) {
                                            RSObject[] objs = objects.getAllAt(tr.getLocation());
                                            if (objs.length > 0) {
                                                goodPos = false;
                                            }
                                        }
                                        if (goodPos) {
                                            tr.interact("Lay");
                                            sleep = (random(500, 1000));
                                        } else {
                                            tr.interact("Take");
                                            sleep = (random(500, 1000));
                                        }
                                    }
                                }
                                break;
                            case 7:
                                status = "Walking to start location";
                                walking.walkTileMM(startLoc);
                                sleep = (random(1000, 2000));
                                break;
                            case 8:
                                status = "Laying trap";
                                if (traps[layAtPos] != null && objAt[layAtPos] == 0) {
                                    RSTile myPos = getMyPlayer().getLocation();
                                    t = System.currentTimeMillis();
                                    if (traps[layAtPos].getX() == myPos.getX() && traps[layAtPos].getY() == myPos.getY()) {
                                        if (getMyPlayer().getAnimation() == -1) {
                                            RSItem trap = null;
                                            if (huntingType == 1) {
                                                if (!jadinkos) {
                                                    trap = inventory.getItem(10008);
                                                } else {
                                                    trap = inventory.getItem(19965);
                                                }
                                            } else if (huntingType == 2) {
                                                trap = inventory.getItem(10006);
                                            }
                                            if (trap != null) {
                                                trap.interact("Lay");
                                                sleep = (random(1000, 2000));
                                            }
                                        }
                                    } else if (calc.distanceTo(traps[layAtPos]) < 4) {
                                        walking.walkTileOnScreen(traps[layAtPos]);
                                        while (calc.distanceTo(traps[layAtPos]) > 0 && System.currentTimeMillis() - t <= 2000) {
                                            sleep(random(100, 200));
                                        }
                                        sleep = 250;
                                    } else {
                                        walking.walkTileMM(traps[layAtPos]);
                                        while (calc.distanceTo(traps[layAtPos]) > 0 && System.currentTimeMillis() - t <= 2000) {
                                            sleep(random(100, 200));
                                        }
                                        sleep = 250;
                                    }
                                }
                                break;
                            case 9:
                                releaseUntil(random(7, 10));
                                break;
                            case 10:
                                if (draconic) {
                                    status = "Using Juju hunter potion on a vine";
                                    if (getMyPlayer().getAnimation() == -1) {
                                        final int Y = getMyPlayer().getLocation().getY();
                                        RSObject vine = null;
                                        RSObject flower = null;
                                        RSObject[] vineS = objects.getAllAt(new RSTile(2950, 2916));
                                        RSObject[] flowerS = objects.getAllAt(new RSTile(2958, 2912));
                                        if (vineS.length > 0) {
                                            vine = vineS[0];
                                        }
                                        if (flowerS.length > 0) {
                                            flower = flowerS[0];
                                        }
                                        if (vine != null) {
                                            if (Y > 2916) {
                                                if (!isDraconic) {
                                                    if (vine.isOnScreen() && calc.distanceTo(vine.getLocation()) < 4) {
                                                        if (vine.interact("Climb Climbable")) {
                                                            sleep(random(2000, 2500));
                                                        }
                                                    } else {
                                                        walking.walkTileMM(walking.getClosestTileOnMap(new RSTile(2950, 2917)));
                                                        sleep(random(1500, 2000));
                                                    }
                                                } else {
                                                    walking.walkTileMM(walking.getClosestTileOnMap(startLoc));
                                                    sleep(random(1500, 2000));
                                                }
                                            } else {
                                                if (flower != null) {
                                                    if (!isDraconic) {
                                                        if (flower.isOnScreen() && calc.distanceTo(flower.getLocation()) < 4) {
                                                            useJujuHunterPotion(flower);
                                                            sleep(random(1000, 1500));
                                                        } else {
                                                            walking.walkTileMM(walking.getClosestTileOnMap(new RSTile(2957, 2910)));
                                                            sleep(random(1500, 2000));
                                                        }
                                                    } else {
                                                        if (vine.isOnScreen() && calc.distanceTo(vine.getLocation()) < 4) {
                                                            if (vine.interact("Climb Climbable")) {
                                                                sleep(random(2000, 2500));
                                                            }
                                                        } else {
                                                            walking.walkTileMM(walking.getClosestTileOnMap(new RSTile(2950, 2915)));
                                                            sleep(random(1500, 2000));
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    } else {
                                        sleep(random(250, 500));
                                    }
                                }
                                break;
                        }
                    } else if (huntingType == 3) {
                        RSObject obj = null;
                        switch (whatToDo()) {
                            case 1:
                                status = "Releasing lizard";
                                RSItem release = inventory.getItem(releaseIDs);
                                if (release != null) {
                                    release.interact("Release");
                                    sleep = (random(750, 1250));
                                }
                                break;
                            case 2:
                                obj = objInArea(layTrapHere);
                                if (interactIt(obj.getLocation(), obj.getID(), obj, "Set-trap Young tree", "Laying trap")) {
                                    addLayedTrap(obj.getLocation());
                                }
                                break;
                            case 3:
                                releaseUntil(3);
                                obj = objInArea(collapsedTraps);
                                if (interactIt(obj.getLocation(), obj.getID(), obj, "Check Net trap", "Checking trap")) {
                                    removeLayedTrap(obj.getLocation());
                                }
                                break;
                            case 4:
                                status = "Taking trap from the ground";
                                RSGroundItem tr = groundItems.getNearest(trapIDs);
                                if (tr != null) {
                                    if (areaContains(areas[actArea], tr.getLocation())) {
                                        if (!tr.isOnScreen()) {
                                            camera.turnTo(tr.getLocation());
                                            camera.setPitch(30);
                                        }
                                        tr.interact("Take " + tr.getItem().getName());
                                        sleep = (random(500, 1000));
                                    }
                                }
                                break;
                            case 5:
                                if (random(1, 3) == 1) {
                                    antiban();
                                }
                                sleep = (random(750, 1250));
                                break;
                            case 6:
                                status = "Going to bank";
                                if (goingToBank == false) {
                                    goingToBank = true;
                                }
                                if (getMyPlayer().getLocation().getY() < 3446) {
                                    walking.walkTileMM(new RSTile(3547, 3450));
                                    sleep(random(15000, 20000));
                                    break;
                                }
                                wTT(new RSTile(3511, 3480));
                                break;
                            case 7:
                                status = "Banking";
                                if (!bank.isOpen()) {
                                    objInteract(objects.getNearest(24914), "Bank Bank booth", "Banking");
                                    sleep(random(500, 1000));
                                } else {
                                    bank.deposit(10149, 0);
                                    if (inventory.getCount(303) < startingTraps || inventory.getCount(954) < startingTraps) {
                                        if (inventory.getCount(303) < startingTraps) {
                                            bankWithdraw(303, inventory.getCount(303) + 1, 1, "Withdrawing net", 5000);
                                        }
                                        if (inventory.getCount(954) < startingTraps) {
                                            bankWithdraw(954, inventory.getCount(954) + 1, 1, "Withdrawing net", 5000);
                                        }
                                    }
                                    if (inventory.getCount(303) > startingTraps || inventory.getCount(954) > startingTraps) {
                                        if (inventory.getCount(303) > startingTraps) {
                                            bank.deposit(303, 1);
                                        }
                                        if (inventory.getCount(954) > startingTraps) {
                                            bank.deposit(954, 1);
                                        }
                                    }
                                    sleep(random(500, 1000));
                                }
                                break;
                            case 8:
                                status = "Going from bank";
                                doNotLay = false;
                                if (getMyPlayer().getLocation().getY() > 3474 && actArea == 2) {
                                    wTT(new RSTile(3534, 3459));
                                } else {
                                    wTT(startLoc);
                                }
                                break;
                            case 9:
                                //log ("Im in 9");
                                if (anyLayedTrap()) {
                                    if (objInArea(collapsedTraps) != null) {
                                        releaseUntil(3);
                                        obj = objInArea(collapsedTraps);
                                        if (interactIt(obj.getLocation(), obj.getID(), obj, "Check Net trap", "Checking trap")) {
                                            removeLayedTrap(obj.getLocation());
                                        }
                                        //sleep(random(500, 1000));
                                    }
                                    if (objInArea(layedTraps) != null) {
                                        releaseUntil(2);
                                        obj = objInArea(layedTraps);
                                        interactIt(obj.getLocation(), obj.getID(), obj, "Dismantle Young tree", "Dismantling trap");
                                        //sleep(random(500, 1000));
                                    }
                                }
                                break;
                        }
                    } else if (huntingType == 4) {
                        switch (whatToDo()) {
                            case 1:
                                if (random(1, 3) == 1) {
                                    antiban();
                                }
                                sleep = (random(750, 1250));
                                break;
                            case 2:
                                status = "Catching butterfly";
                                RSNPC butterfly = npcInArea(butterflyIDs);
                                if (butterfly != null) {
                                    if (butterfly.isOnScreen()) {
                                        mouse.move(butterfly.getModel().getPoint());
                                        if (menu.contains("Catch")) {
                                            if (butterfly.interact("Catch")) {
                                                catchingButterfly = true;
                                                sleep(random(750, 1250));
                                            }
                                        }
                                    } else {
                                        if (random(1, 2) == 1) {
                                            walking.walkTileMM(butterfly.getLocation());
                                            sleep(random(750, 1250));
                                        } else {
                                            camera.turnTo(butterfly);
                                        }
                                        catchingButterfly = false;
                                    }
                                }
                                break;
                            case 3:
                                status = "Releasing butterflies";
                                releaseUntil(0);
                                break;
                            case 4:
                                status = "Walking to the area";
                                walking.walkTileMM(walking.getClosestTileOnMap(startLoc));
                                catchingButterfly = false;
                                sleep(random(750, 1250));
                                break;
                            case 5:
                                status = "Taking jar from the ground";
                                RSGroundItem tr = groundItems.getNearest(butterflyJar);
                                if (tr != null) {
                                    if (calc.distanceTo(tr.getLocation()) < 6) {
                                        tr.interact("Take");
                                        sleep = (random(500, 1000));
                                    }
                                }
                                break;
                        }
                    } else if (huntingType == 5) {
                        switch (whatToDo()) {
                            case 1:
                                if (random(1, 3) == 1) {
                                    antiban();
                                }
                                sleep = (random(750, 1250));
                                break;
                            case 2:
                                status = "Burying bones and dropping items";
                                int currentPath = random(0, pathes[0].length - 1);
                                RSTile stLoc = getMyPlayer().getLocation();
                                while (inventory.getCount(buryIDs) > 0 || inventory.getCount(dropIDs) > 0) {
                                    if (!game.isLoggedIn() || calc.distanceTo(stLoc) > 100) {
                                        break;
                                    }
                                    for (int i = 0; i < 28; i++) {
                                        RSItem itm = inventory.getItemAt(pathes[currentPath][i]);
                                        if (itm != null) {
                                            int ID = itm.getID();
                                            if (Arrays.binarySearch(dropIDs, ID) > -1) {
                                                if (itm.interact("Drop")) {
                                                    sleep(random(300, 700));
                                                }
                                            } else if (Arrays.binarySearch(buryIDs, ID) > -1) {
                                                if (buryBones) {
                                                    if (itm.interact("Bury")) {
                                                        sleep(random(300, 700));
                                                    }
                                                } else {
                                                    if (itm.interact("Drop")) {
                                                        sleep(random(300, 700));
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            case 3:
                                status = "Catching prey";
                                RSNPC kebbit = findFalcon(true);
                                if (kebbit != null) {
                                    if (kebbit.isOnScreen()) {
                                        mouse.move(kebbit.getModel().getPoint());
                                        if (menu.contains("Catch")) {
                                            mouse.click(true);
                                            clickedFalcon = kebbit.getLocation();
                                            sleep(random(1200, 2100));
                                        } else if (menu.contains("Chop ")) {
                                            if (!kebbit.interact("Catch")) {
                                                camera.setAngle(camera.getAngle() + random(120, 220));
                                            }
                                        }
                                    } else {
                                        if (calc.distanceTo(kebbit.getLocation()) >= 8) {
                                            walking.walkTileMM(walking.getClosestTileOnMap(kebbit.getLocation()));
                                            sleep(random(1200, 2100));
                                        } else {
                                            camera.turnTo(kebbit);
                                            if (random(1, 3) == 1) {
                                                camera.setPitch(random(20, 30));
                                            }
                                        }
                                    }
                                }
                                break;
                            case 4:
                                status = "Taking prey";
                                kebbit = findFalcon(false);
                                if (kebbit != null) {
                                    if (kebbit.isOnScreen()) {
                                        mouse.move(kebbit.getModel().getPoint());
                                        if (menu.contains("Retrieve")) {
                                            mouse.click(true);
                                            clickedFalcon = kebbit.getLocation();
                                            sleep(random(1200, 2100));
                                        } else if (menu.contains("Chop ")) {
                                            if (!kebbit.interact("Retrieve")) {
                                                camera.setAngle(camera.getAngle() + random(120, 220));
                                            }
                                        }
                                    } else {
                                        if (calc.distanceTo(kebbit.getLocation()) >= 8) {
                                            walking.walkTileMM(walking.getClosestTileOnMap(kebbit.getLocation()));
                                            sleep(random(1200, 2100));
                                        } else {
                                            camera.turnTo(kebbit);
                                            if (random(1, 3) == 1) {
                                                camera.setPitch(random(20, 30));
                                            }
                                        }
                                    }
                                } else {
                                    for (int i = 0; i < 40; i++) {
                                        sleep(random(75, 100));
                                        kebbit = findFalcon(false);
                                        if (kebbit != null) {
                                            break;
                                        }
                                    }
                                    if (kebbit == null) {
                                        onTheGround = false;
                                        deleteNotMyFalcons();
                                    }
                                }
                            case 5:
                                if (!haveFalcon) {
                                    status = "Retrieving falcon from Matthias";
                                    RSNPC matthias = npcs.getNearest(5092);
                                    if (matthias != null) {
                                        if (matthias.isOnScreen()) {
                                            if (matthias.interact("Falconry")) {
                                                sleep(random(1500, 2000));
                                                mouse.move(random(190, 317), 401);
                                                sleep(random(100, 200));
                                                if (menu.contains("Continue")) {
                                                    mouse.click(true);
                                                    sleep(random(1000, 1500));
                                                    haveFalcon = true;
                                                    log("Falcon retrieved form Matthias!");
                                                }
                                            }
                                        } else {
                                            log("Walking to Matthias...");
                                            walking.walkTileMM(walking.getClosestTileOnMap(matthias.getLocation()));
                                            sleep(random(1200, 2100));
                                        }
                                    } else {
                                        lg("Matthias isn't there! Walking to his place...");
                                        walking.walkTileMM(walking.getClosestTileOnMap(new RSTile(2375, 3598)));
                                    }
                                }
                                break;
                        }
                    }
                }
            }
        } catch (Exception e) {
        }
        return sleep;
    }

    String getTime(double Time, int gainedExperience, boolean takeScreenshot, boolean tnl) {
        long hours, minutes, seconds;
        hours = (int) Time;
        if ((hours < 1000 && gainedExperience > 0) || !tnl) {
            if (tnl) {
                Time = (Time - hours) * 60;
                minutes = (int) Time;
                Time = (Time - minutes) * 60;
                seconds = (int) Time;
            } else {
                seconds = (int) Time / 1000;
                minutes = (int) seconds / 60;
                hours = (int) minutes / 60;
                minutes %= 60;
                seconds %= 60;
            }
            addNull1 = " ";
            addNull2 = " ";
            if (minutes < 10) {
                addNull1 = " 0";
            }
            if (seconds < 10) {
                addNull2 = " 0";
            }
            if (seconds == 1 && lastScreen != hours && minutes == 0 && takeScreenshot) {
                env.saveScreenshot(true);
                lastScreen = hours;
            }
            return hours + ":" + addNull1 + minutes + ":" + addNull2 + seconds;
        } else {
            if (gainedExperience == 0) {
                return "No exp gained";
            } else {
                return "Too long";
            }
        }
    }

    private Image getImage(String url) {
        try {
            return ImageIO.read(new URL(url));
        } catch (IOException e) {
            return null;
        }
    }
    private final Font font = new Font("Arial", 0, 10);
    Rectangle HideShow = new Rectangle(354, 447, 40, 25);
    Rectangle turnItOff = new Rectangle(515, 444, 45, 44);
    Rectangle Menu1 = new Rectangle(22, 447, 70, 15);   //MODE 1 == OVERVIEW
    Rectangle Menu2 = new Rectangle(145, 447, 75, 15);   //MODE 2 == OPTIONS
    Rectangle start = new Rectangle(2, 437, 150, 50);
    Rectangle boxTrapping = new Rectangle(346, 400, 21, 33);
    Rectangle birdSnaring = new Rectangle(368, 400, 40, 33);
    Rectangle netTrapping = new Rectangle(346, 365, 32, 35);
    Rectangle butterflyNet = new Rectangle(378, 365, 32, 35);
    Rectangle bonesArea = new Rectangle(316, 355, 32, 35);
    Rectangle huntLoc = new Rectangle(150, 410, 100, 20);
    Rectangle[] trapPos = {
        new Rectangle(120, 365, 20, 20),
        new Rectangle(143, 365, 20, 20),
        new Rectangle(166, 365, 20, 20),
        new Rectangle(120, 388, 20, 20),
        new Rectangle(143, 388, 20, 20),
        new Rectangle(166, 388, 20, 20),
        new Rectangle(120, 411, 20, 20),
        new Rectangle(143, 411, 20, 20),
        new Rectangle(166, 411, 20, 20)
    };

    private void drawRect(Graphics g, Rectangle rect, boolean fill, Color a) {
        g.setColor(a);
        if (fill) {
            g.fillRect(rect.x, rect.y, rect.width, rect.height);
        } else {
            g.drawRect(rect.x, rect.y, rect.width, rect.height);
        }
        g.setColor(Color.white);
    }

    public void onRepaint(Graphics g1) {
        ((Graphics2D) g1).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        Graphics2D g = (Graphics2D) g1;
        g.setColor(Color.white);
        g.setFont(font);
        g.drawImage(turnOffImage, 512, 440, null);
        long running = System.currentTimeMillis() - startedAt;
        long running2 = System.currentTimeMillis() - startedAt;
        if (hidden) {
            g.drawImage(Show, 351, 438, null);
        } else {
            if (Mode == 1) {
                g.drawImage(Background, 7, 297, null);
                int gainedXp = skills.getCurrentExp(skills.HUNTER) - startExp;
                int currentLvl = skills.getRealLevel(skills.HUNTER);
                int gainedLvl = currentLvl - startLvl;
                int expTnl = skills.getExpToNextLevel(skills.HUNTER);
                int percentTnl = skills.getPercentToNextLevel(skills.HUNTER);
                g.drawString("" + currentLvl, 189, 374);
                g.drawString("" + gainedLvl, 189, 388);
                g.drawString("" + (int) (gainedXp), 189, 403);
                g.drawString("" + (int) ((gainedXp) * 3600000.0 / running), 349, 374);
                g.drawString("" + expTnl, 349, 388);
                g.drawString(getTime(expTnl / ((gainedXp) * 3600000.0 / running), gainedXp, false, true), 349, 403);
                g.drawString(percentTnl + "%", 267, 426);
                g.setColor(new Color(204, 204, 204));
                g.fillRect(258 - (100 - percentTnl), 418, (100 - percentTnl), 4);
                g.setColor(new Color(150, 150, 150));
                g.fillRect(258 - (100 - percentTnl), 422, (100 - percentTnl), 6);
            } else if (Mode == 2 || !started) {
                g.drawImage(Background2, 7, 297, null);
                if (huntingType == 1) {
                    drawRect(g, boxTrapping, false, Color.white);
                } else if (huntingType == 2) {
                    drawRect(g, birdSnaring, false, Color.white);
                } else if (huntingType == 3) {
                    drawRect(g, netTrapping, false, Color.white);
                } else if (huntingType == 4) {
                    drawRect(g, butterflyNet, false, Color.white);
                }
                if (huntingType < 3) {
                    for (int i = 0; i <= 8; i++) {
                        if (huntTrap[i]) {
                            drawRect(g, trapPos[i], true, Color.green);
                        } else {
                            drawRect(g, trapPos[i], true, Color.white);
                        }
                    }
                }
                if (huntingType == 3) {
                    if (!started || started) {
                        g.drawString("Layed traps:", 100, 368);
                        for (int i = 0; i < 5; i++) {
                            g.drawString(trapsX[i] + ", " + trapsY[i], 115, 385 + i * 12);
                        }
                    }
                } else if (huntingType == 5) {
                    g.setColor(Color.yellow);
                    g.drawString("Hunting location:", 120, 410);
                    if (huntEverywhere) {
                        g.drawString("EVERYWHERE ", 130, 425);
                    } else {
                        g.drawString("NEAR START LOC.", 130, 425);
                    }
                    g.setColor(Color.white);
                }
                if (!started) {
                    g.drawImage(startImg, 2, 437, null);
                }
                g.setColor(Color.orange);
                if (buryBones) {
                    g.drawString("BURY", 316, 365);
                } else {
                    g.drawString("DROP", 316, 365);
                }
                g.setColor(Color.white);
                g.drawString("Caught: " + caught, 200, 380);
                g.drawString("Caught/h: " + (int) (caught * 3600000.0 / running), 200, 395);
                g.drawString("Starting location: " + startLoc.getX() + ", " + startLoc.getY(), 200, 436);
            }
            g.setColor(Color.white);
            if (started) {
                g.drawString(version, 430, 460);
                g.drawString("Status: " + status, 20, 475);
                g.drawString(getTime(running2, 0, true, false), 290, 460);
            }
            /* Mouse paint */
            Point m = mouse.getLocation();
            g.drawRect(m.x - 7, m.y - 7, 14, 14);
            g.fillRect(m.x - 1, m.y - 1, 3, 3);
            /*g.drawString("Mouse pos:" + m.x + ", " + m.y, 400, 24);
            for (int i = 0; i < 5; i++) {
            g.drawString("traps[" + i + "] = " + traps[i] + "; ", 400, 39 + i * 15);
            }*/
        }
    }

    public void mouseClicked(MouseEvent e) {
        Point p = e.getPoint();
        if (HideShow.contains(p)) {
            hidden = !hidden;
        } else if (turnItOff.contains(p)) {
            turnOff = true;
            stopScript();
        } else if (Menu1.contains(p) && started) {
            Mode = 1;
        } else if (Menu2.contains(p)) {
            Mode = 2;
        } else if (Mode == 1) {
        } else if (Mode == 2 || !started) {
            for (int i = 0; i <= 8; i++) {
                if (trapPos[i].contains(p)) {
                    if ((getActTraps() < getMaxTraps() && !huntTrap[i]) || huntTrap[i]) {
                        huntTrap[i] = !huntTrap[i];
                        getActTraps();
                    }
                }
            }
            if (start.contains(p) && !started) {
                started = true;
                if (huntingType == 3) {
                    for (int i = 0; i <= 4; i++) {
                        traps[i] = null;
                    }
                    startingTraps = inventory.getCount(303);
                    getActArea();
                } else if (huntingType < 3) {
                    getActTraps();
                    getObjects();
                } else if (huntingType == 4) {
                    getActArea();
                }
                startedAt = System.currentTimeMillis();
            } else if (boxTrapping.contains(p)) {
                huntingType = 1;
            } else if (birdSnaring.contains(p)) {
                huntingType = 2;
            } else if (netTrapping.contains(p)) {
                huntingType = 3;
            } else if (butterflyNet.contains(p)) {
                huntingType = 4;
            } else if (bonesArea.contains(p)) {
                buryBones = !buryBones;
            } else if (huntLoc.contains(p)) {
                huntEverywhere = !huntEverywhere;
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mousePressed(MouseEvent e) {
    }

    public void onFinish() {
        log("Thanks for using! Antibans: " + antibans);
    }

    public void messageReceived(MessageEvent e) {
        String msg = e.getMessage();
        if (msg.contains("caught") || msg.contains("it in a jar") || msg.contains("manage to catch the") || msg.contains("You retrieve the falcon as well as")) {
            caught++;
            if (huntingType == 5) {
                onTheGround = false;
                deleteNotMyFalcons();
            }
        } else if (msg.contains("Your falcon has left its prey")) {
            haveFalcon = false;
            onTheGround = false;
        } else if (msg.contains("You try to catch the creature")) {
            if (!onTheGround) {
                haveFalcon = false;
            }
        } else if (msg.contains("swoops down and captures ")) {
            if (!msg.contains("misses")) {
                onTheGround = true;
            }
        } else if (msg.contains("isn't your bird")) {
            addNotMyFalcons(clickedFalcon);
        } else if (msg.contains("Your juju hunter potion wears off")) {
            isDraconic = false;
        }
    }
}
package com.null8.GameEngine2D.level.manager;

import com.null8.GameEngine2D.level.FakePlayer;
import com.null8.GameEngine2D.level.GameObject;
import com.null8.GameEngine2D.level.Player;
import com.null8.GameEngine2D.math.Matrix4f;
import com.null8.GameEngine2D.math.Vec2;

import java.util.*;

public class GameObjectManager {

    private final GameObject[] gameObjects;
    private final FakePlayer[] fakePlayers;
    private final List<GameObject> texts = new ArrayList<>();
    private final Player player;

    private final GameObjectRepresenter[] gameObjectReps;

    public GameObjectManager(GameObject[] gameObjects, FakePlayer[] fakePlayers, Player player) {
        this.gameObjects = gameObjects;
        this.fakePlayers = fakePlayers;
        this.player = player;

        gameObjectReps = new GameObjectRepresenter[gameObjects.length + fakePlayers.length + 1];

        int i = 0;

        int i1 = 0;
        for (GameObject gameObject:gameObjects) {
            gameObjectReps[i] = new GameObjectRepresenter(gameObject, i1);
            i++;
            i1++;
        }

        i1 = 0;
        for (FakePlayer fakePlayer:fakePlayers) {
            gameObjectReps[i] = new GameObjectRepresenter(fakePlayer, i1);
            i++;
            i1++;
        }

        gameObjectReps[i] = new GameObjectRepresenter(player, 1);

        sort();
    }

    public void render(Matrix4f pr_matrix, Vec2<Float> pos, boolean step) {

        for (GameObjectRepresenter gameObjectRep : gameObjectReps) {
            if (gameObjectRep.getType().equals(GameObject.class))
                gameObjects[gameObjectRep.locate()].render(pr_matrix);
            else if (gameObjectRep.getType().equals(FakePlayer.class))
                fakePlayers[gameObjectRep.locate()].render(pr_matrix);
            else if (gameObjectRep.getType().equals(Player.class)) {
                player.move(new Vec2<>(pos.x, pos.y));
                player.render(pr_matrix, step);
            }
        }

        List<GameObject> textsCopy = new ArrayList<>(texts);
        for (GameObject text:textsCopy) {
            if (text != null) {
                text.render(pr_matrix);
            }
        }

    }

    public void sort() {
        Arrays.sort(this.gameObjectReps, Comparator.comparing(GameObjectRepresenter::getZHeight));
    }

    public GameObject getGameObject(String name) {
        for (GameObjectRepresenter gameObjectRep : gameObjectReps) {
            if (Objects.equals(gameObjectRep.getName(), name)) {

                if (gameObjectRep.getType().equals(GameObject.class))
                    return gameObjects[gameObjectRep.locate()];
                else if (gameObjectRep.getType().equals(FakePlayer.class))
                    return fakePlayers[gameObjectRep.locate()];
                else if (gameObjectRep.getType().equals(Player.class))
                    return player;
                break;
            }
        }
        return null;
    }

    public FakePlayer getFakePlayer(String name) {
        for (GameObjectRepresenter gameObjectRep : gameObjectReps) {
            if (Objects.equals(gameObjectRep.getName(), name)) {

                if (gameObjectRep.getType().equals(FakePlayer.class))
                    return fakePlayers[gameObjectRep.locate()];
                else
                    return null;
            }
        }
        return null;
    }

    public Player getPlayer() {
        return player;
    }


    public void setText(GameObject text) {
        text.setZHeight(4);
        String name = text.getName();

        boolean containsElement = false;
        for (GameObject textElement:texts) {
            if (Objects.equals(textElement.getName(), name)) {
                containsElement = true;
                break;
            }
        }

        if (!containsElement) {
            addText(text);
            return;
        }

        for (GameObject textElement:texts) {
            if (Objects.equals(textElement.getName(), name)) {
                texts.remove(textElement);
                texts.add(text);
            }
        }
    }

    public void addText(GameObject text) {
        text.setZHeight(4);
        texts.add(text);
    }

    public void removeText(GameObject text) {
        text.setZHeight(4);
        String name = text.getName();
        texts.removeIf(textElement -> Objects.equals(textElement.getName(), name));
    }


}

package org.minigame;

import org.minigame.utils.Vector2D;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

public class Snake {

    private Vector2D<Integer> head;
    private Vector2D<Integer> tail;
    private final Vector2D<Integer> direction;
    private final Deque<Vector2D<Integer>> queue;
    private final int speed;

    public Snake(int speed) {
        this.speed = speed;
        this.queue = new ArrayDeque<>();

        resetPosition();
        this.direction = new Vector2D<>(1, 0);
    }

    public Vector2D<Integer> getHead() {
        return head;
    }

    public void setHead(Vector2D<Integer> head) {
        this.head = head;
        queue.add(head);
    }

    public Vector2D<Integer> popTail() {
        this.tail = queue.poll();
        return this.tail;
    }

    public Vector2D<Integer> getDirection() {
        return direction;
    }

    public void setDirection(Vector2D<Integer> nextDirection) {
        if (isOppositeDirection(nextDirection))
            return;

        this.direction.x = nextDirection.x;
        this.direction.y = nextDirection.y;
    }

    public int getSpeed() {
        return this.speed;
    }

    public List<Vector2D<Integer>> getBody() {
        return List.copyOf(queue);
    }

    private boolean isOppositeDirection(Vector2D<Integer> nextDirection) {
        var currentDirection = this.direction;
        return nextDirection.x + currentDirection.x == -1 && nextDirection.y + currentDirection.y == 0;
    }

    public void resetPosition() {
        queue.clear();
        for (int i=0; i<3; i++)
            queue.add(new Vector2D<>(i, 0));

        this.head = queue.peekLast();
        this.tail = queue.peekFirst();
    }
}
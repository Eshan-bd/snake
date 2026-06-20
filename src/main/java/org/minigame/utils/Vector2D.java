package org.minigame.utils;

public class Vector2D<T extends Number> {
    public T x;
    public T y;

    public Vector2D (T x, T y) {
        this.x = x;
        this.y = y;
    }

    public T getX() {
        return x;
    }

    public void setX(T x) {
        this.x = x;
    }

    public T getY() {
        return y;
    }

    public void setY(T y) {
        this.y = y;
    }

    public void set(T x, T y) {
        this.x = x;
        this.y = y;
    }


    public Vector2D<Integer> add(Vector2D<Integer> v) {

        return new Vector2D<> (
                this.x.intValue() + v.x,
                this.y.intValue() + v.y
        );
    }

    public Vector2D<Integer> subtract(Vector2D<Integer> v) {

        return new Vector2D<> (
                this.x.intValue() - v.x,
                this.y.intValue() - v.y
        );
    }

    public Vector2D<Integer> multiply(Vector2D<Integer> v) {

        return new Vector2D<> (
                this.x.intValue() * v.x,
                this.y.intValue() * v.y
        );
    }

    public Vector2D<Integer> multiply(int v) {

        return new Vector2D<> (
                this.x.intValue() * v,
                this.y.intValue() * v
        );
    }
}

public abstract class GameObject {
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected BoundingBox boundingBox;

    public int getX() {
        return x;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public int getY() {
        return y;
    }

    @Override
    public String toString() {
        return "GameObject{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", boundingBox=" + boundingBox +
                '}';
    }
}

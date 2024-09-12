public class Tile extends GameObject {

    public int tileIndex; // 0 - grass, 1 - water
    public Tile(int x, int y, int tileIndex, int size) {
        this.x = x;
        this.y = y;
        this.tileIndex = tileIndex;
        this.width = size;
        this.height = size;
        this.boundingBox = new BoundingBox(new Vec2(x,y), new Vec2(x + width,y + height));
    }

}

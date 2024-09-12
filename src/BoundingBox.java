public class BoundingBox {
    Vec2 min;
    Vec2 max;
    public BoundingBox(Vec2 min, Vec2 max) {
        this.min = min;
        this.max = max;
    }

    public void changePosition(int dx, int dy) {
        min.x += dx;
        min.y += dy;
        max.x -= dx;
        max.y -= dy;
    }

    public boolean intersect(BoundingBox b) {
        return (min.x <= b.max.x) &&
                (max.x >= b.min.x) &&
                (min.y <= b.max.y) &&
                (max.y >= b.min.y);
    }



    public Vec2 overlapSize(BoundingBox b) {
        Vec2 result = new Vec2(0, 0);
        // X-dimension
        if (min.x < b.min.x)
            result.x = max.x - b.min.x;
        else
            result.x = b.max.x - min.x;
        // Y-dimension
        if (min.y < b.min.y)
            result.y = max.y - b.min.y;
        else
            result.y = b.max.y - min.y;
        return result;
    }

    public ColisionType checkColision(BoundingBox b) {

        if (!intersect(b)) {
            return ColisionType.NONE;
        }

        Vec2 overlap = overlapSize(b);

        if (overlap.x > overlap.y) {
            // Vertical collision
            if (min.y < b.min.y) {
                return ColisionType.DOWN;
            } else {
                return ColisionType.UP;
            }
        } else {
            // Horizontal collision
            if (min.x < b.min.x) {
                return ColisionType.RIGHT;
            } else {
                return ColisionType.LEFT;
            }
        }
    }

    public enum ColisionType {
        NONE,
        RIGHT,
        LEFT,
        UP,
        DOWN
    }
    public String toString() {
        return "BoundingBox [min=" + min + ", max=" + max + "]";

    }
}

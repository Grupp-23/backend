package Model;

public class Projectile {
    private double x,y;
    private int damage;

    private double direction;

    private double speed;

    private int id;

    public Projectile(double x, double y, int damage, double direction, double speed, int id) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.direction = Math.cos(direction);
        this.speed = speed;
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getX() {
        return x;
    }

    public int getDamage() {
        return damage;
    }

    public void updatePosition() {
        x = x + (direction*speed);
    }
}

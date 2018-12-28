package game;

import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.List;

import helper.Constants;
import helper.ImageHelper;
import rafgfxlib.Util;

public class Bullet {
	private static final double SPEED = 14.43;
	public static final double GRAVITY = 0.215;

	private Image image;
	private List<Image> explosionImages = new ArrayList<>();
	private double posX;
	private double posY;
	private double angle;
	private int angleDirection = 1;
	private double speedX;
	private double speedY;
	private double deltaX = Constants.SOLDIER_WIDTH * 0.5;
	private double deltaY = Constants.SOLDIER_HEIGHT * -0.13;

	private boolean isVisible = true;
	private boolean isExplosionMode = false;
	private int explosionCounter = 0;
	private int explosionIndex = 0;

	private AffineTransform transform = new AffineTransform();

	public Bullet(double posX, double posY, double angle, boolean direction) {
		angleDirection = direction ? 1 : -1;
		this.posX = (deltaX * Math.cos(Math.toRadians(angle)) - deltaY * Math.sin(Math.toRadians(angle)))
				* angleDirection + posX + Constants.SOLDIER_WIDTH * 0.5;
		this.posY = -(deltaY * Math.cos(Math.toRadians(angle)) + deltaX * Math.sin(Math.toRadians(angle))) + posY
				+ Constants.SOLDIER_HEIGHT * 0.5;
		this.angle = angle;
		this.speedX = angleDirection * (SPEED-2.0) *Math.cos(Math.toRadians(angle));
		this.speedY = (SPEED+1.3) * Math.sin(Math.toRadians(angle));
		init();
	}

	private void init() {
		image = ImageHelper.resizeImage(Util.loadImage("resources/bullet.png"), 10, 10);

		for (int i = 0; i < 3; i++) {
			explosionImages
					.add(ImageHelper.resizeImage(Util.loadImage("resources/explosion/explosion" + i + ".png"), 55, 55));
		}
		explosionIndex = 0;
	}

	public void update() {
		if (isExplosionMode) {
			image = explosionImages.get(explosionIndex);
			explosionIndex = (explosionIndex + 1) % explosionImages.size();
			explosionCounter++;
			if (explosionCounter == 9) {
				isVisible = false;
				// End of turn
				Game.changeTurn();
			}
		} else {
			posX += speedX;
			posY -= speedY;
			speedY -= GRAVITY;

			// Block collision detection
			for (Block b : Game.blocks) {
				if (b.isVisible() && b.getRectangle().contains(new Point((int) posX, (int) posY))) {
					isExplosionMode = true;
					explosionCounter = 0;
					if (b.isDestructible()) {
						b.setVisible(false);
						b.setParticlesLife(150);
					}
				}
			}

			// Ground collision
			if (isVisible && posY + 10 >= Constants.GROUND_Y) {
				posY = Constants.GROUND_Y - 40;
				posX -= 20;
				image = explosionImages.get(explosionIndex);
				isExplosionMode = true;
			}

//			// Soldier collision detection
			for (Soldier s : Game.player1.getSoldiers()) {
				if (s.isVisible() && !s.isActive() && s.getRectangle().contains(new Point((int) posX, (int) posY))) {
					image = explosionImages.get(explosionIndex);
					isExplosionMode = true;
					s.setHurt(true);
					int solderDestruction=destruct(s.getPosX(),s.getPosY(),posX,posY);
					s.setHealth(Math.max(0, s.getHealth()-solderDestruction));
				}
			}

			for (Soldier s : Game.player2.getSoldiers()) {
				if (s.isVisible() && !s.isActive() && s.getRectangle().contains(new Point((int) posX, (int) posY))) {
					image = explosionImages.get(explosionIndex);
					isExplosionMode = true;
					s.setHurt(true);
					int solderDestruction=destruct(s.getPosX(),s.getPosY(),posX,posY);
					s.setHealth(Math.max(0, s.getHealth()-solderDestruction));
				}
			}
		}

		transform.setToIdentity();
		transform.translate(posX, posY);
	}
	
	private int destruct(double solderX, double solderY, double posX, double posY) {
		
		double centerX=solderX+Constants.SOLDIER_WIDTH/2;
		double centerY=solderY+Constants.SOLDIER_HEIGHT/2;
		int value=0;
		if (Math.abs(centerY-posY)<=5) value=8; else 
			if (posY<centerY) value=6; else 
				value=4;
		
		int currentSpeed=(int) Math.sqrt(speedX*speedX + speedY*speedY);
	   
		if (currentSpeed==15) value*=3; else 
			if (currentSpeed==12) value*=2;
		return value;
	}

	public Image getImage() {
		return image;
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public boolean isVisible() {
		return isVisible;
	}

}

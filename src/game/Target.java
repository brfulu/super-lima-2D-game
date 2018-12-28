package game;

import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import helper.ImageHelper;
import rafgfxlib.Util;

public class Target {
	private Image image;
	private double posX;
	private double posY;
	private double angle;
	private double radius = 130;
	private int angleDirection = 1;
	private AffineTransform transform = new AffineTransform();

	public Target(double posX, double posY) {
		this.posX = posX + 110;
		// this.posY = posY + 50;
		posY = 0;
		angle = 0;
		init();
	}

	public void changeAngle(int direction) {
		angle+=direction*10;
		angle %= 70;
		if (angle<0) angle+=70;
		return;
	}

	public void ChangeDirection(int direction) {
		angleDirection = direction;
		angle = 0;
		return;
	}

	public void update(double posX, double posY) {
		this.posX = posX;
		this.posY = posY;

		this.posX += angleDirection * radius * Math.cos(Math.toRadians(angle-angle/12));
		this.posY -= radius * Math.sin(Math.toRadians(angle-angle/12));

		transform.setToIdentity();
		transform.translate(this.posX, this.posY);
	}

	private void init() {
		image = ImageHelper.resizeImage(Util.loadImage("resources/target.png"), 33, 33);
	}

	public Image getImage() {
		return image;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}

	public double getAngle() {
		return angle;
	}

	public int getAngleDirection() {
		return angleDirection;
	}

	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	
}

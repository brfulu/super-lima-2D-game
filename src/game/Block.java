package game;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import helper.ImageHelper;
import rafgfxlib.Util;

public abstract class Block {
	private BufferedImage image;
	private int posX;
	private int posY;
	private int width;
	private int height;
	private boolean isVisible = true;
	private int particlesLife = 0;
	private AffineTransform transform = new AffineTransform();
	
	public Block(int posX, int posY, String imagePath) {
		super();
		this.posX = posX;
		this.posY = posY;
		loadImage(imagePath);
	}
	
	public abstract boolean isDestructible();

	private void loadImage(String imagePath) {
		image = Util.loadImage("resources/blocks/" + imagePath);
		image = ImageHelper.resizeImage(image, 130, image.getHeight(null));
		width = image.getWidth(null);
		height = image.getHeight(null);
	}

	public Image getImage() {
		return image;
	}

	public int getPosX() {
		return posX;
	}

	public int getPosY() {
		return posY;
	}
	
	public Rectangle getRectangle() {
		return new Rectangle((int)posX, (int)posY, width, height);
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public boolean isVisible() {
		return isVisible;
	}

	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	public int getParticlesLife() {
		return particlesLife;
	}

	public void setParticlesLife(int particlesLife) {
		this.particlesLife = particlesLife;
	}

}

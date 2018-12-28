package game;

import java.awt.Container;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import helper.Constants;
import helper.ImageHelper;
import helper.MathHelper;
import rafgfxlib.Util;

public class Soldier {
	public static final double FRICTION = 1;
	public static final double GRAVITY = 0.14;
	public static final double JUMP_STRENGTH = 8;
	public static final double SPEED = 5;

	private double posX;
	private double posY;
	private double speedX = 0;
	private double speedY = 0;
	private int width;
	private int height;
	private AffineTransform transform = new AffineTransform();

	private Point rUpMiddle;
	private Point rUpRight;
	private Point rUpLeft;
	private Point rDownLeft;
	private Point rDownRight;

	private Point lUpMiddle;
	private Point lUpRight;
	private Point lUpLeft;
	private Point lDownLeft;
	private Point lDownRight;

	private Point leftMiddle;
	private Point rightMiddle;
	private Point middleMiddle;

	private int health;
	private BufferedImage image;
	private Target target;
	private Bullet bullet;
	private int team;

	private List<BufferedImage> deadRightImages;
	private int deadRightIndex;
	private List<BufferedImage> deadLeftImages;
	private int deadLeftIndex;

	private List<BufferedImage> idleRightImages;
	private int idleRightIndex;
	private List<BufferedImage> idleLeftImages;
	private int idleLeftIndex;

	private List<BufferedImage> runRightImages;
	private int runRightIndex = 0;
	private List<BufferedImage> runLeftImages;
	private int runLeftIndex = 0;

	private List<BufferedImage> hurtRightImages;
	private int hurtRightIndex = 0;
	private List<BufferedImage> hurtLeftImages;
	private int hurtLeftIndex = 0;

	private BufferedImage jumpRightImage;
	private BufferedImage jumpLeftImage;

	private boolean isVisible = true;
	
	private boolean isRightTurn = true;
	private boolean isActive = false;
	private boolean isShotFired = false;
	private boolean isHurt = false;
	private int hurtCounter = 0;
	private int tickCounter = 0;
	private int deadCounter = 0;
	private boolean isDead = false;

	public Soldier(double posX, double posY, int team) {
		this.posX = posX;
		this.posY = posY;
		this.team = team;
		init();
	}

	private void init() {
		image = Util.loadImage("resources/soldier.png");
		image = ImageHelper.getColoredSoldier(
				ImageHelper.resizeImage(image, Constants.SOLDIER_WIDTH, Constants.SOLDIER_HEIGHT), team);
		width = image.getWidth(null);
		height = image.getHeight(null);
		health = 100;

		loadIdleImages();
		loadRunImages();
		loadJumpImages();
		loadHurtImages();
		loadDeadImages();

		updateCriticalPoints();

		target = new Target(getPosX(), getPosY());
	}

	private void updateCriticalPoints() {
		rUpMiddle = new Point((int) (posX + width * 0.33), (int) posY);
		rUpLeft = new Point((int) (posX + width * 0.13), (int) (posY + height * 0.1));
		rUpRight = new Point((int) (posX + width * 0.6), (int) (posY + height * 0.1));
		rDownLeft = new Point((int) (posX + width * 0.11), (int) (posY + height));
		rDownRight = new Point((int) (posX + width * 0.6), (int) (posY + height));

		lUpMiddle = new Point((int) (posX + width * 0.66), (int) posY);
		lUpLeft = new Point((int) (posX + width * 0.4), (int) (posY + height * 0.1));
		lUpRight = new Point((int) (posX + width * 0.87), (int) (posY + height * 0.1));
		lDownLeft = new Point((int) (posX + width * 0.4), (int) (posY + height));
		lDownRight = new Point((int) (posX + width * 0.89), (int) (posY + height));

		rightMiddle = new Point((int) (posX + width), (int) (posY + height * 0.65));
		leftMiddle = new Point((int) posX, (int) (posY + height * 0.65));
		middleMiddle= new Point((int) (posX + width*0.5), (int) (posY + height * 0.5));
	}

	public void accelerate(double accelerationX, double accelerationY) {
		speedX += accelerationX;
		speedY += accelerationY;
	}

	public void move(double xDelta, double yDelta) {
		posX += xDelta;
		posY += yDelta;

		// do collision detection here. upon collision, set speedX/speedY to zero..!
		if (posY + Constants.SOLDIER_HEIGHT >= Constants.GROUND_Y) {
			posY = Constants.GROUND_Y - Constants.SOLDIER_HEIGHT;
			speedY = 0;
		}

		// Collision detection for each block
		for (Block block : Game.blocks) {
			if (!block.isVisible())
				continue;

			if (MathHelper.doRectanglesIntersect(getRectangle(), block.getRectangle())) {
				// Down + Right
				if (speedY >= 0 && isRightTurn) {
					if ((block.getRectangle().contains(rDownRight) || block.getRectangle().contains(rDownLeft))
							&& Math.abs(posY + height - block.getPosY()) < 15) {
						posY = block.getPosY() - height + 1;
						speedY = 0;
					}
					if (block.getRectangle().contains(rightMiddle)) {
						posX = block.getPosX() - width;
					}
					if (block.getRectangle().contains(rUpRight)) {
						posX -= SPEED;
					}
					if (block.getRectangle().contains(middleMiddle)) {
						posY=block.getPosY();
					}
				}

				// Down + Left
				if (speedY >= 0 && !isRightTurn) {
					if (block.getRectangle().contains(lDownRight) || block.getRectangle().contains(lDownLeft)
							&& Math.abs(posY + height - block.getPosY()) < 15) {
						posY = block.getPosY() - height + 1;
						speedY = 0;
					}
					if (block.getRectangle().contains(leftMiddle)) {
						posX = block.getPosX() + block.getWidth();
					}
					if (block.getRectangle().contains(lUpLeft)) {
						posX += SPEED;
					}
					if (block.getRectangle().contains(middleMiddle)) {
						posY=block.getPosY();
					}
				}

				// Up + Right
				if (speedY < 0 && isRightTurn) {
					if (block.getRectangle().contains(rUpMiddle) || block.getRectangle().contains(rUpRight)
							|| block.getRectangle().contains(rUpLeft)) {
						posY = block.getPosY() + block.getHeight();
						speedY = 0;
					}
					if (block.getRectangle().contains(rightMiddle)) {
						posY = block.getPosY() + block.getHeight() - 40;
						speedY = 0;
					}
				}

				// Up + Left
				if (speedY < 0 && !isRightTurn) {
					if (block.getRectangle().contains(lUpMiddle) || block.getRectangle().contains(lUpRight)
							|| block.getRectangle().contains(lUpLeft)) {
						posY = block.getPosY() + block.getHeight();
						speedY = 0;
					}
					if (block.getRectangle().contains(leftMiddle)) {
						posY = block.getPosY() + block.getHeight() - 40;
						speedY = 0;
					}
				}
			}
		}

		transform.setToIdentity();
		transform.translate(posX, posY);
	}

	public Image getImage() {
		return image;
	}

	public void update() {
		tickCounter++;
		if (isDead) {
			deadCounter++;
			
			if (deadCounter >= 60) {
				// Should disappear
				isVisible = deadCounter % 18 < 9;
				isActive = false;
			}
			
			if (deadCounter >= 180) {
				isVisible = false;
			}
			
			if (tickCounter % 9 != 0) return;

			if (isRightTurn ) {
				image = ImageHelper.getColoredSoldier(deadRightImages.get(deadRightIndex), team);
				if (deadRightIndex < deadRightImages.size() - 1) {
					deadRightIndex++;
				}
			} else {
				image = ImageHelper.getColoredSoldier(deadLeftImages.get(deadLeftIndex), team);
				if (deadLeftIndex < deadLeftImages.size() - 1) {
					deadLeftIndex++;
				}
			}

		} else if (isHurt) {
			if (isRightTurn) {
				image = ImageHelper.getColoredSoldier(hurtRightImages.get(hurtRightIndex), team);
				hurtRightIndex = (hurtRightIndex + 1) % hurtRightImages.size();
			} else {
				image = ImageHelper.getColoredSoldier(hurtLeftImages.get(hurtLeftIndex), team);
				hurtLeftIndex = (hurtLeftIndex + 1) % hurtLeftImages.size();
			}

			hurtCounter++;
			if (hurtCounter == 20) {
				hurtCounter = 0;
				isHurt = false;
			}
		} else if (!isJumpMode() && !isRunMode() && tickCounter % 5 == 0) {
			if (isActive) {
				if (isRightTurn) {
					image = ImageHelper.getColoredSoldier(idleRightImages.get(0), team);
					runRightIndex = 0;
				} else {
					image = ImageHelper.getColoredSoldier(idleLeftImages.get(0), team);
					runLeftIndex = 0;
				}
			} else {
				if (isRightTurn) {
					image = ImageHelper.getColoredSoldier(idleRightImages.get(idleRightIndex), team);
					idleRightIndex = (idleRightIndex + 1) % idleRightImages.size();
				} else {
					image = ImageHelper.getColoredSoldier(idleLeftImages.get(idleLeftIndex), team);
					idleLeftIndex = (idleLeftIndex + 1) % idleLeftImages.size();
				}
			}
		}
		move(speedX, speedY);
		accelerate(0, GRAVITY); // gravity accelerates the object downwards each tick
		if (isActive) {
			updateTarget();
			updateCriticalPoints();

			if (isShotFired && bullet.isVisible()) {
				bullet.update();
			}
			getTransform().rotate(Math.toRadians(target.getAngle()) * -target.getAngleDirection(),
					Constants.SOLDIER_WIDTH * 0.5, Constants.SOLDIER_HEIGHT * 0.5);
		}
	}

	private void updateTarget() {
		double targetX = getPosX() + (isRightTurn ? 20 : 40);
		double targetY = getPosY() + 38;
		target.update(targetX, targetY);
	}

	public boolean isShotMode() {
		return isActive() && !isJumpMode() && !isRunMode();
	}

	public void jump() {
		if (!isJumpMode()) {
			accelerate(0, -JUMP_STRENGTH); // change 5 for some constant or variable indicating the "strength" of the
											// jump
			image = isRightTurn ? ImageHelper.getColoredSoldier(jumpRightImage, team)
					: ImageHelper.getColoredSoldier(jumpLeftImage, team);
		}
	}

	public boolean isJumpMode() {
		return Math.abs(speedY - GRAVITY) > 0.001;
	}

	public boolean isRunMode() {
		return tickCounter < 20;
	}

	public void runRight() {
		tickCounter = 0;
		isRightTurn = true;
		getTarget().ChangeDirection(1);
		int jumpIncrease = isJumpMode() ? 1 : 0;
		move(SPEED + jumpIncrease, 0);
		runLeftIndex = 0; // reset left index
		image = ImageHelper.getColoredSoldier(runRightImages.get(runRightIndex), team);
		runRightIndex = (runRightIndex + 1) % runRightImages.size();
	}

	public void runLeft() {
		tickCounter = 0;
		isRightTurn = false;
		getTarget().ChangeDirection(-1);
		int jumpIncrease = isJumpMode() ? -1 : 0;
		move(-SPEED + jumpIncrease, 0);
		runRightIndex = 0; // reset right index
		image = ImageHelper.getColoredSoldier(runLeftImages.get(runLeftIndex), team);
		runLeftIndex = (runLeftIndex + 1) % runLeftImages.size();
	}

	private void loadDeadImages() {
		deadRightImages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			deadRightImages.add(ImageHelper
					.resizeImage(Util.loadImage("resources/soldier_dead/soldier_right" + i + ".png"), width + 15, height + 15));
		}
		deadRightIndex = 0;

		deadLeftImages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			deadLeftImages.add(ImageHelper
					.resizeImage(Util.loadImage("resources/soldier_dead/soldier_left" + i + ".png"), width + 15, height + 15));
		}
		deadLeftIndex = 0;
	}

	private void loadHurtImages() {
		hurtRightImages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			hurtRightImages.add(ImageHelper
					.resizeImage(Util.loadImage("resources/soldier_hurt/soldier_right" + i + ".png"), width, height));
		}
		hurtRightIndex = 0;

		hurtLeftImages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			hurtLeftImages.add(ImageHelper
					.resizeImage(Util.loadImage("resources/soldier_hurt/soldier_left" + i + ".png"), width, height));
		}
		hurtLeftIndex = 0;
	}

	private void loadIdleImages() {
		idleRightImages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			idleRightImages.add(ImageHelper
					.resizeImage(Util.loadImage("resources/soldier_idle/soldier_right" + i + ".png"), width, height));
		}
		idleRightIndex = 0;

		idleLeftImages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			idleLeftImages.add(ImageHelper
					.resizeImage(Util.loadImage("resources/soldier_idle/soldier_left" + i + ".png"), width, height));
		}
		idleLeftIndex = 0;
	}

	private void loadRunImages() {
		runRightImages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			runRightImages.add(ImageHelper
					.resizeImage(Util.loadImage("resources/soldier_run/soldier_right" + i + ".png"), width, height));
		}
		runRightIndex = 0;

		runLeftImages = new ArrayList<>();
		for (int i = 0; i < 10; i++) {
			runLeftImages.add(ImageHelper.resizeImage(Util.loadImage("resources/soldier_run/soldier_left" + i + ".png"),
					width, height));
		}
		runLeftIndex = 0;
	}

	private void loadJumpImages() {
		jumpRightImage = ImageHelper.getColoredSoldier(
				ImageHelper.resizeImage(Util.loadImage("resources/soldier_jump/soldier_right.png"), width, height),
				team);
		jumpLeftImage = ImageHelper.getColoredSoldier(
				ImageHelper.resizeImage(Util.loadImage("resources/soldier_jump/soldier_left.png"), width, height),
				team);
	}

	public boolean isShotFired() {
		return isShotFired;
	}

	public void fire() {
		bullet = new Bullet(posX, posY, target.getAngle(), isRightTurn);
		isShotFired = true;
	}

	public void keyPressed(int key) {
		if (key == KeyEvent.VK_ENTER) {
			if (!isJumpMode() && !isRunMode()) {
				fire();
			}
		}

		if (key == KeyEvent.VK_LEFT) {
			runLeft();
		}

		if (key == KeyEvent.VK_RIGHT) {
			runRight();
		}

		if (key == KeyEvent.VK_SPACE) {
			jump();
		}
		if (key == KeyEvent.VK_UP) {
			target.changeAngle(1);
			// changeAngle();
		}

		if (key == KeyEvent.VK_DOWN) {
			target.changeAngle(-1);
		}
	}

	public void keyReleased(int key) {
//		if (key == KeyEvent.VK_LEFT) {
//			dx = 0;
//		}
//
//		if (key == KeyEvent.VK_RIGHT) {
//			dx = 0;
//		}
//
//		if (key == KeyEvent.VK_UP) {
//			dy = 0;
//		}
//
//		if (key == KeyEvent.VK_DOWN) {
//			dy = 0;
//		}
	}

	public Target getTarget() {
		return target;
	}

	public boolean isActive() {
		return isActive;
	}

	public void setActive(boolean isActive) {
		if (!isActive) {
			target.setAngle(0);
		}
		this.isActive = isActive;
	}

	public AffineTransform getTransform() {
		return transform;
	}

	public void setTransform(AffineTransform transform) {
		this.transform = transform;
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}

	public double getSpeedX() {
		return speedX;
	}

	public double getSpeedY() {
		return speedY;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public int getHealth() {
		return health;
	}

	public Rectangle getRectangle() {
		return new Rectangle((int) posX, (int) posY, width, height);
	}

	public Bullet getBullet() {
		return bullet;
	}

	public boolean isHurt() {
		return isHurt;
	}

	public void setHurt(boolean isHurt) {
		hurtCounter = 0;
		this.isHurt = isHurt;
	}

	public boolean isRightTurn() {
		return isRightTurn;
	}

	public void setRightTurn(boolean isRightTurn) {
		this.isRightTurn = isRightTurn;
	}

	public void setHealth(int health) {
		this.health = health;
		if (health <= 0) {
			isDead = true;
		}
	}

	public boolean isVisible() {
		return isVisible;
	}

}

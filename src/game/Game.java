package game;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.plaf.synth.SynthSpinnerUI;
import helper.Constants;
import helper.ImageHelper;
import rafgfxlib.GameFrame;
import rafgfxlib.ImageViewer;
import rafgfxlib.Util;

public class Game extends GameFrame {
	public static List<Block> blocks = new ArrayList<>();
	public static List<BufferedImage> skyPictures = new ArrayList<>();
	public static Player player1;
	public static Player player2;
	private BufferedImage sky;
	private BufferedImage mountain;
	private BufferedImage ground;
	private int updates;
	private int point;
	public static boolean gameOver = false;
	private int gameOverCounter = 0;
	private BufferedImage gameOverSnapshot = null;
	private BufferedImage gameOverImage = null;

	// Player that has the turn
	public static int currentPlayer = 1;

	private static final int PARTICLE_MAX = 10000;
	private Particle[] parts = new Particle[PARTICLE_MAX];

	public Game() {
		super("Super Lima", Constants.FRAME_WIDTH, Constants.FRAME_HEIGHT);
		setFocusTraversalKeysEnabled(false);
		setFocusable(true);

		setUpdateRate(60);
		startThread();

	}

	public static void main(String[] args) {
		new Game().initGameWindow();
	}

	@Override
	public void handleWindowInit() {
		System.out.println("poceo init");

		sky = (BufferedImage) ImageHelper.resizeImage(Util.loadImage("resources/background/sky.png"), getWidth(),
				getHeight());
		mountain = (BufferedImage) ImageHelper.resizeImage(Util.loadImage("resources/background/mountain.png"),
				getWidth(), getHeight());
		ground = (BufferedImage) ImageHelper.resizeImage(Util.loadImage("resources/background/ground.png"), getWidth(),
				getHeight());

		for (int i = 0; i < PARTICLE_MAX; ++i)
			parts[i] = new Particle();

		boolean oneEnd = false;

		for (int i = 0; i < 100; i++) {
			int distance = 0;
			if (i == 15) {
				point = Math.abs(point - sky.getWidth());
				oneEnd = true;
			}

			if (point == 0)
				distance = +6;
			else
				distance = -6;

			sky = ImageHelper.moveWhiteOnImage(sky, distance);
			if (i == 15)
				break;

		}

		int doubleEnd = 0;
		for (int i = 0; i < 60; i++) {
			int distance = 0;
			if (i == 27 || i == 55) {
				point = Math.abs(point - sky.getWidth());
				doubleEnd++;
			}

			if (point == 0)
				distance = +6;
			else
				distance = -6;

			sky = ImageHelper.moveWhiteOnImage(sky, distance);
			skyPictures.add(sky);
		}

		int numberOfLevels = 4;

		while (numberOfLevels > 0) {

			Random rnd = new Random();
			int numberOfBlocks = rnd.nextInt(3) + numberOfLevels + 1;

			int currentHeight = Constants.FRAME_HEIGHT - (numberOfLevels * 180);

			int jumpX = Constants.FRAME_WIDTH / (numberOfBlocks + 1);
			int lastX = -50;

			while (numberOfBlocks > 0) {
				int blockY = rnd.nextInt(10) + currentHeight;
				if (numberOfLevels == 1)
					blockY = Constants.FRAME_HEIGHT - 167;
				int blockX = rnd.nextInt(jumpX) + lastX + 130;
				lastX = blockX;
				Block block = rnd.nextInt(3) == 0 ? new MetalBlock(blockX, blockY) : new WoodBlock(blockX, blockY);
				blocks.add(block);
				numberOfBlocks--;
			}
			numberOfLevels--;
		}

		player1 = new Player("Lima", 2, 1);
		player2 = new Player("Fulu", 2, 2);

		player1.getSoldiers().get(0).setActive(true);

		System.out.println("zavrsio init");
	}

	@Override
	public void handleWindowDestroy() {
		// TODO Auto-generated method stub
	}

	@Override
	public void render(Graphics2D g, int sw, int sh) {
		if (gameOver) {
			g.drawImage(gameOverSnapshot, 0, 0, null);
			if (updates % 200 <= 100) {
				BufferedImage gOverImg = ImageHelper.clearGameOver(gameOverImage, 0);
				g.drawImage(gOverImg, getWidth() / 2 - gOverImg.getWidth() / 2, getHeight() / 2 - gOverImg.getHeight() / 2, null);
			} else {
				BufferedImage gOverImg = ImageHelper.clearGameOver(gameOverImage, 1);
				g.drawImage(gOverImg, getWidth() / 2 - gOverImg.getWidth() / 2, getHeight() / 2 - gOverImg.getHeight() / 2, null);
			}
		} else {

			// Drawing the background
			g.drawImage(sky, 0, 0, null);
			g.drawImage(mountain, 0, 0, null);
			g.drawImage(ground, 0, 0, null);

			// Draw blocks
			for (Block b : blocks) {
				if (b.isVisible()) {
					g.drawImage(b.getImage(), b.getPosX(), b.getPosY(), null);
				}

				if (b.getParticlesLife() > 0) {
					g.setColor(Color.black);

					for (Particle p : parts) {
						if (p.life <= 0)
							continue;

						g.drawLine((int) (p.posX - p.dX), (int) (p.posY - p.dY), (int) p.posX, (int) p.posY);
					}
				}
			}

			// Drawing player1 soldiers
			for (Soldier s : player1.getSoldiers()) {
				int value = s.getHealth();
				BufferedImage healthImage = ImageHelper.CreateHealthBar(value);
				AffineTransform transformation = new AffineTransform();
				int healthX = 0;
				if (s.isRightTurn())
					healthX = (int) (s.getPosX() + 5);
				else
					healthX = (int) (s.getPosX() + 30);
				transformation.translate(healthX, s.getPosY() - 20);

				if (s.isVisible()) {
					g.drawImage(healthImage, transformation, null);
					g.drawImage(s.getImage(), s.getTransform(), null);
				}
			}

			// Drawing player 2 soldiers
			for (Soldier s : player2.getSoldiers()) {
				int value = s.getHealth();
				BufferedImage healthImage = ImageHelper.CreateHealthBar(value);
				AffineTransform transformation = new AffineTransform();
				int healthX = 0;
				if (s.isRightTurn())
					healthX = (int) (s.getPosX() + 5);
				else
					healthX = (int) (s.getPosX() + 30);
				transformation.translate(healthX, s.getPosY() - 20);

				if (s.isVisible()) {
					g.drawImage(healthImage, transformation, null);
					g.drawImage(s.getImage(), s.getTransform(), null);
				}
			}

			for (Soldier s : player1.getSoldiers()) {
				if (s.isShotMode()) {
					Target t = s.getTarget();
					g.drawImage(t.getImage(), t.getTransform(), null);

					if (s.isShotFired()) {
						Bullet b = s.getBullet();
						if (b.isVisible()) {
							g.drawImage(b.getImage(), b.getTransform(), null);
						}
					}
				}
			}

			for (Soldier s : player2.getSoldiers()) {
				if (s.isShotMode()) {
					Target t = s.getTarget();
					g.drawImage(t.getImage(), t.getTransform(), null);

					if (s.isShotFired()) {
						Bullet b = s.getBullet();
						if (b.isVisible()) {
							g.drawImage(b.getImage(), b.getTransform(), null);
						}
					}
				}
			}
		}
	}

	@Override
	public void update() {
		updates++;

		if (updates % 100 == 0) {
			int idx = updates / 100;
			sky = skyPictures.get(idx % skyPictures.size());
		}

		for (Block b : blocks) {
			if (b.getParticlesLife() == 150) {

				genEx(b.getPosX() + b.getWidth() / 2, b.getPosY() + b.getHeight() / 2, 3f, 1000, 20);
				b.setParticlesLife(b.getParticlesLife() - 1);
			} else if (b.getParticlesLife() > 0) {

				for (Particle p : parts) {
					if (p.life <= 0)
						continue;

					int q = 0;
					p.life--;
					p.posX += p.dX;
					p.posY += p.dY;
					p.dX *= 0.99f;
					p.dY *= 0.99f;
					p.dY += 0.1f;

					if (p.posX < 0) {
						p.posX = 0.01f;
						p.dX = Math.abs(p.dX) * (float) Math.random() * 0.6f;
					}

					if (p.posY < 0) {
						p.posY = 0.01f;
						p.dY = Math.abs(p.dY) * (float) Math.random() * 0.6f;
					}

					if (p.posX > getWidth()) {
						p.posX = getWidth() - 0.01f;
						p.dX = Math.abs(p.dX) * (float) Math.random() * -0.6f;
					}

					if (p.posY > getHeight()) {
						p.posY = getHeight() - 0.01f;
						p.dY = Math.abs(p.dY) * (float) Math.random() * -0.6f;
					}
					b.setParticlesLife(b.getParticlesLife() - 1);
				}
			}
		}

		int activeSoldiers1 = 0;

		// Move player1 soldiers
		for (Soldier s : player1.getSoldiers()) {
			if (s.getHealth() > 0) {
				activeSoldiers1++;
			}
			s.update();
		}

		int activeSoldiers2 = 0;

		// Move player 2 soldiers
		for (Soldier s : player2.getSoldiers()) {
			if (s.getHealth() > 0) {
				activeSoldiers2++;
			}
			s.update();
		}

		if (activeSoldiers1 == 0) {
			// Winner is player2
			gameOverCounter++;
			if (gameOverCounter > 120 && !gameOver) {
				gameOverSnapshot = (BufferedImage) createImage(getWidth(), getHeight());
				Graphics2D g2 = gameOverSnapshot.createGraphics();
				takeSnapshot(g2, getWidth(), getHeight());
				gameOverSnapshot = ImageHelper.blurImage(gameOverSnapshot);
				gameOverImage = Util.loadImage("resources/gameover.png");
				gameOver = true;
			}
		}
		if (activeSoldiers2 == 0) {
			// Winner is player1
			gameOverCounter++;
			if (gameOverCounter > 120 && !gameOver) {
				gameOverSnapshot = (BufferedImage) createImage(getWidth(), getHeight());
				Graphics2D g2 = gameOverSnapshot.createGraphics();
				takeSnapshot(g2, getWidth(), getHeight());
				gameOverSnapshot = ImageHelper.blurImage(gameOverSnapshot);
				gameOverImage = Util.loadImage("resources/gameover.png");
				gameOver = true;
			}
		}
	}

	@Override
	public void handleKeyDown(int keyCode) {
		if (keyCode == KeyEvent.VK_TAB) {
			Player player = currentPlayer == 1 ? player1 : player2;
			int currentSoldier = player.getCurrentSoldier();
			player.getSoldiers().get(currentSoldier).setActive(false);
			currentSoldier = (currentSoldier + 1) % player.getSoldiers().size();
			player.setCurrentSoldier(currentSoldier);
			player.getSoldiers().get(currentSoldier).setActive(true);
		} else if (keyCode == KeyEvent.VK_P) {
			System.out.println("usao");
			BufferedImage snapshot = (BufferedImage) createImage(getWidth(), getHeight());
			Graphics2D g2 = snapshot.createGraphics();
			render(g2, getWidth(), getHeight());
			ImageViewer.showImageWindow(snapshot, "Soldier without gun");
		} else {
			Player player = currentPlayer == 1 ? player1 : player2;
			player.getSoldiers().get(player.getCurrentSoldier()).keyPressed(keyCode);
		}
	}

	private void genEx(float cX, float cY, float radius, int life, int count) {
		for (int i = 0; i < PARTICLE_MAX; ++i)
			parts[i] = new Particle();

		for (Particle p : parts) {
			if (p.life <= 0) {
				p.life = (int) (Math.random() * life * 0.5) + life / 2;
				p.posX = cX;
				p.posY = cY;
				double angle = Math.random() * Math.PI * 2.0;
				double speed = Math.random() * radius;
				p.dX = (float) (Math.cos(angle) * speed);
				p.dY = (float) (Math.sin(angle) * speed);

				count--;
				if (count <= 0)
					return;
			}
		}
	}

	@Override
	public void handleKeyUp(int keyCode) {
		Player player = currentPlayer == 1 ? player1 : player2;
		player.getSoldiers().get(player.getCurrentSoldier()).keyReleased(keyCode);
	}

	@Override
	public void handleMouseDown(int x, int y, GFMouseButton button) {
	}

	@Override
	public void handleMouseUp(int x, int y, GFMouseButton button) {
	}

	@Override
	public void handleMouseMove(int x, int y) {
	}

	public static void changeTurn() {
		Player player = currentPlayer == 1 ? player1 : player2;
		player.getSoldiers().get(player.getCurrentSoldier()).setActive(false);

		currentPlayer = currentPlayer == 1 ? 2 : 1;

		player = currentPlayer == 1 ? player1 : player2;
		player.getSoldiers().get(player.getCurrentSoldier()).setActive(true);
	}

	private void takeSnapshot(Graphics2D g, int sw, int sh) {
		// Drawing the background
		g.drawImage(sky, 0, 0, null);
		g.drawImage(mountain, 0, 0, null);
		g.drawImage(ground, 0, 0, null);

		// Draw blocks
		for (Block b : blocks) {
			if (b.isVisible()) {
				g.drawImage(b.getImage(), b.getPosX(), b.getPosY(), null);
			}

			if (b.getParticlesLife() > 0) {
				g.setColor(Color.black);

				for (Particle p : parts) {
					if (p.life <= 0)
						continue;

					g.drawLine((int) (p.posX - p.dX), (int) (p.posY - p.dY), (int) p.posX, (int) p.posY);
				}
			}
		}

		// Drawing player1 soldiers
		for (Soldier s : player1.getSoldiers()) {
			int value = s.getHealth();
			BufferedImage healthImage = ImageHelper.CreateHealthBar(value);
			AffineTransform transformation = new AffineTransform();
			int healthX = 0;
			if (s.isRightTurn())
				healthX = (int) (s.getPosX() + 5);
			else
				healthX = (int) (s.getPosX() + 30);
			transformation.translate(healthX, s.getPosY() - 20);

			if (s.isVisible()) {
				g.drawImage(healthImage, transformation, null);
				g.drawImage(s.getImage(), s.getTransform(), null);
			}
		}

		// Drawing player 2 soldiers
		for (Soldier s : player2.getSoldiers()) {
			int value = s.getHealth();
			BufferedImage healthImage = ImageHelper.CreateHealthBar(value);
			AffineTransform transformation = new AffineTransform();
			int healthX = 0;
			if (s.isRightTurn())
				healthX = (int) (s.getPosX() + 5);
			else
				healthX = (int) (s.getPosX() + 30);
			transformation.translate(healthX, s.getPosY() - 20);

			if (s.isVisible()) {
				g.drawImage(healthImage, transformation, null);
				g.drawImage(s.getImage(), s.getTransform(), null);
			}
		}

		for (Soldier s : player1.getSoldiers()) {
			if (s.isShotMode()) {
				Target t = s.getTarget();
				g.drawImage(t.getImage(), t.getTransform(), null);

				if (s.isShotFired()) {
					Bullet b = s.getBullet();
					if (b.isVisible()) {
						g.drawImage(b.getImage(), b.getTransform(), null);
					}
				}
			}
		}

		for (Soldier s : player2.getSoldiers()) {
			if (s.isShotMode()) {
				Target t = s.getTarget();
				g.drawImage(t.getImage(), t.getTransform(), null);

				if (s.isShotFired()) {
					Bullet b = s.getBullet();
					if (b.isVisible()) {
						g.drawImage(b.getImage(), b.getTransform(), null);
					}
				}
			}
		}
	}
}
package helper;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

import rafgfxlib.ImageViewer;
import rafgfxlib.Util;

public class ImageHelper {

	public static BufferedImage flipHorizontally(BufferedImage image) {
		BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
		for (int x = image.getWidth() - 1; x > 0; x--) {
			for (int y = 0; y < image.getHeight(); y++) {
				img.setRGB(image.getWidth() - x, y, image.getRGB(x, y));
			}
		}
		return img;
	}

	public static BufferedImage blurImage(BufferedImage image) {

		WritableRaster source = image.getRaster();
		WritableRaster target = Util.createRaster(image.getWidth(), image.getHeight(), false);

		int distance = 7;

		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight(); j++) {

				int red = 0;
				int green = 0;
				int blue = 0;
				int pixelCount = 0;
				int rgb[] = new int[5];
				for (int l = Math.max(0, i - distance); l < Math.min(image.getWidth(), i + distance); l++)
					for (int k = Math.max(0, j - distance); k < Math.min(image.getHeight(), j + distance); k++) {
						pixelCount++;
						source.getPixel(l, k, rgb);
						red += rgb[0];
						green += rgb[1];
						blue += rgb[2];
					}
				rgb[0] = red / pixelCount;
				rgb[1] = green / pixelCount;
				rgb[2] = blue / pixelCount;
				target.setPixel(i, j, rgb);
			}

		return Util.rasterToImage(target);
	}

	public static int WhiteDistanceOnImage(BufferedImage image, int point) {

		WritableRaster source = image.getRaster();
		WritableRaster target = Util.createRaster(image.getWidth(), image.getHeight(), false);

		int distance = 10000;
		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight() / 2; j++) {
				int rgb[] = new int[5];
				source.getPixel(i, j, rgb);

				if ((rgb[0] == 255 && rgb[1] == 255 && rgb[2] == 255)) {
					distance = Math.min(distance, Math.abs(point - i));
				}
			}

		return distance;

	}

	public static BufferedImage moveWhiteOnImage(BufferedImage image, int distance) {

		WritableRaster source = image.getRaster();
		WritableRaster target = Util.createRaster(image.getWidth(), image.getHeight(), false);

		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight(); j++) {
				int rgb[] = new int[5];
				source.getPixel(i, j, rgb);
				if (i - distance >= 0 && i - distance < image.getWidth()) {
					target.setPixel(i - distance, j, rgb);
				}
			}

		if (distance < 0) {
			distance *= -1;
			for (int i = 0; i < distance; i++)
				for (int j = 0; j < image.getHeight(); j++) {
					int rgb[] = new int[4];
					source.getPixel(image.getWidth() - i - 1, j, rgb);
					target.setPixel(i, j, rgb);
				}
		} else {
			for (int i = 0; i < distance; i++)
				for (int j = 0; j < image.getHeight(); j++) {
					int rgb[] = new int[4];
					source.getPixel(i, j, rgb);
					target.setPixel(image.getWidth() - i - 1, j, rgb);
				}
		}

		return Util.rasterToImage(target);

	}

	public static BufferedImage CreateHealthBar(int value) {

		WritableRaster target = Util.createRaster(Constants.HEALTH_BAR_WIDTH, Constants.HEALTH_BAR_HEIGHT, false);

		int color[] = new int[3];

		if (value <= 20) {
			color[0] = 255;
		} else if (value <= 50) {
			color[1] = 255;
			color[0] = 255;
		} else {
			color[1] = 255;
		}

		for (int i = 0; i < Constants.HEALTH_BAR_WIDTH; i++)
			for (int j = 0; j < Constants.HEALTH_BAR_HEIGHT; j++) {
				int rgb[] = new int[5];

				if (i <= 1 || Constants.HEALTH_BAR_WIDTH - i <= 2 || j <= 1 || Constants.HEALTH_BAR_WIDTH - j <= 2) {
					rgb[0] = 0;
					rgb[1] = 0;
					rgb[2] = 0;
					target.setPixel(i, j, rgb);
				} else if ((i * 100 / Constants.HEALTH_BAR_WIDTH <= value)) {
					target.setPixel(i, j, color);
				}

			}

		return Util.rasterToImage(target);

	}

	public static BufferedImage resizeImage(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();

		return dimg;
	}

	public static BufferedImage eraseGun(BufferedImage image) {
		BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int value = image.getRGB(i, j);

				int alpha = (value >> 24) & 0xff;
				int red = (value >> 16) & 0xff;
				int green = (value >> 8) & 0xff;
				int blue = (value) & 0xff;

				if (red == 168 && green == 75 && blue == 42) {
					output.setRGB(i, j, (new Color(0.0f, 0.0f, 0.0f, 0.0f)).getRGB());
				} else if (red > 150 && green == 72 && blue == 42) {
					output.setRGB(i, j, (new Color(0.0f, 0.0f, 0.0f, 0.0f)).getRGB());
				} else if (red == 140 && green == 67 && blue == 41) {
					output.setRGB(i, j, (new Color(0.0f, 0.0f, 0.0f, 0.0f)).getRGB());
				} else if (red == 93 && green == 53 && blue == 39) {
					output.setRGB(i, j, (new Color(0.0f, 0.0f, 0.0f, 0.0f)).getRGB());
				} else if (red == 135 && green == 59 && blue == 33) {
					output.setRGB(i, j, (new Color(0.0f, 0.0f, 0.0f, 0.0f)).getRGB());
				} else if (red == 51 && green == 51 && blue == 51 && j < 350) {
					output.setRGB(i, j, (new Color(0.0f, 0.0f, 0.0f, 0.0f)).getRGB());
				} else if (red == 38 && green == 38 && blue == 38) {
					output.setRGB(i, j, (new Color(0.0f, 0.0f, 0.0f, 0.0f)).getRGB());
				} else if (red == 64 && green == 64 && blue == 64 && j < 300) {
					output.setRGB(i, j, (new Color(0.0f, 0.0f, 0.0f, 0.0f)).getRGB());
				} else {
					output.setRGB(i, j, image.getRGB(i, j));
				}
			}
		}

		return output;
	}

	public static BufferedImage makeTransparentBackground(BufferedImage image) {
		BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < image.getWidth(); i++) {
			for (int j = 0; j < image.getHeight(); j++) {
				int value = image.getRGB(i, j);

				int alpha = (value >> 24) & 0xff;
				int red = (value >> 16) & 0xff;
				int green = (value >> 8) & 0xff;
				int blue = (value) & 0xff;

				if (red >= 190 && green >= 190 && blue >= 190) {
					output.setRGB(i, j, (new Color(0.0f, 0.0f, 0.0f, 0.0f)).getRGB());
				} else {
					output.setRGB(i, j, image.getRGB(i, j));
				}
			}
		}

		return output;
	}

	public static BufferedImage changeGreenColor(BufferedImage image, int[] newColor) {
		BufferedImage img = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int x = image.getWidth() - 1; x > 0; x--) {
			for (int y = 0; y < image.getHeight(); y++) {
				int value = image.getRGB(x, y);

				int alpha = (value >> 24) & 0xff;
				int red = (value >> 16) & 0xff;
				int green = (value >> 8) & 0xff;
				int blue = (value) & 0xff;

				if (red == 110 && green == 125 && blue == 66) {
					img.setRGB(x, y, (newColor[0] << 16) + (newColor[1] << 8) + (newColor[2]) + (alpha << 24));
				} else if (y < 34 && (!(red >= 25 && red <= 45) || !(green >= 25 && green <= 45)
						|| !(blue >= 25 && blue <= 45))) {
					img.setRGB(x, y, (newColor[0] << 16) + (newColor[1] << 8) + (newColor[2]) + (alpha << 24));
				} else if (red == 78 && green == 89 && blue == 47) {
					img.setRGB(x, y, (newColor[0] << 16) + (newColor[1] << 8) + (newColor[2]) + (alpha << 24));
				} else if (red == 141 && green == 73 && blue == 38) {
					img.setRGB(x, y, (newColor[0] << 16) + (newColor[1] << 8) + (newColor[2]) + (alpha << 24));
				} else if (red == 127 && green == 91 && blue == 47) {
					img.setRGB(x, y, (newColor[0] << 16) + (newColor[1] << 8) + (newColor[2]) + (alpha << 24));
				} else if (red >= 110 && red <= 140 && green >= 110 && green <= 140) {
					img.setRGB(x, y, (newColor[0] << 16) + (newColor[1] << 8) + (newColor[2]) + (alpha << 24));
				} else if (red == 157 && green == 179 && blue == 94) {
					img.setRGB(x, y, (newColor[0] << 16) + (newColor[1] << 8) + (newColor[2]) + (alpha << 24));
				} else {
					img.setRGB(x, y, value);
				}
			}
		}
		return img;
	}

	public static BufferedImage getRedSoldier(BufferedImage image) {
		int redArray[] = { 255, 0, 0 };

		BufferedImage redImage = changeGreenColor(image, redArray);
		return redImage;
	}

	public static BufferedImage getBlueSoldier(BufferedImage image) {
		int blueArray[] = { 0, 0, 255 };
		int myGreenArray[] = { 110, 125, 66 };
		int myGreen2Array[] = { 78, 89, 47 };
		int myGreen3Array[] = { 157, 179, 94 };

		BufferedImage redImage = changeGreenColor(image, blueArray);
		return redImage;
	}

	public static BufferedImage clearGameOver(BufferedImage image, int value) {
		BufferedImage output = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);

		for (int i = 0; i < image.getWidth(); i++)
			for (int j = 0; j < image.getHeight(); j++) {
				int rgb = image.getRGB(i, j);

				int alpha = (rgb >> 24) & 0xff;
				int red = (rgb >> 16) & 0xff;
				int green = (rgb >> 8) & 0xff;
				int blue = (rgb) & 0xff;

				if (j / 173 == value) {
					output.setRGB(i, j, (new Color(0.0f, 0.0f, 0.0f, 0.0f)).getRGB());
				} else {
					output.setRGB(i, j, image.getRGB(i, j));
				}
			}

		return output;
	}

	public static BufferedImage getColoredSoldier(BufferedImage image, int team) {
		return team == 1 ? getRedSoldier(image) : getBlueSoldier(image);
	}

	public static void main(String[] args) {
//		for (int i = 0; i < 10; i++) {
//			BufferedImage image = Util.loadImage("resources/soldier_dead/soldier_right" + i + ".png");
//			BufferedImage flipped = flipHorizontally(image);
//			try {
//				File outputfile = new File("soldier_left" + i + ".png");
//			    ImageIO.write(flipped, "png", outputfile);
//			} catch (Exception e) {
//				
//			}
//		}

//		BufferedImage image = Util.loadImage("resources/soldier_jump/soldier_right.png");
//		BufferedImage flipped = flipHorizontally(image);
//		try {
//			File outputfile = new File("soldier_left1.png");
//		    ImageIO.write(flipped, "png", outputfile);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		int redArray[] = { 255, 0, 0 };
//		int blueArray[] = { 0,0, 255 };
//		int myGreenArray[] = { 110, 125, 66 };
//		int myGreen2Array[] = { 78, 89, 47 };
//		int myGreen3Array[] = { 157, 179, 94 };
//		BufferedImage image = Util.loadImage("resources/soldier_idle/soldier_right2.png");
//		
//		BufferedImage redImage = changeGreenColor(image, redArray);
//		ImageViewer.showImageWindow(redImage, "allllekssssa");

//		try {
//			File outputfile = new File("red_soldier.png");
//		    ImageIO.write(redImage, "png", outputfile);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		BufferedImage blueImage = changeColor(image, myGreenArray, myGreen2Array, myGreen3Array, blueArray);
//		try {
//			File outputfile = new File("blue_soldier.png");
//		    ImageIO.write(blueImage, "png", outputfile);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

//		BufferedImage image = Util.loadImage("resources/soldier.png");
//		BufferedImage blurImage = blurImage(image);
//		ImageViewer.showImageWindow(blurImage, "Blur Image");
		// BufferedImage image1=CreateHealthBar(70);
		// ImageViewer.showImageWindow(image1, "allllekssssa");
//
//		
		BufferedImage image = Util.loadImage("resources/gameover.png");
		BufferedImage gameover = makeTransparentBackground(image);

		BufferedImage upOver = clearGameOver(gameover, 0);
		BufferedImage downOver = clearGameOver(gameover, 1);

		ImageViewer.showImageWindow(downOver, "Soldier without gun");
		try {
			File outputfile = new File("gameover.png");
			ImageIO.write(gameover, "png", outputfile);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
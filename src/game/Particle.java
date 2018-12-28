package game;

import java.awt.Color;
import java.awt.Graphics2D;

import rafgfxlib.GameFrame;

	public class Particle
	{
		public float posX;
		public float posY;
		public float dX;
		public float dY;
		public int life = 0;
		public float getPosX() {
			return posX;
		}
		public void setPosX(float posX) {
			this.posX = posX;
		}
		public float getPosY() {
			return posY;
		}
		public void setPosY(float posY) {
			this.posY = posY;
		}
		public float getdX() {
			return dX;
		}
		public void setdX(float dX) {
			this.dX = dX;
		}
		public float getdY() {
			return dY;
		}
		public void setdY(float dY) {
			this.dY = dY;
		}
		public int getLife() {
			return life;
		}
		public void setLife(int life) {
			this.life = life;
		}
		
		
	}


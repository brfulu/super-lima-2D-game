package game;

public class WoodBlock extends Block {

	public WoodBlock(int posX, int posY) {
		super(posX, posY, "wood.png");
	}

	@Override
	public boolean isDestructible() {
		return true;
	}

}

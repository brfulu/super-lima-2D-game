package game;

public class MetalBlock extends Block {

	public MetalBlock(int posX, int posY) {
		super(posX, posY, "metal.png");
	}

	@Override
	public boolean isDestructible() {
		return false;
	}

}

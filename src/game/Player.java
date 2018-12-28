package game;

import java.util.ArrayList;
import java.util.List;

import helper.Constants;

public class Player {
	private String name;
	private List<Soldier> soldiers;
	private int team;
	private int currentSoldier = 0;
	
	public Player(String name, int soldierCount, int team) {
		this.name = name;
		this.soldiers = new ArrayList<>();
		this.team = team;
		initSoldiers(soldierCount);
	}
	
	private void initSoldiers(int count) {
		for (int i = 0; i < count; i++) {
			double soldierX = (int)(Math.random() * (Constants.FRAME_WIDTH - Constants.SOLDIER_WIDTH));
			double soldierY = Constants.GROUND_Y - Constants.SOLDIER_HEIGHT;
			soldiers.add(new Soldier(soldierX, soldierY, team));
		}
	}

	public String getName() {
		return name;
	}

	public List<Soldier> getSoldiers() {
		return soldiers;
	}

	public int getTeam() {
		return team;
	}

	public int getCurrentSoldier() {
		return currentSoldier;
	}

	public void setCurrentSoldier(int currentSoldier) {
		this.currentSoldier = currentSoldier;
	}
	
	
}

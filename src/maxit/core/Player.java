package maxit.core;

public abstract class Player {

	private final String name;

	private int score = 0;

	public Player(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void addPoints(int points) {
		this.score += points;
	}

	public int getScore() {
		return score;
	}

	public abstract Position move(Maxit game);

}

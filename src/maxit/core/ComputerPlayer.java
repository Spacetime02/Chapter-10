package maxit.core;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ComputerPlayer extends Player {

	private static final String[] NAMES = loadNames();

	private static final Random nameRandy = new Random();

	private static String[] loadNames() {
		try (Scanner nameScanner = new Scanner(new File("MAXIT names.txt"))) {
			List<String> nameList = new ArrayList<>();
			while (nameScanner.hasNext())
				nameList.add(nameScanner.next());
			return nameList.toArray(new String[nameList.size()]);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return new String[] { "ERR_NAME_LOAD_FAILED" };
		}
	}

	private static String getRandomName() {
		return NAMES[nameRandy.nextInt(NAMES.length)];
	}

	public ComputerPlayer() {
		super(getRandomName());
	}

	@Override
	public Position move(Maxit game) {
		return null;
	}

}

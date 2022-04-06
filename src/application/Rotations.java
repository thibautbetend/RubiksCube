package application;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author tbetend, mai/juin 2020
 */

public class Rotations {

	/*
	 * Each of the 27 cubies are stored as a volume in a three dimensional array of
	 * indexes These indexes are related to number in the name of the block of the
	 * 3D model, as they are marked as "Block46", "Block46 (2)",...,"Block72 (6)"
	 * (see Model3D)
	 * 
	 * The initial position refers to: (U)up White, (F)front Blue, (R)right Green,
	 * (L)left Red, (D)down Yellow, (B)back Orange - first 9 indexes are the 9
	 * cubies in (F)Front face, from top left (R/W/B) to down right (Y/O/B) - second
	 * 9 indexes are from (S)Standing, from top left (R/W) to down right (Y/O) -
	 * last 9 indexes are from (B)Back, from top left (G/R/W) to down right (G/Y/O)
	 */

	private final int[][][] cube = { { { 50, 51, 52 }, { 49, 54, 53 }, { 59, 48, 46 } },
			{ { 58, 55, 60 }, { 57, 62, 61 }, { 47, 56, 63 } },
			{ { 67, 64, 69 }, { 66, 71, 70 }, { 68, 65, 72 } } };
	
	private final String[][][] color = {
			{ { "50 B" }, { "51 B" }, { "52 B" }, { "49 B" }, { "54 B" }, { "53 B" }, { "59 B" }, { "48 B" },
					{ "46 B" } },
			{ { "50 W" }, { "51 W" }, { "52 W" }, { "58 W" }, { "55 W" }, { "60 W" }, { "67 W" }, { "64 W" },
					{ "69 W" } },
			{ { "67 G" }, { "64 G" }, { "69 G" }, { "66 G" }, { "71 G" }, { "70 G" }, { "68 G" }, { "65 G" },
					{ "72 G" } },
			{ { "59 Y" }, { "48 Y" }, { "46 Y" }, { "47 Y" }, { "56 Y" }, { "63 Y" }, { "68 Y" }, { "65 Y" },
					{ "72 Y" } },
			{ { "50 R" }, { "49 R" }, { "59 R" }, { "58 R" }, { "57 R" }, { "47 R" }, { "67 R" }, { "66 R" },
					{ "68 R" } },
			{ { "52 O" }, { "53 O" }, { "46 O" }, { "60 O" }, { "61 O" }, { "63 O" }, { "69 O" }, { "70 O" },
					{ "72 O" } } };
	private final String[][][] tempColor = new String[6][9][1];
	private final int[][][] tempCube = new int[3][3][3];

	public Rotations() {
		for (int f = 0; f < 3; f++) {
			for (int l = 0; l < 3; l++) {
				System.arraycopy(cube[f][l], 0, tempCube[f][l], 0, 3);
			}
		}
		for (int f = 0; f < 6; f++) {
			for (int l = 0; l < 9; l++) {

				System.arraycopy(color[f][l], 0, tempColor[f][l], 0, 1);
			}
		}
	}

	/* returns 3D array as a flatten list of indexes */
	public List<Integer> getCube() {
		List<Integer> newArray = new ArrayList<>(27);
		for (int f = 0; f < 3; f++) {
			for (int l = 0; l < 3; l++) {
				for (int a = 0; a < 3; a++) {
					newArray.add(cube[f][l][a]);
				}
			}
		}
		return newArray;
	}

	public List<String> getColor() {
		List<String> newArray = new ArrayList<>(56);
		for (int f = 0; f < 6; f++) {
			for (int l = 0; l < 9; l++) {
				newArray.add(color[f][l][0]);
			}
		}
		return newArray;
	}

	public void setCube(List<Integer> order) {
		int index = 0;
		for (int f = 0; f < 3; f++) {
			for (int l = 0; l < 3; l++) {
				for (int a = 0; a < 3; a++) {
					cube[f][l][a] = order.get(index++);
					tempCube[f][l][a] = cube[f][l][a];
				}
			}
		}
	}

	public void setColor(List<String> order) {
		int index = 0;
		for (int f = 0; f < 6; f++) {
			for (int l = 0; l < 9; l++) {
				color[f][l][0] = order.get(index++);
				tempColor[f][l][0] = color[f][l][0];
			}
		}
	}

	/* copy tempcolor data in cube */
	public void save() {
		for (int f = 0; f < 3; f++) {
			for (int l = 0; l < 3; l++) {
				System.arraycopy(tempCube[f][l], 0, cube[f][l], 0, 3);
			}
		}
	}

	public void saveColor() {
		for (int f = 0; f < 6; f++) {
			for (int l = 0; l < 9; l++) {
				System.arraycopy(tempColor[f][l], 0, color[f][l], 0, 1);
			}
		}
	}

	/*
	 * print 3D array as a flatten list of indexes in groups of 9 cubies (Front -
	 * Standing - Back)
	 */
	public void printcolor() {
		List<Integer> newArray = getCube();
		for (int i = 0; i < 27; i++) {
			if (i == 9 || i == 18) {
				System.out.print(" ||");
			}
			System.out.print(" " + newArray.get(i));
		}
		System.out.println("");
	}

	/*
	 * This is the method to perform any rotation on the 3D array just by swapping
	 * indexes - first index refers to faces F-S-B - second index refers to faces
	 * U-E-D - third index refers to faces L-M-R
	 * 
	 * For notation check http://en.wikipedia.org/wiki/Rubik%27s_color For clockwise
	 * rotations Capital letters are used, for counter-clockwise rotation an "i" is
	 * appended, instead of a ' or a lower letter.
	 */
	public void turn(String rot) {
		if (rot.contains("X") || rot.contains("Y") || rot.contains("Z")) {
			for (int z = 0; z < 3; z++) {
				int t = 0;
				for (int y = 2; y >= 0; --y) {
					for (int x = 0; x < 3; x++) {
						switch (rot) {
						case "X":
							tempCube[t][x][z] = cube[x][y][z];
							break;
						case "Xi":
							tempCube[x][t][z] = cube[y][x][z];
							break;
						case "Y":
							tempCube[t][z][x] = cube[x][z][y];
							break;
						case "Yi":
							tempCube[x][z][t] = cube[y][z][x];
							break;
						case "Z":
							tempCube[z][x][t] = cube[z][y][x];
							break;
						case "Zi":
							tempCube[z][t][x] = cube[z][x][y];
							break;
						}
					}
					t++;
				}
			}
		} else {
			int t = 0;
			for (int y = 2; y >= 0; --y) {
				for (int x = 0; x < 3; x++) {
					switch (rot) {
					case "L":
						tempCube[x][t][0] = cube[y][x][0];
						break;
					case "Li":
						tempCube[t][x][0] = cube[x][y][0];
						break;
					case "M":
						tempCube[x][t][1] = cube[y][x][1];
						break;
					case "Mi":
						tempCube[t][x][1] = cube[x][y][1];
						break;
					case "R":
						tempCube[t][x][2] = cube[x][y][2];
						break;
					case "Ri":
						tempCube[x][t][2] = cube[y][x][2];
						break;
					case "U":
						tempCube[t][0][x] = cube[x][0][y];
						break;
					case "Ui":
						tempCube[x][0][t] = cube[y][0][x];
						break;
					case "E":
						tempCube[x][1][t] = cube[y][1][x];
						break;
					case "Ei":
						tempCube[t][1][x] = cube[x][1][y];
						break;
					case "D":
						tempCube[x][2][t] = cube[y][2][x];
						break;
					case "Di":
						tempCube[t][2][x] = cube[x][2][y];
						break;
					case "F":
						tempCube[0][x][t] = cube[0][y][x];
						break;
					case "Fi":
						tempCube[0][t][x] = cube[0][x][y];
						break;
					case "S":
						tempCube[1][x][t] = cube[1][y][x];
						break;
					case "Si":
						tempCube[1][t][x] = cube[1][x][y];
						break;
					case "B":
						tempCube[2][t][x] = cube[2][x][y];
						break;
					case "Bi":
						tempCube[2][x][t] = cube[2][y][x];
						break;
					}
				}
				t++;
			}
		}
		turnColor(rot);
		save();
	}

	public void turnColor(String rot) {
		int count = 0;
		if (rot.contains("X") || rot.contains("Y") || rot.contains("Z")) {
			int yneg = -1, ypos = 3;
			switch (rot) {
			case "X":
				for (int t = 0; t < 3; t++) {
					yneg++;
					while (yneg < 9) {
						tempColor[1][count][0] = color[0][count][0];
						tempColor[2][count][0] = color[1][count][0];
						tempColor[3][count][0] = color[2][count][0];
						tempColor[0][count][0] = color[3][count][0];
						tempColor[4][count][0] = color[4][yneg][0];
						tempColor[5][count][0] = color[5][yneg][0];
						count++;
						yneg += 3;
					}
					yneg -= 9;
				}
				count = 0;
				break;
			case "Xi":
				for (int t = 0; t < 3; t++) {
					yneg++;
					while (yneg < 9) {
						tempColor[3][count][0] = color[0][count][0];
						tempColor[0][count][0] = color[1][count][0];
						tempColor[1][count][0] = color[2][count][0];
						tempColor[2][count][0] = color[3][count][0];
						tempColor[4][yneg][0] = color[4][count][0];
						tempColor[5][yneg][0] = color[5][count][0];
						count++;
						yneg += 3;
					}
					yneg -= 9;
				}
				count = 0;
				break;
			case "Y":
				for (int t = 0; t < 3; t++) {
					yneg++;
					ypos--;
					while (yneg < 9 && ypos < 9) {
						tempColor[0][count][0] = color[5][count][0];
						tempColor[5][count][0] = color[2][yneg][0];
						tempColor[2][count][0] = color[4][count][0];
						tempColor[4][count][0] = color[0][yneg][0];
						tempColor[1][count][0] = color[1][ypos][0];
						tempColor[3][count][0] = color[3][ypos][0];
						count++;
						yneg += 3;
						ypos += 3;
					}
					yneg -= 9;
					yneg -= 9;
				}
				count = 0;
				break;
			case "Yi":
				for (int t = 0; t < 3; t++) {
					yneg++;
					while (yneg < 9) {
						tempColor[3][count][0] = color[0][count][0];
						tempColor[0][count][0] = color[1][count][0];
						tempColor[1][count][0] = color[2][count][0];
						tempColor[2][count][0] = color[3][count][0];
						tempColor[4][yneg][0] = color[4][count][0];
						tempColor[5][yneg][0] = color[5][count][0];
						count++;
						yneg += 3;
					}
					yneg -= 9;
				}
				count = 0;
				break;
			case "Z":

				break;
			case "Zi":

				break;
			}
		} else {
			int y = 0, yL = 6;
			switch (rot) {
			case "L":
				while (y < 9) {
					tempColor[3][y][0] = color[0][y][0];
					tempColor[0][y][0] = color[1][yL][0];
					tempColor[2][y][0] = color[3][yL][0];
					tempColor[1][y][0] = color[2][y][0];
					count++;
					y += 3;
					yL -= 3;
				}
				y -= 9;
				yL = 6;
				count = 0;
				tempColor[4][0][0] = color[4][6][0];
				tempColor[4][1][0] = color[4][3][0];
				tempColor[4][2][0] = color[4][0][0];
				tempColor[4][3][0] = color[4][7][0];
				tempColor[4][5][0] = color[4][1][0];
				tempColor[4][6][0] = color[4][8][0];
				tempColor[4][7][0] = color[4][5][0];
				tempColor[4][8][0] = color[4][2][0];
				break;
			case "Li":
				while (y < 9) {
					System.out.println(yL);
					tempColor[0][y][0] = color[3][y][0];
					tempColor[1][yL][0] = color[0][y][0];
					tempColor[3][yL][0] = color[2][y][0];
					tempColor[2][y][0] = color[1][y][0];
					count++;
					y += 3;
					yL -= 3;
				}
				y -= 9;
				yL = 6;
				count = 0;
				tempColor[4][6][0] = color[4][0][0];
				tempColor[4][3][0] = color[4][1][0];
				tempColor[4][0][0] = color[4][2][0];
				tempColor[4][7][0] = color[4][3][0];
				tempColor[4][1][0] = color[4][5][0];
				tempColor[4][8][0] = color[4][6][0];
				tempColor[4][5][0] = color[4][7][0];
				tempColor[4][2][0] = color[4][8][0];
				break;

			case "M":
				y = 1;
				yL = 7;
				while (y < 9) {
					tempColor[3][y][0] = color[0][y][0];
					tempColor[0][y][0] = color[1][yL][0];
					tempColor[2][y][0] = color[3][yL][0];
					tempColor[1][y][0] = color[2][y][0];
					count++;
					y += 3;
					yL -= 3;
				}
				y -= 9;
				yL = 6;
				count = 0;
				break;
			case "Mi":
				y = 1;
				yL = 7;
				while (y < 9) {
					System.out.println(yL);
					tempColor[0][y][0] = color[3][y][0];
					tempColor[1][yL][0] = color[0][y][0];
					tempColor[3][yL][0] = color[2][y][0];
					tempColor[2][y][0] = color[1][y][0];
					count++;
					y += 3;
					yL -= 3;
				}
				y -= 9;
				yL = 6;
				count = 0;
				break;
			case "R":
				break;
			case "Ri":
				break;
			case "U":
				break;
			case "Ui":
				break;
			case "E":
				break;
			case "Ei":
				break;
			case "D":
				break;
			case "Di":
				break;
			case "F":
				break;
			case "Fi":
				break;
			case "S":
				break;
			case "Si":
				break;
			case "B":
				break;
			case "Bi":
				break;
			}
		}
		saveColor();
		for (int x = 0; x < 6; x++) {
			System.out.println();
			for (int y = 0; y < 9; y++)
				System.out.print(tempColor[x][y][0] + " ");
		}
	}
}

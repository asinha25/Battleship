/* CS 342- Project 4: BattleShip
 * The puzzle layout class which handles almost all the stuff for the GUI 
 * including buttons and the grid. This class also handles the client side of
 * the connection gameplay.
 */
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.io.*;
import java.net.*;


public class BattleShipClient extends JFrame implements ActionListener {

	static ObjectOutputStream out = null;
	static ObjectInputStream in = null;
	static Socket echoSocket = null;

	private static final long serialVersionUID = 1L;
	public JButton gridButtons_1[][];
	public JButton gridButtons_2[][];
	public JButton shipButtons[];
	public JLabel rowIndex[];
	public JLabel colIndex[];

	static int winner = 0;

	public JLabel ConnectionStatus; /* create a Lable to switch to if the connection is made */

	// use the indices to mark the indices the spaces here are just for the sake of
	// indentation
	public String col_Index[] = { "										A", "										B",
			"										C", "										D",
			"										E", "										F",
			"									G", "									H",
			"									I", "									J" };
	public String row_Index[] = { "		1", "		2", "		3", "		4", "		5", "		6", "		7",
			"		8", "		9", "	10		" };

	// placing ships horizontally or vertically
	public String shipImages_horizontal[] = { "/batt3.gif", "/batt4.gif", "/batt2.gif", "/batt1.gif", };
	public String shipImages_vertical[] = { "/batt8.gif", "/batt9.gif", "/batt7.gif", "/batt6.gif", };

	// used to place the ship head
	public String shipFace[] = { "/batt5.gif", "/batt10.gif" };

	// create a player pannel
	public JPanel GridPanel_1; /* create a JPanel */
	public JPanel aplha_1;
	public JPanel num_1;

	// create a opponent panel
	public JPanel GridPanel_2; /* create a JPanel */
	public JPanel aplha_2;
	public JPanel num_2;

	// create a panel for ship heads where you choose whether you want to place it
	// horizontally or vertically
	public JPanel ShipPanel;

	// addtional variables need for check purposes
	int face_pos = -1;
	int numOfShips = 5;
	int count = 0;
	int check = 0;
	int check_overlap = 1;
	int check_insert = 1;

	// arraylist to store the indicies where the ships are placed for the player:
	static ArrayList<Point> shipPositions = new ArrayList<Point>();
	static ArrayList<Point> check_Pos = new ArrayList<Point>();

	public int GRIDSIZE = 10;

	public BattleShipClient() {
		super("Battle Ships Client");

		/* Intiallize the components for the Grid on the Left side */
		GridPanel_1 = new JPanel(new GridLayout(GRIDSIZE, GRIDSIZE));
		gridButtons_1 = new JButton[GRIDSIZE][GRIDSIZE];
		colIndex = new JLabel[10];
		aplha_1 = new JPanel(new GridLayout(1, 10));
		rowIndex = new JLabel[10];
		num_1 = new JPanel(new GridLayout(10, 1));

		/* Intiallize the components for the Grid on the Right side */
		GridPanel_2 = new JPanel(new GridLayout(GRIDSIZE, GRIDSIZE));
		gridButtons_2 = new JButton[GRIDSIZE][GRIDSIZE];
		colIndex = new JLabel[10];
		aplha_2 = new JPanel(new GridLayout(1, 10));
		rowIndex = new JLabel[10];
		num_2 = new JPanel(new GridLayout(10, 1));

		ShipPanel = new JPanel(new FlowLayout());
		shipButtons = new JButton[2];

		for (int j = 0; j < 2; j++) {
			shipButtons[j] = new JButton();
			shipButtons[j].setIcon(new ImageIcon(getClass().getResource(shipFace[j])));
			shipButtons[j].setBorder(new LineBorder(Color.BLACK));
			shipButtons[j].addActionListener(BattleShipClient.this);
			ShipPanel.add(shipButtons[j]);
		}

		/*
		 * add the Lables for the numbers and the alphapbets for the indicies for Left
		 * Grid
		 */
		for (int i = 0; i < 10; i++) {
			colIndex[i] = new JLabel(col_Index[i]);
			aplha_1.add(colIndex[i]);
			colIndex[i].setText(col_Index[i]);

			rowIndex[i] = new JLabel(row_Index[i]);
			num_1.add(rowIndex[i]);
			rowIndex[i].setText(row_Index[i]);

		} // end of for

		/*
		 * add the Lables for the numbers and the alphapbets for the indicies for Right
		 * Grid
		 */
		for (int i = 0; i < 10; i++) {
			colIndex[i] = new JLabel(col_Index[i]);
			aplha_2.add(colIndex[i]);
			colIndex[i].setText(col_Index[i]);

			rowIndex[i] = new JLabel(row_Index[i]);
			num_2.add(rowIndex[i]);
			rowIndex[i].setText(row_Index[i]);

		} // end of for

		ButtonHandler handler = new ButtonHandler();

		/* Make the 10x10 Grid */
		for (int i = 0; i < GRIDSIZE; i++) {
			for (int j = 0; j < GRIDSIZE; j++) {
				gridButtons_1[i][j] = new JButton();
				gridButtons_1[i][j].setIcon(new ImageIcon(getClass().getResource("/batt100.gif")));
				gridButtons_1[i][j].setPreferredSize(new Dimension(50, 50));
				gridButtons_1[i][j].setBorder(new LineBorder(Color.BLACK));
				gridButtons_1[i][j].addActionListener(BattleShipClient.this);
				GridPanel_1.add(gridButtons_1[i][j]);

				gridButtons_2[i][j] = new JButton();
				gridButtons_2[i][j].setIcon(new ImageIcon(getClass().getResource("/batt100.gif")));
				gridButtons_2[i][j].setPreferredSize(new Dimension(50, 50));
				gridButtons_2[i][j].setBorder(new LineBorder(Color.BLACK));
				gridButtons_2[i][j].addActionListener(handler);
				GridPanel_2.add(gridButtons_2[i][j]);
			}

		}

		JPanel GridPanel_Left = new JPanel(new BorderLayout());
		GridPanel_Left.add(aplha_1, BorderLayout.BEFORE_FIRST_LINE);
		GridPanel_Left.add(num_1, BorderLayout.WEST);
		GridPanel_Left.add(GridPanel_1, BorderLayout.EAST);

		JPanel GridPanel_Right = new JPanel(new BorderLayout());
		GridPanel_Right.add(aplha_2, BorderLayout.NORTH);
		GridPanel_Right.add(num_2, BorderLayout.WEST);
		GridPanel_Right.add(GridPanel_2, BorderLayout.EAST);

		JPanel shipFacePanel = new JPanel(new BorderLayout());
		shipFacePanel.add(ShipPanel, BorderLayout.CENTER);

		// final grid.
		getContentPane().add(GridPanel_Left, BorderLayout.WEST);
		getContentPane().add(GridPanel_Right, BorderLayout.EAST);
		getContentPane().add(shipFacePanel, BorderLayout.CENTER);

		ConnectionStatus = new JLabel("Connection Status: ");
		getContentPane().add(ConnectionStatus, BorderLayout.SOUTH);
		ConnectionStatus.setText("	\n\n" + "			Connection Status: Currently no Connection");

		JMenuBar bar = new JMenuBar();
		setJMenuBar(bar);

		//
		// Creates the File Menu and the menu items
		//
		JMenu file = new JMenu("File");
		bar.add(file);

		JMenuItem exit = new JMenuItem("Quit");
		file.add(exit);

		//
		// Creates the Server Menu and the menu items
		//
		JMenu server = new JMenu("Server");
		bar.add(server);

		JMenuItem connect_Client = new JMenuItem("Connect as Client");
		server.add(connect_Client);

		JMenuItem disconnect = new JMenuItem("Disconnect");
		server.add(disconnect);

		//
		// Creates the Help Menu and the menu items
		//
		JMenu help = new JMenu("Help");
		bar.add(help);

		JMenuItem connect_help = new JMenuItem("Help Connect");
		help.add(connect_help);
		JMenuItem rules = new JMenuItem("Rules");
		help.add(rules);
		JMenuItem statistics = new JMenuItem("Stats");
		help.add(statistics);
		JMenuItem about = new JMenuItem("About");
		help.add(about);

		JMenuItem GUI = new JMenuItem("Using battleship GUI");
		help.add(GUI);

		// Action listener for quitting the program...
		//
		class exitaction implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		}
		exit.addActionListener(new exitaction());

		//action event for connection help button
		class connect_help implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JFrame inst_frame = new JFrame("Connection Help");
				inst_frame.setVisible(true);
				inst_frame.setSize(500, 300);
				JLabel label = new JLabel("For this Battle Ship Game you have to " + "plave this ships first.\n"
						+ "After placing the ships for both the server and the client you hit "
						+ "connect as server for the server GUI and then connect as Client under connect option.");
				JPanel panel = new JPanel();
				inst_frame.add(panel);
				panel.add(label);
			}
		}
		connect_help.addActionListener(new connect_help());

		//action event for connecting client	
		class connect_Client implements ActionListener {
			public void actionPerformed(ActionEvent e) {

				try {
					Client();
					System.out.println("Client Conected");
					ConnectionStatus.setText("	\n\n" + "			Connection Status: Connected");

				} catch (IOException e1) {
					System.out.println("Client not Connected");

					System.out.println("Error");

				}

			}
		}
		connect_Client.addActionListener(new connect_Client());

		//displays dialog box for the rules of the game
		class rules implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(BattleShipClient.this,
						"The game is played on four square grids, two for each player. The player has to first place ships\n"
								+ "on the grid and then the program establishes a connection for the server and client. On one grid the player\n"
								+ "arranges ships and records the shots by the opponent. On the other grid the player records their\n"
								+ "own shots. After the ships have been positioned, the game proceeds in a series of rounds. In each round,\n"
								+ "each player has a turn. During a turn, the player selects a target square in the opponents' grid\n"
								+ "which is to be shot at. If a ship occupies the squares, then it takes a hit. When all of the squares\n"
								+ "of a ship have been hit, the ship is sunk. If at the end of a round all of one player's ships have\n"
								+ "been sunk, the game ends and the other player wins",
						"Rules of the Game", JOptionPane.PLAIN_MESSAGE);
			}
		}
		rules.addActionListener(new rules());

		// Displays the statistics of the game...
		//
		class statistics implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JFrame about_frame = new JFrame("Statistics");
				about_frame.setVisible(true);
				about_frame.setSize(500, 300);
				JLabel label = new JLabel("The current Stats are: \n" + "Number of Hits: " + winner + " \n"
						+ " To Win You need:" + (17 - winner));
				JPanel panel = new JPanel();
				about_frame.add(panel);
				panel.add(label);
			}
		}
		statistics.addActionListener(new statistics());

		// About the authors
		class about implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(BattleShipClient.this,
						"Aditya Sinha - asinha25\n" + "Harshil Patel - hpate59\n" + "Mudit Kumar - mkumar23", "About",
						JOptionPane.PLAIN_MESSAGE);

			}
		}
		about.addActionListener(new about());

		class GUI implements ActionListener {
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(BattleShipClient.this,
						"For this battleship GUI, there are two buttons that determines the orientation of the ships.\n"
								+ "The user can choose to position the ships horizontally or vertically depending on the button chosen.\n"
								+ "If the user wants to establish a connection, click on Server on the menu bar and the button- Connect as a Client\n",
						"GUI HELP", JOptionPane.PLAIN_MESSAGE);

			}
		}
		GUI.addActionListener(new GUI());

		/* set the size of the window */
		setSize(1300, 600);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		JButton temp = (JButton) e.getSource();

		for (int k = 0; k < shipButtons.length; k++) {
			if (temp.equals(shipButtons[k])) {
				face_pos = k;
				System.out.println("Number to be inserted: " + face_pos);
				check++;
				break;
			}
		} // end for for

		for (int x = 0; x < 10; ++x) {
			for (int y = 0; y < 10; ++y) {

				if (temp.equals(gridButtons_1[x][y]) && check >= 1) {

					// first of all you will add the aircraft ship horizontally or vertically
					if (numOfShips == 5) {

						if (((y == 9 || y == 8 || y == 7 || y == 6) && face_pos == 0)
								|| ((x == 9 || x == 8 || x == 7 || x == 6) && face_pos == 1)) {
							JOptionPane.showMessageDialog(null,
									"Ship cannot be placed here as it goes out of the board.", "Error",
									JOptionPane.PLAIN_MESSAGE);
						} else {
							JOptionPane.showMessageDialog(null,
									"You are about to add aircraft carrier\n" + "which is going to take five spaces",
									"AIRCRAFT CARRIER", JOptionPane.PLAIN_MESSAGE);
							numOfShips--;
							System.out.println(" number of ships remaing " + numOfShips);
							// Add the ship horizontally
							if (face_pos == 0) {

								gridButtons_1[x][y].setIcon(new ImageIcon(getClass().getResource(shipFace[face_pos])));
								shipPositions.add(new Point(x, y));

								for (int i = 0; i < 4; i++) {
									gridButtons_1[x][y + (i + 1)]
											.setIcon(new ImageIcon(getClass().getResource(shipImages_horizontal[i])));
									shipPositions.add(new Point(x, y + (i + 1)));

								}
							} // end of if

							// add the ship vertically
							if (face_pos == 1) {
								gridButtons_1[x][y].setIcon(new ImageIcon(getClass().getResource(shipFace[face_pos])));
								shipPositions.add(new Point(x, y));

								for (int i = 0; i < 4; i++) {
									gridButtons_1[x + (i + 1)][y]
											.setIcon(new ImageIcon(getClass().getResource(shipImages_vertical[i])));
									shipPositions.add(new Point(x + i + 1, y));

								}
							} // end of if
						}

					} // end of if(numOfShips == 5 && face_pos == 0 || numOfShips == 5 && face_pos ==
						// 1 )
					else if (numOfShips == 4) {

						if (((y == 9 || y == 8 || y == 7) && face_pos == 0)
								|| ((x == 9 || x == 8 || x == 7) && face_pos == 1)) {
							JOptionPane.showMessageDialog(null,
									"Ship cannot be placed here as it goes out of the board.", "Error",
									JOptionPane.PLAIN_MESSAGE);
						}

						else {
							int flag_horizontal = 0;
							int flag_vertical = 0;

							if(face_pos == 0) {
							flag_horizontal = 1;
							for (int i = 0; i < 3; i++) {
								if (shipPositions.contains(new Point(x, y + 1 + i))) {
									JOptionPane.showMessageDialog(null,
											"Ship cannot be placed here as it Overlaps the other ship.", "Error",
											JOptionPane.PLAIN_MESSAGE);
									flag_horizontal = 0;
									break;
								}
								if (shipPositions.contains(new Point(x, y))) {
									JOptionPane.showMessageDialog(null,
											"Ship cannot be placed here as it Overlaps the other ship.", "Error",
											JOptionPane.PLAIN_MESSAGE);
									flag_horizontal = 0;
									break;
								}
							}
							}

							if(face_pos == 1) {
							flag_vertical = 1;
							for (int i = 0; i < 3; i++) {
								if (shipPositions.contains(new Point(x + 1 + i, y))) {
									JOptionPane.showMessageDialog(null,
											"Ship cannot be placed here as it Overlaps the other ship.", "Error",
											JOptionPane.PLAIN_MESSAGE);
									flag_vertical = 0;
									break;
								}
								if (shipPositions.contains(new Point(x, y))) {
									JOptionPane.showMessageDialog(null,
											"Ship cannot be placed here as it Overlaps the other ship.", "Error",
											JOptionPane.PLAIN_MESSAGE);
									flag_vertical = 0;
									break;
								}
							}
							}

							if (flag_vertical == 1 || flag_horizontal == 1) {

								JOptionPane.showMessageDialog(null,
										"You are about to add Battleship \n" + "which is going to take four spaces",
										"Battleship", JOptionPane.PLAIN_MESSAGE);

								numOfShips--;
								System.out.println(" number of ships remaing " + numOfShips);

								// addd the battleship horizontally
								if (face_pos == 0) {
									gridButtons_1[x][y]
											.setIcon(new ImageIcon(getClass().getResource(shipFace[face_pos])));
									shipPositions.add(new Point(x, y));

									for (int i = 0; i < 2; i++) {
										gridButtons_1[x][y + (i + 1)].setIcon(
												new ImageIcon(getClass().getResource(shipImages_horizontal[i])));
										shipPositions.add(new Point(x, y + 1 + i));

									}
									gridButtons_1[x][y + 3]
											.setIcon(new ImageIcon(getClass().getResource(shipImages_horizontal[3])));
									shipPositions.add(new Point(x, y + 3));

								}
								// add the battle ship vertically
								if (face_pos == 1) {
									gridButtons_1[x][y]
											.setIcon(new ImageIcon(getClass().getResource(shipFace[face_pos])));
									shipPositions.add(new Point(x, y));

									for (int i = 0; i < 2; i++) {
										gridButtons_1[x + i + 1][y]
												.setIcon(new ImageIcon(getClass().getResource(shipImages_vertical[i])));
										shipPositions.add(new Point(x + i + 1, y));

									}
									gridButtons_1[x + 3][y]
											.setIcon(new ImageIcon(getClass().getResource(shipImages_vertical[3])));
									shipPositions.add(new Point(x + 3, y));

								} // end of if
							}
						}
					} // end of numOfShips == 4
					else if (numOfShips == 3 && count < 2) {

						if (((y == 9 || y == 8) && face_pos == 0) || ((x == 9 || x == 8) && face_pos == 1)) {
							JOptionPane.showMessageDialog(null,
									"Ship cannot be placed here as it goes out of the board.", "Error",
									JOptionPane.PLAIN_MESSAGE);
						} else {

							int flag_horizontal = 0;
							int flag_vertical = 0;

							if(face_pos == 0) {
								flag_horizontal = 1;
							for (int i = 0; i < 2; i++) {
								if (shipPositions.contains(new Point(x, y + 1 + i))) {
									JOptionPane.showMessageDialog(null,
											"Ship cannot be placed here as it Overlaps the other ship.", "Error",
											JOptionPane.PLAIN_MESSAGE);
									flag_horizontal = 0;
									break;
								}
								if (shipPositions.contains(new Point(x, y))) {
									JOptionPane.showMessageDialog(null,
											"Ship cannot be placed here as it Overlaps the other ship.", "Error",
											JOptionPane.PLAIN_MESSAGE);
									flag_horizontal = 0;
									break;
								}
							}
							}

							if(face_pos == 1) {
								flag_vertical = 1;
							for (int i = 0; i < 2; i++) {
								if (shipPositions.contains(new Point(x + 1 + i, y))) {
									JOptionPane.showMessageDialog(null,
											"Ship cannot be placed here as it Overlaps the other ship.", "Error",
											JOptionPane.PLAIN_MESSAGE);
									flag_vertical = 0;
									break;
								}
								if (shipPositions.contains(new Point(x, y))) {
									JOptionPane.showMessageDialog(null,
											"Ship cannot be placed here as it Overlaps the other ship.", "Error",
											JOptionPane.PLAIN_MESSAGE);
									flag_vertical = 0;
									break;
								}
							}
							}

							if (flag_vertical == 1 || flag_horizontal == 1) {

								JOptionPane.showMessageDialog(null,
										"You are about to add Destroyer \n" + "which is going to take three spaces",
										"Battleship", JOptionPane.PLAIN_MESSAGE);
								if (count == 1) {
									numOfShips--;
								}
								count++;
								System.out.println(" number of ships remaing " + numOfShips);

								// addd the battleship horizontally
								if (face_pos == 0) {
									gridButtons_1[x][y]
											.setIcon(new ImageIcon(getClass().getResource(shipFace[face_pos])));
									shipPositions.add(new Point(x, y));

									gridButtons_1[x][y + 1]
											.setIcon(new ImageIcon(getClass().getResource(shipImages_horizontal[1])));
									shipPositions.add(new Point(x, y + 1));

									gridButtons_1[x][y + 2]
											.setIcon(new ImageIcon(getClass().getResource(shipImages_horizontal[3])));
									shipPositions.add(new Point(x, y + 2));

								}
								// add the battle ship vertically
								if (face_pos == 1) {
									gridButtons_1[x][y]
											.setIcon(new ImageIcon(getClass().getResource(shipFace[face_pos])));
									shipPositions.add(new Point(x, y));

									gridButtons_1[x + 1][y]
											.setIcon(new ImageIcon(getClass().getResource(shipImages_vertical[1])));
									shipPositions.add(new Point(x + 1, y));

									gridButtons_1[x + 2][y]
											.setIcon(new ImageIcon(getClass().getResource(shipImages_vertical[3])));
									shipPositions.add(new Point(x + 2, y));

								} // end of if
							}
						}
					} // end of numOfShips == 3
					else if (numOfShips == 2) {

						if ((y == 9 && face_pos == 0) || (x == 9 && face_pos == 1)) {
							JOptionPane.showMessageDialog(null,
									"Ship cannot be placed here as it goes out of the board.", "Error",
									JOptionPane.PLAIN_MESSAGE);
						} else {

							int flag_horizontal = 0;
							int flag_vertical = 0;

							if (face_pos == 0) {
								 flag_horizontal = 1;
								for (int i = 0; i < 1; i++) {
									if (shipPositions.contains(new Point(x, y + 1 + i))) {
										JOptionPane.showMessageDialog(null,
												"Ship cannot be placed here as it Overlaps the other ship.", "Error",
												JOptionPane.PLAIN_MESSAGE);
										flag_horizontal = 0;
										break;
									}
									if (shipPositions.contains(new Point(x, y))) {
										JOptionPane.showMessageDialog(null,
												"Ship cannot be placed here as it Overlaps the other ship.", "Error",
												JOptionPane.PLAIN_MESSAGE);
										flag_horizontal = 0;
										break;
									}
								}
							}
							if (face_pos == 1) {
								 flag_vertical = 1;

								for (int i = 0; i < 1; i++) {
									if (shipPositions.contains(new Point(x + 1 + i, y))) {
										JOptionPane.showMessageDialog(null,
												"Ship cannot be placed here as it Overlaps the other ship.", "Error",
												JOptionPane.PLAIN_MESSAGE);
										flag_vertical = 0;
										break;
									}
									if (shipPositions.contains(new Point(x, y))) {
										JOptionPane.showMessageDialog(null,
												"Ship cannot be placed here as it Overlaps the other ship.", "Error",
												JOptionPane.PLAIN_MESSAGE);
										flag_vertical = 0;
										break;
									}
								}
							}

							if (flag_vertical == 1 || flag_horizontal == 1) {

								JOptionPane.showMessageDialog(null,
										"You are about to add Patrol Boat \n" + "which is going to take Two spaces",
										"Battleship", JOptionPane.PLAIN_MESSAGE);

								numOfShips--;
								System.out.println(" number of ships remaing " + numOfShips);

								// addd the battleship horizontally
								if (face_pos == 0) {
									gridButtons_1[x][y]
											.setIcon(new ImageIcon(getClass().getResource(shipFace[face_pos])));
									shipPositions.add(new Point(x, y));

									gridButtons_1[x][y + 1]
											.setIcon(new ImageIcon(getClass().getResource(shipImages_horizontal[3])));
									shipPositions.add(new Point(x, y + 1));

								}
								// add the battle ship vertically
								if (face_pos == 1) {
									gridButtons_1[x][y]
											.setIcon(new ImageIcon(getClass().getResource(shipFace[face_pos])));
									shipPositions.add(new Point(x, y));

									gridButtons_1[x + 1][y]
											.setIcon(new ImageIcon(getClass().getResource(shipImages_vertical[3])));
									shipPositions.add(new Point(x + 1, y));

								} // end of if
							}
						}
					} // end of numOfShips == 2
					else if (numOfShips == 1) {
						JOptionPane.showMessageDialog(null,
								"You cannot add any more ships becuse you are out of ships and "
										+ "all the ships are added.",
								"Battleship", JOptionPane.PLAIN_MESSAGE);
					}
				} // end of if
			} // end of for
		} // end of for

		// for (Point p : shipPositions) {
		// System.out.println("xPos: " + p.x + " yPos: " + p.y);
		// }
	}// end of actionlistner for left grid

	// inner class for button event handling for the Right Grid
	private class ButtonHandler implements ActionListener {

		// handle button event
		public void actionPerformed(ActionEvent event) {

			// button code for the right grade
			JButton temp_grid2 = (JButton) event.getSource();
			for (int x = 0; x < 10; ++x) {
				for (int y = 0; y < 10; ++y) {

					if (temp_grid2.equals(gridButtons_2[x][y])) {

						if (check_Pos.contains(new Point(x, y))) {
							gridButtons_2[x][y].setIcon(new ImageIcon(getClass().getResource("./batt103.gif"))); 
							
							//dipslay whose turn it is
							JOptionPane.showMessageDialog(null,
									"Player2(Client Player) blew up a ship: Its your Turn again", "Battleship",
									JOptionPane.PLAIN_MESSAGE);
							winner++;
							check_Pos.remove(new Point(x, y)); // remove the cordinate from the list
							
							if (winner == 17) {
								JOptionPane.showMessageDialog(null, "Client Player Won", "Battleship",
										JOptionPane.PLAIN_MESSAGE);
								try {
									echoSocket.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									System.out.println("closing appllication error");
								}
								
								//default icon, custom title
						        int n = JOptionPane.showConfirmDialog(
						            null,
						            "Would you Play again?",
						            "Play Again",
						            JOptionPane.YES_NO_OPTION);
						        
						        if(n == JOptionPane.YES_OPTION) {
						        		BattleShipClient application = new BattleShipClient();
						        		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
									setVisible(false); }
						        else {
						            System.exit(0);
						        }
							}
						} else {
							//if it is a miss
							gridButtons_2[x][y].setIcon(new ImageIcon(getClass().getResource("./batt102.gif"))); 
							JOptionPane.showMessageDialog(null,
									"Player2(Client Player) Missed a ship, You Loose your Turn\n"
											+ "Player1(Server Player) its your turn",
									"Battleship", JOptionPane.PLAIN_MESSAGE);
							try {
								System.out.println("Player 1 Turn....");
								Client();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							System.out.println("Error");
						}

						} // end of else
					} // end of temp_grid2.equals(gridButtons_2[x][y])
				} // end of inner for
			} // end of for
		}
	}

	/*
	 * Main method to satrt the GUI
	 */
	public static void main(String[] args) {
		BattleShipClient application = new BattleShipClient();
		application.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}// end of main

	// connect as client
	public static void Client() throws IOException {

		try {
			// echoSocket = new Socket("taranis", 7);
			echoSocket = new Socket("127.0.0.1", 10007);

			out = new ObjectOutputStream(echoSocket.getOutputStream());
			in = new ObjectInputStream(echoSocket.getInputStream());

		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: taranis.");
			System.exit(1);
		} catch (IOException e) {
			System.err.println("Couldn't get I/O for " + "the connection to: taranis.");
			System.exit(1);
		}

		System.out.println("In Client");
		sendAndreceive();

		// echoSocket.close();
	}

	//function that sends and recieves messages between server and client
	public static void sendAndreceive() throws IOException {
		
		out.writeObject(shipPositions);
		out.flush();

		try {
			check_Pos = (ArrayList<Point>) in.readObject();
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

		out.close();
		in.close();

	}
}

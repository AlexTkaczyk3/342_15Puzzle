

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

public class PuzzleController implements Initializable {
	
	@FXML
	private VBox root;
	
	@FXML
	private BorderPane root2;
    
    @FXML
    private Label welcomeLabel;
    
    @FXML
    private GridPane gameGrid;
    
    @FXML
    private Button testBtn;
    
    @FXML
    private MenuBar gameMenuBar;
    
    @FXML
    private Menu solutionMenu;
    
    @FXML
    private MenuItem howItem;
    
    @FXML
    private MenuItem h1Item;

    @FXML
    private MenuItem h2Item;

    @FXML
    private Button solutionItem;
    
    @FXML
    private VBox howToVBox;
    
    @FXML
    private Label howToLabel;
    
    @FXML
    private Button dismissBtn;
    
    private static int counter = 0;

    int[] testPuzzle = new int[] {1,0,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    int[] puzzleSolution = new int[] {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
    
    int[] puzzle1 = new int[] {1,10,9,3,2,5,12,7,4,14,8,11,0,13,6,15}; // Solvable
    int[] puzzle2 = new int[] {1,3,5,7,9,11,13,15,0,2,4,6,8,10,12,14}; // Solvable
    int[] puzzle3 = new int[] {10,2,3,7,14,8,15,5,13,1,11,9,12,4,0,6}; // Solvable
    int[] puzzle4 = new int[] {1,2,3,4,9,10,11,12,13,5,6,7,8,15,14,0}; // Solvable
    int[] puzzle5 = new int[] {9,3,0,2,1,7,8,6,5,4,12,10,11,13,14,15}; // Solvable
    int[] puzzle6 = new int[] {2,7,1,3,4,8,9,10,0,5,6,12,13,14,15,11}; // Solvable
    int[] puzzle7 = new int[] {0,7,2,10,9,13,8,3,1,11,6,12,14,5,4,15}; // Solvable
    int[] puzzle8 = new int[] {15,14,13,12,11,10,9,8,7,6,5,4,3,2,1,0}; // Solvable
    int[] puzzle9 = new int[] {8,3,15,0,10,12,7,6,13,2,5,4,9,1,11,14}; // Solvable
    int[] puzzle10 = new int[] {0,6,4,2,10,14,12,9,5,15,13,1,11,7,8,3}; // Solvable
    HashMap<Integer, int[]> puzzleMap;
    
	Button[] gameButtons = new Button[16];
	
	int[] currPuzzle;
	Random rand = new Random();
	int currNumber;
	
	boolean isGameOver = false;
	int step = 0;
	
	ArrayList<Node> solutionPath;
	ExecutorService ex = Executors.newFixedThreadPool(2);
    
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		if (counter == 0) {
		welcomeLabel.setStyle("-fx-font-size: 40;" + "font-weight: bold;");
		hitGameScreen();
		} else if (counter == 1) {
			puzzleMap = new HashMap<Integer,int[]>();
			puzzleMap.put(1, puzzle1);
			puzzleMap.put(2, puzzle2);
			puzzleMap.put(3, puzzle3);
			puzzleMap.put(4, puzzle4);
			puzzleMap.put(5, puzzle5);
			puzzleMap.put(6, puzzle6);
			puzzleMap.put(7, puzzle7);
			puzzleMap.put(8, puzzle8);
			puzzleMap.put(9, puzzle9);
			puzzleMap.put(10, puzzle10);
			
			// for testing solvable puzzles
//			currPuzzle = puzzleMap.get(1);
			
			// for application-use
			Random rand = new Random();
			currNumber = rand.nextInt(10); // gets random 0-9
			currPuzzle = puzzleMap.get(currNumber + 1);
			
			gameGrid.getStyleClass().add("gamegrid");
			howToVBox.setVisible(false);
			howToVBox.getStyleClass().add("vbox");
			solutionItem.setDisable(true);
			howItem.setDisable(false);
			setUpGame();
		}
    }
	
	
	public void hitGameScreen() {
		PauseTransition welcomePause = new PauseTransition(Duration.seconds(2.5));
		welcomePause.setOnFinished(z->{
			try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/FXML/gameScreen.fxml"));
	        Parent root2 = loader.load();
	        root2.getStylesheets().add("/styles/mainStyle.css");
	        root.getScene().setRoot(root2);
			}
			catch (Exception e) {
				System.out.println("Error while loading game screen");
				e.printStackTrace();
			}
		});
		welcomePause.play();
		counter++;
	}
	
	public void setUpGame() {
		gameGrid.getChildren().clear();
		gameButtons = new Button[16];
		EventHandler<ActionEvent> gameBtnAction = new EventHandler<ActionEvent>(){
			public void handle(ActionEvent event) {
				Button button = (Button)event.getSource();
				int row = GridPane.getRowIndex(button);
				int column = GridPane.getColumnIndex(button);
				int posInArray = (row * 4 + 1) + (column + 1) - 2;
				//System.out.println("Row=" + row + ", Column=" + column + ", PosInArray=" + posInArray);
				
				int cases = -1; // Cases based on possible moves-- [1 = right, 2 = left, 3 = up, 4 = down, -1 = invalid
				
				// left = currPuzzle[posInArray - 1]
				// right = currPuzzle[posInArray + 1]
				// up = currPuzzle[posInArray - 4];
				// down = currPuzzle[posInArray + 4];
				
				// Check if valid tile clicked, if so then find out which move is valid (right, left, up, down)
				if (row == 0) {
					if (column == 0) {  // Pos. 1
						if (currPuzzle[posInArray + 1] == 0) { // Blank in Pos. 2
							cases = 1;
						} else if (currPuzzle[posInArray + 4] == 0) { // Blank in Pos. 5
							cases = 4;
						} else {
							cases = -1;
						}
					} else if (column == 1 || column == 2) {  // Pos. 2-3
						if (currPuzzle[posInArray - 1] == 0) {  // Blank in Pos. 1-2
							cases = 2;
						} else if (currPuzzle[posInArray + 1] == 0) { // Blank in Pos. 3-4
							cases = 1;
						} else if (currPuzzle[posInArray + 4] == 0) { // Blank in Pos. 6-7
							cases = 4;
						} else {
							cases = -1;
						}
					} else if (column == 3) {  // Pos. 4
						if (currPuzzle[posInArray - 1] == 0) {  // Blank in Pos. 3
							cases = 2;
						} else if (currPuzzle[posInArray + 4] == 0) {  // Blank in Pos. 8
							cases = 4;
						} else {
							cases = -1;
						}
					}
				} else if (row == 1 || row == 2) { //  ---------may need to split rows
					if (column == 0) {  // Pos. 5 + 9
						if (currPuzzle[posInArray + 1] == 0) {  // Blank in Pos. 6 + 10
							cases = 1;
						} else if (currPuzzle[posInArray + 4] == 0) {  // Blank in Pos. 9 + 13
							cases = 4;
						} else if (currPuzzle[posInArray - 4] == 0) {  // Blank in Pos. 1 + 5
							cases = 3;
						} else {
							cases = -1;
						}
					} else if (column == 1 || column == 2) {  // Pos. 6-7 + 10-11
						if (currPuzzle[posInArray - 1] == 0) {  // Blank in Pos. 5-6 + 9-10
							cases = 2;
						} else if (currPuzzle[posInArray + 1] == 0) {  // Blank in Pos. 7-8 + 11-12
							cases = 1;
						} else if (currPuzzle[posInArray + 4] == 0) {  // Blank in Pos. 10 + 11 + 14 + 15
							cases = 4;
						} else if (currPuzzle[posInArray - 4] == 0) {  // Blank in Pos. 2-3 + 6-7
							cases = 3;
						} else {
							cases = -1;
						}
					} else if (column == 3) {  // Pos. 8 + 12
						if (currPuzzle[posInArray - 1] == 0) {  // Blank in Pos. 7 + 11
							cases = 2;
						} else if (currPuzzle[posInArray + 4] == 0) {  // Blank in Pos. 12 + 16
							cases = 4;
						} else if (currPuzzle[posInArray - 4] == 0) {  // Blank in Pos. 4 + 8
							cases = 3;
						} else {
							cases = -1;
						}
					}
				} else if (row == 3) {
					if (column == 0) {  // Pos. 13
						if (currPuzzle[posInArray + 1] == 0) { // Blank in Pos. 12
							cases = 1;
						} else if (currPuzzle[posInArray - 4] == 0) {  // Blank in Pos. 9
							cases = 3;
						} else {
							cases = -1;
						}
					} else if (column == 1 || column == 2) {  // Pos. 14-15
						if (currPuzzle[posInArray - 1] == 0) { // Blank in Pos. 13-14
							cases = 2;
						} else if (currPuzzle[posInArray + 1] == 0) {  // Blank in Pos. 15-16
							cases = 1;
						} else if (currPuzzle[posInArray - 4] == 0) {  // Blank in Pos. 10-11
							cases = 3;
						} else {
							cases = -1;
						}
					} else if (column == 3) { // Pos. 16
						if (currPuzzle[posInArray - 1] == 0) {  // Blank in Pos. 15
							cases = 2;
						} else if (currPuzzle[posInArray - 4] == 0) {  // Blank in Pos. 12
							cases = 3;
						} else {
							cases = -1;
						}
					}
				}
				// Update puzzle array
				if (cases == -1) {
					System.out.println("Invalid Move");
				} else if (cases == 1) {
					//System.out.println("Right");
					currPuzzle[posInArray + 1] = currPuzzle[posInArray]; // right == clicked
					currPuzzle[posInArray] = 0;
				} else if (cases == 2) {
					//System.out.println("Left");
					currPuzzle[posInArray - 1] = currPuzzle[posInArray]; // left == clicked
					currPuzzle[posInArray] = 0;
				} else if (cases == 3) {
					//System.out.println("Up");
					currPuzzle[posInArray - 4] = currPuzzle[posInArray]; // up == clicked
					currPuzzle[posInArray] = 0;
				} else if (cases == 4) {
					//System.out.println("Down");
					currPuzzle[posInArray + 4] = currPuzzle[posInArray]; // down == clicked
					currPuzzle[posInArray] = 0;
				} else { // ERROR -- Shouldn't happen!!!
					System.out.println("Invalid Move Cases--Check gameBtn Handler @ Line 110");
					Platform.exit();
				}
				
				// Update tiles in grid
				for (int i = 0; i < 16; i++) {
					if (currPuzzle[i] != 0) {
						gameButtons[i].setText(String.valueOf(currPuzzle[i]));
						gameButtons[i].setStyle("-fx-background-color: white;");
					} else {
						gameButtons[i].setText("");
						gameButtons[i].setStyle("-fx-background-color: transparent;");
					}
				}
				isGameOver = checkForGameWin();
				if (isGameOver) {
					endOfGame();
				}
			}
		};
		int buttonCount = 0;
		for(int i = 0; i < 4; i++){
	        for(int j = 0; j < 4; j++){
	            Button button = new Button();
	            button.setPrefSize(75, 75);
	            button.setOnAction(gameBtnAction);
	            button.setText(String.valueOf(buttonCount));
	            gameGrid.add(button, j, i);
	            gameButtons[buttonCount] = button;
	            buttonCount++;
	        }
	    }
		for (int i = 0; i < 16; i++) {
			if (currPuzzle[i] != 0) {
				gameButtons[i].setText(String.valueOf(currPuzzle[i]));
			} else {
				gameButtons[i].setText("");
				gameButtons[i].setStyle("-fx-background-color: transparent;");
			}
		}
	}
	
	public boolean checkForGameWin() {
		boolean correctFlag = true;
		for (int i = 0; i < 16; i++) {
			if (puzzleSolution[i] != currPuzzle[i]) {
				correctFlag = false;
				System.out.println("Not same " + puzzleSolution[i] + "!=" + currPuzzle[i]);
				break;
			}
		}
		if (correctFlag == true) {
			System.out.println("Congrats, ya won big guy");
			return true;
		} else {
			return false;
		}
	}
	
	public void endOfGame() {
		howItem.setDisable(true);
		gameGrid.setDisable(true);
		solutionMenu.setDisable(true);
		howToVBox.setVisible(true);
		String howToGuide = "Congrats, you won!!\n" +
				"You now have 2 Options\n" + 
				"Option 1: Start a New Puzzle to Solve\n" +
				"To do that: Access the 'Game Options' Menu'\n" +
				"Then select 'New Puzzle'\n" +
				"\n" +
				"Option 2: Exit the Game\n" +
				"To do that: Access the 'Game Options' Menu'\n" +
				"Then select 'Exit'\n";
		howToLabel.setText(howToGuide);
	}
	
	public void howToItemMethod(ActionEvent e) throws IOException {
		
		howToVBox.setVisible(true);
		String howToGuide = "To win:\n" +
				"All tiles must be in correct numerical order\n" + 
				"from left->right then top->bottom\n" +
				"\nTo Play:\n" +
				"Players must click on tiles to with the blank tile\n" +
				"Buttons clicked must be adjacent to the blank\n" +
				"\nFor Help:\n" +
				"Players should click on the 'Solve Options' menu\n" +
				"Then choose an AI hueristic\n" +
				"Then select 'Show Solution'\n" +
				"Which will then display the next 10 moves\n";
		howToLabel.setText(howToGuide);
	}
	
	public void newPuzzleItemMethod(ActionEvent e) throws IOException {
		howItem.setDisable(false);
		howToVBox.setVisible(false);
		gameGrid.setDisable(false);
		solutionMenu.setDisable(false);
		h1Item.setDisable(false);
		h2Item.setDisable(false);
		int newPuzzleNumber = rand.nextInt(10); // gets random 0-9
		while (currNumber == newPuzzleNumber) {
			newPuzzleNumber = rand.nextInt(10);
		}
		currPuzzle = puzzleMap.get(newPuzzleNumber + 1);
		currNumber = newPuzzleNumber;
		System.out.println("New Puzzle Number = " + newPuzzleNumber);
		setUpGame();
	}
	
	public void exitItemMethod(ActionEvent e) throws IOException {
		Platform.exit();
	}
	
	public void hOneItemMethod(ActionEvent e) throws IOException {
		//System.out.println("Starting AI w/ H1");
		gameGrid.setDisable(true);
		h1Item.setDisable(true);
		h2Item.setDisable(true);
		solutionPath = new ArrayList<Node>();
		
		Node startState = new Node(currPuzzle);
		startState.setDepth(0);
		Future<ArrayList<Node>> future = ex.submit(new MyCall(startState, "heuristicOne"));
		try {
			solutionPath = future.get();
		} catch(Exception excep){System.out.println(excep.getMessage());}
		
		solutionItem.setDisable(false);
	}
	
	public void hTwoItemMethod(ActionEvent e) throws IOException {
		//System.out.println("Starting AI w/ H2");
		gameGrid.setDisable(true);
		h1Item.setDisable(true);
		h2Item.setDisable(true);
		solutionPath = new ArrayList<Node>();
		
		Node startState = new Node(currPuzzle);
		startState.setDepth(0);
		Future<ArrayList<Node>> future = ex.submit(new MyCall(startState, "heuristicTwo"));
		try {
			solutionPath = future.get();
		} catch(Exception excep){System.out.println(excep.getMessage());}
		
		solutionItem.setDisable(false);
	}
	
	public void solutionItemMethod(ActionEvent e) throws IOException {
		recursivePause();
		solutionItem.setDisable(true);
	}
	
	public void dismissBtnMethod(ActionEvent e) throws IOException {
		howToVBox.setVisible(false);
	}
	
	public void recursivePause(){
			PauseTransition newMovePause = new PauseTransition(Duration.seconds(1));
			newMovePause.setOnFinished(z->{
				int[] nextStep = solutionPath.get(step).getKey(); // gets next phase
				currPuzzle = nextStep;
				setUpGame();
				isGameOver = checkForGameWin();
				if (isGameOver) { // Solution got winner
					endOfGame();
				} else if (step == 10) { // 10 steps have been displayed
					step = 0;
					h1Item.setDisable(false);
					h2Item.setDisable(false);
					gameGrid.setDisable(false);
				} else {
					recursivePause();
				}
			});
			newMovePause.play();
			step++;
	}
	
	class MyCall implements Callable<ArrayList<Node>>{

		Node startState;
		String hueristic;
		
		MyCall(Node startState, String hueristic){
			this.startState = startState;
			this.hueristic = hueristic;
		}
		@Override
		public ArrayList<Node> call() throws Exception {
			ArrayList<Node> solution = A_IDS_A_15solver.A_Star(startState, hueristic);
			Thread.sleep(1000);
			return solution;
		}
	}
}

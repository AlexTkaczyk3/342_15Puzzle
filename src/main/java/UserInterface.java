import java.util.Scanner;

/*
 * @author Mark Hallenbeck
 * Copyright© 2014, Mark Hallenbeck, All Rights Reservered.
 */

public class UserInterface {

int[] puzzle;
	
	public UserInterface(){
		
		readInString();		//reads in the string from the user
		
		System.out.print("\nThis is the puzzle you entered\n");
		
		printArray();
	}

	/**
	 * Function reads in the string from the user and stores it as an int array
	 */
	public void readInString(){
		
		String puzzleEntered;
	
		System.out.println("Enter in your puzzle as a string with a space between each number");
		
		Scanner userInput = new Scanner(System.in);		//open scanner
		
		puzzleEntered = userInput.nextLine();					//scan in string
		
		puzzle = stringToIntArray(puzzleEntered);
		
		userInput.close();   	  						//close scanner
	}
	
	/**
	 * Function converts a string to an int array
	 * @param puzzle
	 * @return int array
	 */
	int[] stringToIntArray(String puzzle)
	{
		String[] values = puzzle.split("[ ]+");			//split string into array of strings(numbers)
		
		int[] intArray = new int[values.length];			//int array for converted strings
		
		for(int x = 0; x < intArray.length; x++)			//convert to int array
		{
			intArray[x] = Integer.parseInt(values[x]);
		}
		
		return intArray;
	}
	
	public void printArray(){
		
		for(int i =0; i<puzzle.length; i++)
			System.out.print(puzzle[i] + " ");
		
		System.out.print("\n\n");
		
	}
	
	public int[] getPuzzle(){
		return puzzle;
	}

}

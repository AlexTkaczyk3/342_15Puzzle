import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

public class DB_Solver2 {

	/**
	 * This class will perform A* search on a standard 15 puzzle. It can use one of two heuristics to organize the 
	 * queue, manhattan distance or tiles out of place. Tiles out of place is heuristicOne and manhattan distance is heuristicTwo.
	 *  
	 * 
	 * @author Mark Hallenbeck
	 * Copyright© 2014, Mark Hallenbeck, All Rights Reservered.
	 */
		
		private ArrayList<Node> queue;					//queue of states to expand
		private int[] goalState = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};	//goal state used for comparison
		private Node firstState;						//initial state of puzzle
		private Node solutionNode;						//node contains the solution state and parent
		private Hashtable<String, Node> hash;			//used to keep track of visited states
		private String whichHeuristic;					//the heuristic to be used
		
		
		public DB_Solver2(Node startState, String heuristic){
			
			whichHeuristic = heuristic;					//which heuristic to use
			
			if(heuristic == "heuristicOne"){			// heuristic one is tiles out of place
				startState.set_hValue(getH1(startState.getKey()));
			}
			else{										//heuristic two is manhattan distance
				startState.set_hValue(getH2(startState.getKey()));
			}
			
			firstState = startState;					//the start state
			queue = new ArrayList<Node>();				//initialize the queue
			queue.add(startState);						//add the startState node to the queue
			hash = new Hashtable<String, Node>();
			hash.put(startState.getKey2(), startState);		//add the startState node to the hashtable
		}
		
		/**
		 * Function will run search of 15 puzzle for A* search.
		 * This version does keep a hash table of states seen so there will be no repeated states in the queue
		 * @return node with the solution or null(no solution found)
		 */
		public Node findSolutionPath(){
			
			//checking to see if the puzzle entered in by the user is valid
			int[] checkArray = copyArray(firstState.getKey());
			Arrays.sort(checkArray);
			
		/*		for(int i = 0; i<checkArray.length-1; i++)
			{
				if(checkArray[i] < checkArray[i+1])
					moveZero(checkArray, i, i+1);
			}
			
			printArray(checkArray);					//used this for debugging
			*/
			
			//if it is not valid, report error and exit program
			if(!goalTest(checkArray)){
				System.out.println("You have entered an invalid puzzle..........exiting the program\n\n");
				System.exit(-1);
			}

			//test to see if the puzzle entered is already in a goal state
			if(goalTest(firstState.getKey())){
				solutionNode = firstState;
				return solutionNode;
			}
			
			while(!queue.isEmpty()){			//if queue is empty, I have explored all options
				
				Node current = queue.get(0);	//get top node off the queue
				queue.remove(0);				//remove it from the queue
				
				//System.out.println("Popping node with hVal: "+current.get_hValue());
				
				//check right, if it contains the goal state return solution node
				if(moveRight(current.getKey())){
					
					System.out.println("the size of the queue and hash: "+ queue.size()+ " "+ hash.size());
					return solutionNode;
				}
				
				//check left
				if(moveLeft(current.getKey())){
					
					System.out.println("the size of the queue and hash: "+ queue.size()+ " "+ hash.size());
					return solutionNode;
				}
				
				
				//check up
				if(moveUp(current.getKey())){
					
					System.out.println("the size of the queue and hash: "+ queue.size()+ " "+ hash.size());
					return solutionNode;
				}
				
				//check down
				if(moveDown(current.getKey())){
					
					System.out.println("the size of the queue and hash: "+ queue.size()+ " "+ hash.size());
					return solutionNode;
				}	
				
					//this line was used for debugging		
				//System.out.println("the size of the queue and hash: "+ queue.size()+ " "+ hash.size());
			}
			
			System.out.println("the size of the queue and hash: "+ queue.size()+ " "+ hash.size());
			return null;							//queue is empty, no solution
									
				
		}
		
		/**
		 * Function will store the nodes on the path from the start state to solution
		 * @param finalState
		 * @return arrayList of nodes that represent the path to a solution
		 */
		public ArrayList<Node> getSolutionPath(Node finalState){
			
			ArrayList<Node> solutionPath = new ArrayList<Node>();
			
			while(finalState != null)
			{
				solutionPath.add(0,finalState);
				finalState = finalState.getParent();
			}
			
			return solutionPath;
		}
		
		/**
		 * Function copys an int array and returns it
		 * @param puzzleArray
		 * @return int[]
		 */
		public int[] copyArray(int[] puzzleArray){
			
			int[] copy = new int[puzzleArray.length];
			
			for(int i= 0; i<copy.length; i++)
				copy[i] = puzzleArray[i];
			
			return copy;
		}
		
		/**
		 * Function tests a state to see if it is the goal state
		 * @param puzzleArray
		 * @return true or false
		 */
		public boolean goalTest(int[] puzzleArray){
			
			if(Arrays.equals(puzzleArray, goalState))
				return true;
			else
				return false;
		}
		
		/**
		 * Function takes a state and position of that states parent in the queue
		 *  and checks the expansion of that state to the right. If the move right is
		 * out of bounds, it returns false. If move right is a state already encountered, it returns false.
		 * If it is a new state, function will add it to the queue at index_of_node and hash table of seen states
		 * @param puzzleArray
		 * @param index_of_node
		 * @return true or false
		 */
		public boolean moveRight(int[] puzzleArray){
			
			int[] rightArray = copyArray(puzzleArray);	//make copy of puzzleArray
			
			int zeroIndex = findZero(rightArray);		//find index with zero value
			
			if(zeroIndex == 3 || zeroIndex == 7 || zeroIndex == 11 || zeroIndex == 15)	//if index is in this column,can't move right, return
				return false;
			
			moveZero(rightArray, zeroIndex, (zeroIndex+1));		//move the zero one space to the right
			
			if(goalTest(rightArray)){							//check to see if the new formation is the goal
				
				addSolutionState(rightArray, puzzleArray);		//it is the goal so add it to the hashtable and return true
				return true;
			}
			else
			{									
				if(hash.containsKey(Arrays.toString(rightArray))){		//check if the key exists in hashtable, if yes, return
					
				//	System.out.println("********************already hashed*******************");
					return false;
				}
				else
				{
					//System.out.println("New right move");
					addNewState(rightArray, puzzleArray);	//add new state to the queue and hashtable
				
					return false;
				}
			}		
		}
		
		/**
		 * Same as move right, only to the left
		 * @param puzzleArray
		 * @return true or false
		 */
		public boolean moveLeft(int[] puzzleArray){
			
			int[] leftArray = copyArray(puzzleArray);
			
			int zeroIndex = findZero(leftArray);
			
			if(zeroIndex == 0 || zeroIndex == 4 || zeroIndex == 8 || zeroIndex == 12)
				return false;
			
			moveZero(leftArray, zeroIndex, (zeroIndex-1));		//move the zero one space to the left
			
			if(goalTest(leftArray)){							//check to see if the new formation is the goal
				
				addSolutionState(leftArray, puzzleArray);			
				return true;							//found a solution
			}
			else
			{										
				if(hash.containsKey(Arrays.toString(leftArray))){		//check if the key exists in hashtable, if yes, return
					
				//	System.out.println("********************already hashed*******************");
					return false;
				}
				else
				{
					//System.out.println("New left move");

					addNewState(leftArray, puzzleArray);	
					
					return false;
				}
			}
		}
		
		/**
		 * same as above only up
		 * @param puzzleArray
		 * @return true or false
		 */
		public boolean moveUp(int[] puzzleArray){
			
			int[] upArray = copyArray(puzzleArray);
			
			int zeroIndex = findZero(upArray);
			
			if(zeroIndex == 0 || zeroIndex == 1 || zeroIndex == 2 || zeroIndex == 3)
				return false;
			
			moveZero(upArray, zeroIndex, (zeroIndex-4));		//move the zero one space up
			
			if(goalTest(upArray)){							//check to see if the new formation is the goal
				
				addSolutionState(upArray, puzzleArray);
				return true;							//found a solution
			}
			else
			{
				if(hash.containsKey(Arrays.toString(upArray))){		//check if the key exists in hashtable, if yes, return
					
			//		System.out.println("********************already hashed*******************");
					return false;
				}
				else
				{
					//System.out.println("New up move");

					addNewState(upArray, puzzleArray);	
					
					return false;
				}	
			}
		}
		
		/**
		 * Same as above, only down
		 * @param puzzleArray
		 * @return true or false
		 */
		public boolean moveDown(int[] puzzleArray){
			
			int[] downArray = copyArray(puzzleArray);
			
			int zeroIndex = findZero(downArray);
			
			if(zeroIndex == 12 || zeroIndex == 13 || zeroIndex == 14 || zeroIndex == 15)
				return false;
			
			moveZero(downArray, zeroIndex, (zeroIndex+4));		//move the zero one space down
			
			if(goalTest(downArray)){							//check to see if the new formation is the goal
				
				addSolutionState(downArray, puzzleArray);
				return true;							//found a solution
			}
			else
			{
				if(hash.containsKey(Arrays.toString(downArray))){		//check if the key exists in hashtable, if yes, return
					
		//			System.out.println("********************already hashed*******************");
					return false;
				}
				else
				{
				//	System.out.println("New down move");

					addNewState(downArray, puzzleArray);	
					

					return false;
				}
			}
		}
		
		/**
		 * Function adds a new state to the queue, preserving the depth first order, and the hashtable of states seen
		 * @param newArray
		 * @param puzzleArray
		 * @param position_in_queue
		 */
		public void addNewState(int[] newArray, int[] puzzleArray){
			
			int hVal = 0;
			
			Node newNode = new Node(newArray);					//make a new node with rightArray as key 
			
			if(whichHeuristic == "heuristicOne"){				//get the proper heuristic for the state
				//System.out.println("heuristicOne");
				hVal = getH1(newArray);
			}
			else{
				//System.out.println("heuristicTwo");
				hVal = getH2(newArray);
			}
			
			
			newNode.set_hValue(hVal);							//set heuristic in the node
			
		//	System.out.println("this is the newNode h-value: "+ newNode.get_hValue());
			
			
			newNode.setParent(hash.get(Arrays.toString(puzzleArray)));				//set parent in new node
			hash.put(Arrays.toString(newArray), newNode);								//add node to hashtable
			
			if(queue.isEmpty()){										//if queue is empty, add node
				//System.out.println("Empty queue, adding hVal "+ hVal+" to front");
				queue.add(newNode);
			}
			else{													//place node in the queue in increasing order
				for(int i = 0 ; i<queue.size(); i++)
				{
					if(hVal < queue.get(i).get_hValue()){
						
					//	System.out.println("Adding hVal "+hVal+" to position: "+ i);
						queue.add(i,newNode);
						return;
					}
				}
			//	System.out.println("Adding hVal: "+ hVal +" to the back");
				queue.add(newNode);
			}
			
			//System.out.println("this is manhattan: "+ getH2(newArray));
		//	queue.add(newNode);
			
		}
		
		/**
		 * Function only adds solution state to the hashTable. Table contains links to parent nodes
		 * @param newArray
		 * @param puzzleArray
		 */
		public void addSolutionState(int[] newArray, int[] puzzleArray){
			
			Node newNode = new Node(newArray);					//make a new node with rightArray as key 
			newNode.setParent(hash.get(Arrays.toString(puzzleArray)));				//set parent in new node
			hash.put(Arrays.toString(newArray), newNode);								//add node to hashtable
			
			solutionNode = newNode;								//set the solution node to solved state
		}

		/**
		 * Function to find where in the puzzle the zero is
		 * @param puzzleArray
		 * @return index in the int array with value zero
		 */
		public int findZero(int[] puzzleArray){
			
			for(int i=0; i<puzzleArray.length; i++)
			{
				if(puzzleArray[i] == 0)
					return i;
			}
			
			return -1;
		}
		
		/**
		 * moves the zero to another spot (swaps values)
		 * @param puzzleArray
		 * @param zeroIndex
		 * @param moveToIndex
		 */
		public void moveZero(int[] puzzleArray, int zeroIndex, int moveToIndex){
			
			int temp = puzzleArray[moveToIndex];
			puzzleArray[zeroIndex] = temp;
			puzzleArray[moveToIndex] = 0;
		}
		
		/**
		 * Method returns the number of tiles that are out of place in the puzzle state
		 * @param puzzleArray
		 * @return out of place tiles
		 */
		public int getH1(int[] puzzleArray){
			int total = 0;
			
			for(int i = 0 ; i < 16; i++)
			{
				if(puzzleArray[i] != i){
					
					total = total + 1;
				}
			}
			
		//	System.out.println("in getH1: value: "+total);

			return total;
		}
		
		/**
		 * Method returns the manhattan distance heuristic for the puzzle state. 
		 * Get the x,y coordinates for current position of each number and the x,y for where it should be.
		 * Distance is |x0-x1| +|y0-y1|
		 * @param puzzleArray
		 * @return
		 */
		public int getH2(int[] puzzleArray){
			int total = 0;
			int belongs_x = 0;
			int belongs_y = 0;
			int isAt_x = 0;
			int isAt_y = 0;
			int index_value =0;
						
			for(int i=0; i< 16; i++){
				
				index_value = puzzleArray[i];
				
				belongs_x = index_value/4;
				belongs_y = index_value%4;
				
				isAt_y = i%4;
				isAt_x = i/4;
				
				total = total + (Math.abs(belongs_x-isAt_x) + Math.abs(belongs_y- isAt_y));
			}
			
			
			return total;
		}

		/**
		 * This is just here for debugging purposes. It prints out a puzzle array
		 * @param arr
		 */
		public void printArray(int arr[]){
			
			for(int i =0; i<arr.length; i++)
				System.out.print(arr[i] + " ");
			
			System.out.print("\n\n");
			
		}


}

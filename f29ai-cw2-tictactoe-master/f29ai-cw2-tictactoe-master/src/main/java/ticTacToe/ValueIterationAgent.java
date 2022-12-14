package ticTacToe;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A Value Iteration Agent, only very partially implemented. The methods to implement are: 
 * (1) {@link ValueIterationAgent#iterate}
 * (2) {@link ValueIterationAgent#extractPolicy}
 * 
 * You may also want/need to edit {@link ValueIterationAgent#train} - feel free to do this, but you probably won't need to.
 * @author ae187
 *
 */
public class ValueIterationAgent extends Agent {

	/**
	 * This map is used to store the values of states
	 */
	Map<Game, Double> valueFunction=new HashMap<Game, Double>();
	
	/**
	 * the discount factor
	 */
	double discount=0.9;
	
	/**
	 * the MDP model
	 */
	TTTMDP mdp=new TTTMDP();
	
	/**
	 * the number of iterations to perform - feel free to change this/try out different numbers of iterations
	 */
	int k=10;
	
	
	/**
	 * This constructor trains the agent offline first and sets its policy
	 */
	public ValueIterationAgent()
	{
		super();
		mdp=new TTTMDP();
		this.discount=0.9;
		initValues();
		train();
	}
	
	
	/**
	 * Use this constructor to initialise your agent with an existing policy
	 * @param p
	 */
	public ValueIterationAgent(Policy p) {
		super(p);
		
	}

	public ValueIterationAgent(double discountFactor) {
		
		this.discount=discountFactor;
		mdp=new TTTMDP();
		initValues();
		train();
	}
	
	/**
	 * Initialises the {@link ValueIterationAgent#valueFunction} map, and sets the initial value of all states to 0 
	 * (V0 from the lectures). Uses {@link Game#inverseHash} and {@link Game#generateAllValidGames(char)} to do this. 
	 * 
	 */
	public void initValues()
	{
		
		List<Game> allGames=Game.generateAllValidGames('X');//all valid games where it is X's turn, or it's terminal.
		for(Game g: allGames)
			this.valueFunction.put(g, 0.0);
		
		
		
	}
	
	
	
	public ValueIterationAgent(double discountFactor, double winReward, double loseReward, double livingReward, double drawReward)
	{
		this.discount=discountFactor;
		mdp=new TTTMDP(winReward, loseReward, livingReward, drawReward);
	}
	
	/**
	 
	
	/*
	 * Performs {@link #k} value iteration steps. After running this method, the {@link ValueIterationAgent#valueFunction} map should contain
	 * the (current) values of each reachable state. You should use the {@link TTTMDP} provided to do this.
	 * 
	 *
	 */
	public void iterate()
	{
		for(int i=0; i<k; i++)	//looping till k
		{
			for(Game g: valueFunction.keySet()) 	//loop for taking a game state from the set of game states
			{
				if(g.isTerminal())
				{
					this.valueFunction.put(g, 0.0);		//skips g if terminal
					continue;
				}
				double max = -99999 ;	//initializing and declaring max variable
				for(Move m:g.getPossibleMoves())	//loop for getting all possible moves from the game class
				{
					double sum=0;		//initializing and declaring sum variable to 0
					for (TransitionProb tps:mdp.generateTransitions(g, m)) //loop for generating all transitions from g and m or looping over all of transitionProb objects
					{
					sum= sum + tps.prob*(tps.outcome.localReward+(discount* this.valueFunction.get(tps.outcome.sPrime)));
					//finding the sum Q in which the transition probablity is multiplied by the sum of reward and product of discount and the target state
					}
					if(sum>max)  
					{
						max=sum; 	//if sum is greater than max, update max with the sum value
					}
				}
				valueFunction.put(g, max);	//setting the value of g to max
			}
		}
	}
	
	/**This method should be run AFTER the train method to extract a policy according to {@link ValueIterationAgent#valueFunction}
	 * You will need to do a single step of expectimax from each game (state) key in {@link ValueIterationAgent#valueFunction} 
	 * to extract a policy.
	 * 
	 * @return the policy according to {@link ValueIterationAgent#valueFunction}
	 */
	public Policy extractPolicy()
	{
		Policy p = new Policy();	//creating a policy object p
		Move move = null;
			for(Game g: valueFunction.keySet()) //loop for taking a game state from the set of game states
			{
				double max = -99999 ;
				for(Move m:g.getPossibleMoves())	//loop for getting all possible moves from the game class			
				{
					double sum=0;
					for (TransitionProb tps:mdp.generateTransitions(g, m)) //all transitions from g and m
					{
					sum= sum + tps.prob*(tps.outcome.localReward+(discount* this.valueFunction.get(tps.outcome.sPrime)));
					//finding the sum Q in which the transition probablity is multiplied by the sum of reward and product of discount and the target state
					}
					if(sum>max)
					{
						max=sum; 	//if sum is greater than max then,change move to m and max to sum
						move = m;
					}
				}
				p.policy.put(g,move); 	//put the move in policy
			}
			return p;
	
		/*
		 same thing as iterate()
			you need maxMove out not the Q value
			
			when out of the move loop
			you set the move associated with current state to maxMove
			in this.policy
		 */
	}
	
	/**
	 * This method solves the mdp using your implementation of {@link ValueIterationAgent#extractPolicy} and
	 * {@link ValueIterationAgent#iterate}. 
	 */
	public void train()
	{
		/**
		 * First run value iteration
		 */
		this.iterate();
		/**
		 * now extract policy from the values in {@link ValueIterationAgent#valueFunction} and set the agent's policy 
		 *  
		 */
		
		super.policy=extractPolicy();
		
		if (this.policy==null)
		{
			System.out.println("Unimplemented methods! First implement the iterate() & extractPolicy() methods");
			//System.exit(1);
		}
		
		
		
	}

	public static void main(String a[]) throws IllegalMoveException
	{
		//Test method to play the agent against a human agent.
		ValueIterationAgent agent=new ValueIterationAgent();
		HumanAgent d=new HumanAgent();
		
		Game g=new Game(agent, d, d);
		g.playOut();
		
		
		

		
		
	}
}

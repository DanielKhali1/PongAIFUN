package Agent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.Serializable;

import Network.NeuralNetwork;

public class PongDQN extends DQN implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 8320263365854467870L;
	/**
	 * 
	 */
	private int steps;
	private int score;
	private boolean gameOver = false;
	
	private double angleToBall;
	private double ballX;
	private double ballY;
	
	private double myY;
	
	
	final static int[] topology = {10, 70, 60, 3};
		
	
	public PongDQN(double learningRate, double discountFactor)
	{
		super(topology, learningRate, discountFactor);
		
		reset();
	}

	public void reset()
	{
		setScore(0);
		steps = 0;

		ballX = 500;
		ballY = 500;
		
		getAngleToBall();
		
	}
	
	//updates the angleToBall
	public void getAngleToBall()
	{
		
	}
	
	@Override
	protected double[] getState()
	{
		//what the agent see's
		return new double[] {angleToBall, ballX, ballY, };
	}

	@Override
	public boolean isDone()
	{
		return steps >= 500 || isGameOver();
	}

	@Override
	protected double executeActionAndGetReward(int action)
	{		
		return 2;
	}
	
	public static void savePongDQN(PongDQN snek, String filePath)
	{
		try(FileOutputStream fout = new FileOutputStream(filePath);
				ObjectOutputStream oos = new ObjectOutputStream(fout);)
		{
			oos.writeObject(snek);
		}
		catch(Exception e)
		{
			System.err.println("ERROR: Failure in saving network to " + filePath + ". Reason is " + e.getMessage());
		}
	}
	
	public static PongDQN loadPongDQN(String filePath)
	{
		try(FileInputStream fin = new FileInputStream(filePath);
				ObjectInputStream ois = new ObjectInputStream(fin);)
			{
				return (PongDQN) ois.readObject();
			}
			catch(Exception e)
			{
				System.err.println("ERROR: Failure in loading network from " + filePath + ". Reason is " + e.getMessage());
			}
			return null;
	}
	
	public int getSteps(){return steps;}
	public int getScore(){return score;}
	public boolean isGameOver() {return gameOver;}
	public void setGameOver(boolean gameOver) {this.gameOver = gameOver;}
	public void setScore(int score) {this.score = score;}

}
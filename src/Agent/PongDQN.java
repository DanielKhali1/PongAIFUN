package Agent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
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
	private String name;
	
	private int steps;
	private int score;
	private boolean gameOver = false;
	
	private double ballX;
	private double ballY;
	
	private double paddleY;
	private double paddleX;
	
	private double OpponentY;
	private double OpponentX; 
	
	
	private double movingx;
	private double movingy;
	
	
	public int humanScore;
	
	
	private double rewardForHittingBall;
	private double rewardForScoring;
	private double rewardForBeingScoredOn;
	
	public int bounces;
	
	
	final static int[] topology = {4, 80, 80, 5};
		
	
	public PongDQN(double learningRate, double discountFactor, double EpsilonDecay, double hittingBall, double scoring, double scoredOn)
	{
		super(topology, learningRate, discountFactor);
		
		rewardForHittingBall = hittingBall;
		rewardForScoring = scoring;
		rewardForBeingScoredOn = scoredOn;
		
		super.setEpsilonDecay(EpsilonDecay);
		
		reset();
	}
	
	public double randomBetween(double min, double max)
	{
		Random r = new Random();
		double randomValue = min + (max-min)*r.nextDouble();
		return randomValue;
	}
	
	public void resetBall()
	{
		setMovingy(randomBetween(4, 8));
		setMovingx(9 - getMovingy());
		
		setMovingx(getMovingx() * 2);
		
		double coinFlip = randomBetween(1, 2);
		
		if(coinFlip > 1.5)
		{
			setMovingx(getMovingx() * -1);
		}
		
		setBallX((500));
		setBallY((375));
	}
	
	public void reset()
	{
		bounces = 0;
		score = 0;
		
		gameOver = false;
		
		setScore(0);
		steps = 0;
		humanScore = 0;
		
		setPaddleX(20);
		setPaddleY(300);
		
		setOpponentX(950);
		setOpponentY(300);
		
		setMovingx(0);
		setMovingy(0);
		

		setBallX(500);
		setBallY(375);
		
		resetBall();
		
	}
	
	@Override
	protected double[] getState()
	{
		//what the agent see's
		return new double[] { 
				Relu(Math.abs(getPaddleY()-getBallY())),
				Relu(Math.abs(getPaddleX()-getBallX())),
				Relu(getBallX()),
				Relu(getBallY()),
				};
	}
	
	private double Relu(double x)
	{
		return 1 / (1 + Math.pow(Math.E, x));
	}

	@Override
	public boolean isDone()
	{
		return  bounces >= 200 || isGameOver();
	}

	@Override
	protected double executeActionAndGetReward(int action)
	{		
		//System.out.println(action);
		
		double reward = 0;
				
		switch(action)
		{
		case 0: setPaddleY(getPaddleY() - 15);
			break;
		case 1: setPaddleY(getPaddleY() - 5);
			break;
		case 2: setPaddleY(getPaddleY() + 0);
			break;
		case 3: setPaddleY(getPaddleY() + 5);
			break;
		case 4: setPaddleY(getPaddleY() + 15);
			break;
		}
		
		if(paddleY > 800-130)
		{
			paddleY = 800-130;
		}
		else if(paddleY < 0)
		{
			paddleY = 0;
		}
		
		
		steps++;
		
		if(getOpponentY()+65 > getBallY() && getOpponentY() >= 0)
		{
	        setOpponentY(getOpponentY() - 10);
		}
		else if(getOpponentY()+65 < getBallY() && getOpponentY() + 130 <= 950)
		{
	        setOpponentY(getOpponentY() + 10);
		}
		
		if(getBallY() > 800 || getBallY() < 0)
		{
			setMovingy(getMovingy() * -1);
		}
		
		if(getBallY() > getOpponentY() && getBallY() < getOpponentY()+130 && getBallX() >= 950 )
		{
			if(Math.abs(getMovingx()) >= 600)
				setMovingx(getMovingx() * -1);
			else
			{
				setMovingx(getMovingx() * -1.2);
				setMovingy(getMovingy() * 1.2);
			}
			
			bounces++;
		}
		else if(getBallY() >= getPaddleY() && getBallY() <= getPaddleY() +130 && getBallX() <= 45)
		{
			if(Math.abs(getMovingx()) >= 600)
				setMovingx(getMovingx() * -1);
			else
			{
				setMovingx(getMovingx() * -1.2);
				setMovingx(getMovingx() * 1.2);
			}
			bounces++;
			
			reward += rewardForHittingBall;
		}
		else if(getBallX() > 1000)
		{
			setScore(getScore()+1);
			resetBall();
			
			if(score > 10)
			{
				gameOver = true;
			}
			reward += rewardForScoring;
		}
		else if(getBallX() < 0)
		{
			humanScore++;
			resetBall();
			
			if(humanScore > 10)
			{
				gameOver = true;
			}
			
			reward += rewardForBeingScoredOn;
		}
		
		setBallX(getBallX() + getMovingx());
		setBallY(getBallY() + getMovingy());
		
		
		return reward;
		
		
		
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

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getBallX() {
		return ballX;
	}

	public void setBallX(double ballX) {
		this.ballX = ballX;
	}

	public double getBallY() {
		return ballY;
	}

	public void setBallY(double ballY) {
		this.ballY = ballY;
	}

	public double getPaddleY() {
		return paddleY;
	}

	public void setPaddleY(double paddleY) {
		this.paddleY = paddleY;
	}

	public double getPaddleX() {
		return paddleX;
	}

	public void setPaddleX(double paddleX) {
		this.paddleX = paddleX;
	}

	public double getOpponentY() {
		return OpponentY;
	}

	public void setOpponentY(double opponentY) {
		OpponentY = opponentY;
	}

	public double getOpponentX() {
		return OpponentX;
	}

	public void setOpponentX(double opponentX) {
		OpponentX = opponentX;
	}

	public double getMovingx() {
		return movingx;
	}

	public void setMovingx(double movingx) {
		this.movingx = movingx;
	}

	public double getMovingy() {
		return movingy;
	}

	public void setMovingy(double movingy) {
		this.movingy = movingy;
	}

}
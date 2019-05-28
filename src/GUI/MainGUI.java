package GUI;

import java.util.Random;

import Agent.PongDQN;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

public class MainGUI extends Application
{
	
	
	Text Player1Score = new Text("Score:\t 0");
	Text Player2Score = new Text("Score:\t 0");
	Circle ball = new Circle(15);
	
	
	Rectangle paddle1 = new Rectangle(30, 130);
	Rectangle paddle2 = new Rectangle(30, 130);
	Pane pane = new Pane();
	Scene scene = new Scene(pane, 1000, 800);
	
	Button settingsButton = new Button("  i  ");
	
	Timeline timeline = new Timeline();
	
	PongDQN agent1 = new PongDQN(0.001, 0.995);
	int iteration = 0;
	
	double speed = 15;
	
	private boolean up = false;
	private boolean down = false;
	
	double movingx = 0;
	double movingy = 0;
	
	public int humanScore = 0;

	
	public void start(Stage primaryStage)
	{
		initializeGameObjects();

		
		primaryStage.setTitle("Pong DQN");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(KeyFrameContent(Duration.millis(40)));
		timeline.play();
		
	}
	
	public KeyFrame KeyFrameContent(Duration duration)
	{
		KeyFrame keyFrame = new KeyFrame(duration, action ->
		{
			/*try {
				Thread.sleep(speed);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/
			// Boolean Value that Determines whether you can go back on top of yourself

			//clears the display grid
			
			/*agent1.step();
			
	
			if(agent1.scored())
			{
				iteration++;
				agent1.reset();
			}
			*/
			
			realPlayerMovement();
			
			if(ball.getLayoutX() > 1000)
			{
				agent1.setScore(agent1.getScore()+1);
				resetBall();
			}
			else if(ball.getLayoutX() < 0)
			{
				humanScore++;
				resetBall();
			}
			
			if(ball.getLayoutY() > 800 || ball.getLayoutY() < 0)
			{
				//movingx *= -1;
				movingy *= -1;
			}
			
			ball.setLayoutX(movingx + ball.getLayoutX());
			ball.setLayoutY(movingy + ball.getLayoutY());			
			
			//adds to score if snake eats objective item
			Player1Score.setText("Score: \t" + agent1.getScore());
			Player2Score.setText("Score: \t" + humanScore);
		    
		    //---------------------------- AI Integration ------------------------------- //


		});
		
		return keyFrame;
	}
	
	
	public void realPlayerMovement()
	{
		
		scene.setOnKeyPressed(e -> {
		    if (e.getCode() == KeyCode.W) 
		    {
		    	up = true;
		    }
		    if(e.getCode() == KeyCode.S)
		    {
		    	down = true;
		    }
		});
		
		scene.setOnKeyReleased(e -> {
		    if (e.getCode() == KeyCode.W) 
		    {
		    	up = false;
		    }
		    if(e.getCode() == KeyCode.S)
		    {
		    	down = false;
		    }
		});
		
		
		if(up)
		{
	        paddle2.setLayoutY(paddle2.getLayoutY() - 15);
		}
		else if(down)
		{
			paddle2.setLayoutY(paddle2.getLayoutY() + 15);
		}
		
	}
	
	public void resetBall()
	{
		
		
		movingx = randomBetween(0, speed*2);
		movingy = 2 * speed - movingx;
		movingx -= speed;
		
		ball.setLayoutX(500);
		ball.setLayoutY(375);

	}
	
	public double randomBetween(double min, double max)
	{
		Random r = new Random();
		double randomValue = min + (max-min)*r.nextDouble();
		return randomValue;
	}
	
	public void initializeGameObjects()
	{
		pane.setStyle("-fx-background-color: 'black';");
		
		Player1Score.setLayoutX(100);
		Player1Score.setLayoutY(750);
		Player1Score.setFill(Color.WHITE);
		Player1Score.setStyle("-fx-font-size: 16;");
		pane.getChildren().add(Player1Score);
		
		Player2Score.setLayoutX(800);
		Player2Score.setLayoutY(750);
		Player2Score.setFill(Color.WHITE);
		Player2Score.setStyle("-fx-font-size: 16;");
		pane.getChildren().add(Player2Score);
		

		ball.setFill(Color.WHITE);
		pane.getChildren().add(ball);
		
		
		paddle1.setLayoutX(20);
		paddle1.setLayoutY(300);
		paddle1.setFill(Color.WHITE);
		pane.getChildren().add(paddle1);
		
		paddle2.setLayoutX(950);
		paddle2.setLayoutY(300);
		paddle2.setFill(Color.WHITE);
		pane.getChildren().add(paddle2);
		
		settingsButton.setLayoutX(480);
		settingsButton.setLayoutY(740);
		settingsButton.setStyle("-fx-background-color: '#494949'; -fx-text-fill: 'white'; -fx-padding: 10;");

		pane.getChildren().add(settingsButton);
		
		resetBall();
		
		settingsButton.setOnMouseEntered(e->{
			settingsButton.setStyle("-fx-background-color: '#e0e0e0'; -fx-text-fill: 'black'; -fx-padding: 11;");
		});
		
		settingsButton.setOnMouseExited(e->{
			settingsButton.setStyle("-fx-background-color: '#494949'; -fx-text-fill: 'white'; -fx-padding: 10;");
		});
		
		settingsButton.setOnMouseClicked(e->{
			settingsButton.setStyle("-fx-background-color: '#303030'; -fx-text-fill: 'darkgrey'; -fx-padding: 10;");
			
			//popupsettingsMenu
			Stage tempStage = new Stage();
			Pane tempPane = new Pane();
			tempPane.setStyle("-fx-background-color: '#424242';");			
			
			//------------------- whatever tweak stuff -----------------//
			

			
			
			//------------------- whatever tweak stuff -----------------//
			
			Scene tempScene = new Scene(tempPane, 300, 400);
			tempStage.setScene(tempScene);
			tempStage.setTitle("Settings");
			tempStage.show();
		});
		
		
		
		
		
	}
	
	public static void main(String[] args){launch(args);}
}

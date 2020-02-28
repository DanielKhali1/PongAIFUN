package GUI;

import java.io.FileInputStream;
import java.util.Random;

import Agent.PongDQN;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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
	Pane gamePane = new Pane();
	Scene scene = new Scene(gamePane, 1000, 800);
	
	Text ballSpeedTxt;
	Text numberOfBouncesTxt;
	int numberOfBounces = 0;
	
	Button settingsButton = new Button("  i  ");
	
	Timeline timeline = new Timeline();
	
	PongDQN agent1;
	int iteration = 0;
	
	double speed = 15;
	
	private boolean up = false;
	private boolean down = false;
	
	Pane menuPane = new Pane();
	
	double movingx = 0;
	double movingy = 0;
	
	public int humanScore = 0;
	public int staticScore = 0;

	Pane dqnSetupPane;
	boolean dqnPaneCreated = false;
	
	boolean dynamic = false;
	boolean train = false;
	boolean save = false;

	
	
	public void start(Stage primaryStage)
	{

		
		primaryStage.setTitle("Pong DQN");
		primaryStage.setScene(scene);
		primaryStage.show();
		
		primaryStage.setResizable(false);		

		
		initializeGameObjects();
		initializeMenu();
	
	}

	
	private void initializeMenu() 
	{
		menuPane.setPrefSize(1000, 800);
		menuPane.setStyle("-fx-background-color: 'black';");
		
		Text pongTitle = new Text("PONG");
		pongTitle.setStyle("-fx-font-size: 75;");
		pongTitle.setFill(Color.WHITE);
		pongTitle.setLayoutX(425);
		pongTitle.setLayoutY(175);
		menuPane.getChildren().add(pongTitle);
		
		Button playAgainstStaticAI = new Button("Play Against Static AI");
		playAgainstStaticAI.setLayoutX(370);
		playAgainstStaticAI.setLayoutY(250);
		playAgainstStaticAI.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 30; -fx-padding: 25;");
		
		playAgainstStaticAI.setOnMouseEntered(e->
		{
			playAgainstStaticAI.setStyle("-fx-background-color: 'white'; -fx-text-fill: 'black'; -fx-border-color: grey; -fx-font-size: 30; -fx-padding: 25;");
		});
		playAgainstStaticAI.setOnMouseExited(e->
		{
			playAgainstStaticAI.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 30; -fx-padding: 25;");
		});
		playAgainstStaticAI.setOnAction(e->
		{
			menuPane.setVisible(false);
			timeline.play();
		});
		menuPane.getChildren().add(playAgainstStaticAI);		

		Button playAgainstDQN = new Button("Play Against DQN AI");
		playAgainstDQN.setLayoutX(370);
		playAgainstDQN.setLayoutY(400);
		playAgainstDQN.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 30; -fx-padding: 25;");
		
		playAgainstDQN.setOnMouseEntered(e->
		{
			playAgainstDQN.setStyle("-fx-background-color: 'white'; -fx-text-fill: 'black'; -fx-border-color: grey; -fx-font-size: 30; -fx-padding: 25;");
		});
		playAgainstDQN.setOnMouseExited(e->
		{
			playAgainstDQN.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 30; -fx-padding: 25;");
		});
		playAgainstDQN.setOnAction(e->
		{
			dynamic = true;
			menuPane.setVisible(false);
			
			if(!dqnPaneCreated)
			{
				createDqnPane();
			}
			
			dqnSetupPane.setVisible(true);

			
			//timeline.play();
		});
		menuPane.getChildren().add(playAgainstDQN);
		
		
		
		
		gamePane.getChildren().add(menuPane);
	}

	public KeyFrame KeyFrameContent(Duration duration)
	{
		KeyFrame keyFrame = new KeyFrame(duration, action ->
		{
			if(!dynamic)
			{	
				realPlayerMovement();
				
				
				//ai paddle
				if(paddle1.getLayoutY()+65 > ball.getLayoutY() && paddle1.getLayoutY() >= 0)
				{
			        paddle1.setLayoutY(paddle1.getLayoutY() - 10);
				}
				else if(paddle1.getLayoutY()+65 < ball.getLayoutY() && paddle1.getLayoutY() + 130 <= 950)
				{
			        paddle1.setLayoutY(paddle1.getLayoutY() + 10);
				}
				
				
				
				
				if(ball.getLayoutY() > 800 || ball.getLayoutY() < 0)
				{
					movingy *= -1;
				}
				
				if(ball.getLayoutY() > paddle2.getLayoutY() && ball.getLayoutY() < paddle2.getLayoutY()+130 && ball.getLayoutX() >= 950 )
				{
					if(Math.abs(movingx) >= 600)
						movingx *= -1;
					else
					{
						movingx *= -1.2;
						movingy *= 1.2;
					}
					
					numberOfBounces++;
				}
				else if(ball.getLayoutY() > paddle1.getLayoutY() && ball.getLayoutY() < paddle1.getLayoutY()+130 && ball.getLayoutX() <= 45)
				{
					if(Math.abs(movingx) >= 600)
						movingx *= -1;
					else
					{
						movingx *= -1.2;
						movingx *= 1.2;
					}
					
					numberOfBounces++;
				}
				else if(ball.getLayoutX() > 1000)
				{
					staticScore++;
					resetBall();
				}
				else if(ball.getLayoutX() < 0)
				{
					humanScore++;
					resetBall();
				}
				
				
				ball.setLayoutX(movingx + ball.getLayoutX());
				ball.setLayoutY(movingy + ball.getLayoutY());		
				
				Player1Score.setText("Score: \t" + staticScore);
				numberOfBouncesTxt.setText("Bounces: " + numberOfBounces);

			}
			else
			{
				agent1.step();
				
				if(agent1.isDone())
				{
					agent1.reset();
				}
				
				ball.setLayoutX(agent1.getBallX());
				ball.setLayoutY(agent1.getBallY());
				
				paddle2.setLayoutX(agent1.getOpponentX());
				paddle2.setLayoutY(agent1.getOpponentY());
				
				paddle1.setLayoutX(agent1.getPaddleX());
				paddle1.setLayoutY(agent1.getPaddleY());
				
				if(humanScore > 10 || agent1.getScore() > 10)
				{
					agent1.reset();
				}
				System.out.println("AgentScore: " + agent1.getScore());
				//System.out.println("PlayerScore: " + humanScore);

				
				numberOfBouncesTxt.setText("Bounces: " + agent1.bounces);
				Player1Score.setText("Score: \t" + agent1.getScore());
				Player2Score.setText("Score: \t" + agent1.humanScore);
			}

			Player2Score.setText("Score: \t" + humanScore);
			
			ballSpeedTxt.setText("Ball Speed: " + Math.abs((int)movingx));
		});
		
		return keyFrame;
	}
	
	public void createDqnPane()
	{
		dqnPaneCreated = true;
		dqnSetupPane = new Pane();
		dqnSetupPane.setStyle("-fx-background-color: 'black'");
		dqnSetupPane.setPrefSize(1050, 850);

		
		
		Text dqnSetupTitle = new Text("DQN Setup");
		dqnSetupTitle.setLayoutX(400);
		dqnSetupTitle.setLayoutY(150);
		dqnSetupTitle.setFill(Color.WHITE);
		dqnSetupTitle.setStyle("-fx-font-size: 50;");
		dqnSetupPane.getChildren().add(dqnSetupTitle);
		
		Button btnBack = new Button(" < ");
		btnBack.setLayoutX(20);
		btnBack.setLayoutY(20);
		btnBack.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 30; -fx-padding: 10;");
		
		btnBack.setOnMouseEntered(a->
		{
			btnBack.setStyle("-fx-background-color: 'white'; -fx-text-fill: 'black'; -fx-border-color: grey; -fx-font-size: 30; -fx-padding: 10;");
		});
		btnBack.setOnMouseExited(a->
		{
			btnBack.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 30; -fx-padding: 10;");
		});
		
		btnBack.setOnAction(a ->
		{
			dqnSetupPane.setVisible(false);
			menuPane.setVisible(true);
		});
		
		dqnSetupPane.getChildren().add(btnBack);
		
		Text txtAgents = new Text("Agents");
		
		txtAgents.setLayoutX(150);
		txtAgents.setLayoutY(250);
		txtAgents.setFill(Color.WHITE);
		txtAgents.setStyle("-fx-font-size: 25;");
		dqnSetupPane.getChildren().add(txtAgents);
		
		
		ScrollPane scroll = new ScrollPane();
		VBox scrollMeat = new VBox();
		scrollMeat.setStyle("-fx-background-color: 'black';");
		scroll.setContent(scrollMeat);
		scrollMeat.setPrefSize(300, 450);
		scroll.setStyle("-fx-border-color: 'white'");
		
		scroll.setLayoutX(50);
		scroll.setLayoutY(270);
		dqnSetupPane.getChildren().add(scroll);
		
		gamePane.getChildren().add(dqnSetupPane);
		
		Pane createNewPane = new Pane();
		createNewPane.setPrefSize(570, 450);
		createNewPane.setLayoutX(400);
		createNewPane.setLayoutY(270);
		createNewPane.setStyle("-fx-border-color: 'grey'; -fx-background-color: 'black'; -fx-border-width: 2;");
		dqnSetupPane.getChildren().add(createNewPane);
		
		Button btnCreateNew = new Button("Create New Agent");
		btnCreateNew.setLayoutX(400);
		btnCreateNew.setLayoutY(200);
		btnCreateNew.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		dqnSetupPane.getChildren().add(btnCreateNew);
		
		btnCreateNew.setOnMouseEntered( e ->
		{
			btnCreateNew.setStyle("-fx-background-color: 'white'; -fx-text-fill: 'black'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		});
		
		btnCreateNew.setOnMouseExited( e ->
		{
			btnCreateNew.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		});
		

		
		
		Text txtDQNPar = new Text("DQN Parameters");
		txtDQNPar.setLayoutX(20);
		txtDQNPar.setLayoutY(40);
		txtDQNPar.setStyle("-fx-font-size: 20;");
		txtDQNPar.setFill(Color.WHITE);
		createNewPane.getChildren().add(txtDQNPar);
		
		Text txtEpsilonDecay = new Text("Epsilon Decay");
		txtEpsilonDecay.setLayoutX(20);
		txtEpsilonDecay.setLayoutY(70+20);
		txtEpsilonDecay.setFill(Color.WHITE);
		createNewPane.getChildren().add(txtEpsilonDecay);
		
		TextField tfEpsilonDecay = new TextField("0.99999");
		tfEpsilonDecay.setLayoutX(20);
		tfEpsilonDecay.setLayoutY(80+20);
		tfEpsilonDecay.setStyle("-fx-border-color: 'black'");
		createNewPane.getChildren().add(tfEpsilonDecay);
		tfEpsilonDecay.setDisable(true);
		
		Text txtLearningRate = new Text("Learning Rate");
		txtLearningRate.setLayoutX(20);
		txtLearningRate.setLayoutY(130+20);
		txtLearningRate.setFill(Color.WHITE);
		createNewPane.getChildren().add(txtLearningRate);
		
		TextField tfLearningRate = new TextField("0.001");
		tfLearningRate.setLayoutX(20);
		tfLearningRate.setLayoutY(140+20);
		tfLearningRate.setStyle("-fx-border-color: 'black'");
		createNewPane.getChildren().add(tfLearningRate);
		tfLearningRate.setDisable(true);
		
		Text txtDiscountFactor = new Text("Discount Factor");
		txtDiscountFactor.setLayoutX(20);
		txtDiscountFactor.setLayoutY(190+20);
		txtDiscountFactor.setFill(Color.WHITE);
		createNewPane.getChildren().add(txtDiscountFactor);
		
		TextField tfDiscountFactor = new TextField("0.995");
		tfDiscountFactor.setLayoutX(20);
		tfDiscountFactor.setLayoutY(200+20);
		tfDiscountFactor.setStyle("-fx-border-color: 'black'");
		createNewPane.getChildren().add(tfDiscountFactor);
		tfDiscountFactor.setDisable(true);
		
		Text txtRewards = new Text("Rewards");
		txtRewards.setLayoutX(300);
		txtRewards.setLayoutY(40);
		txtRewards.setStyle("-fx-font-size: 20;");
		txtRewards.setFill(Color.WHITE);
		createNewPane.getChildren().add(txtRewards);
		
		
		Text txtHittingBall = new Text("Hitting Ball");
		txtHittingBall.setLayoutX(300);
		txtHittingBall.setLayoutY(70+20);
		txtHittingBall.setFill(Color.WHITE);
		createNewPane.getChildren().add(txtHittingBall);
		
		TextField tfHittingBall = new TextField("1");
		tfHittingBall.setLayoutX(300);
		tfHittingBall.setLayoutY(80+20);
		tfHittingBall.setStyle("-fx-border-color: 'black'");
		createNewPane.getChildren().add(tfHittingBall);
		tfHittingBall.setDisable(true);
		
		Text txtScoredOn = new Text("Scored On");
		txtScoredOn.setLayoutX(300);
		txtScoredOn.setLayoutY(130+20);
		txtScoredOn.setFill(Color.WHITE);
		createNewPane.getChildren().add(txtScoredOn);
		
		TextField tfScoredOn = new TextField("-1");
		tfScoredOn.setLayoutX(300);
		tfScoredOn.setLayoutY(140+20);
		tfScoredOn.setStyle("-fx-border-color: 'black'");
		createNewPane.getChildren().add(tfScoredOn);
		tfScoredOn.setDisable(true);
		
		Text txtScoring = new Text("Scoring");
		txtScoring.setLayoutX(300);
		txtScoring.setLayoutY(190+20);
		txtScoring.setFill(Color.WHITE);
		createNewPane.getChildren().add(txtScoring);
		
		TextField tfScoring = new TextField("3");
		tfScoring.setLayoutX(300);
		tfScoring.setLayoutY(200+20);
		tfScoring.setStyle("-fx-border-color: 'black'");
		createNewPane.getChildren().add(tfScoring);
		tfScoring.setDisable(true);
		
		
		Button btnTrain = new Button("Train");
		btnTrain.setLayoutX(350);
		btnTrain.setLayoutY(350);
		btnTrain.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		createNewPane.getChildren().add(btnTrain);
		
		scroll.setOnMouseClicked( e ->{
			
		});
		
		btnTrain.setOnMouseEntered( e ->
		{
			if(train)
				btnTrain.setStyle("-fx-background-color: 'white'; -fx-text-fill: 'black'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		});
		
		btnTrain.setOnMouseExited( e ->
		{
			if(train)
				btnTrain.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		});
		
		btnTrain.setOnAction(e ->{
			if(train)
			{
				double learningRate = Double.parseDouble(tfLearningRate.getText());
				double discountFactor = Double.parseDouble(tfDiscountFactor.getText());
				double EpsilonDecay = Double.parseDouble(tfEpsilonDecay.getText());
				double hittingBall = Double.parseDouble(tfHittingBall.getText());
				double scoring = Double.parseDouble(tfScoring.getText());
				double scoredOn = Double.parseDouble(tfScoredOn.getText());
				
				//	public PongDQN(double learningRate, double discountFactor, double EpsilonDecay, double hittingBall, double scoring, double scoredOn)
				agent1 = new PongDQN(learningRate, discountFactor, EpsilonDecay, hittingBall, scoring, scoredOn);
				
				Pane tempPane = new Pane();
				tempPane.setStyle("-fx-background-color: 'black'");
				Stage tempStage = new Stage();
				Scene tempScene = new Scene(tempPane, 500, 500);
				tempStage.setScene(tempScene);
				
				Image gif;
				try {
					gif = new Image(new FileInputStream("loadingpong.gif"));


				ImageView giffyboi = new ImageView(gif);
				giffyboi.setLayoutX(125);
				giffyboi.setLayoutY(125);
				tempPane.getChildren().add(giffyboi);
				}
				catch(Exception a)
				{
					System.out.println("houston theres a problem");
				}
				
				Text txtTraining = new Text("Training...");
				txtTraining.setLayoutX(230);
				txtTraining.setLayoutY(450);
				txtTraining.setFill(Color.WHITE);
				tempPane.getChildren().add(txtTraining);

				tempStage.show();
//				
				new Thread(new Runnable() 
				{

					@Override
					public void run() 
					{
						
						for(int round = 0; round < 5; round++)
						{
							double averageScore = 0;
							int maxScore = 0;
							double averageEpsilon = 0;
							int totalSteps = 0;
							int averageBounce = 0;
							for(int gameIndex = 0; gameIndex < 500; gameIndex++)
							{
								System.out.println("round " + round + " game " + gameIndex);
								agent1.reset();

								while(!agent1.isDone())
								{
									totalSteps++;
									averageEpsilon += agent1.getEpsilon();
									agent1.step();
									
									if(humanScore > 10)
									{
										break;
									}

									
									//System.out.println(agent1.humanScore + " " + agent1.getScore());
									//System.out.println(agent1.getSteps());
									//System.out.println(round + "," + averageScore + "," + maxScore + "," + averageEpsilon+"\n");
								}
								averageBounce += agent1.bounces;
								averageScore += agent1.getScore();

								if(agent1.getScore() > maxScore)
								{
									maxScore = agent1.getScore();
								}
							}
							averageBounce /= 500;
							averageScore /= 500;
							averageEpsilon /= (double)totalSteps;
							//System.out.println(round + "," + averageScore + "," + maxScore + "," + averageEpsilon);
							System.out.println(round + "," + averageBounce + "," + averageScore + "," + maxScore + "," + averageEpsilon+"\n");

						
						Platform.runLater(new Runnable() 
						{
							@Override
							public void run() 
							{
								tempStage.close();
							}
							
						});
						
					}
					
					}
					
			}).start();
				
				
				
				
			}
		});
		
		
		Button btnSave = new Button("Save");
		btnSave.setLayoutX(450);
		btnSave.setLayoutY(350);
		btnSave.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		createNewPane.getChildren().add(btnSave);
		
		btnSave.setOnMouseEntered( e ->
		{
			btnSave.setStyle("-fx-background-color: 'white'; -fx-text-fill: 'black'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		});
		
		btnSave.setOnMouseExited( e ->
		{
			btnSave.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		});
		
		btnCreateNew.setOnAction(e -> {
			createNewPane.setStyle("-fx-border-color: 'white'; -fx-background-color: 'black'; -fx-border-width: 2;");
			scroll.setStyle("-fx-border-color: 'black';");
			tfScoring.setDisable(false);
			tfScoredOn.setDisable(false);
			tfHittingBall.setDisable(false);
			tfDiscountFactor.setDisable(false);
			tfLearningRate.setDisable(false);
			tfEpsilonDecay.setDisable(false);
			
			train = true;

		});
		
		Button btnDQNvsSTATIC = new Button("DQN vs Static");
		btnDQNvsSTATIC.setLayoutX(427);
		btnDQNvsSTATIC.setLayoutY(470);
		btnDQNvsSTATIC.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		createNewPane.getChildren().add(btnDQNvsSTATIC);
		
		btnDQNvsSTATIC.setOnMouseEntered( e ->
		{
			btnDQNvsSTATIC.setStyle("-fx-background-color: 'white'; -fx-text-fill: 'black'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		});
		
		btnDQNvsSTATIC.setOnMouseExited( e ->
		{
			btnDQNvsSTATIC.setStyle("-fx-background-color: 'black'; -fx-text-fill: 'white'; -fx-border-color: white; -fx-font-size: 20; -fx-padding: 10;");
		});
		
		btnDQNvsSTATIC.setOnAction( e ->
		{
			dynamic = true;
			menuPane.setVisible(false);
			dqnSetupPane.setVisible(false);
			timeline.play();
		});
		
		
		
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
		
		
		if(up && paddle2.getLayoutY() > 0)
		{
	        paddle2.setLayoutY(paddle2.getLayoutY() - 15);
		}
		else if(down && paddle2.getLayoutY() < 650)
		{
			paddle2.setLayoutY(paddle2.getLayoutY() + 15);
		}
		
		
		gamePane.setOnMouseMoved(e ->
		{
			paddle2.setLayoutY(e.getY()-65);
		});
	
		
	}
	
	public void resetBall()
	{
		
		movingy = randomBetween(4, 8);
		movingx = 9 - movingy;
		
		movingx *= 2;
		
		double coinFlip = randomBetween(1, 2);
		
		if(coinFlip > 1.5)
		{
			movingx *= -1;
		}
		
		
		
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
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.getKeyFrames().add(KeyFrameContent(Duration.millis(40)));
		
		gamePane.setStyle("-fx-background-color: 'black';");
		
		Player1Score.setLayoutX(100);
		Player1Score.setLayoutY(750);
		Player1Score.setFill(Color.WHITE);
		Player1Score.setStyle("-fx-font-size: 16;");
		gamePane.getChildren().add(Player1Score);
		
		Player2Score.setLayoutX(800);
		Player2Score.setLayoutY(750);
		Player2Score.setFill(Color.WHITE);
		Player2Score.setStyle("-fx-font-size: 16;");
		gamePane.getChildren().add(Player2Score);
		

		ball.setFill(Color.WHITE);
		gamePane.getChildren().add(ball);
		
		
		paddle1.setLayoutX(20);
		paddle1.setLayoutY(300);
		paddle1.setFill(Color.WHITE);
		gamePane.getChildren().add(paddle1);
		
		paddle2.setLayoutX(950);
		paddle2.setLayoutY(300);
		paddle2.setFill(Color.WHITE);
		gamePane.getChildren().add(paddle2);
		
		Button exitButton = new Button(" X ");
		exitButton.setStyle("-fx-background-color: '#494949'; -fx-text-fill: 'white'; -fx-padding: 10;");
		exitButton.setLayoutX(430);
		exitButton.setLayoutY(741);
		gamePane.getChildren().add(exitButton);
		
		exitButton.setOnMouseEntered(e->{
			exitButton.setStyle("-fx-background-color: 'red'; -fx-text-fill: 'white'; -fx-padding: 11;");
		});
		
		exitButton.setOnMouseExited(e->{
			exitButton.setStyle("-fx-background-color: '#494949'; -fx-text-fill: 'white'; -fx-padding: 10;");
		});
		
		exitButton.setOnMouseClicked(e->{
			exitButton.setStyle("-fx-background-color: '#303030'; -fx-text-fill: 'darkgrey'; -fx-padding: 10;");
			
			timeline.stop();
			
			//probably should put a "are you sure?" popup
			
			menuPane.setVisible(true);
		});
		
		settingsButton.setLayoutX(470);
		settingsButton.setLayoutY(740);
		settingsButton.setStyle("-fx-background-color: '#494949'; -fx-text-fill: 'white'; -fx-padding: 10;");
		gamePane.getChildren().add(settingsButton);

		
		ballSpeedTxt = new Text("Ball Speed: " + Math.abs((int)movingx));
		ballSpeedTxt.setLayoutX(470);
		ballSpeedTxt.setLayoutY(20);
		ballSpeedTxt.setStyle("-fx-font-size: 10");
		ballSpeedTxt.setFill(Color.WHITE);
		gamePane.getChildren().add(ballSpeedTxt);

		
		numberOfBouncesTxt = new Text("Bounces: " + numberOfBounces);
		numberOfBouncesTxt.setLayoutX(400);
		numberOfBouncesTxt.setLayoutY(20);
		numberOfBouncesTxt.setStyle("-fx-font-size: 10");
		numberOfBouncesTxt.setFill(Color.WHITE);
		gamePane.getChildren().add(numberOfBouncesTxt);
		
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

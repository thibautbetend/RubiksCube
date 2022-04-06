package application;

import java.io.File;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

/**
*
* @author tbetend, mai/juin 2020
*/

public class TestRubikFX extends Application {
    private final BorderPane pane=new BorderPane();
    private Rubik rubik;
    private Moves moves=new Moves();
    private LocalTime time=LocalTime.now();
    private Timeline timer;
    private final StringProperty clock = new SimpleStringProperty("00:00:00");
    private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(ZoneId.systemDefault());
    private String monCube = "";
    private String musicFile = "src\\application\\Finish.mp4";
	private Media sound = new Media(new File(musicFile).toURI().toString());
	private MediaPlayer mediaPlayer = new MediaPlayer(sound);
	
    public void start(Stage stage) {
    	if(monCube.equals(null)||monCube.equals(""))
    	{
    		monCube = "Cube2.obj";
    	}
        rubik=new Rubik(monCube);
        

        // create toolbars
        ToolBar tbTop=new ToolBar(new Button("U"),new Button("Ui"),new Button("F"),
                                  new Button("Fi"),new Separator(),new Button("Y"),
                                  new Button("Yi"),new Button("Z"),new Button("Zi"));
        // restart button
        Button bReset=new Button("Restart");
        bReset.setDisable(true);
        bReset.setOnMouseClicked(e->{
            if(moves.getNumMoves()>0){
            	Alert alert = 
            	        new Alert(AlertType.WARNING, 
            	            "Voulez-vous recommencer? " +
            	            "L'avancement sera perdu.",
            	             ButtonType.OK, 
            	             ButtonType.CANCEL);
            	alert.setTitle("Reset?");
            	
            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.isPresent() && result.get() == ButtonType.OK) {
                    moves.getMoves().clear();
                    rubik.doReset();
                    time=LocalTime.now();
                    timer.playFromStart();
            	}
            }
        });
        // scramble button
        Button bSc=new Button("Scramble");
        bSc.setOnMouseClicked(e->{
            if(moves.getNumMoves()>0){
            	Alert alert = 
            	        new Alert(AlertType.WARNING, 
            	            "Voulez-vous mélanger le cube? " +
            	            "L'avancement sera perdu.",
            	             ButtonType.OK, 
            	             ButtonType.CANCEL);
            	alert.setTitle("Mélangeur");
            	
            	Optional<ButtonType> result = alert.showAndWait();
            	if (result.isPresent() && result.get() == ButtonType.OK) {
                    rubik.doReset();
                    doScramble();
            	}
            } else {
                doScramble();
            }
        });
        ChangeListener<Number> clockLis=(ov,l,l1)->clock.set(LocalTime.ofNanoOfDay(l1.longValue()).format(fmt));
        // replay button
        Button bReplay=new Button("Replay");
        bReplay.setDisable(true);        
        rubik.isOnReplaying().addListener((ov,b,b1)->{
            if(b&&!b1){
                rubik.getTimestamp().removeListener(clockLis);
                if(!rubik.isSolved().get()){
                    timer.play();
                }
            }
        });

        bReplay.setOnMouseClicked(e->{
            timer.stop();
            rubik.getTimestamp().addListener(clockLis);
            rubik.doReset();
            doReplay();
        });
        // sequence button
        Button bSeq=new Button("Sequence");
        bSeq.setOnMouseClicked(e->{
        	
        	TextField txt;
        	if(moves.getNumMoves()>0)
        	{
        		txt = new TextField(moves.getSequence());
        	}
        	else
        	{
        		txt = new TextField("");
        		
        	}
        	Alert alert = 
        	        new Alert(AlertType.INFORMATION, 
        	            "",
        	             ButtonType.OK, 
        	             ButtonType.CANCEL);
        	txt.setPromptText("Inscrivez votre séquence ici");
        	alert.setHeaderText("Votre séquence :");
        	alert.setGraphic(txt);
        	
        	alert.setTitle("Sequence");
        	
        	Optional<ButtonType> result = alert.showAndWait();
        	if (result.isPresent() && result.get() == ButtonType.OK) {
        		moves.getMoves().clear();
                rubik.doSequence(txt.getText().toString(), true);
        	}
        });
        // change cube
        Button bCube2=new Button("2x2");
        bCube2.setOnMouseClicked(e->{
        	monCube = "Cube2.obj";
        	stage.close();
        	Platform.runLater( () -> {
				try {
					new TestRubikFX().start( new Stage(), monCube );
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} );
        	
        });
        // change cube
        Button bCube3=new Button("3x3");
        bCube3.setOnMouseClicked(e->{
        	monCube = "Cube3.obj";
        	stage.close();
        	Platform.runLater( () -> {
				try {
					new TestRubikFX().start( new Stage(), monCube );
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			} );
        });
        
        Label lSolved=new Label("Solved");
        lSolved.setVisible(false);
        Label lSimulated=new Label();
        lSimulated.textProperty().bind(rubik.getPreviewFace());
        tbTop.getItems().addAll(new Separator(),bReset,bSc,bSeq, bReplay, bCube2, bCube3,
        		new Separator(),lSolved,new Separator(),lSimulated);
        pane.setTop(tbTop);
        //E for 3x3
        ToolBar tbBottom=new ToolBar(new Button("B"),new Button("Bi"),new Button("D"),
                                     new Button("Di"));
        if(monCube == "Cube3.obj")
        	tbBottom.getItems().addAll(new Button("E"), new Button("Ei"));
        	
        rubik.getLastRotation().addListener((ov,v,v1)->{
            if(!v1.isEmpty() && !rubik.isOnReplaying().get()){
                moves.addMove(new Move(v1, LocalTime.now().minusNanos(time.toNanoOfDay()).toNanoOfDay()));
            }
        });
        // move counter
        Label lMov=new Label();
        rubik.getCount().addListener((ov,v,v1)->{
            bReset.setDisable(moves.getNumMoves()==0);
            bReplay.setDisable(moves.getNumMoves()==0);
            lMov.setText("Movements: "+(v1.intValue()+1));
        });
        tbBottom.getItems().addAll(new Separator(),lMov);
        
        
        // timer
        Label lTime=new Label();
        lTime.textProperty().bind(clock);
        tbBottom.getItems().addAll(new Separator(),lTime);

        timer=new Timeline(new KeyFrame(Duration.ZERO, e->{
            clock.set(LocalTime.now().minusNanos(time.toNanoOfDay()).format(fmt));
        }),new KeyFrame(Duration.seconds(1)));
        timer.setCycleCount(Animation.INDEFINITE);
        // if solved
        rubik.isSolved().addListener((ov,b,b1)->{
            if(b1){
            	sound = new Media(new File(musicFile).toURI().toString());
            	mediaPlayer = new MediaPlayer(sound);
            	mediaPlayer.play();
            	
                lSolved.setVisible(true);
                
                timer.stop();
            	Alert alert = 
            	        new Alert(AlertType.INFORMATION, 
            	            "",
            	             ButtonType.OK);
            	alert.setHeaderText("Tu gagnes en " + (rubik.getCount().get()+1) + " coups !!! ");
            	alert.setTitle("BRAVO !!!");
            	alert.show();
                moves.setTimePlay(LocalTime.now().minusNanos(time.toNanoOfDay()).toNanoOfDay());
                System.out.println(moves);
            } else {
                lSolved.setVisible(false);
            }
        });
        
        time=LocalTime.now();
        timer.playFromStart();
        
        
        
        pane.setBottom(tbBottom);
        ToolBar tbRight=new ToolBar(new Button("R"),new Button("Ri"),new Separator(),
                                    new Button("X"),new Button("Xi"));
        tbRight.setOrientation(Orientation.VERTICAL);

        pane.setRight(tbRight);
        //M et S pour 3x3
        ToolBar tbLeft=new ToolBar(new Button("L"),new Button("Li"));
        if(monCube == "Cube3.obj")
        	tbLeft.getItems().addAll(new Button("M"), new Button("Mi"), new Button("S"), new Button("Si"));

        
        tbLeft.setOrientation(Orientation.VERTICAL);
        pane.setLeft(tbLeft);
        
        pane.setCenter(rubik.getSubScene());
        
        pane.getChildren().stream()
        .filter(withToolbars())
        .forEach(tb->{
            ((ToolBar)tb).getItems().stream()
                .filter(withMoveButtons())
                .forEach(n->{
                    Button b=(Button)n;
                    b.setOnAction(e->rotateFace(b.getText()));
                    b.hoverProperty().addListener((ov,b0,b1)->updateArrow(b.getText(),b1));
                });
        });
        rubik.isOnRotation().addListener((ov,b,b1)->{
            pane.getChildren().stream()
            .filter(n->(n instanceof ToolBar))
            .forEach(tb->tb.setDisable(b1));
        });
        
        final Scene scene = new Scene(pane, 880, 680, true);
        scene.addEventHandler(MouseEvent.ANY, rubik.eventHandler);
        scene.setFill(Color.ALICEBLUE);
        stage.setTitle("Rubik's Cube - JavaFX3D");
        stage.setScene(scene);
        stage.show();
    }
    
    // called on button click
    private void rotateFace(final String btRot){
        pane.getChildren().stream()
            .filter(withToolbars())
            .forEach(tb->{
                ((ToolBar)tb).getItems().stream()
                    .filter(withMoveButtons().and(withButtonTextName(btRot)))
                    .findFirst().ifPresent(n->rubik.isHoveredOnClick().set(((Button)n).isHover()));
            });
        rubik.rotateFace(btRot);
    }
    // called on button hover
    private void updateArrow(String face, boolean hover){
        rubik.updateArrow(face,hover);
    }
    // called from button Scramble
    private void doScramble(){
        pane.getChildren().stream().filter(withToolbars()).forEach(setDisable(true));
        rubik.doScramble();
        rubik.isOnScrambling().addListener((ov,v,v1)->{
            if(v && !v1){
                System.out.println("scrambled!");
                pane.getChildren().stream().filter(withToolbars()).forEach(setDisable(false));
                moves=new Moves();
                time=LocalTime.now();
                timer.playFromStart();
            }
        });
    }
    private void doReplay(){
        pane.getChildren().stream().filter(withToolbars()).forEach(setDisable(true));
        rubik.doReplay(moves.getMoves());
        rubik.isOnReplaying().addListener((ov,v,v1)->{
            if(v && !v1){
                System.out.println("replayed!");
                pane.getChildren().stream().filter(withToolbars()).forEach(setDisable(false));
            }
        });
    }
    private static Predicate<Node> withToolbars(){
        return n -> (n instanceof ToolBar);
    }
    private static Consumer<Node> setDisable(boolean disable){
        return n -> n.setDisable(disable);
    }
    private static Predicate<Node> withMoveButtons(){
        return n -> (n instanceof Button) && ((Button)n).getText().length()<=2;
    }
    private static Predicate<Node> withButtonTextName(String text){
        return n -> ((Button)n).getText().equals(text);
    }
	public void start(Stage stage, String taille) throws Exception {
		monCube = taille;
		start(stage);
	}
	public static void main(String[] args) {
		launch(args);
	}
}
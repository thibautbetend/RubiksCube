package application;

import com.javafx.experiments.jfx3dviewer.AutoScalingGroup;
import com.javafx.experiments.jfx3dviewer.Xform;
import javafx.event.EventHandler;
import javafx.scene.AmbientLight;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.SubScene;
import javafx.scene.input.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

/**
 * 3D Content Model for Viewer App. 
 * Contains the 3D subscene and everything related to it: light, cameras, axis
 * Based on com.javafx.experiments.jfx3dviewer.ContentMode
 *
 * @author tbetend, mai/juin 2020
 */
public class ContentModel {
    private SubScene subScene;
    private final Group root3D = new Group();
    private final PerspectiveCamera camera = new PerspectiveCamera(true);
    private final Rotate yUpRotate = new Rotate(180,0,0,0,Rotate.X_AXIS); // y Up
    private final Rotate cameraLookXRotate = new Rotate(0,0,0,0,Rotate.X_AXIS);
    private final Rotate cameraLookZRotate = new Rotate(0,0,0,0,Rotate.Z_AXIS);
    private final Translate cameraPosition = new Translate(0,0,0);
    private final Xform cameraXform = new Xform();
    private final Xform cameraXform2 = new Xform();
    private final AutoScalingGroup autoScalingGroup = new AutoScalingGroup(2);
    private final AmbientLight ambientLight = new AmbientLight(Color.WHITE);
    private final PointLight light1 = new PointLight(Color.WHITE);
    private final double paneW, paneH;
    private double dimModel=100d;
    private double mousePosX, mousePosY;
    private double mouseOldX, mouseOldY;
    private double mouseDeltaX, mouseDeltaY;
    private final double modifierFactor = 0.3;
            
    public ContentModel(double paneW, double paneH, double dimModel) {
        this.paneW=paneW;
        this.paneH=paneH;
        this.dimModel=dimModel;
        buildCamera();
        buildSubScene();        
        buildAxes();
        addLights();        
    }

    private void buildCamera() {
        camera.setNearClip(1.0);
        camera.setFarClip(10000.0);
        camera.setFieldOfView(2d*dimModel/3d);
        camera.getTransforms().addAll(yUpRotate,cameraPosition,
                                      cameraLookXRotate,cameraLookZRotate);
        cameraXform.getChildren().add(cameraXform2);
        cameraXform2.getChildren().add(camera);
        cameraPosition.setZ(-2d*dimModel);
        root3D.getChildren().add(cameraXform);
        /*
        Rotate camera to show isometric view X right, Y top, Z 120?? left-down from each
        
        Three ways: 
        
        One: Calculate vector and angle of rotation to combine two rotations:
        
        Qy[Pi/6].Qx[-Pi/6] (note angles for camera are opposite to model rotations)
        
        With some calculations:
        (http://jperedadnr.blogspot.com.es/2013/06/leap-motion-controller-and-javafx-new.html) 
        * camera.setRotationAxis(new Point3D(-0.694747,0.694747,0.186157));
        * camera.setRotate(42.1812);
        
        Two: with rotation matrix, taking all the previous transformations, by appending all of
        them in a single matrix before prepending these two rotations:
        
        * Affine affineCamIni=new Affine();    
        * camera.getTransforms().stream().forEach(affineCamIni::append);
        * affineCamIni.prepend(new Rotate(-30, Rotate.X_AXIS));
        * affineCamIni.prepend(new Rotate(30, Rotate.Y_AXIS));
        * camera.getTransforms().setAll(affineCamIni);
        
        Three: setting first RX rotation, then RY:        
        */
        cameraXform.setRx(-30.0);
        cameraXform.setRy(30);

    }
    
    private void buildSubScene() {
        root3D.getChildren().add(autoScalingGroup);
        
        subScene = new SubScene(root3D,paneW,paneH,true,javafx.scene.SceneAntialiasing.BALANCED);
        subScene.setCamera(camera);
        subScene.setFill(Color.CADETBLUE);
        setListeners(true);
    }
    
    private void buildAxes() {
        double length = 2d*dimModel;
        double width = dimModel/100d;
        double radius = 2d*dimModel/100d;
        final PhongMaterial redMaterial = new PhongMaterial();
        redMaterial.setDiffuseColor(Color.DARKRED);
        redMaterial.setSpecularColor(Color.RED);
        final PhongMaterial greenMaterial = new PhongMaterial();
        greenMaterial.setDiffuseColor(Color.DARKGREEN);
        greenMaterial.setSpecularColor(Color.GREEN);
        final PhongMaterial blueMaterial = new PhongMaterial();
        blueMaterial.setDiffuseColor(Color.DARKBLUE);
        blueMaterial.setSpecularColor(Color.BLUE);
        
        Sphere xSphere = new Sphere(radius);
        Sphere ySphere = new Sphere(radius);
        Sphere zSphere = new Sphere(radius);
        xSphere.setMaterial(redMaterial);
        ySphere.setMaterial(greenMaterial);
        zSphere.setMaterial(blueMaterial);
        
        xSphere.setTranslateX(dimModel);
        ySphere.setTranslateY(dimModel);
        zSphere.setTranslateZ(dimModel);
        
        Box xAxis = new Box(length, width, width);
        Box yAxis = new Box(width, length, width);
        Box zAxis = new Box(width, width, length);
        xAxis.setMaterial(redMaterial);
        yAxis.setMaterial(greenMaterial);
        zAxis.setMaterial(blueMaterial);
        
        autoScalingGroup.getChildren().addAll(xAxis, yAxis, zAxis);
        autoScalingGroup.getChildren().addAll(xSphere, ySphere, zSphere);
    }
    
    private void addLights(){
        root3D.getChildren().add(ambientLight);
        root3D.getChildren().add(light1);
        light1.setTranslateX(dimModel*0.6);
        light1.setTranslateY(dimModel*0.6);
        light1.setTranslateZ(dimModel*0.6);
    }
    //handle camera
    private final EventHandler<MouseEvent> mouseEventHandler = event -> {
        double xFlip = -1.0, yFlip=1.0; // yUp
        if (event.getEventType() == MouseEvent.MOUSE_PRESSED) {
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();

        } else if (event.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            double modifier = event.isControlDown()?0.1:event.isShiftDown()?3.0:1.0;

            mouseOldX = mousePosX;
            mouseOldY = mousePosY;
            mousePosX = event.getSceneX();
            mousePosY = event.getSceneY();
            mouseDeltaX = (mousePosX - mouseOldX);
            mouseDeltaY = (mousePosY - mouseOldY);

            if(event.isMiddleButtonDown() || (event.isPrimaryButtonDown() && event.isSecondaryButtonDown())) {
                cameraXform2.setTx(cameraXform2.t.getX() + xFlip*mouseDeltaX*modifierFactor*modifier*0.3); 
                cameraXform2.setTy(cameraXform2.t.getY() + yFlip*mouseDeltaY*modifierFactor*modifier*0.3);
            }
            else if(event.isPrimaryButtonDown()) {
                cameraXform.setRy(cameraXform.ry.getAngle() - yFlip*mouseDeltaX*modifierFactor*modifier*2.0);
                cameraXform.setRx(cameraXform.rx.getAngle() + xFlip*mouseDeltaY*modifierFactor*modifier*2.0);
            }
            else if(event.isSecondaryButtonDown()) {
                double z = cameraPosition.getZ();
                double newZ = z - xFlip*(mouseDeltaX+mouseDeltaY)*modifierFactor*modifier;
                cameraPosition.setZ(newZ);
            }
        }
    };
    //handle scroll from different type
    private final EventHandler<ScrollEvent> scrollEventHandler = event -> {
        if (event.getTouchCount() > 0) { // touch pad scroll
            cameraXform2.setTx(cameraXform2.t.getX() - (0.01*event.getDeltaX()));  // -
            cameraXform2.setTy(cameraXform2.t.getY() + (0.01*event.getDeltaY()));  // -
        } else { // mouse wheel
            double z = cameraPosition.getZ()-(event.getDeltaY()*0.2);
            z = Math.max(z,-10d*dimModel);
            z = Math.min(z,0);
            cameraPosition.setZ(z);
        }
    };
    //handle zoom
    private final EventHandler<ZoomEvent> zoomEventHandler = event -> {
        if (!Double.isNaN(event.getZoomFactor()) && event.getZoomFactor() > 0.8 && event.getZoomFactor() < 1.2) {
            double z = cameraPosition.getZ()/event.getZoomFactor();
            z = Math.max(z,-10d*dimModel);
            z = Math.min(z,0);
            cameraPosition.setZ(z);
        }
    };
    
    private void setListeners(boolean addListeners){
        if(addListeners){
            subScene.addEventHandler(MouseEvent.ANY, mouseEventHandler);
            subScene.addEventHandler(ZoomEvent.ANY, zoomEventHandler);
            subScene.addEventHandler(ScrollEvent.ANY, scrollEventHandler);
        } else {
            subScene.removeEventHandler(MouseEvent.ANY, mouseEventHandler);
            subScene.removeEventHandler(ZoomEvent.ANY, zoomEventHandler);
            subScene.removeEventHandler(ScrollEvent.ANY, scrollEventHandler);
        }
    }
    
    /*
    Public methods
    */
    public SubScene getSubScene() { return subScene; }    
    public void setContent(Node content) { autoScalingGroup.getChildren().add(content);  }
    public void stopEventHandling(){ setListeners(false); }
    public void resumeEventHandling(){ setListeners(true); }
    public void resetCam(){
        cameraXform.ry.setAngle(0.0);
        cameraXform.rx.setAngle(0.0);
        cameraPosition.setZ(-2d*dimModel);
        cameraXform2.t.setX(0.0);
        cameraXform2.t.setY(0.0);
        cameraXform.setRx(-30.0);
        cameraXform.setRy(30);
    }

}

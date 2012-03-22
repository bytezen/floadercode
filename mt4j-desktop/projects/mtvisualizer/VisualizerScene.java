package mtvisualizer;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.TransformSpace;
import org.mt4j.components.clipping.Clip;
import org.mt4j.components.visibleComponents.shapes.MTPolygon;
import org.mt4j.components.visibleComponents.widgets.MTSlider;
import org.mt4j.components.visibleComponents.widgets.MTTextArea;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.font.FontManager;
import org.mt4j.util.font.IFont;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import oscP5.OscMessage;
import peasy.PeasyCam;

public class VisualizerScene extends AbstractScene {
	
	AbstractMTApplication app;
	VisualizerComponent vizComp;
	PeasyCam cam;
	private VisualizerScene thisScene;

	public VisualizerScene(AbstractMTApplication app, String name) {
		super(app, name);
		this.app = app;
		thisScene = this;
		this.registerGlobalInputProcessor(new CursorTracer(app, this));
		
		vizComp = new VisualizerComponent(app);
		vizComp.translate(new Vector3D(400,400,0,1));
		this.getCanvas().addChild(vizComp);
		addSlider();
		//this.getCanvas().addChild(new SimpleComponent(app));
	}
	
	public void addHelloWorld()
	{

		MTColor white = new MTColor(255,255,255);
		
		IFont fontArial = FontManager.getInstance().createFont(app, "arial.ttf", 
				50, 	//Font size
				white);	//Font color
		//Create a textfield
		MTTextArea textField = new MTTextArea(app, fontArial); 
		
		textField.setNoStroke(true);
		textField.setNoFill(true);
		
		textField.setText("Hello World!");
		//Center the textfield on the screen
		textField.setPositionGlobal(new Vector3D(app.width/2f, app.height/2f));
		//Add the textfield to our canvas
		this.getCanvas().addChild(textField);
	}

	public void addSlider()
	{
		MTSlider slider = new MTSlider(app, 100, 100, 200, 38, 0.05f, 2.0f);
        slider.setValue(1.0f);
        //slider.rotateZ(new Vector3D(), 90, TransformSpace.LOCAL);
        //slider.translate(new Vector3D(-7, 325));
        slider.setStrokeColor(new MTColor(255,255,255));
        slider.setFillColor(new MTColor(220,220,220));
        slider.getKnob().setFillColor(new MTColor(70,70,70));
        slider.getKnob().setStrokeColor(new MTColor(70,70,70));
        slider.addPropertyChangeListener("value", new PropertyChangeListener() {
			public void propertyChange(PropertyChangeEvent p) {
				thisScene.sliderEvent(0, (Float)p.getNewValue());
			}
		});
        this.getCanvas().addChild(slider);
        slider.getKnob().sendToFront();
	}
	
	public void sliderEvent(int sliderIndex, float value)
	{
		System.out.println(value);
	}
	
	public void onEnter() {}
	
	public void onLeave() {}
	
	public void oscEvent(OscMessage msg){
		vizComp.oscEvent(msg);
	}
}

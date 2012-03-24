package mtvisualizer.scenes;

import org.mt4j.AbstractMTApplication;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.sceneManagement.transition.SlideTransition;

import oscP5.OscMessage;

public abstract class AbstractVisualizationScene extends AbstractScene implements IOSCScene {

	SlideTransition slideLeftTransition;
	
	
	public AbstractVisualizationScene(AbstractMTApplication app, String name) {
		super(app, name);
		this.setTransition(new SlideTransition(app, 800, true));
		
		// TODO Auto-generated constructor stub
	}

}

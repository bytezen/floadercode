package org.mt4j.test.testUtil;

import org.mt4j.AbstractMTApplication;
import org.mt4j.sceneManagement.AbstractScene;

public class DummyScene extends AbstractScene {

	public DummyScene(AbstractMTApplication mtApplication, String name) {
		super(mtApplication, name);
	}

	@Override
	public void onEnter() {}
	
	@Override
	public void onLeave() {}

}

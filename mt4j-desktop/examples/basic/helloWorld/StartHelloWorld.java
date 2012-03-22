package basic.helloWorld;

import org.mt4j.MTApplication;

import processing.core.PFont;

public class StartHelloWorld extends MTApplication {
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		initialize();
	}
	@Override
	public void startUp() {
		addScene(new HelloWorldScene(this, "Hello World Scene"));
	}
}

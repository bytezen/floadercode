package mtphysics;

import java.util.ArrayList;

import netP5.NetAddress;
import oscP5.OscMessage;
import oscP5.OscP5;
import java.util.List;

import mtvisualizer.MTVisualizerConstants;
import mtvisualizer.components.VisualizationComponent;

import org.jbox2d.collision.AABB;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.ContactListener;
import org.jbox2d.dynamics.World;
import org.jbox2d.dynamics.contacts.ContactPoint;
import org.jbox2d.dynamics.contacts.ContactResult;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.MTComponent;
import org.mt4j.components.visibleComponents.shapes.MTRectangle;
import org.mt4j.components.visibleComponents.widgets.MTBackgroundImage;
import org.mt4j.input.IMTInputEventListener;
import org.mt4j.input.inputData.AbstractCursorInputEvt;
import org.mt4j.input.inputData.MTInputEvent;
import org.mt4j.input.inputProcessors.globalProcessors.CursorTracer;
import org.mt4j.sceneManagement.AbstractScene;
import org.mt4j.util.MTColor;
import org.mt4j.util.camera.MTCamera;
import org.mt4j.util.math.ToolsMath;
import org.mt4j.util.math.Vector3D;
import org.mt4j.util.math.Vertex;

import floader.looksgood.ani.Ani;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.core.PVector;

import wblut.core.processing.WB_Render;
import wblut.hemesh.core.HE_Mesh;
import wblut.hemesh.creators.HEC_Creator;
import wblut.hemesh.creators.HEC_Sphere;

import advanced.physics.physicsShapes.IPhysicsComponent;
import advanced.physics.physicsShapes.PhysicsPolygon;
import advanced.physics.physicsShapes.PhysicsRectangle;
import advanced.physics.util.PhysicsHelper;
import mtphysics.UpdatePhysicsAction;
import advanced.simpleParticles.ImageParticle;
import advanced.simpleParticles.MTParticleSystem;

public class PhysicsScene extends AbstractScene {
	private float timeStep = 1.0f / 60.0f;
	private int constraintIterations = 10;
	
	/** THE CANVAS SCALE **/
	private float scale = 20;
	private AbstractMTApplication app;
	private World world;
	PhysicsRectangle physRect;
	
	
	NetAddress remoteAddress;
	
	private MTComponent physicsContainer;
	
	final MTParticleSystem mtPs;
	final PImage texture;
	PVector particleLocation;
	PhysicsHemesh[] objects;
	private String backgroundImagesPath =  "mtphysics" + AbstractMTApplication.separator + "data" +  AbstractMTApplication.separator ;
	
	MTBackgroundImage background;
	
	public PhysicsScene(AbstractMTApplication mtApplication, String name, OscP5 oscP5) {
		super(mtApplication, name);
		this.app = mtApplication;
		

		objects = new PhysicsHemesh[8];
		remoteAddress = new NetAddress(MTVisualizerConstants.OSC_REMOTE_ADDR, MTVisualizerConstants.OSC_REMOTE_PORT);
		
		float worldOffset = 10; //Make Physics world slightly bigger than screen borders
		//Physics world dimensions
		AABB worldAABB = new AABB(new Vec2(-worldOffset, -worldOffset), new Vec2((app.width)/scale + worldOffset, (app.height)/scale + worldOffset));
		Vec2 gravity = new Vec2(0, 0);
		boolean sleep = true;
		//Create the physics world
		this.world = new World(worldAABB, gravity, sleep);
		
		
		this.registerGlobalInputProcessor(new CursorTracer(app, this));
		
		//Update the positions of the components according the the physics simulation each frame
		this.registerPreDrawAction(new UpdatePhysicsAction(world, timeStep, constraintIterations, scale));
		
		physicsContainer = new MTComponent(app);
		//Scale the physics container. Physics calculations work best when the dimensions are small (about 0.1 - 10 units)
		//So we make the display of the container bigger and add in turn make our physics object smaller
		physicsContainer.scale(scale, scale, 1, Vector3D.ZERO_VECTOR);
		this.getCanvas().addChild(physicsContainer);
		
		//Create borders around the screen
		this.createScreenBorders(physicsContainer);
		
		//Add background image
		background = new MTBackgroundImage(this.app, this.app.loadImage(backgroundImagesPath + "beach.jpg"), false);
		physicsContainer.addChild(background);
		
		//Create meshes
		float restitution = 0.2f;
		int i = 0;
		objects[i] = new PhysicsHemesh(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, restitution, scale, i, oscP5, new MTColor(195, 54, 44), PhysicsHemesh.SPHERE);
		PhysicsHelper.addDragJoint(world, objects[i], objects[i].getBody().isDynamic(), scale);
		physicsContainer.addChild(objects[i]);
		
		i = 1;
		objects[i] = new PhysicsHemesh(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, restitution, scale, i, oscP5, new MTColor(255, 134, 66), PhysicsHemesh.SPHERE);
		PhysicsHelper.addDragJoint(world, objects[i], objects[i].getBody().isDynamic(), scale);
		physicsContainer.addChild(objects[i]);
		
		i = 2;
		objects[i] = new PhysicsHemesh(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, restitution, scale, i, oscP5, new MTColor(102, 141, 60), PhysicsHemesh.SPHERE);
		PhysicsHelper.addDragJoint(world, objects[i], objects[i].getBody().isDynamic(), scale);
		physicsContainer.addChild(objects[i]);
		
		i = 3;
		objects[i] = new PhysicsHemesh(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, restitution, scale, i, oscP5, new MTColor(177, 221, 161), PhysicsHemesh.SPHERE);
		PhysicsHelper.addDragJoint(world, objects[i], objects[i].getBody().isDynamic(), scale);
		physicsContainer.addChild(objects[i]);
		
		i = 4;
		objects[i] = new PhysicsHemesh(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, restitution, scale, i, oscP5, new MTColor(0, 151, 172), PhysicsHemesh.SPHERE);
		PhysicsHelper.addDragJoint(world, objects[i], objects[i].getBody().isDynamic(), scale);
		physicsContainer.addChild(objects[i]);
		
		i = 5;
		objects[i] = new PhysicsHemesh(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, restitution, scale, i, oscP5, new MTColor(60, 214, 230), PhysicsHemesh.CUBE);
		PhysicsHelper.addDragJoint(world, objects[i], objects[i].getBody().isDynamic(), scale);
		physicsContainer.addChild(objects[i]);
		
		i = 6;
		objects[i] = new PhysicsHemesh(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, restitution, scale, i, oscP5, new MTColor(177, 221, 161), PhysicsHemesh.CUBE);
		PhysicsHelper.addDragJoint(world, objects[i], objects[i].getBody().isDynamic(), scale);
		physicsContainer.addChild(objects[i]);
		
		i = 7;
		objects[i] = new PhysicsHemesh(app, new Vector3D(ToolsMath.getRandom(60, mtApplication.width-60), ToolsMath.getRandom(60, mtApplication.height-60)), 50, world, 1.0f, 0.3f, restitution, scale, i, oscP5, new MTColor(195, 183, 172), PhysicsHemesh.CUBE);
		PhysicsHelper.addDragJoint(world, objects[i], objects[i].getBody().isDynamic(), scale);
		physicsContainer.addChild(objects[i]);
		
//		//Create rectangle
//		physRect = new PhysicsRectangle(new Vector3D(100,300), 500, 75, app, world, 1f, 0.4f, 0.0f, scale);
//		MTColor col = new MTColor(ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255),ToolsMath.getRandom(60, 255));
//		physRect.setFillColor(col);
//		physRect.setStrokeColor(col);
//		PhysicsHelper.addDragJoint(world, physRect, physRect.getBody().isDynamic(), scale);
//		physicsContainer.addChild(physRect);
		
		//Create particle
		String path = "mtphysics" + AbstractMTApplication.separator + "data" + AbstractMTApplication.separator;
		mtPs = new MTParticleSystem(getMTApplication(), 0,0, mtApplication.width, mtApplication.height);
		mtPs.attachCamera(new MTCamera(getMTApplication()));
		mtPs.setPickable(false);
		particleLocation = new PVector(app.width/2, app.height/2);
		this.getCanvas().addChild(mtPs);
		texture = getMTApplication().loadImage(path + "particle.png");
		
		//Collision detection
		world.setContactListener(new ContactListener() {
			@Override
			public void result(ContactResult point) {
//				System.out.println("Result contact");
			}
			//@Override
			@Override
			public void remove(ContactPoint point) {
//				System.out.println("remove contact");
			}
			//@Override
			@Override
			public void persist(ContactPoint point) {
//				System.out.println("persist contact");
			}
			//@Override
			@Override
			public void add(ContactPoint point) {
//				/*
				Shape shape1 = point.shape1;
				Shape shape2 = point.shape2;
				
				
				final Body body1 = shape1.getBody();
				final Body body2 = shape2.getBody();
				Object userData1 = body1.getUserData();
				Object userData2 = body2.getUserData();
				
				if (userData1 instanceof IPhysicsComponent  && userData2 instanceof IPhysicsComponent) { //Check for ball/star collision
					IPhysicsComponent physObj1 = (IPhysicsComponent) userData1;
					IPhysicsComponent physObj2 = (IPhysicsComponent) userData2;
//					System.out.println("Collided: " + mt4jObj1 + " with " + mt4jObj2);
					if (physObj1 instanceof MTComponent && physObj2 instanceof MTComponent) {
						MTComponent comp1 = (MTComponent) physObj1;
						MTComponent comp2 = (MTComponent) physObj2;
						
						if(comp1.getName().contains("border") || comp2.getName().contains("border"))
						{
							mtPs.getParticleSystem().addParticle(new ImageParticle(getMTApplication(), new PVector(point.position.x * scale, point.position.y * scale), texture));
							//physObj2.getBody().setLinearVelocity(new Vec2(0, 0));
							//physObj1.getBody().setLinearVelocity(new Vec2(0, 0));
						}
							//System.out.println(comp1.getName() + " hit " + comp2.getName());
					}
				}
				
			}
		});
	}
	
	public void oscEvent(OscMessage msg) {
		System.out.println("osc message received");
	}
	
	public void drawAndUpdate(PGraphics g, long timeDelta)
	{
		//Show particle system
		mtPs.getParticleSystem().addParticle(new ImageParticle(getMTApplication(), particleLocation, texture));
	
		//Determine distances for objects
		for(int i=0;i<objects.length;i++)
		{
			float distance = objects[i].getCenterPointRelativeToParent().distance2D(new Vector3D(app.width/(2 * scale), app.height/(2 * scale)));
			if(distance < 13)
			{
				objects[i].addEnergy((13.0f - distance)/195.0f);			
			}
		}
		
		super.drawAndUpdate(g, timeDelta);
	}
	
	public void keyPressed(int key)
	{
		
	}
	
	private void createScreenBorders(MTComponent parent){
		//Left border 
		float borderWidth = 50f;
		float borderHeight = app.height;
		Vector3D pos = new Vector3D(-(borderWidth/2f) , app.height/2f);
		PhysicsRectangle borderLeft = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderLeft.setName("borderLeft");
		parent.addChild(borderLeft);
		//Right border
		pos = new Vector3D(app.width + (borderWidth/2), app.height/2);
		PhysicsRectangle borderRight = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderRight.setName("borderRight");
		parent.addChild(borderRight);
		//Top border
		borderWidth = app.width;
		borderHeight = 50f;
		pos = new Vector3D(app.width/2, -(borderHeight/2));
		PhysicsRectangle borderTop = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderTop.setName("borderTop");
		parent.addChild(borderTop);
		//Bottom border
		pos = new Vector3D(app.width/2 , app.height + (borderHeight/2));
		PhysicsRectangle borderBottom = new PhysicsRectangle(pos, borderWidth, borderHeight, app, world, 0,0,0, scale);
		borderBottom.setName("borderBottom");
		parent.addChild(borderBottom);
	}

	@Override
	public void onEnter() {
	}
	
	@Override
	public void onLeave() {	
	}

	public void noteObjEvent(int note, int vel) {
		//System.out.println(note + ", " + vel);
		if(vel > 0 && note < objects.length)objects[note].emphasize();
	}
	

}

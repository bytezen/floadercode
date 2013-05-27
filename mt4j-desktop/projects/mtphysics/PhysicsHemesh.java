package mtphysics;

import java.util.Iterator;

import mtvisualizer.MTVisualizerConstants;
import mtvisualizer.components.VisualizationComponent;
import netP5.NetAddress;

import org.jbox2d.collision.MassData;
import org.jbox2d.collision.shapes.CircleDef;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.World;
import org.mt4j.AbstractMTApplication;
import org.mt4j.components.visibleComponents.shapes.MTEllipse;
import org.mt4j.input.inputProcessors.IGestureEventListener;
import org.mt4j.input.inputProcessors.MTGestureEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragEvent;
import org.mt4j.input.inputProcessors.componentProcessors.dragProcessor.DragProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.rotateProcessor.RotateProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleEvent;
import org.mt4j.input.inputProcessors.componentProcessors.scaleProcessor.ScaleProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapAndHoldProcessor.TapAndHoldProcessor;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapEvent;
import org.mt4j.input.inputProcessors.componentProcessors.tapProcessor.TapProcessor;
import org.mt4j.util.MTColor;
import org.mt4j.util.math.Vector3D;

import floader.looksgood.ani.Ani;
import floader.visuals.VisualConstants;

import advanced.physics.physicsShapes.IPhysicsComponent;

import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.core.processing.WB_Render;
import wblut.geom.core.WB_Plane;
import wblut.geom.core.WB_Point3d;
import wblut.geom.core.WB_Vector3d;
import wblut.hemesh.core.HE_Face;
import wblut.hemesh.core.HE_Mesh;
import wblut.hemesh.core.HE_Selection;
import wblut.hemesh.creators.*;
import wblut.hemesh.modifiers.HEM_Lattice;
import wblut.hemesh.modifiers.HEM_Noise;
import wblut.hemesh.modifiers.HEM_Slice;
import wblut.hemesh.modifiers.HEM_VertexExpand;
import oscP5.OscMessage;
import oscP5.OscP5;

public class PhysicsHemesh extends MTEllipse implements IPhysicsComponent{
	private float angle;
	
	private World world;
	private Body body;
	private float density;
	private float friction;
	private float restituion;
	private int id;
	private PApplet app;
	OscP5 oscP5;
	
	WB_Render meshRenderer;
	HE_Mesh mesh;
	HE_Selection selection;
	Iterator<HE_Face> fItr;
	HE_Face f;
	HEC_Creator creator;
	MTColor shapeColor;
	static final int SPHERE = 0;
	static final int CUBE= 1;
	static final int TUBE = 2;
	static final int TORUS = 3;
	
	
	//TODO erst zu world addedn when added to parent and root=canvas?

	Ani emphasisAni;
	float emphasis = 0;
	float defaultMaxEmphasis = .3f;
	float maxEmphasis = defaultMaxEmphasis;
	float durationIn = .04f;
	float durationOut = 1.1f;
	
	float energy;
	float maxEnergy = 1;
	float energyDrainAmount = .003f;
	float scaleAmount = 1;
	
	
	public PhysicsHemesh(PApplet applet, Vector3D centerPoint, float radius,
			World world, float density, float friction, float restitution, float worldScale, int id, OscP5 oscP5, MTColor shapeColor, int shapeType
	) {
		//super(applet, centerPoint, radius/(float)worldScale, radius/(float)worldScale);
		super(applet, centerPoint, radius/ worldScale, radius/ worldScale);
		this.oscP5 = oscP5;
		this.app = applet;
		this.id = id;
		this.angle = 0;
		this.world = world;
		this.density = density;
		this.friction = friction;
		this.restituion = restitution;
		this.setNoFill(true);
		this.setNoStroke(true);
		
		this.setGestureAllowance(RotateProcessor.class, false);
		this.setGestureAllowance(ScaleProcessor.class, false);
		
//		this.registerInputProcessor(new TapAndHoldProcessor(app));
//		this.setGestureAllowance(TapAndHoldProcessor.class, true);
//		this.addGestureListener(TapAndHoldProcessor.class, new IGestureEventListener() {
//			@Override
//			public boolean processGestureEvent(MTGestureEvent ge) {
//				TapAndHoldEvent te = (TapAndHoldEvent) ge;
//				
//				System.out.println(te.isHoldComplete());
//				return true;
//			}
//		});
		
		BodyDef dymBodyDef = new BodyDef();
		dymBodyDef.position = new Vec2(centerPoint.x / worldScale, centerPoint.y / worldScale);
		this.bodyDefB4CreationCallback(dymBodyDef);
		this.body = this.world.createBody(dymBodyDef);
		
		CircleDef circleDef = new CircleDef();
//		circleDef.radius = radius/(float)worldScale; 
		 //FIXME HACK so textured circles really connect to other bodies
//		circleDef.radius = radius/(float)worldScale - 2/(float)worldScale; 
		circleDef.radius = radius/ worldScale;
		if (density != 0.0f){
			circleDef.density 		= density;
			circleDef.friction 		= friction;
			circleDef.restitution 	= restituion;
		}
		this.circleDefB4CreationCallback(circleDef);
		this.body.createShape(circleDef);
		this.body.setMassFromShapes();
		this.setPositionGlobal(centerPoint);
		this.body.setUserData(this);
		this.setUserData("box2d", this.body); 
		
		//hemesh
		if(shapeType == SPHERE)
			creator = new HEC_Sphere().setRadius(2.8).setUFacets(8).setVFacets(8).setCenter(0, 0, 0);
		else if(shapeType == CUBE)
			creator = new HEC_Cube().setRadius(2.1).setDepthSegments(3).setWidthSegments(2);
		else if(shapeType == TUBE)
			creator = new HEC_Tube().setOuterRadius(2.4).setInnerRadius(.1).setSteps(3).setFacets(3).setHeight(4);
		else if(shapeType == TORUS)
			creator = new HEC_Torus().setTorusFacets(8).setTubeFacets(8).setRadius(1, 2);
			
		mesh = new HE_Mesh(creator);
		//mesh.modify(new HEM_Slice().setCap(true).setPlane(new WB_Plane(new WB_Point3d(0, -2, 0), new WB_Vector3d(1, 1, 0))));
		//mesh.modify(new HEM_Slice().setCap(true).setPlane(new WB_Plane(new WB_Point3d(0, 2, 0), new WB_Vector3d(0, -1, 0))));
		meshRenderer = new WB_Render(applet);
		this.shapeColor = shapeColor;
	}


	public void setScaleAmount(float amount)
	{
		scaleAmount = amount;
	}

	protected void circleDefB4CreationCallback(CircleDef def){
		
	}
	
	protected void bodyDefB4CreationCallback(BodyDef def){
		
	}
	
	
	//@Override
	@Override
	public void rotateZGlobal(Vector3D rotationPoint, float degree) {
		angle += degree;
		super.rotateZGlobal(rotationPoint, degree);
	}

	public float getAngle() {
		return angle;
	}
	
	@Override
	public void setCenterRotation(float angle){
		float degreeAngle = PApplet.degrees(angle);
		float oldAngle = this.getAngle();
		float diff = degreeAngle-oldAngle;
		//System.out.println("Old angle: " + oldAngle + " new angle:" + degreeAngle + " diff->" +  diff);
		this.rotateZGlobal(this.getCenterPointGlobal(), diff);
	}
	
	//@Override
	@Override
	protected void destroyComponent() {
		Object o = this.getUserData("box2d");
		if (o != null && o instanceof Body){ 
			Body box2dBody = (Body)o;
			boolean exists = false;
			for (Body body = world.getBodyList(); body != null; body = body.getNext()) {
				if (body.equals(this.body))
					exists = true;//Delete later to avoid concurrent modification
			}
			if (exists)
				box2dBody.getWorld().destroyBody(box2dBody);
		}
		super.destroyComponent();
	}


	public World getWorld() {
		return world;
	}
	
	@Override
	public void drawComponent(PGraphics g)
	{
		
		OscMessage msg;
		msg = new OscMessage(VisualConstants.OSC_CTRL_PATH);
				
		float yMax = 36f;
		float xMax = 49f;
		
		int volumeControlId = id * 10;
		//int yControl = id * 10 + 1;
		
		//Send energy
		msg = new OscMessage(VisualConstants.OSC_CTRL_PATH);
		msg.add(volumeControlId);
		msg.add(energy);
		oscP5.send(msg, new NetAddress(MTVisualizerConstants.OSC_REMOTE_ADDR, MTVisualizerConstants.OSC_REMOTE_PORT));
		
//		//Send X
//		msg = new OscMessage(VisualConstants.OSC_CTRL_PATH);
//		msg.add(this.id);
//		msg.add(this.getCenterPointRelativeToParent().x / xMax);
//		oscP5.send(msg, new NetAddress(MTVisualizerConstants.OSC_REMOTE_ADDR, MTVisualizerConstants.OSC_REMOTE_PORT));
//		
//		//Send Y
//		msg = new OscMessage(VisualConstants.OSC_CTRL_PATH);
//		msg.add(this.id+1);
//		msg.add(this.getCenterPointRelativeToParent().y / yMax);
//		oscP5.send(msg, new NetAddress(MTVisualizerConstants.OSC_REMOTE_ADDR, MTVisualizerConstants.OSC_REMOTE_PORT));
		
		super.drawComponent(g);
	}
	
	@Override
	public void preDraw(PGraphics g)
	{	
		g.pushMatrix();
			//move objects forward so they don't intersect background
			g.translate(0, 0, 4);
			
			g.translate(this.getCenterPointRelativeToParent().x, this.getCenterPointRelativeToParent().y);
			g.rotate(this.getAngle()/100);

			g.rotateX(this.getAngle()/100);
			
			
			g.fill(shapeColor.getR()*energy, shapeColor.getG()*energy, shapeColor.getB()*energy, energy * 255);
			
			// Vertex Expansion
			fItr = mesh.fItr();
			selection = new HE_Selection(mesh);
			boolean toggle = true;
			while (fItr.hasNext()) {
				f = fItr.next();
				
				if(toggle)
					selection.add(f);
				toggle=false;
			}
			//mesh.modifySelected(new HEM_VertexExpand().setDistance(emphasis*10), selection);
			
			
//			
//			meshRenderer.drawFace(mesh.getFacesAsArray()[0]);
//			meshRenderer.drawFace(mesh.getFacesAsArray()[3]);
//			meshRenderer.drawFace(mesh.getFacesAsArray()[5]);
			
			//g.scale(1 - (emphasis * 1.7f));
			g.scale(scaleAmount);
			
			mesh.modify(new HEM_Noise().setDistance(emphasis/1.5));
			
			
			g.stroke(255 - (255 * energy));
			meshRenderer.drawEdges(mesh);
			
			fItr = mesh.getFacesAsList().iterator();
			while (fItr.hasNext()) {
				f = fItr.next();
				//shapecolor = g.color(this.app.random(0,255), 20, 240, 255);
				meshRenderer.drawFace(f);
			}
			
		g.popMatrix();
		
		//System.out.println("energy: " + energy);
		
		if(energy - energyDrainAmount <= 0)
			energy = 0;
		else
			energy -= energyDrainAmount;
		
		super.preDraw(g);
	}

	void undoEmphasis(Ani theAni) {
		Ani.to(this, durationOut, "emphasis", 0);
	}

	@Override
	public Body getBody() {
		return body;
	}



	public float getDensity() {
		return density;
	}



	public float getFriction() {
		return friction;
	}



	public float getRestituion() {
		return restituion;
	}
	
	public void setFacets(int facets)
	{
		creator = new HEC_Sphere().setRadius(2.8).setUFacets(facets).setVFacets(facets).setCenter(0, 0, 0);
		mesh = new HE_Mesh(creator);
	}
	
	public void emphasize()
	{
		maxEmphasis = energy * defaultMaxEmphasis;
		emphasisAni = new Ani(this, durationIn, "emphasis", maxEmphasis, Ani.LINEAR, "onEnd:undoEmphasis"); 
		emphasisAni.pause();
		mesh = new HE_Mesh(creator);
		emphasisAni.start();
	}
	
	public void setColor(MTColor color)
	{
		shapeColor = color;
	}
	
	public void addEnergy(float amount)
	{
		if(energy + amount > maxEnergy)
			energy = maxEnergy;
		else
			energy += amount;
	}
}

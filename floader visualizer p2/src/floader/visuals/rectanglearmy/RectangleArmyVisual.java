package floader.visuals.rectanglearmy;

import java.awt.Color;
import java.util.Iterator;
import java.util.Vector;

import floader.visuals.AbstractVisual;
import floader.visuals.IVisual;
import floader.visuals.VisualConstants;
import toxi.geom.Plane;
import toxi.geom.Vec3D;
import wblut.hemesh.*;
import wblut.processing.*;
import oscP5.*;
import peasy.*;
import processing.core.*;
import floader.visuals.colorschemes.*;
import floader.looksgood.ani.Ani;

@SuppressWarnings("serial")
public class RectangleArmyVisual extends AbstractVisual {
	// Meshes
	WB_Render meshRenderer;
	HEC_Creator creator;

	int numRows = 15;
	int numCols = 15;
	int RESET = 0;

	boolean enableParticles = false;
	boolean fillBackground = true;
	float maxVertexDistance = 400;
	float vertexDistance = 10;
	float maxPerspectiveWidth = 5000;
	float perspectiveWidth;
	float maxBendAmount = 1000;
	float bendAmount;
	float explodeAmount;

	// presentation
	int bgcolor; // background color
	int maxBgColor = 255;
	boolean facesOn = true; // toggle display of faces
	boolean edgesOn = true; // toggle display of edges
	float ax, ay;
	boolean clock = false;
	boolean eighth = true;
	int maxCounter = 11;
	HE_Selection selection;
	Iterator<HE_Face> fItr;
	HE_Face f;
	CameraState camState;
	float minDistance = 400;
	float initDistance = 700;
	float maxDistance = 2000;

	float individualRotateZ;
	float maxIndividualRotateZ = .02f;
	float individualRotateZDegrees = 0;

	float individualRotateX;
	float maxIndividualRotateX = .02f;
	float individualRotateXDegrees = 0;

	float individualRotateY;
	float maxIndividualRotateY = .02f;
	float individualRotateYDegrees = 0;

	float globalRotateAmount;
	float maxGlobalRotateAmount = 5;
	float globalRotateDegrees = 0;

	float rotateZ = 0;
	float maxRotateZ = .4f;
	float rotateIndividual = 0;
	float rotateForward = 0;
	float maxRotateForward = 1;
	float rotateCombine = 0;
	float maxRotateCombine = .4f;
	float rotateCrazy = 0;
	float maxRotateCrazy = 1;
	float rotateY;

	PApplet app;

	Ani localSpinAni;
	float localSpinAniSpeed = 1;
	float localSpinAniSpeedMin = .5f;
	float localSpinAniSpeedMax = 10;

	Ani globalSpinAni;
	float globalSpinAniSpeed = 1;
	float globalSpinAniSpeedMin = .5f;
	float globalSpinAniSpeedMax = 10;

	Ani noiseAni;
	static final int MAXNOISE = 8;
	float noise = 0;
	float maxNoise = 0;
	float initNoiseDuration = .2f;

	Ani explodeEaseAni;
	float explodeEaseAniDuration = 2;

	Ani scaleAni;
	float rectScale = 1;
	float minRectScale = 1;
	float maxRectScale = 7.6f;

	// Particle variables
	Vec3D globalOffset, avg, cameraCenter;
	public float neighborhood, viscosity, speed, turbulence, cameraRate,
			rebirthRadius, spread, independence, dofRatio;
	public float neighborhoodDefault = 100, viscosityDefault = .1f,
			speedDefault = 9, turbulenceDefault = 0.2f,
			cameraRateDefault = .07f, rebirthRadiusDefault = 250,
			spreadDefault = 30, independenceDefault = .01f,
			dofRatioDefault = 50;
	public int n, rebirth;
	public int nDefault = numCols * numRows, rebirthDefault = 0;
	public boolean averageRebirth, paused;
	Vector particles;
	Plane focalPlane;
	PeasyCam cam;
	Vec3D centeringForce = new Vec3D();
	int meshRadius, meshRadiusDefault = 20;
	Particle cur;

	public RectangleArmyVisual(PApplet app) {
		this.app = app;
	}

	void setParameters() {
		n = nDefault;
		dofRatio = dofRatioDefault;
		neighborhood = neighborhoodDefault;
		speed = speedDefault;
		viscosity = viscosityDefault;
		spread = spreadDefault;
		independence = independenceDefault;
		rebirth = rebirthDefault;
		rebirthRadius = rebirthRadiusDefault;
		turbulence = turbulenceDefault;
		cameraRate = cameraRateDefault;
		averageRebirth = false;
	}

	public void setup() {
		super.setup();
		meshRenderer = new WB_Render(app);

		// Ani's
		localSpinAni = new Ani(this, localSpinAniSpeed, "rotateIndividual", 360);
		localSpinAni.repeat();
		globalSpinAni = new Ani(this, globalSpinAniSpeed, "rotateZ", 360);
		globalSpinAni.repeat();
		explodeEaseAni = new Ani(this, explodeEaseAniDuration, "explodeAmount",
				1);
		explodeEaseAni.repeat();
		explodeEaseAni.setPlayMode(Ani.YOYO);
		explodeEaseAni.setEasing(Ani.EXPO_IN_OUT);
		noiseAni = new Ani(this, initNoiseDuration, "noise", 0);
		noiseAni.setEasing(Ani.EXPO_IN);
		noiseAni.pause();
		scaleAni = new Ani(this, 1, "rectScale", maxRectScale);
		scaleAni.setEasing(Ani.EXPO_OUT);
		scaleAni.pause();

		// Particles
		setParameters();
		cameraCenter = new Vec3D();
		avg = new Vec3D();
		globalOffset = new Vec3D(0, 1.f / 3, 2.f / 3);

	

		reset();
	}

	class Particle {
		Vec3D position, velocity, force;
		Vec3D localOffset;

		Particle() {
			resetPosition();
			velocity = new Vec3D();
			force = new Vec3D();
			localOffset = Vec3D.randomVector();
		}

		void resetPosition() {
			position = new Vec3D(0,0,0);//Vec3D.randomVector();
			position.scaleSelf(app.random(rebirthRadius));
			if (particles.size() == 0)
				position.addSelf(avg);
			else
				position.addSelf(randomParticle().position);
		}

		void applyFlockingForce() {
			force.addSelf(
					app.noise(position.x / neighborhood + globalOffset.x
							+ localOffset.x * independence, position.y
							/ neighborhood, position.z / neighborhood) - .5f,
					app.noise(position.x / neighborhood, position.y
							/ neighborhood + globalOffset.y + localOffset.y
							* independence, position.z / neighborhood) - .5f,
					app.noise(position.x / neighborhood, position.y
							/ neighborhood, position.z / neighborhood
							+ globalOffset.z + localOffset.z * independence) - .5f);
		}

		void applyViscosityForce() {
			force.addSelf(velocity.scale(-viscosity));
		}

		void applyCenteringForce() {
			centeringForce.set(position);
			centeringForce.subSelf(avg);
			float distanceToCenter = centeringForce.magnitude();
			centeringForce.normalize();
			centeringForce.scaleSelf(-distanceToCenter / (spread * spread));
			force.addSelf(centeringForce);
		}

		void update() {
			force.clear();
			applyFlockingForce();
			applyViscosityForce();
			applyCenteringForce();
			velocity.addSelf(force); // mass = 1
			position.addSelf(velocity.scale(speed));
		}
	}

	Particle randomParticle() {
		return ((Particle) particles.get((int) app.random(particles.size())));
	}

	int panYAmount = 5;
	int totalYPan = 0;
	int panXAmount = 5;
	int totalXPan = 0;
	boolean scroll = false;
	boolean rotate = false;
	boolean enableMouse = true;
	boolean addLattice = false;

	public void draw(PGraphics g) {

		g.noStroke();
		g.lights();
		// creator = new HEC_Grid().setUSize(110).setVSize(110).setU(2).setV(2);
		creator = new HEC_Cube().setRadius(10);
		HE_Mesh rect = new HE_Mesh(creator);
		rect.modify(new HEM_Noise().setDistance(noise));
		
		g.pushMatrix();
		globalRotateDegrees = (globalRotateDegrees + globalRotateAmount) % 360;
		g.rotateZ(PApplet.radians(globalRotateDegrees));
		if(enableParticles)
		{
			avg = new Vec3D();
			for (int i = 0; i < particles.size(); i++) {
				Particle cur = ((Particle) particles.get(i));
				avg.addSelf(cur.position);
			}
			avg.scaleSelf(1.f / particles.size());
		}

		g.translate(-avg.x, -avg.y, -avg.z);
		
		for (int j = 0; j < numCols; j++) {
			for (int k = 0; k < numRows; k++) {
				cur = ((Particle) particles.get((j*numCols) + k));
				if (enableParticles)
					cur.update();
				
				g.fill(curColorScheme.getColor(k % curColorScheme.getLength())
						.getRGB(), curColorScheme.getColor(k % curColorScheme.getLength())
						.getAlpha());

				g.pushMatrix();
				int padding = 120;
				g.translate(j * padding - (numCols * padding / 2), k * padding
						- (numRows * padding / 2) );
				
				
				g.translate(cur.position.x,  cur.position.y, cur.position.z);
				
				individualRotateZDegrees = (individualRotateZDegrees + individualRotateZ) % 360;
				g.rotateZ(PApplet.radians(-individualRotateZDegrees));

				individualRotateYDegrees = (individualRotateYDegrees + individualRotateY) % 360;
				g.rotateY(PApplet.radians(-individualRotateYDegrees));

				individualRotateXDegrees = (individualRotateXDegrees + individualRotateX) % 360;
				g.rotateX(PApplet.radians(-individualRotateXDegrees));

				// Explode!
				rect.rotateAboutAxis(PApplet.radians(explodeAmount),
						numCols / 2 * 150, numRows / 2 * 150, 0,
						numCols / 2 * 100, numRows / 2 * 150, 0);

				g.scale(rectScale);
				meshRenderer.drawFaces(rect);
				g.popMatrix();
			}
		}

		g.popMatrix();

		for (int i = 0; i < rebirth; i++)
			randomParticle().resetPosition();

		if (particles.size() > n)
			particles.setSize(n);
		while (particles.size() < n)
			particles.add(new Particle());

		globalOffset.addSelf(turbulence / neighborhood, turbulence
				/ neighborhood, turbulence / neighborhood);
		
		
		
	}

	public void reset() {
		super.reset();
		particles = new Vector();
		for (int i = 0; i < n; i++)
			particles.add(new Particle());
		
		noise = 0;
		individualRotateZ = 0;
		individualRotateY = 0;
		individualRotateX = 0;
		individualRotateZDegrees = 0;
		individualRotateYDegrees = 0;
		individualRotateXDegrees = 0;
		globalRotateAmount = 0;
		rectScale = 1;
		rotateZ = 0;
		rotateIndividual = 0;
		rotateForward = 0;
		rotateCrazy = 0;
		rotateCombine = 0;
		explodeAmount = 0;
		rotate = false;
		localSpinAni.pause();
		globalSpinAni.pause();
		explodeEaseAni.pause();
		explodeEaseAniDuration = 2;
	}

	@Override
	public void ctrlEvent(int index, float val) {
		// Rotate cam Z amount
		if (index == VisualConstants.LOCAL_EFFECT_1) {
			globalRotateAmount = val * maxGlobalRotateAmount;
		} else if (index == VisualConstants.LOCAL_EFFECT_2) {
			noise = maxNoise = val * MAXNOISE;
		} else if(index == VisualConstants.LOCAL_EFFECT_3)
		{
			if (val > 0.1) {
				independence = PApplet.map(val, 0, 1, 0, 5f);
				turbulence = PApplet.map(val, 0, 1, 0, 4f);
				viscosity = PApplet.map(val, 0, 1, 0, .1f);
				neighborhood = PApplet.map(val, 0, 1, 0, 70);
				speed = PApplet.map(val, 0, 1, 0, 150);
				spread = PApplet.map(val, 0, 1, 0, 100);

			} else {
				independence = independenceDefault;
				turbulence = turbulenceDefault;
				viscosity = viscosityDefault;
				neighborhood = neighborhoodDefault;
				speed = speedDefault;
				spread = spreadDefault;
			}
		} else if(index == VisualConstants.LOCAL_EFFECT_4)
		{
			if(val > 0.1)
				enableParticles = true;
			else enableParticles = false;
			
		}

		// Explode - kind of annoying to work with
		/*
		 * if (num == 2) { explodeEaseAni.setBegin(explodeAmount);
		 * explodeEaseAni.setEnd(val * 3); explodeEaseAni.start();
		 * 
		 * //explodeAmount = val * 360 - (360 / 2); } else //Rotate combine
		 * amount if (num == 3) { explodeEaseAniDuration = 10 - (val * 10) +
		 * .2f; explodeEaseAni.setDuration(explodeEaseAniDuration); } else
		 */
	}

	@Override
	public void camEvent(int note) {
		/*
		 * switch (note) { case 0: cam.setDistance(minDistance); break; case 1:
		 * cam.setDistance(maxDistance); break; case 2:
		 * cam.setDistance((minDistance + maxDistance) / 2); break; }
		 */
	}

	public void scale(float amount) {
		// rectScale = 1 + (amount * maxRectScale);

		float scaleDelta = Math.abs(rectScale - (amount * maxRectScale));
		scaleAni.setBegin(rectScale);
		scaleAni.setEnd(minRectScale + (amount * maxRectScale));
		scaleAni.setDuration(.5f * (1 / (scaleDelta / maxRectScale)));
		scaleAni.start();
	}

	public void rotateZ(float amount) {
		individualRotateZ = amount * maxIndividualRotateZ;
	}

	public void rotateX(float amount) {
		individualRotateX = amount * maxIndividualRotateX;
	}

	public void rotateY(float amount) {
		individualRotateY = amount * maxIndividualRotateY;
	}

}

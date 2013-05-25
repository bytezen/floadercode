package floader.visuals.particles;

import java.util.Vector;
import floader.visuals.AbstractVisual;
import floader.visuals.IVisual;
import floader.visuals.VisualConstants;
import toxi.geom.*;
import wblut.geom.nurbs.WB_BSpline;
import wblut.hemesh.*;
import oscP5.*;
import peasy.PeasyCam;
import processing.core.PApplet;
import processing.core.PGraphics;
import wblut.hemesh.core.*;
import wblut.hemesh.creators.HEC_Creator;
import wblut.hemesh.creators.HEC_Cube;
import wblut.hemesh.creators.HEC_Sphere;
import wblut.hemesh.creators.HEC_SweepTube;
import wblut.core.processing.*;

@SuppressWarnings("serial")
public class ParticleMirrorVisual extends AbstractVisual implements IVisual {
	PApplet app;

	Vec3D globalOffset, avg, cameraCenter;
	public float neighborhood, viscosity, speed, turbulence, cameraRate, rebirthRadius, spread, independence, dofRatio;
	public float neighborhoodDefault = 700, viscosityDefault = .1f, speedDefault = 30, turbulenceDefault = 0.1f, cameraRateDefault = .07f, rebirthRadiusDefault = 250, spreadDefault = 60, independenceDefault = .04f, dofRatioDefault = 50;
	public int n, rebirth;
	public int nDefault = 120, rebirthDefault = 0;
	public boolean averageRebirth, paused;
	Vector particles;
	Plane focalPlane;
	PeasyCam cam;
	Vec3D centeringForce = new Vec3D();
	// Meshes
	HE_Mesh mesh;
	WB_Render meshRenderer;
	HEC_Creator creator;
	int meshRadius, meshRadiusDefault = 20;
	boolean setBackground = true;

	PApplet offline;

	public ParticleMirrorVisual(PApplet app) {
		this.app = app;
	}

	public void setup() {

		offline = new PApplet();
		offline.g = app.createGraphics(VisualConstants.WIDTH / 2, VisualConstants.HEIGHT / 2, PApplet.P3D);
		meshRenderer = new WB_Render(offline);
		mesh = new HE_Mesh();

		setParameters();
		cameraCenter = new Vec3D();
		avg = new Vec3D();
		globalOffset = new Vec3D(0, 1.f / 3, 2.f / 3);

		particles = new Vector();
		for (int i = 0; i < n; i++)
			particles.add(new Particle());

		cam = new PeasyCam(app, 0);
		cam.setMaximumDistance(1000);
		cam.setDistance(600);
		cam.setActive(false);
	}

	void setParameters() {
		n = nDefault;
		meshRadius = meshRadiusDefault;
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

	public void draw() {
		cam.feed();

		avg = new Vec3D();
		for (int i = 0; i < particles.size(); i++) {
			Particle cur = ((Particle) particles.get(i));
			avg.addSelf(cur.position);
		}
		avg.scaleSelf(1.f / particles.size());

		cameraCenter.scaleSelf(1 - cameraRate);
		cameraCenter.addSelf(avg.scale(cameraRate));

		offline.g.beginDraw();
		if(setBackground)offline.g.background(0);
		
		offline.g.translate(-cameraCenter.x + VisualConstants.WIDTH / 2, -cameraCenter.y + VisualConstants.HEIGHT / 2, -cameraCenter.z);

		float[] camPosition = cam.getPosition();
		focalPlane = new Plane(avg, new Vec3D(VisualConstants.WIDTH / 2, VisualConstants.HEIGHT / 2, 600));

		app.noFill();
		app.hint(PApplet.DISABLE_DEPTH_TEST);
		offline.g.lights();

		for (int i = 0; i < particles.size(); i++) {
			Particle cur = ((Particle) particles.get(i));
			if (!paused)
				cur.update();

			cur.draw();
		}

		offline.g.endDraw();

		app.pushMatrix();
		app.scale(1f, -1f);
		app.image(offline.g, -VisualConstants.WIDTH / 2, -VisualConstants.HEIGHT / 2);
		app.popMatrix();

		app.pushMatrix();
		app.scale(1f, 1f);
		app.image(offline.g, -VisualConstants.WIDTH / 2, -VisualConstants.HEIGHT / 2);
		app.popMatrix();

		app.pushMatrix();
		app.scale(-1f, -1f);
		app.image(offline.g, -VisualConstants.WIDTH / 2, -VisualConstants.HEIGHT / 2);
		app.popMatrix();

		app.pushMatrix();
		app.scale(-1f, 1f);
		app.image(offline.g, -VisualConstants.WIDTH / 2, -VisualConstants.HEIGHT / 2);
		app.popMatrix();

		for (int i = 0; i < rebirth; i++)
			randomParticle().resetPosition();

		if (particles.size() > n)
			particles.setSize(n);
		while (particles.size() < n)
			particles.add(new Particle());

		globalOffset.addSelf(turbulence / neighborhood, turbulence / neighborhood, turbulence / neighborhood);

	}

	Particle randomParticle() {
		return ((Particle) particles.get((int) app.random(particles.size())));
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
			position = Vec3D.randomVector();
			position.scaleSelf(app.random(rebirthRadius));
			if (particles.size() == 0)
				position.addSelf(avg);
			else
				position.addSelf(randomParticle().position);
		}

		void draw() {
			float distanceToFocalPlane = focalPlane.getDistanceToPoint(position);
			distanceToFocalPlane *= 1 / dofRatio;
			distanceToFocalPlane = PApplet.constrain(distanceToFocalPlane, 1, 15);
			// offline.g.strokeWeight(distanceToFocalPlane);
			// offline.g.stroke(255, PApplet.constrain(255 /
			// (distanceToFocalPlane * distanceToFocalPlane), 1, 150));
			// meshRadius = app.mouseX;
			offline.g.stroke(100);
			offline.g.fill(255, 255, 255, 150);
			creator = new HEC_Cube().setRadius(meshRadius).setCenter(position.x, position.y, position.z);
			mesh = new HE_Mesh(creator);
			// offline.g.fill(255, PApplet.constrain(255 / (distanceToFocalPlane
			// * distanceToFocalPlane), 1, 255));
			meshRenderer.drawFaces(mesh);
			// meshRenderer.drawEdges(mesh);

			// offline.g.point(position.x, position.y, position.z);
			// offline.g.stroke(255, PApplet.constrain(255 /
			// (distanceToFocalPlane * distanceToFocalPlane), 1, 20));
			// offline.g.line(position.x, position.y, position.z, 0, 0, 0);
		}

		void applyFlockingForce() {
			force.addSelf(app.noise(position.x / neighborhood + globalOffset.x + localOffset.x * independence, position.y / neighborhood, position.z / neighborhood) - .5f, app.noise(position.x / neighborhood, position.y / neighborhood + globalOffset.y + localOffset.y * independence, position.z / neighborhood) - .5f, app.noise(position.x / neighborhood, position.y / neighborhood, position.z / neighborhood + globalOffset.z + localOffset.z * independence) - .5f);
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

	@Override
	public void dragEvent(int eventType, float amount) {

	}

	@Override
	public void tapEvent(int eventType, boolean isTapDown) {
		if (isTapDown) {
			independence = 3f;
			turbulence = 4f;
			viscosity = .1f;
			neighborhood = 100;
			speed = 100;
			spread = 100;

		} else {
			independence = independenceDefault;
			turbulence = turbulenceDefault;
			viscosity = viscosityDefault;
			neighborhood = neighborhoodDefault;
			speed = speedDefault;
			spread = spreadDefault;
		}
	}

	@Override
	public void noteObjEvent(int note, int velocity) {
		if (note == 0 && velocity > 0) {
			independence = 3f;
			turbulence = 4f;
			viscosity = .1f;
			neighborhood = 100;
			speed = 100;
			spread = 100;

		} else if (note == 0 && velocity == 0) {
			independence = independenceDefault;
			turbulence = turbulenceDefault;
			viscosity = viscosityDefault;
			neighborhood = neighborhoodDefault;
			speed = speedDefault;
			spread = spreadDefault;

		} else if (note == 1) {
			speed = speedDefault / 1.4f;
			viscosity = viscosityDefault * 1.4f;
		} else if (note == 2) {
			speed = speedDefault / 1.4f;
			viscosity = viscosityDefault * 1.4f;
			spread = 70;
			independence = .4f;
		} else if (note == 3) {
			speed = speedDefault;
			viscosity = viscosityDefault;
			independence = independenceDefault;
		} else if (note == 4 && velocity > 0) {
			paused = true;
		} else if (note == 4 && velocity == 0) {
			paused = false;
		} else if(note == 5)
		{//Slowest
			speed = speedDefault / 2f;
			viscosity = viscosityDefault * 2f;
			spread = 70;
			independence = .4f;
		} else if(note == 6 &&  velocity > 0)
			//Disable background
		{
			setBackground = false;
			paused = true;
		} else if(note==6 && velocity == 0)
		{
			setBackground = true;
			paused = false;
		}

	}

	@Override
	public void ctrlEvent(int num, int val, int chan) {
		if (chan == 1 && num == 1) {
			meshRadius = (int) PApplet.map(val, 0, 127, 4, 512);
		}

	}

	@Override
	public void noteCamEvent(int note, int vel) {

	}

}

package org.andengine.extension.debugdraw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import org.andengine.entity.Entity;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import android.util.Log;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Joint;
import com.badlogic.gdx.physics.box2d.Shape.Type;



public class DebugRenderer extends Entity {
	private PhysicsWorld mWorld;
	private final VertexBufferObjectManager mVBO;

	private HashMap<Body, RenderOfBody> mToBeRenderred = new HashMap<Body, RenderOfBody>();
	private Set<RenderOfBody> mInactiveSet = new HashSet<RenderOfBody>();
	private Set<RenderOfBody> mActiveSet = new HashSet<RenderOfBody>();

	private HashMap<Joint, RenderOfJoint> mJointsToBeRenderred = new HashMap<Joint, RenderOfJoint>();
	private Set<RenderOfJoint> mJointsInactiveSet = new HashSet<RenderOfJoint>();
	private Set<RenderOfJoint> mJointsActiveSet = new HashSet<RenderOfJoint>();

	/**
	 * To construct the renderer physical world is needed (to access physics)
	 * and VBO (to construct visible representations)
	 * @param world
	 * @param pVBO
	 */
	public DebugRenderer(PhysicsWorld world, VertexBufferObjectManager pVBO) {
		super();
		this.mWorld = world;
		this.mVBO = pVBO;
	}

	/**
	 * This is where all the magic happens. Bodies representations are rendered.
	 * Dead bodies (not being part of physical world anymore) are removed from
	 * the rendering.
	 */
	@Override
	protected void onManagedUpdate(float pSecondsElapsed) {
		super.onManagedUpdate(pSecondsElapsed);

		// BODIES

		mActiveSet.clear();
		mInactiveSet.clear();

		Iterator<Body> iterator = mWorld.getBodies();
		while (iterator.hasNext()) {
			Body body = iterator.next();
			RenderOfBody renderOfBody;
			if (!mToBeRenderred.containsKey(body)) {
				renderOfBody = new RenderOfBody(body, mVBO);
				mToBeRenderred.put(body, renderOfBody);
				this.attachChild(renderOfBody);
			} else {
				renderOfBody = mToBeRenderred.get(body);
			}

			mActiveSet.add(renderOfBody);

			/**
			 * This is where debug renders are moved to match body position.
			 * These 4 lines probably have to be modified if you are not using new
			 * GLES2-AnchorCenter branch of AE (i.e. you are using old GLES2 branch)
			 */
			renderOfBody.updateColor();
			renderOfBody.setRotationCenter(body.getMassData().center.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, body.getMassData().center.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
			renderOfBody.setRotation((float) (360 - body.getAngle() * (180 / Math.PI)));
			renderOfBody.setPosition(body.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, body.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
		}

		/**
		 * Get rid of all bodies that where not rendered in this iteration
		 */
		// inactive = renderred - active
		mInactiveSet.addAll(mToBeRenderred.values());
		mInactiveSet.removeAll(mActiveSet);
		for (RenderOfBody killme : mInactiveSet) {
			this.detachChild(killme);
		}

		mToBeRenderred.values().removeAll(mInactiveSet);

		// JOINTS

		mJointsActiveSet.clear();
		mJointsInactiveSet.clear();

		Iterator<Joint> iteratorJoints = mWorld.getJoints();
		while (iteratorJoints.hasNext()) {
//			Log.e(getClass().getSimpleName(), "foo");
			Joint joint = iteratorJoints.next();
			RenderOfJoint renderOfJoint;
			if (!mJointsToBeRenderred.containsKey(joint)) {
				renderOfJoint = new RenderOfJoint(joint, mVBO);
				mJointsToBeRenderred.put(joint, renderOfJoint);
				this.attachChild(renderOfJoint);
			} else {
				renderOfJoint = mJointsToBeRenderred.get(joint);
			}

			mJointsActiveSet.add(renderOfJoint);

			/**
			 * This is where debug renders are moved to match body position.
			 * These 4 lines probably have to be modified if you are not using new
			 * GLES2-AnchorCenter branch of AE (i.e. you are using old GLES2 branch)
			 */
			renderOfJoint.update();
		}

		/**
		 * Get rid of all bodies that where not rendered in this iteration
		 */
		// inactive = renderred - active
		mJointsInactiveSet.addAll(mJointsToBeRenderred.values());
		mJointsInactiveSet.removeAll(mJointsActiveSet);
		for (RenderOfJoint killme : mJointsInactiveSet) {
			this.detachChild(killme);
		}

		mJointsToBeRenderred.values().removeAll(mJointsInactiveSet);
	}

	/**
	 * Translates b2d Fixture to appropriate color, depending on body state/type
	 * Modify to suit your needs
	 * @param fixture
	 * @return
	 */
	private static Color fixtureToColor(Fixture fixture) {
		if (fixture.isSensor()) {
			return Color.PINK;
		} else {
			Body body = fixture.getBody();
			if (!body.isActive()) {
				return Color.BLACK;
			} else {
				if (!body.isAwake()) {
					return Color.RED;
				} else {
					switch (body.getType()) {
					case StaticBody:
						return Color.CYAN;
					case KinematicBody:
						return Color.WHITE;
					case DynamicBody:
					default:
						return Color.GREEN;
					}
				}
			}
		}
	}

	/**
	 * Physical body representation- it contains of multiple RenderFixture
	 * @author nazgee
	 *
	 */
	private class RenderOfBody extends Entity {
		public LinkedList<IRenderOfFixture> mRenderFixtures = new LinkedList<IRenderOfFixture>();

		public RenderOfBody(Body pBody, VertexBufferObjectManager pVBO) {
			ArrayList<Fixture> fixtures = pBody.getFixtureList();

			/**
			 * Spawn all IRenderOfFixture for this body that are out there,
			 * and bind them to this RenderOfBody
			 */
			for (Fixture fixture : fixtures) {
				IRenderOfFixture renderOfFixture;
				if (fixture.getShape().getType() == Type.Circle) {
					renderOfFixture = new RenderOfCircleFixture(fixture, pVBO);
				} else if (fixture.getShape().getType() == Type.Edge) {
					renderOfFixture = new RenderOfEdgeFixture(fixture, pVBO);
				} else if (fixture.getShape().getType() == Type.Chain) {
					renderOfFixture = new RenderOfChainFixture(fixture, pVBO);
				} else {
					renderOfFixture = new RenderOfPolyFixture(fixture, pVBO);
				}

				updateColor();
				mRenderFixtures.add(renderOfFixture);
				this.attachChild(renderOfFixture.getEntity());
			}
		}

		public void updateColor() {
			for (IRenderOfFixture renderOfFix : mRenderFixtures) {
				renderOfFix.getEntity().setColor(fixtureToColor(renderOfFix.getFixture()));
			}
		}
	}

	/**
	 * Physical body representation- it contains of multiple RenderFixture
	 * @author nazgee
	 *
	 */
	private static class RenderOfJoint extends Entity {
		RenderOfJointPolyline mRender;

		public RenderOfJoint(Joint pJoint, VertexBufferObjectManager pVBO) {
			mRender = new RenderOfJointPolyline(pJoint, pVBO);
			this.attachChild(mRender.getEntity());
		}

		public void update() {
			mRender.update();
		}
	}
}

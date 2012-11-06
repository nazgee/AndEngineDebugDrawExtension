package org.andengine.extension.debugdraw;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import org.andengine.entity.Entity;
import org.andengine.extension.debugdraw.primitives.Ellipse;
import org.andengine.extension.debugdraw.primitives.PolyLine;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape.Type;



public class DebugRenderer extends Entity {
	private PhysicsWorld mWorld;
	private final VertexBufferObjectManager mVBO;
	private HashMap<Body, RenderOfBody> mToBeRenderred = new HashMap<Body, RenderOfBody>();

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
				renderOfBody.keepRendering(true);
			}

			/**
			 * This is where debug renders are moved to match body position.
			 * These 4 lines probably have to be modified if you are not using new
			 * GLES2-AnchorCenter branch of AE (ie. you are using old GLES2 branch)
			 */
			renderOfBody.updateColor();
			renderOfBody.setRotationCenter(body.getMassData().center.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, body.getMassData().center.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
			renderOfBody.setRotation((float) (body.getAngle() * (180 / Math.PI)));
			renderOfBody.setPosition(body.getPosition().x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT, body.getPosition().y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT);
		}

		/**
		 * Get rid of all bodies that where not rendered in this iteration
		 * (where removed from physical world). Also assume that all other bodies
		 * will not be rendered anymore (it will be verified on next iteration).
		 */
		Iterator<RenderOfBody> renderBodyIter = mToBeRenderred.values().iterator();
		while ( renderBodyIter.hasNext()) {
			RenderOfBody renderOfBody = renderBodyIter.next();
			if (renderOfBody.hasToBeRendered()) {
				renderOfBody.keepRendering(false);
			} else {
				renderBodyIter.remove();
				this.detachChild(renderOfBody);
			}
		}
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
	 * Binds fixture and it's graphical representation together
	 * @author nazgee
	 */
	private interface IRenderOfFixture {
		public Fixture getFixture();
		public Entity getEntity();
	}

	/**
	 * Base implementation of fixture and it's graphical representation bound together
	 * @author nazgee
	 */
	private static abstract class RenderOfFixture implements IRenderOfFixture {
		protected final Fixture fixture;
		protected Entity entity;

		public RenderOfFixture(Fixture fixture) {
			super();
			this.fixture = fixture;
		}

		@Override
		public Fixture getFixture() {
			return fixture;
		}

		@Override
		public Entity getEntity() {
			return entity;
		}
	}

	/**
	 * Circular fixture representation
	 * @author nazgee
	 */
	private static class RenderOfCircleFixture extends RenderOfFixture {
		public RenderOfCircleFixture(Fixture fixture, VertexBufferObjectManager pVBO) {
			super(fixture);

			CircleShape fixtureShape = (CircleShape) fixture.getShape();
			Vector2 position = fixtureShape.getPosition();
			float radius = fixtureShape.getRadius() * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

			entity = new Ellipse(position.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
					position.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT,
					radius, radius, pVBO);
		}
	}

	/**
	 * Polygonal fixture representation
	 * @author nazgee
	 */
	private static class RenderOfPolyFixture extends RenderOfFixture {
		public RenderOfPolyFixture(Fixture fixture, VertexBufferObjectManager pVBO) {
			super(fixture);

			PolygonShape fixtureShape = (PolygonShape) fixture.getShape();
			int vSize = fixtureShape.getVertexCount();
			float[] xPoints = new float[vSize];
			float[] yPoints = new float[vSize];

			Vector2 vertex = Vector2Pool.obtain();
			for (int i = 0; i < fixtureShape.getVertexCount(); i++) {
				fixtureShape.getVertex(i, vertex);
				xPoints[i] = vertex.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
				yPoints[i] = vertex.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
			}
			Vector2Pool.recycle(vertex);

			entity = new PolyLine(0, 0, xPoints, yPoints, pVBO);
		}
	}

	/**
	 * Physical body representation- it contains of multiple RenderFixture
	 * @author nazgee
	 *
	 */
	private class RenderOfBody extends Entity {
		public Body body;
		public LinkedList<IRenderOfFixture> mRenderFixtures = new LinkedList<DebugRenderer.IRenderOfFixture>();
		private boolean mKeepRendering = true;

		public RenderOfBody(Body pBody, VertexBufferObjectManager pVBO) {
			this.body = pBody;
			ArrayList<Fixture> fixtures = pBody.getFixtureList();

			/**
			 * Spawn all IRenderOfFixture for this body that are out there,
			 * and bind them to this RenderOfBody
			 */
			for (Fixture fixture : fixtures) {
				IRenderOfFixture renderOfFixture;
				if (fixture.getShape().getType() == Type.Circle) {
					renderOfFixture = new RenderOfCircleFixture(fixture, pVBO);
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

		public boolean hasToBeRendered() {
			return mKeepRendering;
		}

		public void keepRendering(boolean pRender) {
			this.mKeepRendering = pRender;
		}
	}
}

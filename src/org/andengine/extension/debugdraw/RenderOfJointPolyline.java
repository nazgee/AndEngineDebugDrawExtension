package org.andengine.extension.debugdraw;

import org.andengine.extension.debugdraw.primitives.PolyLine;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Joint;

/**
 * Base implementation of joint and it's graphical representation bound together
 * @author nazgee
 */
class RenderOfJointPolyline extends RenderOfJoint {
	private static final float EQUALITY_EPSILON = 0.1f;

	private float[] mXPoints;
	private float[] mYPoints;
	private float mMarkerSize;

	public RenderOfJointPolyline(Joint joint, VertexBufferObjectManager pVBO, float pMarkerSize) {
		super(joint);
		mMarkerSize = pMarkerSize;

		mXPoints = new float[4];
		mYPoints = new float[4];

		mEntity = new PolyLine(0, 0, mXPoints, mYPoints, pVBO);
		mEntity.setColor(Color.RED);	// just to overcome some polyline issues
	}

	@Override
	public PolyLine getEntity() {
		return (PolyLine) super.getEntity();
	}

	public void update() {
		Vector2 aA = getJoint().getAnchorA();
		Vector2 aB = getJoint().getAnchorB();

		if (aA.epsilonEquals(aB, EQUALITY_EPSILON)) {
			mXPoints[0] = aA.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - mMarkerSize;
			mYPoints[0] = aA.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - mMarkerSize;

			mXPoints[1] = aA.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT + mMarkerSize;
			mYPoints[1] = aA.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT + mMarkerSize;

			mXPoints[2] = aA.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - mMarkerSize;
			mYPoints[2] = aA.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT + mMarkerSize;

			mXPoints[3] = aA.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT + mMarkerSize;
			mYPoints[3] = aA.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT - mMarkerSize;

			getEntity().setVertexCountToDraw(4);
			getEntity().updateVertices(mXPoints, mYPoints);
		} else {
			mXPoints[0] = aA.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
			mYPoints[0] = aA.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

			mXPoints[1] = aB.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
			mYPoints[1] = aB.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

			getEntity().setVertexCountToDraw(2);
			getEntity().updateVertices(mXPoints, mYPoints);
		}
	}
}
package org.andengine.extension.debugdraw;

import org.andengine.extension.debugdraw.primitives.PolyLine;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

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

	private float p2m;

	public RenderOfJointPolyline(Joint joint, VertexBufferObjectManager pVBO, float pMarkerSize, float p2m) {
		super(joint);
		mMarkerSize = pMarkerSize;
		this.p2m = p2m;

		mXPoints = new float[4];
		mYPoints = new float[4];

		mEntity = new PolyLine(0, 0, mXPoints, mYPoints, pVBO);
		mEntity.setColor(1, 1, 1);	// just to overcome some polyline issues
	}

	@Override
	public PolyLine getEntity() {
		return (PolyLine) super.getEntity();
	}

	public void update() {
		Vector2 aA = getJoint().getAnchorA();
		Vector2 aB = getJoint().getAnchorB();

		if (aA.epsilonEquals(aB, EQUALITY_EPSILON)) {
			mXPoints[0] = aA.x * p2m - mMarkerSize;
			mYPoints[0] = aA.y * p2m - mMarkerSize;

			mXPoints[1] = aA.x * p2m + mMarkerSize;
			mYPoints[1] = aA.y * p2m + mMarkerSize;

			mXPoints[2] = aA.x * p2m - mMarkerSize;
			mYPoints[2] = aA.y * p2m + mMarkerSize;

			mXPoints[3] = aA.x * p2m + mMarkerSize;
			mYPoints[3] = aA.y * p2m - mMarkerSize;

			getEntity().setVertexCountToDraw(4);
			getEntity().updateVertices(mXPoints, mYPoints);
		} else {
			mXPoints[0] = aA.x * p2m;
			mYPoints[0] = aA.y * p2m;

			mXPoints[1] = aB.x * p2m;
			mYPoints[1] = aB.y * p2m;

			getEntity().setVertexCountToDraw(2);
			getEntity().updateVertices(mXPoints, mYPoints);
		}
	}
}
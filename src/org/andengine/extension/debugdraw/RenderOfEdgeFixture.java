package org.andengine.extension.debugdraw;

import org.andengine.extension.debugdraw.primitives.PolyLine;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.extension.physics.box2d.util.Vector2Pool;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Edge fixture representation
 * @author OzLark
 */
class RenderOfEdgeFixture extends RenderOfFixture {
	public RenderOfEdgeFixture(Fixture fixture,
			VertexBufferObjectManager pVBO) {
		super(fixture);

		EdgeShape fixtureShape = (EdgeShape) fixture.getShape();

		float[] xPoints = new float[2];
		float[] yPoints = new float[2];

		Vector2 vertex = Vector2Pool.obtain();

		fixtureShape.getVertex1(vertex);
		xPoints[0] = vertex.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
		yPoints[0] = vertex.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

		fixtureShape.getVertex2(vertex);
		xPoints[1] = vertex.x * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;
		yPoints[1] = vertex.y * PhysicsConnector.PIXEL_TO_METER_RATIO_DEFAULT;

		Vector2Pool.recycle(vertex);

		mEntity = new PolyLine(0, 0, xPoints, yPoints, pVBO);
	}
}
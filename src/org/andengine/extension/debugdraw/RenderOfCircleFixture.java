package org.andengine.extension.debugdraw;

import org.andengine.extension.debugdraw.primitives.Ellipse;
import org.andengine.extension.physics.box2d.PhysicsConnector;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;

/**
 * Circular fixture representation
 * @author nazgee
 */
class RenderOfCircleFixture extends RenderOfFixture {
	private float p2m;

	public RenderOfCircleFixture(Fixture fixture, VertexBufferObjectManager pVBO, float p2m) {
		super(fixture);
		this.p2m = p2m;

		CircleShape fixtureShape = (CircleShape) fixture.getShape();
		Vector2 position = fixtureShape.getPosition();
		float radius = fixtureShape.getRadius() * p2m;

		mEntity = new Ellipse(position.x * p2m,
				position.y * p2m,
				radius, radius, pVBO);
	}
}
package org.andengine.extension.debugdraw.primitives;

import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.HighPerformanceVertexBufferObject;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.andengine.util.debug.Debug;

/**
 * (c) Zynga 2012
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 18:46:51 - 28.03.2012
 */
public class HighPerformanceTexturedMeshVertexBufferObject extends HighPerformanceVertexBufferObject implements ITexturedMeshVertexBufferObject {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Fields
	// ===========================================================

	protected final int mVertexCount;

	// ===========================================================
	// Constructors
	// ===========================================================

	public HighPerformanceTexturedMeshVertexBufferObject(final VertexBufferObjectManager pVertexBufferObjectManager, final float[] pBufferData, final int pVertexCount, final DrawType pDrawType, final boolean pAutoDispose, final VertexBufferObjectAttributes pVertexBufferObjectAttributes) {
		super(pVertexBufferObjectManager, pBufferData, pDrawType, pAutoDispose, pVertexBufferObjectAttributes);

		this.mVertexCount = pVertexCount;
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	@Override
	public void onUpdateColor(final TexturedMesh pMesh) {
		final float[] bufferData = this.mBufferData;

		final float packedColor = pMesh.getColor().getABGRPackedFloat();

		for(int i = 0; i < this.mVertexCount; i++) {
			bufferData[(i * TexturedMesh.VERTEX_SIZE) + TexturedMesh.COLOR_INDEX] = packedColor;
		}

		this.setDirtyOnHardware();
	}

	@Override
	public void onUpdateVertices(final TexturedMesh pMesh) {
		/* Since the buffer data is managed from the caller, we just mark the buffer data as dirty. */

		this.setDirtyOnHardware();
	}

	@Override
	public void onUpdateTextureCoordinates(final TexturedMesh pMesh) {
		final float[] bufferData = this.mBufferData;

		final ITextureRegion textureRegion = pMesh.getTextureRegion(); // TODO Optimize with field access?

		float textureWidth = textureRegion.getWidth();
		float textureHeight = textureRegion.getHeight();

		// x0 is mapped to u0
		// y0 is mapped to v0

		// TODO get initial mapping
		float x0 = 0; // pMesh.getX0();
		float y0 = 0; // pMesh.getY0();

		for (int i = 0; i < this.mVertexCount; i++) {
			float x = bufferData[(i * TexturedMesh.VERTEX_SIZE)
					+ TexturedMesh.VERTEX_INDEX_X];
			float y = bufferData[(i * TexturedMesh.VERTEX_SIZE)
					+ TexturedMesh.VERTEX_INDEX_Y];

			float u = (x - x0) / textureWidth;
			float v = (y - y0) / textureHeight;

			Debug.d("u = " + u);
			Debug.d("v = " + v);

			Debug.d("x = " + x);
			Debug.d("y = " + y);

			bufferData[(i * TexturedMesh.VERTEX_SIZE)
					+ TexturedMesh.TEXTURECOORDINATES_INDEX_U] = u;
			bufferData[(i * TexturedMesh.VERTEX_SIZE)
					+ TexturedMesh.TEXTURECOORDINATES_INDEX_V] = v;
		}

		Debug.d("v ---");

		this.setDirtyOnHardware();
	}
	// ===========================================================
	// Methods
	// ===========================================================

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================
}
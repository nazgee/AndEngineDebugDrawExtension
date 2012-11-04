package org.andengine.extension.debugdraw.primitives;

import org.andengine.opengl.vbo.IVertexBufferObject;

/**
 * (c) Zynga 2012
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 18:46:51 - 28.03.2012
 */
public interface ITexturedMeshVertexBufferObject extends IVertexBufferObject {
	// ===========================================================
	// Constants
	// ===========================================================

	// ===========================================================
	// Methods
	// ===========================================================

	public float[] getBufferData();
	public void onUpdateColor(final TexturedMesh pMesh);
	public void onUpdateVertices(final TexturedMesh pMesh);
	public void onUpdateTextureCoordinates(final TexturedMesh pMesh);
}
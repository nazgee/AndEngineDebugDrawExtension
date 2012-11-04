package org.andengine.extension.debugdraw.primitives;

import org.andengine.engine.camera.Camera;
import org.andengine.entity.IEntity;
import org.andengine.entity.primitive.DrawMode;
import org.andengine.entity.primitive.Line;

import org.andengine.entity.shape.Shape;
import org.andengine.opengl.shader.PositionColorTextureCoordinatesShaderProgram;
import org.andengine.opengl.shader.constants.ShaderProgramConstants;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.util.GLState;
import org.andengine.opengl.vbo.DrawType;
import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttribute;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributes;
import org.andengine.opengl.vbo.attribute.VertexBufferObjectAttributesBuilder;
import org.andengine.util.exception.MethodNotSupportedException;

import android.opengl.GLES20;

/**
 * (c) Zynga 2012
 *
 * @author Nicolas Gramlich <ngramlich@zynga.com>
 * @since 16:44:50 - 09.02.2012
 */
public class TexturedMesh extends Shape {
	// ===========================================================
	// Constants
	// ===========================================================
	public static final int VERTEX_INDEX_X = 0;
	public static final int VERTEX_INDEX_Y = TexturedMesh.VERTEX_INDEX_X + 1;
	public static final int COLOR_INDEX = TexturedMesh.VERTEX_INDEX_Y + 1;
	public static final int TEXTURECOORDINATES_INDEX_U = TexturedMesh.COLOR_INDEX + 1;
	public static final int TEXTURECOORDINATES_INDEX_V = TexturedMesh.TEXTURECOORDINATES_INDEX_U + 1;
	
	public static final int VERTEX_SIZE = 2 + 1 + 2;

	public static final VertexBufferObjectAttributes VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT = new VertexBufferObjectAttributesBuilder(3)
		.add(ShaderProgramConstants.ATTRIBUTE_POSITION_LOCATION, ShaderProgramConstants.ATTRIBUTE_POSITION, 2, GLES20.GL_FLOAT, false)
		.add(ShaderProgramConstants.ATTRIBUTE_COLOR_LOCATION, ShaderProgramConstants.ATTRIBUTE_COLOR, 4, GLES20.GL_UNSIGNED_BYTE, true)
		.add(ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES_LOCATION, ShaderProgramConstants.ATTRIBUTE_TEXTURECOORDINATES, 2, GLES20.GL_FLOAT, false)
		.build();
	// ===========================================================
	// Fields
	// ===========================================================

	protected final ITexturedMeshVertexBufferObject mMeshVertexBufferObject;
	private int mVertexCountToDraw;
	private int mDrawMode;
	protected ITextureRegion mTextureRegion;

	// ===========================================================
	// Constructors
	// ===========================================================
	
	/**
	 * Uses a default {@link HighPerformanceTexturedMeshVertexBufferObject} in {@link DrawType#STATIC} with the {@link VertexBufferObjectAttribute}s: {@link Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public TexturedMesh(final float pX, final float pY, final float[] pBufferData, final int pVertexCount, final DrawMode pDrawMode, final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pBufferData, pVertexCount, pDrawMode, null, pVertexBufferObjectManager, DrawType.STATIC);
	}
	
	/**
	 * Uses a default {@link HighPerformanceTexturedMeshVertexBufferObject} in {@link DrawType#STATIC} with the {@link VertexBufferObjectAttribute}s: {@link Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public TexturedMesh(final float pX, final float pY, final float[] pBufferData, final int pVertexCount, final DrawMode pDrawMode, final ITextureRegion pTextureRegion,  final VertexBufferObjectManager pVertexBufferObjectManager) {
		this(pX, pY, pBufferData, pVertexCount, pDrawMode, pTextureRegion, pVertexBufferObjectManager, DrawType.STATIC);
	}
	
	/**
	 * Uses a default {@link HighPerformanceTexturedMeshVertexBufferObject} with the {@link VertexBufferObjectAttribute}s: {@link Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public TexturedMesh(final float pX, final float pY, final float[] pBufferData, final int pVertexCount, final DrawMode pDrawMode, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType) {
		this(pX, pY, pBufferData, pVertexCount, pDrawMode, null, pVertexBufferObjectManager, pDrawType);
	}

	/**
	 * Uses a default {@link HighPerformanceTexturedMeshVertexBufferObject} with the {@link VertexBufferObjectAttribute}s: {@link Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public TexturedMesh(final float pX, final float pY, final float[] pBufferData, final int pVertexCount, final DrawMode pDrawMode, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType) {
		this(pX, pY, pVertexCount, pDrawMode, pTextureRegion, new HighPerformanceTexturedMeshVertexBufferObject(pVertexBufferObjectManager, pBufferData, pVertexCount, pDrawType, true, TexturedMesh.VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT));
	}
	
	/**
	 * Uses a default {@link HighPerformanceTexturedMeshVertexBufferObject} with the {@link VertexBufferObjectAttribute}s: {@link Mesh#VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT}.
	 */
	public TexturedMesh(final float pX, final float pY, final float[] pVertexX, final float[] pVertexY, final DrawMode pDrawMode, final ITextureRegion pTextureRegion, final VertexBufferObjectManager pVertexBufferObjectManager, final DrawType pDrawType) {
		this(pX, pY, pVertexX.length, pDrawMode, pTextureRegion, new HighPerformanceTexturedMeshVertexBufferObject(pVertexBufferObjectManager, buildVertexList(pVertexX, pVertexY), pVertexX.length, pDrawType, true, TexturedMesh.VERTEXBUFFEROBJECTATTRIBUTES_DEFAULT));
	}

	public TexturedMesh(final float pX, final float pY, final int pVertexCount, final DrawMode pDrawMode, final ITextureRegion pTextureRegion, final ITexturedMeshVertexBufferObject pMeshVertexBufferObject) {
		super(pX, pY, PositionColorTextureCoordinatesShaderProgram.getInstance());

		this.mDrawMode = pDrawMode.getDrawMode();
		this.mTextureRegion = pTextureRegion;
		this.mMeshVertexBufferObject = pMeshVertexBufferObject;
		this.mVertexCountToDraw = pVertexCount;

		this.setBlendingEnabled(true);
		this.initBlendFunction(pTextureRegion);
		this.onUpdateTextureCoordinates();

		this.onUpdateVertices();
		this.onUpdateColor();

		this.mMeshVertexBufferObject.setDirtyOnHardware();

		this.setBlendingEnabled(true);
	}

	// ===========================================================
	// Getter & Setter
	// ===========================================================


	public float[] getBufferData() {
		return this.mMeshVertexBufferObject.getBufferData();
	}

	public void setVertexCountToDraw(final int pVertexCountToDraw) {
		this.mVertexCountToDraw = pVertexCountToDraw;
	}

	public void setDrawMode(final DrawMode pDrawMode) {
		this.mDrawMode = pDrawMode.mDrawMode;
	}
	
	public ITextureRegion getTextureRegion() {
		return this.mTextureRegion;
	}

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================
	@Override
	public ITexturedMeshVertexBufferObject getVertexBufferObject() {
		return this.mMeshVertexBufferObject;
	}

	@Override
	public void reset() {
		super.reset();

		this.initBlendFunction(this.getTextureRegion().getTexture());
	}

	@Override
	protected void preDraw(final GLState pGLState, final Camera pCamera) {
		super.preDraw(pGLState, pCamera);

		this.mTextureRegion.getTexture().bind(pGLState);
		this.mMeshVertexBufferObject.bind(pGLState, this.mShaderProgram);
	}

	@Override
	protected void draw(final GLState pGLState, final Camera pCamera) {
		this.mMeshVertexBufferObject.draw(this.mDrawMode, this.mVertexCountToDraw);
	}

	@Override
	protected void postDraw(final GLState pGLState, final Camera pCamera) {
		this.mMeshVertexBufferObject.unbind(pGLState, this.mShaderProgram);

		super.postDraw(pGLState, pCamera);
	}

	@Override
	protected void onUpdateColor() {
		this.mMeshVertexBufferObject.onUpdateColor(this);
	}

	@Override
	protected void onUpdateVertices() {
		this.mMeshVertexBufferObject.onUpdateVertices(this);
	}

	protected void onUpdateTextureCoordinates() {
		this.mMeshVertexBufferObject.onUpdateTextureCoordinates(this);
	}

	@Override
	@Deprecated
	public boolean contains(final float pX, final float pY) {
		throw new MethodNotSupportedException();
	}

	@Override
	public boolean collidesWith(final IEntity pOtherEntity) {
		if(pOtherEntity instanceof TexturedMesh) {
			// TODO
			return super.collidesWith(pOtherEntity);
		} else if(pOtherEntity instanceof Line) {
			// TODO
			return super.collidesWith(pOtherEntity);
		} else {
			return super.collidesWith(pOtherEntity);
		}
	}
	// ===========================================================
	// Methods
	// ===========================================================

	protected static float[] buildVertexList(float[] pVertexX, float[] pVertexY)
	{
		assert( pVertexX.length == pVertexY.length );

		float[] bufferData = new float[VERTEX_SIZE * pVertexX.length];
		updateVertexList(pVertexX, pVertexY, bufferData);
		return bufferData;
	}

	protected static void updateVertexList(float[] pVertexX, float[] pVertexY, float[] pBufferData)
	{
		for( int i = 0; i < pVertexX.length; i++) {
			pBufferData[(i * TexturedMesh.VERTEX_SIZE) + TexturedMesh.VERTEX_INDEX_X] = pVertexX[i];
			pBufferData[(i * TexturedMesh.VERTEX_SIZE) + TexturedMesh.VERTEX_INDEX_Y] = pVertexY[i];
		}
	}

	// ===========================================================
	// Inner and Anonymous Classes
	// ===========================================================

}

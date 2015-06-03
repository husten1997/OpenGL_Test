package res.graphic;

import static org.lwjgl.opengl.GL11.GL_MODELVIEW;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_TEXTURE;
import static org.lwjgl.opengl.GL11.glBegin;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glEnd;
import static org.lwjgl.opengl.GL11.glLoadIdentity;
import static org.lwjgl.opengl.GL11.glMatrixMode;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glRotatef;
import static org.lwjgl.opengl.GL11.glTexCoord2f;
import static org.lwjgl.opengl.GL11.glTranslatef;
import static org.lwjgl.opengl.GL11.glVertex2f;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;

public class RectObj {
	
	private int vaoId = 0;
    private int vboId = 0;
    private int vboiId = 0;
    private int indicesCount = 0;
    private VertexData[] vertices = null;
    private ByteBuffer verticesByteBuffer = null;
    
    private Vector3f modelPos = null;
    private Vector3f modelAngle = null;
    private Vector3f modelScale = null;
    
    private Shader shader;
    private int texture;
    private MatrixHandler matrix;
    private float tc_x;
    private float tc_y;
   
    
    public RectObj(Texture texture, MatrixHandler matrix, Vector2f tc){
    	this.shader = new Shader(matrix);
    	this.texture = texture.getTexID();
    	this.matrix = matrix;
    	tc_x = tc.x;
    	tc_y = tc.y;
    	setupQuad();
    }
    
    
	public void setupQuad() {
        // We'll define our quad using 4 vertices of the custom 'TexturedVertex' class
        VertexData v0 = new VertexData(); 
        v0.setXYZ(-0.5f, 0.5f, 0); v0.setRGB(1, 0, 0); v0.setST(0, 0);
        VertexData v1 = new VertexData(); 
        v1.setXYZ(-0.5f, -0.5f, 0); v1.setRGB(0, 1, 0); v1.setST(0, tc_y);
        VertexData v2 = new VertexData(); 
        v2.setXYZ(0.5f, -0.5f, 0); v2.setRGB(0, 0, 1); v2.setST(tc_x, tc_y);
        VertexData v3 = new VertexData(); 
        v3.setXYZ(0.5f, 0.5f, 0); v3.setRGB(1, 1, 1); v3.setST(tc_x, 0);
         
        vertices = new VertexData[] {v0, v1, v2, v3};
         
        // Put each 'Vertex' in one FloatBuffer
        verticesByteBuffer = BufferUtils.createByteBuffer(vertices.length * 
                VertexData.stride);             
        FloatBuffer verticesFloatBuffer = verticesByteBuffer.asFloatBuffer();
        for (int i = 0; i < vertices.length; i++) {
            // Add position, color and texture floats to the buffer
            verticesFloatBuffer.put(vertices[i].getElements());
        }
        verticesFloatBuffer.flip();
        
         
        // OpenGL expects to draw vertices in counter clockwise order by default
        byte[] indices = {
                0, 1, 2,
                2, 3, 0
        };
        indicesCount = indices.length;
        ByteBuffer indicesBuffer = BufferUtils.createByteBuffer(indicesCount);
        indicesBuffer.put(indices);
        indicesBuffer.flip();
         
        // Create a new Vertex Array Object in memory and select it (bind)
        vaoId = GL30.glGenVertexArrays();
        GL30.glBindVertexArray(vaoId);
         
        // Create a new Vertex Buffer Object in memory and select it (bind)
        vboId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, verticesFloatBuffer, GL15.GL_STREAM_DRAW);
         
        // Put the position coordinates in attribute list 0
        GL20.glVertexAttribPointer(0, VertexData.positionElementCount, GL11.GL_FLOAT, 
                false, VertexData.stride, VertexData.positionByteOffset);
        // Put the color components in attribute list 1
        GL20.glVertexAttribPointer(1, VertexData.colorElementCount, GL11.GL_FLOAT, 
                false, VertexData.stride, VertexData.colorByteOffset);
        // Put the texture coordinates in attribute list 2
        GL20.glVertexAttribPointer(2, VertexData.textureElementCount, GL11.GL_FLOAT, 
                false, VertexData.stride, VertexData.textureByteOffset);
         
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
         
        // Deselect (bind to 0) the VAO
        GL30.glBindVertexArray(0);
         
        // Create a new VBO for the indices and select it (bind) - INDICES
        vboiId = GL15.glGenBuffers();
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, 
                GL15.GL_STATIC_DRAW);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
         
        // Set the default quad rotation, scale and position values
        modelPos = new Vector3f(0, 0, 0);
        modelAngle = new Vector3f(0, 0, 0);
        modelScale = new Vector3f(2, 2, 1);
       
        updateMatrix();
         
        this.exitOnGLError("setupQuad");
    }
	
	
	
	private void exitOnGLError(String errorMessage) {
        int errorValue = GL11.glGetError();
         
        if (errorValue != GL11.GL_NO_ERROR) {
            String errorString = GLU.gluErrorString(errorValue);
            System.err.println("ERROR - " + errorMessage + ": " + errorString);
             
            if (Display.isCreated()) Display.destroy();
            System.exit(-1);
        }
    }
	
	
	
	public void draw() {
		
		GL20.glUseProgram(shader.getPID());
        // Bind the texture
		
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture);
         
        // Bind to the VAO that has all the information about the vertices
        GL30.glBindVertexArray(vaoId);
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
         
        // Bind to the index VBO that has all the information about the order of the vertices
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboiId);
         
        // Draw the vertices
        GL11.glDrawElements(GL11.GL_TRIANGLES, indicesCount, GL11.GL_UNSIGNED_BYTE, 0);
         
        // Put everything back to default (deselect)
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        
        GL20.glUseProgram(0);
        
        
        
         
        this.exitOnGLError("renderCycle");
    }
	
	
	
	public void deletRectObj(){
		// Select the VAO
        GL30.glBindVertexArray(vaoId);
         
        // Disable the VBO index from the VAO attributes list
        GL20.glDisableVertexAttribArray(0);
        GL20.glDisableVertexAttribArray(1);
         
        // Delete the vertex VBO
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboId);
         
        // Delete the index VBO
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, 0);
        GL15.glDeleteBuffers(vboiId);
         
        // Delete the VAO
        GL30.glBindVertexArray(0);
        GL30.glDeleteVertexArrays(vaoId);
        
//        shader.deletShader();
        
        updateMatrix();
	}


	public Vector3f getModelPos() {
		return modelPos;
	}


	public void setModelPos(Vector3f modelPos) {
		this.modelPos = modelPos;
	}


	public Vector3f getModelAngle() {
		return modelAngle;
	}


	public void setModelAngle(Vector3f modelAngle) {
		this.modelAngle = modelAngle;
		updateMatrix();
	}


	public Vector3f getModelScale() {
		return modelScale;
	}


	public void setModelScale(Vector3f modelScale) {
		this.modelScale = modelScale;
		updateMatrix();
	}
	
	public void transform2f(float x, float y){
		this.modelPos.x += x;
		this.modelPos.y += y;
		updateMatrix();
	}
	
	public void scale(float x, float y){
		this.modelScale.x = x;
		this.modelScale.y = y;
		updateMatrix();
	}
	
	public void updateMatrix(){
		matrix.updateMatrix(modelScale, modelPos, modelAngle, shader.getPID());
	}
//	
//	public Matrix4f getMatrix(){
//		return modelMatrix;
//	}
	
	public int getShaderId(){
		return shader.getPID();
	}
	
	
	
	
	
	

}

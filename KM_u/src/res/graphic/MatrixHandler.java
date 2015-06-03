package res.graphic;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector3f;

public class MatrixHandler {
	
	
	private Matrix4f projectionMatrix = null;
	private Matrix4f viewMatrix = null;
	private Matrix4f modelMatrix = null;
	
	private Camera camera;
	private Shader shader;
	
	private int projectionMatrixLocation = 0;
	private int viewMatrixLocation = 0;
	private int modelMatrixLocation = 0;
	
	private FloatBuffer matrix44Buffer = null;
	
	public MatrixHandler(float Width, float Height, Camera camera){
		setupMatrices(Width, Height);
		this.camera = camera;
	}
	private void setupMatrices(float WIDTH, float HEIGHT) {
        // Setup projection matrix
        projectionMatrix = new Matrix4f();
        float fieldOfView = 60f;
        float aspectRatio = (float)WIDTH / (float)HEIGHT;
        float near_plane = 0.1f;
        float far_plane = 1000f;
         
        float y_scale = this.coTangent(this.degreesToRadians(fieldOfView / 2f));
        float x_scale = y_scale / aspectRatio;
        float frustum_length = far_plane - near_plane;
         
        projectionMatrix.m00 = x_scale;
        projectionMatrix.m11 = y_scale;
        projectionMatrix.m22 = -((far_plane + near_plane) / frustum_length);
        projectionMatrix.m23 = -1;
        projectionMatrix.m32 = -((2 * near_plane * far_plane) / frustum_length);
                projectionMatrix.m33 = 0;
         
        // Setup view matrix
        viewMatrix = new Matrix4f();
         
        // Setup model matrix
        modelMatrix = new Matrix4f();
        
         
        // Create a FloatBuffer with the proper size to store our matrices later
        matrix44Buffer = BufferUtils.createFloatBuffer(16);
        shader = new Shader(this);
        camera = new Camera(this);
    }
	
	private float degreesToRadians(float degrees) {
        return degrees * (float)(Math.PI / 180d);
    }
	
	private float coTangent(float angle) {
        return (float)(1f / Math.tan(angle));
    }
	public int getProjectionMatrixLocation() {
		return projectionMatrixLocation;
	}
	public void setProjectionMatrixLocation(int projectionMatrixLocation) {
		this.projectionMatrixLocation = projectionMatrixLocation;
	}
	public int getViewMatrixLocation() {
		return viewMatrixLocation;
	}
	public void setViewMatrixLocation(int viewMatrixLocation) {
		this.viewMatrixLocation = viewMatrixLocation;
	}
	public int getModelMatrixLocation() {
		return modelMatrixLocation;
	}
	public void setModelMatrixLocation(int modelMatrixLocation) {
		this.modelMatrixLocation = modelMatrixLocation;
	}
	
	public void updateMatrix(Vector3f cameraPos, Vector3f modelScale, Vector3f modelPos, Vector3f modelAngle, int shader){
		//-- Update matrices
        // Reset view and model matrices
        viewMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
         
        // Translate camera
        Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
         
        // Scale, translate and rotate model
        Matrix4f.scale(modelScale, modelMatrix, modelMatrix);
        Matrix4f.translate(modelPos, modelMatrix, modelMatrix);
        Matrix4f.rotate(this.degreesToRadians(modelAngle.z), new Vector3f(0, 0, 1), 
                modelMatrix, modelMatrix);
        Matrix4f.rotate(this.degreesToRadians(modelAngle.y), new Vector3f(0, 1, 0), 
                modelMatrix, modelMatrix);
        Matrix4f.rotate(this.degreesToRadians(modelAngle.x), new Vector3f(1, 0, 0), 
                modelMatrix, modelMatrix);
         
        // Upload matrices to the uniform variables
        GL20.glUseProgram(shader);
         
        projectionMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
        viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
        modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
         
        GL20.glUseProgram(0);
	}
	
	public void updateMatrix(Vector3f cameraPos){
		//-- Update matrices
        // Reset view and model matrices
        viewMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
        int shaderI = this.shader.getPID();
         
        // Translate camera
        Matrix4f.translate(cameraPos, viewMatrix, viewMatrix);
         
       
         
        // Upload matrices to the uniform variables
        GL20.glUseProgram(shaderI);
         
        projectionMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
        viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
        modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
         
        GL20.glUseProgram(0);
	}
	
	public void updateMatrix(Vector3f modelScale, Vector3f modelPos, Vector3f modelAngle, int shader){
		//-- Update matrices
        // Reset view and model matrices
        viewMatrix = new Matrix4f();
        modelMatrix = new Matrix4f();
         
        // Translate camera
        Matrix4f.translate(camera.getCameraPos(), viewMatrix, viewMatrix);
         
        // Scale, translate and rotate model
        Matrix4f.scale(modelScale, modelMatrix, modelMatrix);
        Matrix4f.translate(modelPos, modelMatrix, modelMatrix);
        Matrix4f.rotate(this.degreesToRadians(modelAngle.z), new Vector3f(0, 0, 1), 
                modelMatrix, modelMatrix);
        Matrix4f.rotate(this.degreesToRadians(modelAngle.y), new Vector3f(0, 1, 0), 
                modelMatrix, modelMatrix);
        Matrix4f.rotate(this.degreesToRadians(modelAngle.x), new Vector3f(1, 0, 0), 
                modelMatrix, modelMatrix);
         
        // Upload matrices to the uniform variables
        GL20.glUseProgram(shader);
         
        projectionMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(projectionMatrixLocation, false, matrix44Buffer);
        viewMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(viewMatrixLocation, false, matrix44Buffer);
        modelMatrix.store(matrix44Buffer); matrix44Buffer.flip();
        GL20.glUniformMatrix4(modelMatrixLocation, false, matrix44Buffer);
         
        GL20.glUseProgram(0);
	}
	
	public void getRPos(){
		
	}
	
	public void setCamera(Camera camera){
		this.camera = camera;
	}

	
	

}

package res.graphic;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.glu.GLU;


public class Shader {
	
	
	private int pId = 0;
	
	MatrixHandler matrix;
	 
	 
	public Shader(MatrixHandler matrix){
		this.matrix = matrix;
		
		setupShaders();
	}
	
	private void setupShaders() {       
        // Load the vertex shader
        int vsId = this.loadShader("src/res/shader/vertex_m.glsl", GL20.GL_VERTEX_SHADER);
        // Load the fragment shader
        int fsId = this.loadShader("src/res/shader/fragment.glsl", GL20.GL_FRAGMENT_SHADER);
         
        // Create a new shader program that links both shaders
        pId = GL20.glCreateProgram();
        GL20.glAttachShader(pId, vsId);
        GL20.glAttachShader(pId, fsId);
 
        // Position information will be attribute 0
        GL20.glBindAttribLocation(pId, 0, "in_Position");
        // Color information will be attribute 1
        GL20.glBindAttribLocation(pId, 1, "in_Color");
        // Textute information will be attribute 2
        GL20.glBindAttribLocation(pId, 2, "in_TextureCoord");
 
        GL20.glLinkProgram(pId);
        GL20.glValidateProgram(pId);
 
        // Get matrices uniform locations
        matrix.setProjectionMatrixLocation(GL20.glGetUniformLocation(pId,"projectionMatrix"));
        matrix.setViewMatrixLocation(GL20.glGetUniformLocation(pId, "viewMatrix"));
        matrix.setModelMatrixLocation(GL20.glGetUniformLocation(pId, "modelMatrix"));
 
        this.exitOnGLError("setupShaders");
    }
	
	private int loadShader(String filename, int type) {
        StringBuilder shaderSource = new StringBuilder();
        int shaderID = 0;
         
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                shaderSource.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Could not read file.");
            e.printStackTrace();
            System.exit(-1);
        }
         
        shaderID = GL20.glCreateShader(type);
        GL20.glShaderSource(shaderID, shaderSource);
        GL20.glCompileShader(shaderID);
         
        if (GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == GL11.GL_FALSE) {
            System.err.println("Could not compile shader." + filename);
            System.exit(-1);
        }
         
        this.exitOnGLError("loadShader");
         
        return shaderID;
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
	
	public int getPID(){
		return pId;
	}
	
	public void deletShader(){
		GL20.glUseProgram(0);
        GL20.glDeleteProgram(pId);
	}

}

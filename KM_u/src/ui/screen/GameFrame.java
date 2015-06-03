package ui.screen;

import org.lwjgl.LWJGLException;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.PixelFormat;
import org.lwjgl.util.glu.GLU;
import org.lwjgl.util.vector.Vector2f;

import res.graphic.Camera;
import res.graphic.MatrixHandler;
import res.graphic.RectObj;
import res.graphic.Shader;
import res.graphic.Texture;
import de.matthiasmann.twl.renderer.lwjgl.LWJGLRenderer;

public class GameFrame {
	
	private MatrixHandler matrix;
	
	private Camera camera;
	private RectObj quad[][];
	private int mx = 64;
	private int my = 64;
	
	private RectObj quad1;
	private RectObj quad2;
	private Texture texture;
	private Texture texture2;
	private final static String WINDOW_TITLE = "The Quad: Moving";
	private final static int WIDTH = 1600;
	private final static int HEIGHT = 900;
	
	private long lastFrame, lastFPS;
	int fps;
	

	public static void main(String[] args) {
		GameFrame game = new GameFrame();
		game.start();

	}
	
	public void start(){
		// Initialize OpenGL (Display)
		setupOpenGL();
        ObjectInit();
        
        
        while (!Display.isCloseRequested()) {
            // Do a single loop (logic/render)
        	
            loopCycle();
            
           
            // Force a maximum FPS of about 60
//            Display.sync(60);
            // Let the CPU synchronize with the GPU if GPU is tagging behind
            Display.update();
        }
         
        // Destroy OpenGL (Display)
        destroyOpenGL();
	}
	
	public void ObjectInit(){
		
		matrix = new MatrixHandler(WIDTH, HEIGHT, camera);
		texture = new Texture("src/res/textures/water2.png");
		camera = new Camera(matrix);
		matrix.setCamera(camera);
		texture2 = new Texture("src/res/textures/gras.png");
		quad = new RectObj[mx][my];
		for(int i = 0; i < mx; i++){
			for(int j = 0; j < my; j++){
				quad[i][j] = new RectObj(texture2, matrix, new Vector2f(1f, 1f));
				quad[i][j].transform2f(i, j);
			}
		}
		quad1 = new RectObj(texture, matrix, new Vector2f(513f, 513f));
		quad1.scale(513, 513);
		
//		quad1.setupQuad();
//		quad2 = new RectObj(texture2, matrix);
//		quad2.setupQuad1();
		
		
		
	}
	
	private void loopCycle() {
        // Update logic
        logicCycle();
        // Update rendered frame
        
        renderCycle();
        
         
        exitOnGLError("loopCycle");
    }
	
	private void logicCycle() {
		
        //-- Input processing
       
        
        
        pollInput();
        updateFPS();
        
         
        matrix.updateMatrix(camera.getCameraPos(), quad1.getModelScale(), quad1.getModelPos(), quad1.getModelAngle(), quad1.getShaderId());
//        matrix.updateMatrix(camera.getCameraPos(), quad2.getModelScale(), quad2.getModelPos(), quad2.getModelAngle(), quad2.getShaderId());
//        for(int i = 0; i < mx; i++){
//			for(int j = 0; j < my; j++){
//				quad[i][j].updateMatrix();;
//			}
//		}
//        quad1.updateMatrix();
//        quad2.updateMatrix();
        GameFrame.exitOnGLError("logicCycle");
    }
	
	private void setupOpenGL() {
        // Setup an OpenGL context with API version 3.2
        try {
            PixelFormat pixelFormat = new PixelFormat();
            ContextAttribs contextAtrributes = new ContextAttribs(3, 2)
                .withForwardCompatible(true)
                .withProfileCore(true);
             
            Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
            Display.setTitle(WINDOW_TITLE);
            Display.create(pixelFormat, contextAtrributes);
             
            GL11.glViewport(0, 0, WIDTH, HEIGHT);
        } catch (LWJGLException e) {
            e.printStackTrace();
            System.exit(-1);
        }
         
        // Setup an XNA like background color
        GL11.glClearColor(0.4f, 0.6f, 0.9f, 0f);
         
        // Map the internal OpenGL coordinate system to the entire screen
        GL11.glViewport(0, 0, WIDTH, HEIGHT);
        lastFPS = getTime();
        exitOnGLError("setupOpenGL");
    }
	
	private void renderCycle() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        
        
        
        quad1.draw();
        for(int i = 0; i < mx; i++){
			for(int j = 0; j < my; j++){
				quad[i][j].draw();
			}
		}
        
       
//        quad2.draw();
         
         
//        exitOnGLError("renderCycle");
    }
	
	private static void exitOnGLError(String errorMessage) {
        int errorValue = GL11.glGetError();
         
        if (errorValue != GL11.GL_NO_ERROR) {
            String errorString = GLU.gluErrorString(errorValue);
            System.err.println("ERROR - " + errorMessage + ": " + errorString);
             
            if (Display.isCreated()) Display.destroy();
            System.exit(-1);
        }
    }
	
	private void destroyOpenGL() { 
		
        // Delete the texture
        texture.deletTexture();
        texture2.deletTexture();
         
        // Delete the shaders
        
         
         for(int i = 0; i < mx; i++){
 			for(int j = 0; j < my; j++){
 				quad[i][j].deletRectObj();;
 			}
 		}
         quad1.deletRectObj();
//         quad2.deletRectObj();
        
         
        exitOnGLError("destroyOpenGL");
         
        Display.destroy();
    }
	
	public void updateFPS() {
		if (getTime() - lastFPS > 1000) {
			Display.setTitle("FPS: " + fps);
			fps = 0;
			lastFPS += 1000;
		}
		fps++;
	}
	
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public void pollInput(){
		pollKeyboard();
		pollMouse();
	}
	
	private void pollKeyboard(){
		float posDelta = getDelta(1, 100);
		if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
			camera.setYPosR(-posDelta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
			camera.setYPosR(posDelta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
			camera.setXPosR(posDelta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
			camera.setXPosR(-posDelta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			quad2.transform2f(0, posDelta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			quad2.transform2f(0, -posDelta);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
			quad2.transform2f(posDelta, 0);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
			quad2.transform2f(-posDelta, 0);
		}
		while(Keyboard.next()) {
			
            
            if (!Keyboard.getEventKeyState()){
            	if (Keyboard.getEventKey() == Keyboard.KEY_UP) {
            		camera.setYPosR(-posDelta);
            	}
            	if (Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
            		camera.setYPosR(posDelta);
            	}
            	if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
            		camera.setXPosR(-posDelta);
            	}
            	if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
            		camera.setXPosR(posDelta);
            	}
            	
            }
             
            
            
             
            
            // Rotation
//            case Keyboard.KEY_LEFT:
//                modelAngle.z += rotationDelta;
//                break;
//            case Keyboard.KEY_RIGHT:
//                modelAngle.z -= rotationDelta;
//                break;
            
        }
	}
	
	private void pollMouse(){
		float scaleDelta = 0.1f;
		while (Mouse.next()) {
			if (Mouse.getEventButtonState()) {
				if (Mouse.getEventButton() == 0) {
					System.out.println("X: " + Mouse.getX());
					System.out.println("Y: " + Mouse.getY());
					
				}
			}
			
			float dWheel = Mouse.getDWheel()/100;
			if (dWheel < 0) {
				camera.setZPosR(dWheel);
			} else if (dWheel > 0) {
				camera.setZPosR(dWheel);
			}
		}
	}
	
	public float getDelta(float s, float t){
		return s/t;
	}

}

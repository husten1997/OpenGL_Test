package res.graphic;

import org.lwjgl.util.vector.Vector3f;

public class Camera {
	
	 private Vector3f cameraPos = null;
	 private MatrixHandler matrix;
	 
	 public Camera(MatrixHandler matrix){
		 cameraPos = new Vector3f(0, 0, -16);
		 this.matrix = matrix;
	 }
	 
	 public float getXPos(){
		 return cameraPos.x;
	 }
	 
	 public float getYPos(){
		 return cameraPos.y;
	 }
	 
	 public float getZPos(){
		 return cameraPos.z;
	 }
	 
	 public void setXPos(float x){
		 cameraPos.x = x;
		 updateMatrix();
	 }
	 
	 public void setYPos(float y){
		 cameraPos.y = y;
		 updateMatrix();
	 }
	 
	 public void setZPos(float z){
		 cameraPos.z = z;
		 updateMatrix();
	 }
	 
	 public void setXPosR(float x){
		 cameraPos.x += x;
		 updateMatrix();
	 }
	 
	 public void setYPosR(float y){
		 cameraPos.y += y;
		 updateMatrix();
	 }
	 
	 public void setZPosR(float z){
		 cameraPos.z += z;
		 updateMatrix();
	 }
	 
	 public Vector3f getCameraPos(){
		 return cameraPos;
		 
	 }
	 
	 public void updateMatrix(){
		 matrix.updateMatrix(cameraPos);
	 }
	 
	 

}

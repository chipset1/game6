abstract class Entity {
	PImage img;

	int fillColor = 255;

	PVector position, velocity, acceleration;


	//width and height of entity
	float width = -1;
	float height = -1;
        float mass = -1;

	boolean isDead = false;
	//public PVector getSize() { return img == null ? new PVector(0,0) : new PVector(img.width,img.height); }

	void setSize(float w, float h){
		width = w;
		height = h;
	}

  float getX(){ return position.x;}
  float getY(){ return position.y;}
  float getWidth(){ return width;}
  float getHeight(){ return height;}

	abstract void update(float dt);
	abstract void display();
 //  abstract void display(PGrahics ac);
 
	boolean collidesBox(Entity e){
		// edges for A
		  float a_left= position.x; 
  		float a_right = position.x + width;
  		float a_top = position.y;
  		float a_bottom = position.y + height;
		// edges for B
  		float b_left= e.position.x; 
  		float b_right = e.position.x + e.width;
  		float b_top = e.position.y;
  		float b_bottom = e.position.y + e.height;

  		if(a_right >= b_left && a_left <= b_right && a_bottom >= b_top && a_top <= b_bottom){
  			return true;
  		}
  		return false;
	}

  PVector rv = new PVector(0,0);
  PVector normal = new PVector(0,0);

  boolean isOnScreen(){ 
    float buffer = width;
    if(position.x < WIDTH + buffer)  return true;
    else if(position.x >     - buffer)  return true;
    if(position.y < HEIGHT + buffer)  return true;
    else if(position.y >     - buffer)  return true;
    return false;
  }

  void applyForce(PVector force) {
    PVector f = PVector.div(force, mass);
    acceleration.add(force);
  }

  void circleResponce(Entity e2){
      //PVector rv = PVector.sub( e2.velocity, this.velocity );
      rv.x = e2.velocity.x - velocity.x;
      rv.y = e2.velocity.y - velocity.y;
      // might be wrong normal cal
      //PVector normal = PVector.sub( this.position, e2.position );
      normal.x = position.x - e2.position.x;
      normal.y = position.y - e2.position.y;

      normal.normalize();
      float velAlong = rv.dot(normal);

      if(velAlong <= 0f){
        // if inside the circle then set the pos to the surface
        return;
      }
      //TODO add restitude
      float j = 2 * velAlong;
      normal.mult(j); 
      
      this.velocity.add(normal);
      normal.mult(-1);
      e2.velocity.add(normal);
  }

  boolean collisionCircle(Entity e){
    PVector posA = e.position;
    float widthA = e.width;

    float d = dist(position.x,position.y, posA.x, posA.y);
    return d < width/2 + widthA/2;
  }

}

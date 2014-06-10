class Planet extends Entity {

	float maxAttraction = 100.0;
  	float gravD; 

  	float r;
 	float ox , oy;


	Planet(float x, float y, float mass){
		position = new PVector(x, y);
		velocity = new PVector();
		this.mass = mass;
		setSize(mass * 3, mass * 3);
		gravD = width * 4;
	}

	PVector attract(Entity e, float strength) {
	    PVector force = PVector.sub(position ,e.position); 
	    float d = force.mag();    
	    force.normalize();                    
	    force.mult(strength);
	    // force.setMag(strength);                             
	    // float strength = (10 * mass * e.mass) / (d * d);
	    // force.mult(1);                        
	    return force;
	}

	PVector attract(Entity e) {
	    PVector force = PVector.sub(position ,e.position); 
	    float d = force.mag();                           
	    // force.setMag(strength);                             
	    float strength = ( mass * e.mass) / (d * d);
	    force.mult(strength);                        
	    return force;
	}

	void display(){
		stroke(0,0,255);
		noFill();
		ellipse(position.x, position.y, gravD, gravD);
		fill(255);
		noStroke();
		ellipse(position.x, position.y, width, height);
	}

	void update(float dt){
	    // velocity.add(acceleration);
	    // velocity.mult(0.98);
	    // velocity.limit(4);
	    // position.add(velocity);
	}

	boolean gravCollision(Entity e){
		float d = dist(position.x, position.y, e.position.x, e.position.y);
		return d < e.width /2 + gravD/2;
	}

}

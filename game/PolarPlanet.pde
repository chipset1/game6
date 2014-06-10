class PolarPlanet extends Planet {
	// Angle and angular velocity, accleration
	float theta;
	float theta_vel;
	float theta_acc;

	PolarPlanet(float x, float y, float mass){
		super(x,y,mass);
		theta = atan(y / x) + random(mass * 2);
		theta_vel = 0;
		//√ (122 + 52)
		r = sqrt(sq(x) + sq(y));
  		theta_acc = 5 / mass;
	}

	void update(float dt){
		updateVel();
		theta_vel += theta_acc * dt;
 		theta += theta_vel;
 		theta_vel = 0;
 		position.x = ox + r * cos(theta); 
 		position.y = oy + r * sin(theta);
	}

	void updateVel(){
		// look up better way 
		PVector p = new PVector();
		p.x = position.x - oy + r * cos(theta) ;
		p.y = position.y - oy + r * sin(theta) ;
		velocity.set(PVector.sub(p, position));
	}

}

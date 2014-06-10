class Bullet extends Entity
{
	boolean playerBullet = true;
	PVector oldPos;
	int lifeTime = 250;
	color strokeColor;

	Bullet(float x, float y, PVector vel){
		position = new PVector(x, y);
		velocity = new PVector(0,0);
		mass =1;
		oldPos = position;
		acceleration = vel;
		velocity.limit(10);
		setSize(1, 1);
		strokeColor = color(random(120, 200), random(120, 200), 255);
	}
	void display(){
		stroke(strokeColor);
		line(oldPos.x - acceleration.x * 2, oldPos.y - acceleration.y * 2, position.x , position.y );
	}

	void display(PGraphics ac){
		ac.strokeWeight(2);
		ac.stroke(strokeColor);
		ac.line(oldPos.x - acceleration.x, oldPos.y - acceleration.y, position.x , position.y );
		// ac.line(oldPos.x , oldPos.y, position.x , position.y );
	}

	void init(float x, float y, PVector vel){
		position.set(x,y);

		acceleration.x = vel.x;
		acceleration.y = vel.y;
	}

	void update(float dt){
		oldPos = position;
                //acceleration.mult(dt);
		velocity.add(acceleration);
		position.add(velocity);
		velocity.mult(0);
		lifeTime -=1;

	}

  boolean isDead(){
    return lifeTime <=0 || !isOnScreen();
  }

}

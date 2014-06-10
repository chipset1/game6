class Enemy extends Entity{

	PVector target;
	Timer change;
	float speedX = 20;
	float speedY = 20;
	Enemy(float x, float y){
		position = new PVector(x, y);
		velocity = new PVector();
		target = new PVector();
		setSize(10,10);
		change = new Timer(500, 0);
	}
        
        void update(){
        }

	void update(float dt){
		PVector playerPos = player.position;
		if(playerPos.x > position.x){
			velocity.x += speedX;
		}
		else if(playerPos.x < position.x){
			velocity.x -= speedX;
		}
		if(playerPos.y > position.y){
			velocity.y += speedY;
		}
		else if(playerPos.y < position.y ){
			velocity.y -= speedY;
		}
                velocity.mult(dt);
		velocity.limit(10);
		position.add(velocity);
	}

	void display(){
		fill(200, 200, 0);
		rect(position.x , position.y, width, height);
	}
}

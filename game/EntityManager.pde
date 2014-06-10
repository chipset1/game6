class EntityManager{
	ArrayList<Entity> entities = new ArrayList();
	// ArrayList<Bullet> bullets= new ArrayList();
	ArrayList<Bullet> bullets = new ArrayList();
	ObjectPool bulletPool;
	ArrayList<Enemy> enemies = new ArrayList();
	ArrayList<Planet> planets = new ArrayList();

	boolean isUpdating;

	// entities add to this list when entities are updatings
	ArrayList<Entity> addedEntities = new ArrayList<Entity>();

	Quadtree quad;

	int numBullets = 100;

	EntityManager(){
		quad = new Quadtree(0, new Rectangle(0,0, WIDTH, HEIGHT)); 
		// bulletPool = new ObjectPool(numBullets);
		// bullets = bulletPool.eList;
		// for(Bullet b : bullets){
		// 	entities.add(b);
		// }
	}

	void addEntity(Entity entity){
		entities.add(entity);
		// if(entity instanceof Bullet){
		// 	bullets.add((Bullet)entity);
		// }
		if(entity instanceof Planet){
			planets.add((Planet)entity);
		} 
		if(entity instanceof Enemy){
			enemies.add((Enemy)entity);
		}
	}

	void addBullet(Bullet b){
		// bulletPool.get(pPosX, pPosY, vel);
		bullets.add(b);
	}

	void update(float dt){
		// quadCollision();
		isUpdating = true;
		for (Entity e : entities) {
			e.display();
			e.update(dt);
		}
		isUpdating = false;
		// bulletPool.animate();
		for (Entity e : addedEntities) {
			add(e);
		}
		expiredCheck();
		addedEntities.clear();
		handleAttraction();
		// bulletUpdate();
//                quadCollision();

	}

	void bulletUpdate(PGraphics tCanvas, float dt){
		for(int i = bullets.size() -1; i >= 0; i--){
			Bullet b = bullets.get(i);
			if(b.isDead()){
				bullets.remove(b);
			}
			b.display(tCanvas);	
			b.update(dt);
		}
		// for (Bullet b : bullets) {
		// 	b.display(tCanvas);	
		// 	b.update(dt);
		// }
	}

	void bulletUpdate(float dt){
		for(int i = bullets.size() -1; i >= 0; i--){
			Bullet b = bullets.get(i);
			if(b.isDead()){
//				println("remove");
				bullets.remove(b);
			}
			b.display();	
			b.update(dt);
		}
		// for (Bullet b : bullets) {
		// 	b.display();	
		// 	b.update(dt);
		// }
	}

	void quadCollision(){
		quad.clear();
		for (int i = 0; i < entities.size(); i++) {
	  		quad.insert(entities.get(i));
		}
		
		ArrayList returnObjects = new ArrayList();
		
		for (int i = 0; i < entities.size(); i++) {
		  	returnObjects.clear();
		  	Entity e1 = entities.get(i);
		  	if( !e1.isOnScreen() ) continue;
		  	quad.retrieve(returnObjects, e1);
		 
			for (int x = 0; x < returnObjects.size(); x++) {
			  	Entity e2 = (Entity) returnObjects.get(x);

			  	// if(e1 == e2) continue;
			   	if(e1.collidesBox(e2)){
			   		// fill(255);
			   		e1.circleResponce(e2);
			   		// text("colliding", e1.getX() + 10, e1.getY());
			   		// text("colliding", e2.getX() + 10, e2.getY());
			   	}
		  	}
		}		
	}

	void expiredCheck(){
		for(int i = entities.size() -1; i >= 0; i--){
			Entity e = entities.get(i);
			if(e.isDead){
				entities.remove(e);
			}
		}
	}

	void handleAttraction(){
		for(Planet p: planets){
			playerToPlanetA(p);
			bulletToPlanetA(p);

		}
		for(Enemy e: enemies){
			playerEnemyCollision(e);
			bulletToEnemyC(e);
		}
	}

	void bulletToPlanetA(Planet p){
		for(Bullet b : bullets){
			if(p.gravCollision(b)){
				PVector f = p.attract(b, 1);	
				b.applyForce(f);
			}
		}		
	}

	void bulletToEnemyC(Enemy e){
		for(Bullet b: bullets){
			if(e.collisionCircle(b)){
				e.isDead = true;
			}
		}
	}

	void playerToPlanetA(Planet p){
		if(p.gravCollision(player)){
			PVector f = p.attract(player, 0.1); 
			
			stroke(200,0,0);
			line(p.position.x, p.position.y, player.position.x, player.position.y);
			
			player.applyForce(f);
			if(p.collisionCircle(player)){
				player.circleResponce(p);
				player.damage(0.5);
			}
		}
	}

	void eToEnemiesCollision(Enemy e){
		// if(!e.isOnScreen()) return;
		// Enemy tEnemy = e.isTouchingBox(enemies);
		// if(tEnemy != null){
		// 	e.circleResponce(tEnemy);
		// }
	}

	void playerEnemyCollision(Enemy e){
		if(e.collidesBox(player)){
			e.circleResponce(player);
			e.isDead = true;
			player.damage(2);
		}
	}

	int count(){
		return entities.size();
	}

	void add(Entity entity){
		if(!isUpdating){
			addEntity(entity);
		}else{
			addedEntities.add(entity);
		}
	}
}//

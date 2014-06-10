import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class game extends PApplet {

Player player;

EntityManager entityManager;
Keyboard keyboard;

int enemy_max = 20;
int WIDTH, HEIGHT;
float cameraOffsetX, cameraOffsetY;

// canvas for drawing trails
PGraphics tCanvas;
boolean drawTrail = false;
Timer enemyTimer;

//ObjectPool pool;
// good bullet size = 50;

public void setup() {
	size(700, 700);
	WIDTH = width;
	HEIGHT = height;

	tCanvas = createGraphics(WIDTH, HEIGHT);
	// tCanvas.colorMode(HSB);

	entityManager = new EntityManager();

	player = new Player(300, HEIGHT/2);
	keyboard = new Keyboard();
//	pool = new ObjectPool(150);
	addPlanets();
	frameRate(60);
	smooth();

	enemyTimer = new Timer(500, 0);

	// colorMode(HSB);
}

public void addPlanets(){
	PVector p = new PVector(0,0);
	float w = 0;
	Planet m = new Planet(WIDTH/2, HEIGHT/2, 20);
	entityManager.add(m);	
	for (int i = 0; i < 20; ++i) {
			float ma = random(5, 10);
			// Planet pl = new PolarPlanet(random(i * 2, width - 100), random(i * 2, height - 100), ma);
			float x = map(i , 0, 40, ma * 5, width - 300);
			float y = map(i , 0, 40, ma * 5, height - 300);
			Planet pl = new PolarPlanet(x , y, ma);
			pl.ox = m.position.x;
			pl.oy = m.position.y;
			entityManager.add(pl);
	}
}

float lastMillis = 0;
public void draw() {
	background(0);

        long nextMillis = millis();
        float deltaTime;
        if (0 == lastMillis) {
          deltaTime = 0;
        } else {
          deltaTime = (nextMillis - lastMillis) / 1000;
        }
        lastMillis = nextMillis;


	if(drawTrail){
		tCanvas.beginDraw();
		tCanvas.fill(0, 20);
		tCanvas.noStroke();
		tCanvas.rect(0,0, width, height);
		entityManager.bulletUpdate(tCanvas, deltaTime);
		tCanvas.endDraw();

		image(tCanvas, 0,0);
	}

        

        entityManager.bulletUpdate(deltaTime);
	fill(255);
	text(frameRate, 20, 50);
	noFill();
	stroke(255);
	ellipse(width/2, height/2, width - 10, height -10);
	trailButton();
	entityManager.update(deltaTime);
	player.display();
	player.update(deltaTime);

	if(enemyTimer.canRun()) addE();
}

public void trailButton(){
	float x = 29;
	float y = 80;
	float w = 83;
	float h = 25;
	rect(29, 80, 83, 25);
	fill(0,255,0);
	if(mouseX >x && mouseX < x + w && mouseY > y && mouseY < y + h){
		fill(255);	
		if(mousePressed){
			drawTrail = !drawTrail;
		}
	}
	if(drawTrail){
		text("trails On", 30, 100);
	}else{
		text("trails Off", 30, 100);
	}
}

public void addE(){
	// level bound
	float lb = (width/2) - 10;
	float x = width /2 + random(- lb, lb);
	float y = height /2 + random(-lb, lb);
	entityManager.add(new Enemy(x, y));
}

public void rC(Entity e1, Entity e2){
	PVector p = e1.position;
	PVector b = e2.position;
	PVector d = PVector.sub(b, p);
	float radius = e2.width/2;
	PVector bp = PVector.sub(p, b);
	bp.setMag((e2.width/2) + e1.width/2 );
	bp.add(b);
	fill(0,0,255);
	line(b.x, b.y, bp.x, bp.y);
	e1.position.set(bp);
}

public void keyPressed(){
	if(keyboard.holdingZ){
		drawTrail = !drawTrail;
	} 
	keyboard.pressKey(key, keyCode);
}

public void keyReleased(){
	keyboard.releaseKey(key, keyCode);
}
class Bullet extends Entity
{
	boolean playerBullet = true;
	PVector oldPos;
	int lifeTime = 250;
	int strokeColor;

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
	public void display(){
		stroke(strokeColor);
		line(oldPos.x - acceleration.x * 2, oldPos.y - acceleration.y * 2, position.x , position.y );
	}

	public void display(PGraphics ac){
		ac.strokeWeight(2);
		ac.stroke(strokeColor);
		ac.line(oldPos.x - acceleration.x, oldPos.y - acceleration.y, position.x , position.y );
		// ac.line(oldPos.x , oldPos.y, position.x , position.y );
	}

	public void init(float x, float y, PVector vel){
		position.set(x,y);

		acceleration.x = vel.x;
		acceleration.y = vel.y;
	}

	public void update(float dt){
		oldPos = position;
                //acceleration.mult(dt);
		velocity.add(acceleration);
		position.add(velocity);
		velocity.mult(0);
		lifeTime -=1;

	}

  public boolean isDead(){
    return lifeTime <=0 || !isOnScreen();
  }

}
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
        
        public void update(){
        }

	public void update(float dt){
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

	public void display(){
		fill(200, 200, 0);
		rect(position.x , position.y, width, height);
	}
}
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

	public void setSize(float w, float h){
		width = w;
		height = h;
	}

  public float getX(){ return position.x;}
  public float getY(){ return position.y;}
  public float getWidth(){ return width;}
  public float getHeight(){ return height;}

	public abstract void update(float dt);
	public abstract void display();
 //  abstract void display(PGrahics ac);
 
	public boolean collidesBox(Entity e){
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

  public boolean isOnScreen(){ 
    float buffer = width;
    if(position.x < WIDTH + buffer)  return true;
    else if(position.x >     - buffer)  return true;
    if(position.y < HEIGHT + buffer)  return true;
    else if(position.y >     - buffer)  return true;
    return false;
  }

  public void applyForce(PVector force) {
    PVector f = PVector.div(force, mass);
    acceleration.add(force);
  }

  public void circleResponce(Entity e2){
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

  public boolean collisionCircle(Entity e){
    PVector posA = e.position;
    float widthA = e.width;

    float d = dist(position.x,position.y, posA.x, posA.y);
    return d < width/2 + widthA/2;
  }

}
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

	public void addEntity(Entity entity){
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

	public void addBullet(Bullet b){
		// bulletPool.get(pPosX, pPosY, vel);
		bullets.add(b);
	}

	public void update(float dt){
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

	public void bulletUpdate(PGraphics tCanvas, float dt){
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

	public void bulletUpdate(float dt){
		for(int i = bullets.size() -1; i >= 0; i--){
			Bullet b = bullets.get(i);
			if(b.isDead()){
				println("remove");
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

	public void quadCollision(){
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

	public void expiredCheck(){
		for(int i = entities.size() -1; i >= 0; i--){
			Entity e = entities.get(i);
			if(e.isDead){
				entities.remove(e);
			}
		}
	}

	public void handleAttraction(){
		for(Planet p: planets){
			playerToPlanetA(p);
			bulletToPlanetA(p);

		}
		for(Enemy e: enemies){
			playerEnemyCollision(e);
			bulletToEnemyC(e);
		}
	}

	public void bulletToPlanetA(Planet p){
		for(Bullet b : bullets){
			if(p.gravCollision(b)){
				PVector f = p.attract(b, 1);	
				b.applyForce(f);
			}
		}		
	}

	public void bulletToEnemyC(Enemy e){
		for(Bullet b: bullets){
			if(e.collisionCircle(b)){
				e.isDead = true;
			}
		}
	}

	public void playerToPlanetA(Planet p){
		if(p.gravCollision(player)){
			PVector f = p.attract(player, 0.1f); 
			
			stroke(200,0,0);
			line(p.position.x, p.position.y, player.position.x, player.position.y);
			
			player.applyForce(f);
			if(p.collisionCircle(player)){
				player.circleResponce(p);
				player.damage(0.5f);
			}
		}
	}

	public void eToEnemiesCollision(Enemy e){
		// if(!e.isOnScreen()) return;
		// Enemy tEnemy = e.isTouchingBox(enemies);
		// if(tEnemy != null){
		// 	e.circleResponce(tEnemy);
		// }
	}

	public void playerEnemyCollision(Enemy e){
		if(e.collidesBox(player)){
			e.circleResponce(player);
			e.isDead = true;
			player.damage(2);
		}
	}

	public int count(){
		return entities.size();
	}

	public void add(Entity entity){
		if(!isUpdating){
			addEntity(entity);
		}else{
			addedEntities.add(entity);
		}
	}
}//
class Keyboard {
  Boolean holdingUp,holdingRight,holdingLeft,holdingDown,holdingZ,
  holdingW,holdingA,holdingS,holdingD,holdingM;
  
  Keyboard() {
    holdingUp=holdingRight=holdingLeft=holdingDown=holdingZ=holdingW=holdingA=holdingS=holdingD=holdingM=false;
  }

  public void pressKey(int key,int keyCode) {
    if (keyCode == UP) {
      holdingUp = true;
    }
    if (keyCode == LEFT) {
      holdingLeft = true;
    }
    if (keyCode == RIGHT) {
      holdingRight = true;
    }
    if (keyCode == DOWN) {
      holdingDown = true;
    }
    if (key == 'z' || key == 'Z') {
      holdingZ = true;      
    }
    if (key == 'w' || key == 'W') {
      holdingW = true;      
    }
    if (key == 'a' || key == 'A') {
      holdingA = true;      
    }
    if (key == 's' || key == 'S') {
      holdingS = true;      
    }
    if (key == 'd' || key == 'D') {
      holdingD = true;      
    }
    if (key == 'm' || key == 'M') {
      holdingM = true;      
    }
   
    /* // reminder: for keys with letters, check "key"
       // instead of "keyCode" !
    if (key == 'r') {
      // reset program?
    }
    if (key == ' ') {
      holdingSpace = true;
    }*/
  }
  public void releaseKey(int key,int keyCode) {
    if(key == 'z' || key == 'Z') {
      holdingZ = false;
    }
    if (keyCode == UP) {
      holdingUp = false;
    }
    if (keyCode == LEFT) {
      holdingLeft = false;
    }
    if (keyCode == RIGHT) {
      holdingRight = false;
    }
    if (keyCode == DOWN) {
      holdingDown = false;
    }
    if (key == 'z' || key == 'Z') {
      holdingZ = false;      
    }
    if (key == 'w' || key == 'W') {
      holdingW = false;      
    }
    if (key == 'a' || key == 'A') {
      holdingA = false;      
    }
    if (key == 's' || key == 'S') {
      holdingS = false;      
    }
    if (key == 'd' || key == 'D') {
      holdingD = false;      
    }
    if (key == 'm' || key == 'M') {
      holdingD = false;      
    }
  }
}
public class ObjectPool {
 
  private int size;
  private ArrayList<Bullet> eList;
 
  /**
   * Constructor
   */
  public ObjectPool (int pSize) {
    size = pSize;
    eList = new ArrayList<Bullet>();
 
    // Initialize the array with particles
    for (int i = 0; i < size; i++) {
      eList.add( new Bullet(-150, -150, new PVector(0, 0)));
    }
  }
 
  /**
   * Get the first available particle and assign it the new values
   */
  public void get(float pPosX, float pPosY, PVector vel) {
    for(int i = 0; i < size; i++){
      if(eList.get(i).isDead){
        eList.get(i).init(pPosX, pPosY, vel);
        return;
      }
      // Bullet b = eList.remove(size-1);
      // eList.add(0, b);
    }

  }
  /**
   * Animate the object pool. Any dead eList will be placed at the front of the list to be reused
   */
  public void animate() {
    for (int i = 0; i < size; i++) {
      // if (eList.get(i).isDead()) {
      //   Bullet b = eList.remove(i);
      //   eList.add(size-1, b);
      // }
        eList.get(i).display();
//        eList.get(i).update();
    }
  }
}
class Planet extends Entity {

	float maxAttraction = 100.0f;
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

	public PVector attract(Entity e, float strength) {
	    PVector force = PVector.sub(position ,e.position); 
	    float d = force.mag();    
	    force.normalize();                    
	    force.mult(strength);
	    // force.setMag(strength);                             
	    // float strength = (10 * mass * e.mass) / (d * d);
	    // force.mult(1);                        
	    return force;
	}

	public PVector attract(Entity e) {
	    PVector force = PVector.sub(position ,e.position); 
	    float d = force.mag();                           
	    // force.setMag(strength);                             
	    float strength = ( mass * e.mass) / (d * d);
	    force.mult(strength);                        
	    return force;
	}

	public void display(){
		stroke(0,0,255);
		noFill();
		ellipse(position.x, position.y, gravD, gravD);
		fill(255);
		noStroke();
		ellipse(position.x, position.y, width, height);
	}

	public void update(float dt){
	    // velocity.add(acceleration);
	    // velocity.mult(0.98);
	    // velocity.limit(4);
	    // position.add(velocity);
	}

	public boolean gravCollision(Entity e){
		float d = dist(position.x, position.y, e.position.x, e.position.y);
		return d < e.width /2 + gravD/2;
	}

}
class Player extends Entity{

  float damping = 0.98f;
  float health = 150;

  float playerSpeed = 20;
  float maxspeed = 5;

  PVector mousePos;

  Timer shotTimer;


  Player(float x, float y) {
    position = new PVector(x, y);
    velocity = new PVector(0, 0);
    acceleration = new PVector(0,0);

    fillColor = 0xff0C28FA;
    //79EBA9
    mass = 1;
    setSize(10,10);

    mousePos = new PVector();
    shotTimer = new Timer(50, 0);
  }

  public void update(float dt) {
    inputCheck();
    if(mousePressed && shotTimer.canRun()) shoot(); 
    bounds();
    acceleration.mult(dt);
    velocity.add(acceleration);
    velocity.mult(damping);
    velocity.limit(maxspeed);

    // velocity.mult(deltaTime * 2);
    
    position.add(velocity);
    acceleration.mult(0);
  }

  public void bounds(){
    float buffer = width;
    if(position.x > WIDTH + buffer) position.x = 0; 
    else if(position.x <     - buffer) position.x = WIDTH;
    if(position.y > HEIGHT + buffer)  position.y = 0; 
    else if(position.y <     - buffer) position.y = HEIGHT;
  }

  public void drawHealthBar(){
    fill(0,200,0);
    textSize(20);
    text("health: "+health, 20, 20);
  }

  public void damage(float d){
    health -= d;
    fillColor = 0xffFA0C5C;

  }

  public void display() {    
    drawHealthBar();
    noStroke();
    fill(fillColor);
    fillColor = 0xff0C28FA;
    ellipse(position.x, position.y, width, height);
  }

  public void shoot(){
    mousePos.x = mouseX;
    mousePos.y = mouseY;

    PVector bvel = PVector.sub(mousePos, position);
    bvel.normalize();
    bvel.mult(2);
    entityManager.addBullet( new Bullet(position.x, position.y, bvel));
    // entityManager.addBullet(position.x, position.y, bvel);
    // pool.get(position.x, position.y, bvel);
  }

  public void inputCheck() {
    if (keyboard.holdingLeft || keyboard.holdingA) {
        acceleration.x -= playerSpeed;
    } 
    if ( keyboard.holdingRight || keyboard.holdingD) {
        acceleration.x += playerSpeed;
    }
    if (keyboard.holdingUp || keyboard.holdingW) {
        acceleration.y -= playerSpeed;
    }
    if (keyboard.holdingDown ||keyboard.holdingS) {
        acceleration.y += playerSpeed;
    }
  }
}// end of player class

class PolarPlanet extends Planet {
	// Angle and angular velocity, accleration
	float theta;
	float theta_vel;
	float theta_acc;

	PolarPlanet(float x, float y, float mass){
		super(x,y,mass);
		theta = atan(y / x) + random(mass * 2);
		theta_vel = 0;
		//\u221a (122 + 52)
		r = sqrt(sq(x) + sq(y));
  		theta_acc = 5 / mass;
	}

	public void update(float dt){
		updateVel();
		theta_vel += theta_acc * dt;
 		theta += theta_vel;
 		theta_vel = 0;
 		position.x = ox + r * cos(theta); 
 		position.y = oy + r * sin(theta);
	}

	public void updateVel(){
		// look up better way 
		PVector p = new PVector();
		p.x = position.x - oy + r * cos(theta) ;
		p.y = position.y - oy + r * sin(theta) ;
		velocity.set(PVector.sub(p, position));
	}

}
//http://gamedevelopment.tutsplus.com/tutorials/
//quick-tip-use-quadtrees-to-detect-likely-collisions-in-2d-space--gamedev-374
class Quadtree{
	private int MAX_OBJECTS = 10;
  	private int MAX_LEVELS = 5;
 
  	private int level;
//  	private List objects;
  	private ArrayList objects;
    private Rectangle bounds;
  	private Quadtree[] nodes;
 
	public Quadtree(int pLevel, Rectangle pBounds) {
		level = pLevel;
		objects = new ArrayList();
		bounds = pBounds;
		nodes = new Quadtree[4];
	}

	public void clear(){
		objects.clear();
		for (int i = 0; i < nodes.length; ++i) {
			if(nodes[i] != null){
				nodes[i].clear();
				nodes[i] = null;
			}

		}
	}

	// split node into 4 sub nodes
	public void split(){
		int subWidth = (int) bounds.width / 2;
		int subHeight = (int) bounds.height / 2;

		int x = (int) bounds.getX();
		int y = (int) bounds.getY();

		nodes[0] = new Quadtree(level+1, new Rectangle(x + subWidth, y, subWidth, subHeight));
	    nodes[1] = new Quadtree(level+1, new Rectangle(x, y, subWidth, subHeight));
	    nodes[2] = new Quadtree(level+1, new Rectangle(x, y + subHeight, subWidth, subHeight));
	    nodes[3] = new Quadtree(level+1, new Rectangle(x + subWidth, y + subHeight, subWidth, subHeight));
	}
	
        // List
	public ArrayList retrieve(ArrayList returnObjects, Entity entity) {
    	int index = getIndex(entity);
    	if (index != -1 && nodes[0] != null) {
     		nodes[index].retrieve(returnObjects, entity);
   		}
 
   		returnObjects.addAll(objects);
   		return returnObjects;
 	} 

	public void insert(Entity entity){
		if(nodes[0] != null){
			int index = getIndex(entity);
			if(index != -1){
				nodes[index].insert(entity);
				return;
			}
		}

		objects.add(entity);
		if(objects.size() > MAX_OBJECTS && level < MAX_LEVELS){
			if(nodes[0] == null){
				split();
			}

			int i = 0;

			while(i < objects.size()){
				int index = getIndex((Entity)objects.get(i));
				if(index != -1){
					nodes[index].insert((Entity)objects.remove(i));
				}
				else {
					i++;
				}
			}
		}
	}

	//It determines where an object belongs in the quadtree by 
	// determining which node the object can fit into.
	public int getIndex(Entity entity){
		int index = -1;
		double verticalMidpoint = bounds.getX() + bounds.getWidth() / 2;
		double horizontalMidpoint = bounds.getY() + bounds.getHeight() /2;

		//Object can fit in the top quadrant
		boolean topQuadrant = (entity.getY() < horizontalMidpoint && entity.getY() + entity.getHeight() < horizontalMidpoint);

		//can completely fit within the bottom quadrants
		boolean bottomQuadrant = (entity.getY() > horizontalMidpoint);

		if(entity.getX() < verticalMidpoint && entity.getX() + entity.getWidth() < verticalMidpoint){
			if(topQuadrant){
				index = 1;
			}
			else if(bottomQuadrant){
				index =2;
			}
		}
		else if(entity.getX() > verticalMidpoint){
			if(topQuadrant){
				index = 0;
			}
			else if (bottomQuadrant){
				index = 3;
			}
		}

		return index;
	}

}

class Rectangle{
	// used to define quad tree node bounds
	int x;
	int y;
	int width;
	int height;

	Rectangle(int x, int y, int width, int height){
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public int getX(){ return x;}
	public int getY(){ return x;}
	public int getWidth(){ return width;}
	public int getHeight(){ return height;}
}
//HTIMER 
class Timer {
	private int lastInterval, interval, cycleCounter, numCycles;
	private boolean usesFrames = false;

	public Timer(int timerInterval, int numberOfCycles) {
		interval = timerInterval;
		numCycles = numberOfCycles;
	}
	
	public void useFrames() {
		usesFrames = true;
	}

	// find better name
	public boolean canRun() {
		int curr = (usesFrames)? frameCount : millis();
		if(lastInterval < 0) lastInterval = curr;
		if(curr-lastInterval >= interval) {
			lastInterval = curr;
			if(numCycles > 0 && ++cycleCounter >= numCycles) stop();
			return true;
		}
		return false;
	}

	public void stop() {
		numCycles = 0;
		lastInterval = -1;
	}

}
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "game" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}

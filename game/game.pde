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

void setup() {
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

void addPlanets(){
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
void draw() {
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

void trailButton(){
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

void addE(){
	// level bound
	float lb = (width/2) - 10;
	float x = width /2 + random(- lb, lb);
	float y = height /2 + random(-lb, lb);
	entityManager.add(new Enemy(x, y));
}

void rC(Entity e1, Entity e2){
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

void keyPressed(){
	if(keyboard.holdingZ){
		drawTrail = !drawTrail;
	} 
	keyboard.pressKey(key, keyCode);
}

void keyReleased(){
	keyboard.releaseKey(key, keyCode);
}

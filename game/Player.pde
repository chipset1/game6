class Player extends Entity{

  float damping = 0.98;
  float health = 150;

  float playerSpeed = 20;
  float maxspeed = 5;

  PVector mousePos;

  Timer shotTimer;


  Player(float x, float y) {
    position = new PVector(x, y);
    velocity = new PVector(0, 0);
    acceleration = new PVector(0,0);

    fillColor = #0C28FA;
    //79EBA9
    mass = 1;
    setSize(10,10);

    mousePos = new PVector();
    shotTimer = new Timer(50, 0);
  }

  void update(float dt) {
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

  void bounds(){
    float buffer = width;
    if(position.x > WIDTH + buffer) position.x = 0; 
    else if(position.x <     - buffer) position.x = WIDTH;
    if(position.y > HEIGHT + buffer)  position.y = 0; 
    else if(position.y <     - buffer) position.y = HEIGHT;
  }

  void drawHealthBar(){
    fill(0,200,0);
    textSize(20);
    text("health: "+health, 20, 20);
  }

  void damage(float d){
    health -= d;
    fillColor = #FA0C5C;

  }

  void display() {    
    drawHealthBar();
    noStroke();
    fill(fillColor);
    fillColor = #0C28FA;
    ellipse(position.x, position.y, width, height);
  }

  void shoot(){
    mousePos.x = mouseX;
    mousePos.y = mouseY;

    PVector bvel = PVector.sub(mousePos, position);
    bvel.normalize();
    bvel.mult(2);
    entityManager.addBullet( new Bullet(position.x, position.y, bvel));
    // entityManager.addBullet(position.x, position.y, bvel);
    // pool.get(position.x, position.y, bvel);
  }

  void inputCheck() {
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


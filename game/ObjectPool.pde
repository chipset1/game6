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
  void get(float pPosX, float pPosY, PVector vel) {
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

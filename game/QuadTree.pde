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

	void clear(){
		objects.clear();
		for (int i = 0; i < nodes.length; ++i) {
			if(nodes[i] != null){
				nodes[i].clear();
				nodes[i] = null;
			}

		}
	}

	// split node into 4 sub nodes
	void split(){
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
	ArrayList retrieve(ArrayList returnObjects, Entity entity) {
    	int index = getIndex(entity);
    	if (index != -1 && nodes[0] != null) {
     		nodes[index].retrieve(returnObjects, entity);
   		}
 
   		returnObjects.addAll(objects);
   		return returnObjects;
 	} 

	void insert(Entity entity){
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
	int getIndex(Entity entity){
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

	int getX(){ return x;}
	int getY(){ return x;}
	int getWidth(){ return width;}
	int getHeight(){ return height;}
}
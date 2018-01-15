package io.anuke.ucore.entities;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.IntMap;
import io.anuke.ucore.util.QuadTree;

public class EntityGroup<T extends Entity>{
	private static int lastid;
	private final int id;

	private IntMap<T> map;
	private Array<T> entityArray = new Array<>();
	private Array<T> entitiesToRemove = new Array<>();
	private Array<T> entitiesToAdd = new Array<>();
	private QuadTree<SolidEntity> tree;
	private Class<T> type;
	
	public  final boolean useTree;
	
	public EntityGroup(Class<T> type, boolean useTree){
		this.useTree = useTree;
		this.id = lastid ++;
		this.type = type;
	}

	public EntityGroup<T> enableMapping(){
		map = new IntMap<>();
		return this;
	}

	public Class<T> getType(){
		return type;
	}

	public int getID(){
		return id;
	}

	public void updateRemovals(){
		for(T e : entitiesToAdd){
			if(e == null)
				continue;
			entityArray.add(e);
			e.added();

			if(map != null){
				map.put(e.id, e);
			}
		}

		entitiesToAdd.clear();

		for(T e : entitiesToRemove){
			entityArray.removeValue(e, true);
			if(map != null){
				map.remove(e.id);
			}
		}

		entitiesToRemove.clear();
	}

	public T getByID(int id){
		if(map == null) throw new RuntimeException("Mapping is not enabled for this group!");
		return map.get(id);
	}

	public void remap(T entity, int newID){
		map.remove(entity.id);
		entity.id = newID;
		map.put(newID, entity);
	}
	
	public QuadTree<SolidEntity> tree(){
		return tree;
	}
	
	public void setTree(float x, float y, float w, float h){
		tree = new QuadTree(Entities.maxLeafObjects, new Rectangle(x, y, w, h));
	}
	
	public int amount(){
		return entityArray.size;
	}
	
	public void add(T type){
		if(type == null) throw new RuntimeException("Cannot add a null entity!");
		if(type.group != null) return; //throw new RuntimeException("Entities cannot be added twice!");
		type.group = this;
		entitiesToAdd.add(type);
	}
	
	public void remove(T type){
		if(type == null) throw new RuntimeException("Cannot remove a null entity!");
		type.group = null;
		entitiesToRemove.add(type);
	}
	
	public void clear(){
		for(Entity entity : entityArray)
			entity.group = null;
		
		for(Entity entity : entitiesToAdd)
			entity.group = null;
		
		for(Entity entity : entitiesToRemove)
			entity.group = null;
		
		entitiesToAdd.clear();
		entitiesToRemove.clear();
		entityArray.clear();
		if(map != null)
			map.clear();
	}
	
	public Array<T> all(){
		return entityArray;
	}
}

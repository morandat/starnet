package fr.labri.starnet.policies.commons;

import java.util.Collection;
import java.util.Map;

public class Utils {
	
	static public boolean isEmpty(Map<String,Object> storage, String setName){
		Collection<?> set = (Collection<?>) storage.get(setName);
		return set.isEmpty();
	}
	
	static public boolean isContained(Map<String,Object> storage, String setName,String msgName){
		Object obj = storage.get(msgName);
		Collection<?> set= (Collection<?>) storage.get(setName);
		if(set.contains(obj))
			return true;
		return false;
	}
	
	@SuppressWarnings("unchecked")
	static public void addToSet(Map<String,Object> storage, String setName,String msgName){	
		Object msg =  storage.get(msgName);
		Collection<Object> set = (Collection<Object>) storage.get(setName);
		set.add(msg);
	}
}

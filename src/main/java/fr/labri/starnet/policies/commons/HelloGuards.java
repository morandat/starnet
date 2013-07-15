package fr.labri.starnet.policies.commons;

import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.tima.ITimedAutomata.PredicateAdapter;

public class HelloGuards {
	
	static public boolean isEmpty(Map<String,Object> storage, String setName){
		HelloSet hellos = (HelloSet) storage.get(setName);
		return hellos.isEmpty();
	}
		
	public static class IsEmpty extends PredicateAdapter<INode> {
		public IsEmpty() {}
		@Override
		public boolean isValid(INode context, String key) {
			return isEmpty(context.getStorage(),CommonVar.HELLO_SET);
		}
	}
	
	public static class IsNotEmpty extends PredicateAdapter<INode> {
		public IsNotEmpty() {}
		@Override
		public boolean isValid(INode context, String key) {
			return !isEmpty(context.getStorage(),CommonVar.HELLO_SET);
		}
	}
}

package fr.labri.starnet.policies.commons;

import java.util.Map;

import fr.labri.starnet.INode;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

public class HelloGuards {
	

	static public boolean isEmpty(Map<String,Object> storage, String setName){
		HelloSet hellos = (HelloSet) storage.get(setName);
		return hellos.isEmpty();
	}
		
	public static class IsEmpty extends TransitionAdapter<INode> {
		public IsEmpty() {}
		@Override
		public boolean isValid(INode context) {
			return isEmpty(context.getStorage(),CommonVar.HELLO_SET);
		}
	}
	
	public static class IsNotEmpty extends TransitionAdapter<INode> {
		public IsNotEmpty() {}
		@Override
		public boolean isValid(INode context) {
			return !isEmpty(context.getStorage(),CommonVar.HELLO_SET);
		}
	}
}

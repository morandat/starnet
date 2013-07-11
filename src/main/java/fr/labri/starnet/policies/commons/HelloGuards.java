package fr.labri.starnet.policies.commons;

import fr.labri.starnet.INode;
import fr.labri.starnet.Message;
import fr.labri.timedautomata.TimedAutomata.TransitionAdapter;

import java.util.Map;

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

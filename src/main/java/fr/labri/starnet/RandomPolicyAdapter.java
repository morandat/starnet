package fr.labri.starnet;

import java.util.Random;

import fr.labri.starnet.Node.PolicyAdapter;


public class RandomPolicyAdapter implements PolicyAdapter {
	public final static Long ADAPT_SEED = Long.getLong("starnet.adapt.seed", System.nanoTime());
	public static Random _numberGenerator = new Random(); 
	RoutingPolicy[] _policies;
	RoutingPolicy _current;
	
	RandomPolicyAdapter(RoutingPolicy[] policies) {
		_policies = policies;
		adaptPolicy();
	}

	public RoutingPolicy adaptPolicy() {
		_current = _policies[_numberGenerator.nextInt(_policies.length)];
		return _current;
	}

	public RoutingPolicy getCurrentPolicy() {
		return _current;
	}
}

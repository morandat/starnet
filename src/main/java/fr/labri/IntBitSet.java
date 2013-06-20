package fr.labri;

import java.math.BigInteger;
import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Set;

public class IntBitSet extends AbstractCollection<Integer> implements Set<Integer> {
	BigInteger set = BigInteger.ZERO;
	
	public int size() {
		return set.bitCount();
	}

	public boolean isEmpty() {
		return set.equals(BigInteger.ZERO);
	}

	public boolean contains(Object o) {
		if(o instanceof Integer)
			return set.testBit(((Integer)o).intValue());
		return false;
	}

	public Iterator<Integer> iterator() {
		return new Iterator<Integer>() {
			BigInteger currentSet = set;
			BigInteger originalSet = set;
			int bit = -1;
			
			public boolean hasNext() {
				return !currentSet.equals(BigInteger.ZERO);
			}

			public Integer next() {
				bit = currentSet.getLowestSetBit();
				currentSet = currentSet.clearBit(bit);
				return bit;
			}

			public void remove() {
				if(originalSet != set)
					throw new ConcurrentModificationException();
				IntBitSet.this.remove(bit);
				originalSet = set;
			}
		};
	}


	public boolean add(Integer e) {
		if(!set.testBit(e)) {
			set = set.setBit(e);
			return true;
		}
		return false;
	}

	public boolean remove(Object o) {
		if(!contains(o)) {
			set = set.clearBit((Integer)o);
			return true;
		}
		return false;
	}

	public void clear() {
		set = BigInteger.ZERO;
	}
	
	public BigInteger asBigInteger() {
		return set;
	}
}

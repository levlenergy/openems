package io.openems.edge.levl.controller.controllers.common;

import java.util.ArrayList;
import java.util.List;

public class CollectionUtil {

	/**
	 * Utility method to join lists.
	 * 
	 * @param <T>   type of list
	 * @param lists to join
	 * @return joined list
	 */
	@SafeVarargs
	public static <T> List<T> join(List<T>... lists) {
		var result = new ArrayList<T>();
		for (var list : lists) {
			result.addAll(list);
		}
		return result;
	}
}

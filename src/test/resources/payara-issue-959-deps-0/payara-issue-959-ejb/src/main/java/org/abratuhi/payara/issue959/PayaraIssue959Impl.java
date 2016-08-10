package org.abratuhi.payara.issue959;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class PayaraIssue959Impl implements PayaraIssue959Intf {
	public void computSmthInvolvinLambdas() {
		List<String> somelist = new ArrayList<>();
		somelist.add("1");
		somelist.add("2");
		somelist.add("3");
		List<Integer> anotherlist = somelist.stream().filter(s -> s.contains("1")).map(s -> Integer.valueOf(s)).collect(Collectors.toList());
	}
}

package telran.interviews;

import java.time.LocalDate;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

public class InterviewQuestions {
	public static void displayOccurrences(String [] strings) {
		HashMap<String, Integer> mapOccurrences = getOccurrencesMap(strings);
		TreeMap<Integer, TreeSet<String>> treeMapOccurrences = getTreeMapOccurrences(mapOccurrences);
		displayOccurrences(treeMapOccurrences);
	}
	
	public static void displayOccurrencesStream(String[] strings) {
		Arrays.stream(strings)
		.collect(Collectors.groupingBy(s -> s, Collectors.counting()))
		.entrySet().stream().sorted((e1, e2) -> {
			int res = Long.compare(e2.getValue(), e1.getValue());
			return res == 0 ? e1.getKey().compareTo(e2.getKey()) : res;
		}).forEachOrdered(e -> System.out.printf("%s -> %d\n", e.getKey(), e.getValue()));
	}
	
	private static void displayOccurrences(TreeMap<Integer, TreeSet<String>> treeMapOccurrences) {
		treeMapOccurrences.entrySet().forEach(e -> {
			e.getValue().forEach(str -> System.out.printf("%s => %d\n",str, e.getKey()));
		});
		
	}
	
	private static TreeMap<Integer, TreeSet<String>> getTreeMapOccurrences(HashMap<String, Integer> mapOccurrences) {
		TreeMap<Integer, TreeSet<String>> result =
				new TreeMap<Integer, TreeSet<String>>(Comparator.reverseOrder());
		mapOccurrences.entrySet()
		.forEach(e -> result.computeIfAbsent(e.getValue(), k -> new TreeSet<>() ).add(e.getKey()));
		
		return result;
	}
	
	private static HashMap<String, Integer> getOccurrencesMap(String[] strings) {
		HashMap<String, Integer> result = new HashMap<>();
		for(String str: strings) {
			result.merge(str, 1, Integer::sum);
		}
		return result;
	}
	
	static public boolean isSum2(int [] array, int sum) {
		//returns true if a given array contains two numbers, the summing of which
		//equals a given 'sum' value
		//complexity O[N] only one pass over the elements
		HashSet<Integer> helper = new HashSet<>();
		int index = 0;
		while(index < array.length && !helper.contains(sum - array[index])) {
			helper.add(array[index++]);
		}
		return index < array.length;
	}
	
	static public int getMaxWithNegativePresentation(int [] array) {
		//returns maximal positive value for which exists negative one with the same abs value
		//if no pair of positive and negative values with the same abs value the method returns -1
		//complexity O[N] only one pass over the elements
		int maxRes = -1;
		HashSet<Integer> helper = new HashSet<>();
		for(int num: array) {
			if(helper.contains(-num)) {
				maxRes = Math.max(maxRes, Math.abs(num));
			} else {
				helper.add(num);
			}
		}
		return maxRes;
	}
	
	public static Map<Integer, Integer> getMapSquares(List<Integer> numbers) {
		Map<Integer, Integer> res = numbers.stream()
				.collect(Collectors.toMap(n -> n, n -> n * n, (v1, v2) -> v1,
						LinkedHashMap::new));
		return res;
	}
	
	//two passes
	public static boolean isAnagram(String word, String anagram) {
		//returns true if "anagram" string contains all
		// letters from "word" in another order (case sensitive)
		//O[N] (sorting is disallowed)
		//two passes can be: one pass through the word, second is pass through anagram
		boolean result = false;
	    if (word != null && anagram != null && word.length() == anagram.length() && !word.equals(anagram)) {
		    Map<Character, Long> charCountMap = word.chars()
		    	.mapToObj(c -> (char) c)
				.collect(Collectors.groupingBy(c -> c, Collectors.counting()));
			    
			anagram.chars()
				.mapToObj(c -> (char) c)
		        .forEach(c -> {
		        	if (charCountMap.merge(c, -1L, Long::sum) == 0) {
		        	    charCountMap.remove(c);
		        	}
		        });
			result = charCountMap.isEmpty();
			
	    }	    
	    return result;	    
	}
	
	//three passes
	public static boolean isAnagramWithStreamOnly(String word, String anagram) {
		//returns true if "anagram" string contains all
		// letters from "word" in another order (case sensitive)
		//O[N] (sorting is disallowed)
		//two passes can be: one pass through the word, second is pass through anagram
	    return word != null && anagram != null && word.length() == anagram.length() 
	    		&& !word.equals(anagram) && charsCount(word).equals(charsCount(anagram));  
	}

	private static Map<Character, Long> charsCount(String word) {
		return word.chars().mapToObj(c -> (char) c)
		        .collect(Collectors.groupingBy(c -> c, Collectors.counting()));
	}
	
	// used TreeMap without stream - longest but efficient
	public static List<DateRole> assignRoleDates(List<DateRole> rolesHistory,
			List<LocalDate> dates) {
		// Use TreeMap for efficient floor entry lookups
		TreeMap<LocalDate, String> rolesMap = new TreeMap<>();
		for (DateRole role : rolesHistory) {
			rolesMap.put(role.date(), role.role());
		}
		
		List<DateRole> result = new ArrayList<>();
		for (LocalDate date : dates) {
			Map.Entry<LocalDate, String> entry = rolesMap.floorEntry(date);
			String role = (entry != null) ? entry.getValue() : null;
			result.add(new DateRole(date, role));
		}
		
		return result;
	}
	
	//used TreeMap but with stream - shortest and still efficient
	public static List<DateRole> assignRoleDatesWithStream(List<DateRole> rolesHistory,
			List<LocalDate> dates) {
		//create List<DateRole> with roles matching with the given dates
		//most effective data structure
		// Use TreeMap for efficient floor entry lookups
		TreeMap<LocalDate, String> rolesMap = rolesHistory.stream()
				.collect(Collectors.toMap(DateRole::date, DateRole::role, (oldValue, newValue) -> newValue, TreeMap::new));
			
		return dates.stream()
			.map(date -> new DateRole(date, rolesMap.floorEntry(date) != null ? rolesMap.floorEntry(date).getValue() : null))
			.toList();
	}
	
	public static void displayDigitsStatistics() {
		//display out statistics in the following format (example)
		/* 1 -> <count of occurrences>
		 * 2 -> .....
		 * .........
		 */
		//sorted by counts of occurrences in the descending order.
		//takes 1000000 random numbers in range [0-Integer.MAX_VALUE) 
		//where '[' means inclusive & ')' exclusive.
		//one stream pipeline with no additional yours methods
        new Random().ints(0, Integer.MAX_VALUE)
        .limit(1_000_000)
        .flatMap(n -> String.valueOf(n).chars())
        .mapToObj(c -> (char) c)
        .collect(Collectors.groupingBy(c -> c, Collectors.counting()))
        .entrySet().stream()
        .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
        .forEach(entry -> System.out.println(entry.getKey() + " -> " + entry.getValue()));
	
	}

}

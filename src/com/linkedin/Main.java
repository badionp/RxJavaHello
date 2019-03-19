package com.linkedin;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import io.reactivex.*;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class Main<T> {

	public static void main(String[] args) {
		hello("Iron Man", "The Hulk", "Thor");
		
		String[] stringArray = new String[] {"hi","hola","bonjour"};
		Observable<String> observable = Observable.fromArray(stringArray);
		
		List<Integer> integerList = Arrays.asList(new Integer[]{1,2,3,4,5,6,7,8,9,10});
		Observable<Integer> integerObservable = Observable.fromIterable(integerList);
		Observable<String> singleValueObservable = Observable.just("Just One String");
		
		//Custom Observable
		Observable.create(emitter-> {
			try {
				//Emit values 0 to 10
				IntStream.range(0,10).boxed().forEach(integer ->
						System.out.println("integer = " + integer)
				);
				emitter.onComplete();			
			}
			catch (Exception e) {
				emitter.onError(e);
			}
		});
		
		Observable interval = Observable.interval(100, TimeUnit.MILLISECONDS);
		
		//Consuming observables
		integerObservable.subscribe(intVal -> System.out.print(intVal + " "));
		System.out.println();
		System.out.println("Using static method reference");
		integerObservable.subscribe(Main::printObservable);
		
		//Filter out value that isn't greater than 4
		System.out.println("AFTER FILTER");
		integerObservable.filter(integer -> integer > 4).subscribe(Main::printObservable);
		
		//Map each value to the square of that value
		System.out.println();
		System.out.println("AFTER MAPPING TO SQUARE");
		integerObservable
			.map(integer -> Math.multiplyExact(integer, integer))
			.subscribe(Main::printObservable);
		
		//Subscribe using ConsolePrintObserver
		System.out.println();
		System.out.println("Using ConsolePrintObserver");
		integerObservable.subscribe(new ConsolePrintObserver());
		
		System.out.println("Using new thread scheduler");		
		integerObservable
			.unsubscribeOn(Schedulers.newThread())
			.subscribe(new ConsolePrintObserver());
		
		//Use it all in one statement
		System.out.println("All together now");
		integerObservable
			.filter(v -> v > 4)
			.map( integer -> Math.multiplyExact(integer, integer))
			.unsubscribeOn(Schedulers.newThread())
			.subscribe(new ConsolePrintObserver());
	}
	
    private static <T> void printObservable(T val) {
        System.out.print(val + " - ");
    }

	public static void hello(String... names) {
		  Flowable.fromArray(names).subscribe(new Consumer<String>() {
		      @Override
		      public void accept(String s) {
		          System.out.println("Hello " + s + "!");
		      }
		  });
		}
}
